package org.wildstang.framework.subsystems.lift;

import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsRemoteAnalogInput;
import org.wildstang.hardware.roborio.outputs.WsRemoteAnalogOutput;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.year2023.robot.CANConstants;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;

import org.wildstang.year2023.subsystems.swerve.DriveConstants;

import com.ctre.phoenix.sensors.CANCoder;


//Please put this in the year2023.subsystems class, not in the framework
public class LiftControler implements Subsystem{
    //things it do
    //two buttons to make it go up and down
    //record encoder positiion


    private DigitalInput up_input, down_input;
    private WsSparkMax liftDriver, liftFollower;//will only need the driver if WsOutputs is set correctly
    private int direction = 0;
    private double liftSpeed = 5.0; //lets start this at 0.25

    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        if (source == up_input && up_input.getValue()){
            direction = 1;
        }
        //up or down only not both
        else if (source == down_input && down_input.getValue()){
            direction = -1;
        }else if (!down_input.getValue() && !up_input.getValue()){
            //nether is on stop the lift
            direction = 0;
        }//make this an else instead of else if. We want it to not move for all other cases
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        liftDriver = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.LIFT_DRIVER);
        liftFollower = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.LIFT_Follower);
        liftFollower.getController().follow(liftDriver.getController(),true);
        //lets call setCurrentLimit on the lift Driver, and again won't need the lift follower

        up_input = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_DPAD_UP);
        up_input.addInputListener(this);
        down_input = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_DPAD_DOWN);
        down_input.addInputListener(this);
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update() {
        //get the position
        //up and down
        if (direction != 0){
            liftDriver.setSpeed(direction*liftSpeed);
        }else{
            liftDriver.stop();
        }//Like the prototype arm, it's be nice to be able to modify liftSpeed with some buttons 
        //instead of needing to push code every time

        //also, let's put getPosition to shuffleboard
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub
        //I'd put something here
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Lift Controler";
    }

    //custom
    public double getPosition(){
        return liftDriver.getPosition();
    }
    
}
