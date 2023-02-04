package org.wildstang.year2023.subsystems.lift;

import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



//Please put this in the year2023.subsystems class, not in the framework
public class LiftControler implements Subsystem{
    //things it do
    //two buttons to make it go up and down
    //record encoder positiion


    private DigitalInput up_input, down_input, select;
    private WsSparkMax liftDriver;
    private double direction = 0;
    private boolean holding;

    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        if (source == up_input && up_input.getValue()){
            direction += 10;
        }
        else if (source == down_input && down_input.getValue()){
            direction -= 10;
        }

        
        if (source == select && select.getValue()){
            holding = !holding;
        }
    }

    @Override
    public void init() {
        //DPAD up gives positive, down gives negative, right speeds up, left slows down
        liftDriver = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.LIFT_DRIVER);
        liftDriver.setBrake();
        liftDriver.initClosedLoop(0.1, 0.0, 0.0, 0);
        liftDriver.setCurrentLimit(40, 40, 0);

        up_input = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_UP);
        up_input.addInputListener(this);
        down_input = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_DOWN);
        down_input.addInputListener(this);

        select = (DigitalInput) WSInputs.MANIPULATOR_SELECT.get();
        select.addInputListener(this);

        holding = false;
    }

    @Override
    public void selfTest() {
        
    }

    @Override
    public void update() {
        
        if (direction > 76) direction = 76;
        if (direction < 0) direction = 0;
        if (holding) {
            liftDriver.setPosition(direction);
        } else {
            liftDriver.setSpeed(0.0);
        }
        SmartDashboard.putNumber("Lift encoder", liftDriver.getPosition());
        SmartDashboard.putBoolean("isHolding", holding);
        SmartDashboard.putNumber("Lift target", direction);
    }

    @Override
    public void resetState() {
        direction = 0;
        holding = false;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Lift";
    }    
}
