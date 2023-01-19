package org.wildstang.year2023.subsystems.arm;
import org.wildstang.year2023.subsystems.arm.Network;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.outputs.WsPhoenix;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;
import java.util.Random;
import org.javatuples.Quartet;
import java.io.File;
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
    int InSize = 3;
    int MidSize = 6;
    int OutSize = 3;
    double[][] W1;
    double[][] W2;
    double[] B1;
    double[] B2;

    String file = "NetworkData.txt"

    @Override
    public void init() {

    }

    @Override
    public void resetState() {
        speed = 0;
    }

    @Override
    public void update() {
        motor.setValue(speed);
    }

    @Override
    public void inputUpdate(Input source) {
        if (source == joystick) {
            speed = joystick.getValue();
        }
    }

    @Override
    public String getName() {
        return "Sample";
    }

    @Override
    public void selfTest() {
    }
    private void initNetwork(){
        W1 = initWeights(InSize,MidSize);
        B1 = initWeights(MidSize);
        W2 = initWeights(MidSize,OutSize);
        B2 = initWeights(OutSize);
    }
    private void saveToFile(){
        File myObj = new File(file);
        Quad data = new Quartet<double[][],double[],double[][],double[]>(W1,B1,W2,B2);

    }
    private double[][] initWeights(inSize,outSize){
        double[outSize][inSize] out;
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
    private double[] initBias(outSize){
        double[outSize] out;
        Random random = new Random();
        int c = 0;
        while(c<outSize){
            out[c] = random.nextDouble();
            c += 1;
        }
        return out;
    }
}