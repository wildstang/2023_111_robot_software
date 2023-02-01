package org.wildstang.year2023.subsystems.lift;

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

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardComponent;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.sensors.CANCoder;


//Please put this in the year2023.subsystems class, not in the framework
public class LiftControler implements Subsystem{
    //things it do
    //two buttons to make it go up and down
    //record encoder positiion


    private DigitalInput up_input, down_input, SpeedUp, SpeedDown;
    private WsSparkMax liftDriver, liftFollower;//will only need the driver if WsOutputs is set correctly
    private int direction = 0;
    private boolean toggle;
    private double liftSpeed = 5.0; //lets start this at 0.25
    private ShuffleboardTab armTab;
    private GenericEntry ArmPosEntry;

    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        if (source == up_input && up_input.getValue()){
            direction = 1;
        }
        else if (source == down_input && down_input.getValue()){
            direction = -1;
        }else{
            //nether is on stop the lift
            direction = 0;
        }

        if (source == SpeedUp && SpeedUp.getValue() && !toggle){
            liftSpeed += 0.05;
            toggle = true;
        }else if (source == SpeedDown && SpeedUp.getValue() && !toggle){
            liftSpeed -= 0.05;
            toggle = true;
        }else {
            toggle = false;
        }
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

        SpeedUp = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_DPAD_LEFT);
        SpeedUp.addInputListener(this);
        up_input = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_DPAD_RIGHT);
        up_input.addInputListener(this);
        toggle = false;

        armTab = Shuffleboard.getTab("Arm");
        ArmPosEntry = armTab.add("Arm Position",0.0).getEntry();
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
        }
        ArmPosEntry.setDouble(getPosition());
        
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
