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
    private DigitalInput leftShoulder, start, select;
    private AddressableLED led;
    private AddressableLEDBuffer ledBuffer;

    private int port = 1;//placeholder port
    private int length = 60;//placeholder length
    private int initialHue = 0;
    private boolean isRainbow;

    @Override
    public void update(){
        if (isRainbow){
            rainbow();
            led.setData(ledBuffer);
        }

    }

    @Override
    public void inputUpdate(Input source) {
        if (rightShoulder.getValue()) cubeDisplay();
        if (leftShoulder.getValue()) coneDisplay();
    }

    @Override
    public void init() {
        //Inputs
        rightShoulder = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_SHOULDER);
        rightShoulder.addInputListener(this);
        leftShoulder = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_SHOULDER);
        leftShoulder.addInputListener(this);
        start = (DigitalInput) WSInputs.MANIPULATOR_START.get();
        start.addInputListener(this);
        select = (DigitalInput) WSInputs.MANIPULATOR_SELECT.get();
        select.addInputListener(this);

        //Outputs
        led = new AddressableLED(port);
        ledBuffer = new AddressableLEDBuffer(length);

        led.setLength(ledBuffer.getLength());

        led.setData(ledBuffer);
    }

    public void cubeDisplay(){
        for (var i = 0; i < length; i++) {
            ledBuffer.setRGB(i, 255, 0, 255);
        }
        led.setData(ledBuffer);
        led.start();
    }

    public void coneDisplay(){
        for (var i = 0; i < length; i++) {
            ledBuffer.setRGB(i, 255, 255, 0);
        }
         
        led.setData(ledBuffer);
        led.start();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void resetState() {
        initialHue = 0;
        rainbow();
        isRainbow = true;
    }

    @Override
    public String getName() {
        return "Led Controller";
    }
    private void rainbow(){
        for (int i = 0; i < ledBuffer.getLength(); i++){
            ledBuffer.setHSV(i, (initialHue + (i*180/ledBuffer.getLength()))%180, 255, 128);
        }
        initialHue = (initialHue + 3) % 180;
        led.setData(ledBuffer);
        led.start();
    }
    
}
