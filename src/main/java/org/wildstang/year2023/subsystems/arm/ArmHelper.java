package org.wildstang.year2023.subsystems.arm;
import org.wildstang.year2023.subsystems.arm.Network;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.outputs.WsPhoenix;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;
import java.util.Random;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
/**
 * Sample Subsystem that controls a motor with a joystick.
 * @author Liam
 */
public class ArmHelper implements Subsystem {
    // inputs
    //WsJoystickAxis joystick;
    // outputs
    //WsPhoenix motor;

    // states
    //double speed;
    int OutSize = 3;
    int MidSize = 6;
    int InSize = 3;
    double[][] W1;
    double[][] W2;
    double[] B1;
    double[] B2;

    String fileName = "NetworkData.txt";

    @Override
    public void init(){
        
    }

    @Override
    public void resetState() {
    }

    @Override
    public void update() {
    }

    @Override
    public void inputUpdate(Input source) {
    }

    @Override
    public String getName() {
        return "ArmHelper";
    }

    @Override
    public void selfTest() {
    }
    private void initNetwork(){
        W1 = initWeights(InSize,MidSize);
        B1 = initBias(MidSize);
        W2 = initWeights(MidSize,OutSize);
        B2 = initBias(OutSize);
    }

    private double[][] initWeights(int inSize,int outSize){
        double[][] out = new double[outSize][inSize];
        Random random = new Random();
        int c = 0;
        while(c<outSize){
            int c2 = 0;
            while(c2<inSize){
                out[c][c2] = random.nextDouble();
                c2 += 1;
            }
            c += 1;
        }
        return out;
    }
    private double[] initBias(int outSize){
        double[] out = new double[outSize];
        Random random = new Random();
        int c = 0;
        while(c<outSize){
            out[c] = random.nextDouble();
            c += 1;
        }
        return out;
    }
    private void printIn() {
        File file = new File(fileName);
        boolean check = file.createNewFile();
        if (check) {
            initNetwork();//activates if the file is successful in being created 
        } else {
            //activates if one was already there with that name
        }
        FileReader input = new FileReader(file);
    }
    private void printOut() {
        File file = new File(fileName);
        FileWriter output = new FileWriter(file,false);
        double [][][] tempData = {W1,{B1},W2,{B2}};
        output.write(Arrays.toString(tempData));
    }
    private void parseData(String dataString){
        String[] data = dataString.split(""); 
        int c = 0;
        int part = 0;
        int partOfPart = 0;
        int partOfPartOfPart = 0;
        int partOfPartOfPartOfPart = 0;
        int layer = 0;
        int pos1 = 0;
        int pos2 = 0;
        while(c<data.length){
            if (data[c] == "[") {
                pos2 = c-1;
                layer++;
                if (layer == 2) {
                    partOfPart = -1;
                    part++;
                } else if (layer == 3) {
                    partOfPart++;
                    partOfPartOfPart = -1;
                }
            } else if (data[c] == "]") {
                layer--;
                if (layer == 2) {
                pos1 = pos2;
                pos2 = c;
                partOfPartOfPart++;
                if (part == 1) {
                W1[partOfPart][partOfPartOfPart] = Double.parseDouble(data.substring(pos1 + 2, pos2));
            } else if (part == 2) {
                B1[partOfPartofPart] = Double.parseDouble(data.substring(pos1 + 2, pos2));
            } else if (part == 3) {
                W2[partOfPart][partOfPartOfPart] = Double.parseDouble(data.substring(pos1 + 2, pos2));
            } else {
                B2[partOfPartOfPart] = Double.parseDouble(data.substring(pos1 + 2, pos2));
            }
                }
            }
            if (data[c] == "," && layer == 3) {
                pos1 = pos2;
                pos2 = c;
                partOfPartOfPart++;
                if (part == 1) {
                String[] um = String.join("",Arrays.copyOfRange(data,pos1 + 2, pos2));
                W1[partOfPart][partOfPartOfPart] = Double.parseDouble(um);
            } else if (part == 2) {
                B1[partOfPartOfPart] = Double.parseDouble(data.substring(pos1 + 2, pos2));
            } else if (part == 3) {
                W2[partOfPart][partOfPartOfPart] = Double.parseDouble(data.substring(pos1 + 2, pos2));
            } else {
                B2[partOfPartOfPart] = Double.parseDouble(data.substring(pos1 + 2, pos2));
            }
            }
            c++;
        }
    }
}