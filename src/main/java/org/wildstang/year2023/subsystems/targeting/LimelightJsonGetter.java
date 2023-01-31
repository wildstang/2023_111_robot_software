package org.wildstang.year2023.subsystems.targeting;

import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.io.inputs.Input;

import edu.wpi.first.networktables.NetworkTableInstance;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;
import java.util.HashMap;

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

    //pipeline
    private int currentPipeline;
    private Map<String, Integer> pipelineStringToInt = new HashMap<String, Integer>() {{
        put("aprilTag", 0);
        put("reflective", 1);
    }};

    private enum AprilTag{
        ObjectOne;
    
        double aprilTagID;
        double targetSize;
        double targetXCoord;
        double targetYCoord;
        
        double[] returnValues(){
            double[] array = {aprilTagID,targetSize, targetXCoord, targetYCoord};
            return array;
        }
    
        void setPosition(int fID, double ta, double tx, double ty){
            aprilTagID =  fID;
            targetSize = ta;
            targetXCoord = tx;
            targetYCoord = ty;
        }
    }

    private enum retroTape{
        0;
        
    
        double targetSize;
        double targetXCoord;
        double targetYCoord;
        
        double[] returnValues(){
            double[] array = {targetSize, targetXCoord, targetYCoord};
            return array;
        }
    
        void setPosition(double ta, double tx, double ty){
            targetSize = ta;
            targetXCoord = tx;
            targetYCoord = ty;
        }
    }

    @Override
    public void init() {
        JSONDUMP = (WsRemoteAnalogInput) WSInputs.JSONDUMP.get();

        //create parser
        this.parser = new JSONParser();

        //everthing below this comment is the same code as is in update(),
        //just repeated here as well to decrease the amount of time we have to wait to get access to the data.

        //get json string

        this.currentObjectString = JSONDUMP.getString(null);

        //if the string is null do not save it as the one we are parsing
        //hopefully helps to avoid most ParseExceptions
        if (currentObjectString != null) {
            this.currentObjectString = latestFetch;
        }

        //parse the string
        try {

            this.currentObject = (JSONObject) parser.parse(currentObjectString);
            switch (currentPipeline){
                    case 0: //retroTape
                    Iterator<Map.Entry> iterateResults = JSONDUMP.entrySet().iterator();
                        while (itr1.hasNext()) {
                            Map.Entry pair = itr1.next();
                            System.out.println(pair.getKey() + " : " + pair.getValue());
                        }

                    Iterator iterateFiducialMarkers = JSONDUMP.iterator();
            
                    private int i=0;
                    while (iterateFiducialMarkers.hasNext()) 
                    {
                        //int fID, double ta, double tx, double ty
                        iterateResults = ((Map) itr2.next()).Retro().entrySet().iterator();
                        
                        private double[] = {0,0,0}
                        while (itr1.hasNext()) {
                            Map.Entry pair = iterateResults.next();
                            switch (pair.getKey()){
                                case "ta": double[1] = pair.getValue();
                                    break;
                                case "tx": double[2] = pair.getValue();
                                    break;
                                case "ty": double[3] = pair.getValue();
                                    break;
                                default: //Nothing there?
                                    break;
                            }
                            System.out.println(pair.getKey() + " : " + pair.getValue());
                        }
                        retroTape i
                        i++;
                    }
                    break;
                case 1:

                    break;
            }

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

        } catch (ParseException e) { //if ParseException does occur then <do something>

            e.printStackTrace();

        }


    }

    private double getDoubleFromJSON(String... jsonPropertyPath) {
        //currentObject.g
        return 0;
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