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

    /*private int port = 1;
    private int length = 60;

    @Override
    public void update(){

    }

    @Override
    public void inputUpdate(Input source) {
        if(rightShoulder.getValue()){
            cubeDisplay();
        }else if(leftShoulder.getValue()){
            coneDisplay();
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
        led.start();
    }

    public void coneDisplay(){
        for (var i = 0; i < length; i++) {
            ledBuffer.setRGB(i, 250, 0, 250);
        }
         
        led.setData(ledBuffer);
        led.start();
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resetState() {
        led.stop();
        for (var i = 0; i < length; i++) {
            ledBuffer.setRGB(i, 0, 0, 0);
        }
        led.setData(ledBuffer);
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }
    */
    
}
