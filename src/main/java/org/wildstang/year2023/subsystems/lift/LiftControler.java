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


    private DigitalInput up_input, down_input, SpeedUp, SpeedDown, start;
    private WsSparkMax liftDriver;
    private int direction = 0;
    private double liftSpeed = 0.25;

    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        if (up_input.getValue()){
            direction = 1;
        }
        else if (down_input.getValue()){
            direction = -1;
        }else{
            //nether is on stop the lift
            direction = 0;
        }

        if (source == SpeedUp && SpeedUp.getValue()){
            liftSpeed += 0.05;
        }
        if (source == SpeedDown && SpeedUp.getValue()){
            liftSpeed -= 0.05;
        }
        if (source == start && start.getValue()){
            liftDriver.resetEncoder();
        }
    }

    @Override
    public void init() {
        //DPAD up gives positive, down gives negative, right speeds up, left slows down
        liftDriver = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.LIFT_DRIVER);
        liftDriver.setCurrentLimit(40, 40, 0);

        up_input = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_UP);
        up_input.addInputListener(this);
        down_input = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_DOWN);
        down_input.addInputListener(this);

        SpeedUp = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_RIGHT);
        SpeedUp.addInputListener(this);
        SpeedDown = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_LEFT);
        SpeedDown.addInputListener(this);

        start = (DigitalInput) WSInputs.MANIPULATOR_START.get();
        start.addInputListener(this);
    }

    @Override
    public void selfTest() {
        
    }

    @Override
    public void update() {
        //get the position
        //up and down
        if (direction != 0){
            liftDriver.setSpeed(direction*liftSpeed);
        }else{
            liftDriver.stop();
        }
        SmartDashboard.putNumber("Lift encoder", liftDriver.getPosition());
        SmartDashboard.putNumber("Lift speed", liftSpeed);
        
    }

    @Override
    public void resetState() {
        direction = 0;
        liftSpeed = 0.25;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Lift";
    }    
}
