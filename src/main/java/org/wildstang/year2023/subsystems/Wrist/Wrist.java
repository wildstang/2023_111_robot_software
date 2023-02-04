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

    private DigitalInput Rotate_Positive, Rotate_Negative, start, armUp, armDown;
    private WsSparkMax WristMotor, BaseMotor;
    private AbsoluteEncoder WristAbsEncoder, absEncoder;
    
    private double target;
    private boolean isPID;



    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        if (armUp.getValue()){
            target +=25;
        }
        else if (armDown.getValue()){
            target -=25;
        }
        if (source == start && start.getValue()){
            isPID = !isPID;
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
        start = (DigitalInput) WSInputs.MANIPULATOR_START.get();
        start.addInputListener(this);
        armUp = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_UP);
        armUp.addInputListener(this);
        armDown = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_DOWN);
        armDown.addInputListener(this);

        WristMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.WRIST);
        this.WristAbsEncoder = WristMotor.getController().getAbsoluteEncoder(Type.kDutyCycle);
        this.WristAbsEncoder.setPositionConversionFactor(360.0);
        this.WristAbsEncoder.setVelocityConversionFactor(360.0/60.0);
        WristMotor.getController().getPIDController().setFeedbackDevice(WristAbsEncoder);
        WristMotor.initClosedLoop(0.10, 0.0, 0.1, 0);
        WristMotor.setBrake();
        WristMotor.setCurrentLimit(10,10,0);

        BaseMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ARM_ONE);
        this.absEncoder = BaseMotor.getController().getAbsoluteEncoder(Type.kDutyCycle);
        this.absEncoder.setPositionConversionFactor(360.0);
        this.absEncoder.setVelocityConversionFactor(360.0/60.0);
        BaseMotor.getController().getPIDController().setFeedbackDevice(absEncoder);
        BaseMotor.initClosedLoop(0.015, 0, 0.0, 0);
        BaseMotor.setBrake();

        BaseMotor.setCurrentLimit(40,40,0);
    }

    @Override
    public void selfTest() {        
    }

    @Override
    public void update() {
        //check to see if movement
        // if (TurnDirection != 0){
        //     WristMotor.setSpeed(BaseSpeed*TurnDirection);
        // }else{
        //     WristMotor.stop();
        // }
        // if (isPID){
        //     WristMotor.setPosition(setPosition(target));
        //     WristMotor.setBrake();
        // } else {
        //     WristMotor.stop();
        //     target = getPosition();
        //     WristMotor.setCoast();
        // }
        if (isPID){
            BaseMotor.setPosition(setArmPosition(target));
        } else {
            BaseMotor.stop();
            target = getArmPosition();
        }
        WristMotor.setPosition(setPosition((360.0-getArmPosition())%360));
        // SmartDashboard.putNumber("Wrist Field Position", getPosition());
        // SmartDashboard.putNumber("Wrist raw encoder", WristAbsEncoder.getPosition());
        // SmartDashboard.putNumber("Wrist Field target", target);
        // SmartDashboard.putNumber("Wrist raw target", setPosition(target));
        SmartDashboard.putBoolean("Wrist holding", isPID);
        SmartDashboard.putNumber("Wrist new position", (360.0-getArmPosition())%360);
        SmartDashboard.putNumber("Arm field position", getArmPosition());
        SmartDashboard.putNumber("Arm raw encoder", absEncoder.getPosition());
        SmartDashboard.putNumber("Arm field target", target);
        SmartDashboard.putNumber("Arm raw target", setArmPosition(target));
    }

    @Override
    public void resetState() {
        isPID = false;
        target = getArmPosition();
        
    }
    public double getPosition(){
        return (WristAbsEncoder.getPosition()+310.0)%360;
    }
    private double setPosition(double target){
        return (target+50.0)%360;
    }
    public double getArmPosition(){
        return (absEncoder.getPosition()+54.0)%360;
    }
    private double setArmPosition(double target){
        return (target + 306)%360;
    }

    @Override
    public String getName() {
        return "Wrist";
    }
    
}
