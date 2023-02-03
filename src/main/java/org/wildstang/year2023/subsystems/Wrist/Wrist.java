package org.wildstang.year2023.subsystems.Wrist;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class Wrist implements Subsystem{
    //Things it does rn
    //rotate base clockwise and counter clockwise
    //track positition

    private DigitalInput Rotate_Positive, Rotate_Negative;
    private WsSparkMax BaseMotor;
    private AbsoluteEncoder absEncoder;
    

    private int TurnDirection;
    private double BaseSpeed = 0.5;



    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        if (Rotate_Positive.getValue()){
            TurnDirection = 1;
        }
        else if (Rotate_Negative.getValue()){
            TurnDirection = -1;
        }
        else{
            TurnDirection = 0;
        }
        
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

        //***********exact imputs are tempeary*********
        //A gives positive, Y gives negative, B speed up, X slow down
        Rotate_Positive = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_SHOULDER);
        Rotate_Positive.addInputListener(this);
        Rotate_Negative = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_SHOULDER);
        Rotate_Negative.addInputListener(this);

        BaseMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.WRIST);
        this.absEncoder = BaseMotor.getController().getAbsoluteEncoder(Type.kDutyCycle);
        this.absEncoder.setPositionConversionFactor(360.0);
        this.absEncoder.setVelocityConversionFactor(360.0/60.0);
        BaseMotor.getController().getPIDController().setFeedbackDevice(absEncoder);
        
        BaseMotor.setBrake();
        BaseMotor.setCurrentLimit(15,15,0);
    }

    @Override
    public void selfTest() {        
    }

    @Override
    public void update() {
        //check to see if movement
        if (TurnDirection != 0){
            BaseMotor.setSpeed(BaseSpeed*TurnDirection);
        }else{
            BaseMotor.stop();
        }
        SmartDashboard.putNumber("Wrist Position", (BaseMotor.getPosition()+360.0)%360);
        SmartDashboard.putNumber("Wrist Speed", BaseSpeed);
    }

    @Override
    public void resetState() {
        TurnDirection = 0;
        
    }

    @Override
    public String getName() {
        return "Wrist";
    }
    
}
