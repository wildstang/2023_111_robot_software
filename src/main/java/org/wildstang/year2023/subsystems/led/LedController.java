package org.wildstang.year2023.subsystems.led;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsRemoteAnalogOutput;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class LedController implements Subsystem {

    private DigitalInput rightShoulder;
    private DigitalInput leftShoulder;
    private AddressableLED led;
    private AddressableLEDBuffer ledBuffer;

    private int port = 1;//placeholder port
    private int length = 60;//placeholder length

    @Override
    public void update(){

    }

    @Override
    public void inputUpdate(Input source) {
        if(rightShoulder.getValue() || leftShoulder.getValue()){
            if(rightShoulder.getValue()){
                if(leftShoulder.getValue() == false){
                    cubeDisplay();
                }else{
                    resetState();
                }
            }
        
            if(leftShoulder.getValue()){
                if(rightShoulder.getValue() == false){
                    coneDisplay();
                }else{
                    resetState();
                }
            }
            resetState();
        }
    }

    @Override
    public void init() {
        //Inputs
        rightShoulder = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_SHOULDER);
        rightShoulder.addInputListener(this);
        leftShoulder = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_SHOULDER);
        leftShoulder.addInputListener(this);

        //Outputs
        led = new AddressableLED(port);
        ledBuffer = new AddressableLEDBuffer(length);

        led.setLength(ledBuffer.getLength());

        led.setData(ledBuffer);
    }

    public void cubeDisplay(){
        for (var i = 0; i < length; i++) {
            ledBuffer.setRGB(i, 215, 0, 250);
        }
         
        led.setData(ledBuffer);
        led.setSyncTime(2000000);
        led.start();
    }

    public void coneDisplay(){
        for (var i = 0; i < length; i++) {
            ledBuffer.setRGB(i, 250, 0, 250);
        }
         
        led.setData(ledBuffer);
        led.setSyncTime(2000000);
        led.start();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void resetState() {
        led.stop();
        for (var i = 0; i < length; i++) {
            ledBuffer.setRGB(i, 0, 0, 0);
        }
        led.setData(ledBuffer);
        led.setSyncTime(0);
    }

    @Override
    public String getName() {
        return "Led Controller";
    }
    
    
}
