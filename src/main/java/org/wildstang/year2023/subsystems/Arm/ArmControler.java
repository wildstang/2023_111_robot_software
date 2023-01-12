package org.wildstang.year2023.subsystems.Arm;

// ton of imports
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

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.wildstang.year2023.subsystems.swerve.DriveConstants;
import org.wildstang.year2023.subsystems.swerve.SwerveModule;
import org.wildstang.year2023.subsystems.swerve.WSSwerveHelper;

import com.ctre.phoenix.sensors.CANCoder;



public class ArmControler implements Subsystem{
    //Things it does rn
    //rotate base clockwise and counter clockwise
    //track positition

    private DigitalInput Rotate_Clockwise, Rotate_Counter_Clockwise;
    private WsSparkMax BaseMotor;
    

    private int TurnDirection;
    private double BaseSpeed = 5.5;
    private double EncodedPositition;



    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        if (source == Rotate_Clockwise && Rotate_Clockwise.getValue()){
            TurnDirection = 1;
        }
        else if (source == Rotate_Counter_Clockwise && Rotate_Counter_Clockwise.getValue()){
            TurnDirection = -1;
        }
        else if (!(Rotate_Counter_Clockwise.getValue() && source == Rotate_Clockwise && Rotate_Clockwise.getValue())){
            TurnDirection = 0;
        }
        
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

        //***********exact imputs are tempeary*********
        Rotate_Clockwise = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_DPAD_RIGHT);
        Rotate_Clockwise.addInputListener(this);
        Rotate_Counter_Clockwise = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_DPAD_LEFT);
        Rotate_Counter_Clockwise.addInputListener(this);
        BaseMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ARM_ONE);
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

        //check to see if movement
        if (TurnDirection != 0){
            BaseMotor.setSpeed(BaseSpeed*TurnDirection);
        }else{
            BaseMotor.setSpeed(0.0);
        }

        //update encoded value
        EncodedPositition = BaseMotor.getPosition();
        
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub
        TurnDirection = 0;
        EncodedPositition = 0;
        BaseMotor.resetEncoder();
        
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Arm";
    }
    
}
