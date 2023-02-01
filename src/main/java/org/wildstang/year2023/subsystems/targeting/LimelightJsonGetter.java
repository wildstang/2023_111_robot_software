package org.wildstang.year2023.subsystems.targeting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsRemoteAnalogInput;
import org.wildstang.year2023.robot.WSInputs;

import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Subsysytem that fetches JSON from Limelight constantly and gives access to it.
 * @author foxler2010,
 * @author jwaters3457,
 */
public class LimelightJsonGetter implements Subsystem {

    //json
    private String latestFetch;
    private String currentObjectString;
    private JSONParser parser;
    private JSONObject currentObject;
    private WsRemoteAnalogInput JSONDUMP;

    //pipeline
    private int currentPipeline;
    private Map<String, Integer> pipelineStringToInt = new HashMap<String, Integer>() {{
        put("aprilTag", 0);
        put("reflective", 1);
    }};

    @Override
    public void init() {
        JSONDUMP = (WsRemoteAnalogInput) WSInputs.JSONDUMP.get();

        //create parser
        this.parser = new JSONParser();

        //everthing below this comment is the same code as is in update(),
        //just repeated here as well to decrease the amount of time we have to wait to get access to the data.

        //get json string

        this.latestFetch = NetworkTableInstance.getDefault().getTable("limelight").getEntry("json").getString(null);

        //if the string is null do not save it as the one we are parsing
        //hopefully helps to avoid most ParseExceptions
        if (currentObjectString != null) {
            this.currentObjectString = latestFetch;
        }

        //parse the string
        try {

            switch (currentPipeline) {

                case 0: { //retroTape

                    Iterator<Map.Entry<String, Double>> iterateResults = JSONDUMP.entrySet().iterator();

                    while (itr1.hasNext()) {
                        Map.Entry pair = itr1.next();
                        System.out.println(pair.getKey() + " : " + pair.getValue());
                    }

                    Iterator<Map.Entry<String, Double>> iterateFiducialMarkers = JSONDUMP.iterator();

                    int i = 0;

                    while (iterateFiducialMarkers.hasNext()) {

                        //int fID, double ta, double tx, double ty
                        iterateResults = ((Map) itr2.next()).Retro().entrySet().iterator();

                        double[] doubleArray = {0,0,0};

                        while (itr1.hasNext()) {

                            Map.Entry<String, Double> pair = iterateResults.next();

                            switch (pair.getKey()) {

                                case "ta": {
                                    doubleArray[1] = pair.getValue();
                                    break;
                                }

                                case "tx": {
                                    doubleArray[2] = pair.getValue();
                                    break;
                                }

                                case "ty": {
                                    doubleArray[3] = pair.getValue();
                                    break;
                                }

                                default: { //Nothing there?
                                    break;
                                }
                            }

                            System.out.println(pair.getKey() + " : " + pair.getValue());

                        }

                        i++;

                    }

                    break;

                }

                case 1: {

                    break;

                }

            }

            this.currentObject = (JSONObject) parser.parse(currentObjectString);

        } catch (ParseException e) { //if ParseException does occur then <do something>
            e.printStackTrace();
        }

    }

    @Override
    public void resetState() {
        this.latestFetch = null;
        this.currentObjectString = null;
    }

    @Override
    public void update() {

        //get json string
        this.currentObjectString = NetworkTableInstance.getDefault().getTable("limelight").getEntry("json").getString(null);

        //if the string is null do not save it as the one we are parsing
        //hopefully helps to avoid most ParseExceptions
        if (currentObjectString != null) {
            this.currentObjectString = latestFetch;
        }

        //parse the string
        try {
            this.currentObject = (JSONObject) parser.parse(currentObjectString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /** Get the object from inside the JSON dump that actually has the numbers.
     * This method automatically finds out what pipeline we are using,
     * and gets (from within the JSON dump) the JSON object that actually has the numbers.
     * This method is used by most of the property-getter methods to clean them up,
     * and eliminate duplicate code.
     * @return A JSON object containing the numbers (without all the clout).
     */
    private JSONObject getObjectWithTheData() {

        //What array of "Results {}" to look in.
        String arrayToLookIn;

        switch (this.currentPipeline) {
            case 0: {
                arrayToLookIn = "Fiducial";
            }
            case 1: {
                arrayToLookIn = "Retro";
            }
            default: {
                arrayToLookIn = "Retro";
            }
        }

        //1 layer deep: "Results"
        JSONObject resultsObject = (JSONObject) currentObject.get("\"Results\"");

        //2 layers deep: "Fiducial", or "Retro"
        JSONArray arrayContainingTheObjectWithTheData = (JSONArray) resultsObject.get("\"" + arrayToLookIn + "\"");

        //3 layers deep: The object from index 0 within the array
        return (JSONObject) arrayContainingTheObjectWithTheData.get(0); //JSONArray extends ArrayList so we can easily get() stuff from it

    }

    /** Gets the value of a key whose value is a double.
     * WILL NOT work on keys whose value is a JSONArray.
     * @param key The key of the value you would like to retrieve
     */
    public double getDoubleProperty(String key) {

        //continues getObjectWithTheData() from layer 3
        JSONObject objectWithTheData = getObjectWithTheData();

        //4 layers in: key
        return (Double) objectWithTheData.get("\"" + key + "\"");

    }

    public void changePipeline(String pipelineString) {
        currentPipeline = pipelineStringToInt.get(pipelineString);

        NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(currentPipeline);
    }

    @Override
    public void inputUpdate(Input source) {}

    @Override
    public String getName() {
        return "LimelightJsonGetter";
    }

    @Override
    public void selfTest() {}
}