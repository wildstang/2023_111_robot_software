package org.wildstang.hardware.roborio.inputs;

import java.io.FileReader;
import org.json.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class WsRemoteJsonInput extends AnalogInput {

    private String objectName;

    /**
     * Construct the remote input.
     * @param p_name Name of the input.
     * @param objectName JSON Object name.
     */
    public WsRemoteJsonInput(String p_name, String objectName) {
        super(p_name);
        JSONObject object = new JSONObject();
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
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(new FileReader(jsonPropertyPath));
        //String pageName = obj.getJSONObject("pageInfo").getString("pageName"); I dont think we need this but uncomment if we do

        JSONArray arr = obj.getJSONArray("posts"); // notice that `"posts": [...]`

        for (int i = 0; i < arr.length(); i++) {
            String post_id = arr.getJSONObject(i).getString("post_id");
        }

        return property;
    }
}
