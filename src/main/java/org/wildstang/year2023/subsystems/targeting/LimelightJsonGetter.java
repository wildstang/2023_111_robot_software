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

    @Override
    public void init() {

        //create parser
        this.parser = new JSONParser();

        //everthing below this comment is the same code as is in update(),
        //just repeated here as well to decrease the amount of time we have to wait to get access to the data.

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
        currentObject.g
    }

    public void changePipeline(String pipelineString) {
        this.currentPipeline = pipelineStringToInt.get(pipelineString);

        NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(this.currentPipeline);
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