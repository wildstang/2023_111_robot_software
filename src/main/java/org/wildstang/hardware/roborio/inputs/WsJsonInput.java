package org.wildstang.hardware.roborio.inputs;

import java.io.FileReader;

import org.json.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wildstang.framework.io.inputs.DiscreteInput;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class WsJsonInput extends DiscreteInput {

    /**
     * Construct the remote input.
     * @param p_name Name of the input.
     * @param objectString String representation of the JSON object.
     */
    public WsJsonInput(String p_name) {
        super(p_name);


        String objectString = NetworkTableInstance.getDefault().getTable("limelight").getEntry("json").getString(null);
        if (objectString == null) {
            throw new NoValueFoundException();
        }
        JSONParser parser = new JSONParser();

        try {
            JSONObject object = (JSONObject) parser.parse(objectString);

        //tbd
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /** Gets a property's value and returns a double representing the value. The property value has to be castable to a double.
     * The property is specified as a vararg with the first Strings being the names of the JSONArrays that the property is inside.
     * The last parameter is the property's name.
     *
     * Using limelight apriltags as an example:
     * "getProperty("Fiducial", "tx");"
     *
     * This would get the tx of the apriltag.
     *
     * @param jsonPropertyPath The "path" of the JSON property, as described above.
     * @return The value of the property
     */
    public double getDoubleProperty(String... jsonPropertyPath) {
        //tbd
        return 0.0;
    }
}
