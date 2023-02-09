package org.wildstang.year2023.subsystems.superstructure;

import org.wildstang.hardware.roborio.outputs.WsSparkMax;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm {

    private WsSparkMax motor;
    private AbsoluteEncoder absEncoder;

    public Arm(WsSparkMax outputMotor){
        motor = outputMotor;
        motor.setBrake();
        absEncoder = motor.getController().getAbsoluteEncoder(Type.kDutyCycle);
        absEncoder.setPositionConversionFactor(360.0);
        absEncoder.setVelocityConversionFactor(360.0/60.0);
        absEncoder.setInverted(SuperConts.ARM_ENCODER_DIRECTION);
        absEncoder.setZeroOffset(29.3);//54
        motor.initClosedLoop(SuperConts.ARM_P, SuperConts.ARM_I, SuperConts.ARM_D, 0, absEncoder, false);
        motor.setCurrentLimit(30, 30, 0);
    }
    public double getPosition(){
        return (absEncoder.getPosition())%360;
    }
    public double getRawPosition(){
        return absEncoder.getPosition();
    }
    public void setPosition(double position){
        //if (atPosition(position)){
            motor.setPosition((position)%360);
        // } else {
        //    motor.setSpeed(getSpeed(position));
        // }
    }
    public double getSpeed(double target){
        if (getPosition() > target && getPosition() <= 180.0){
            return -SuperConts.ARM_SLOW;
        } else if (getPosition() < target && getPosition() >= 180.0){
            return SuperConts.ARM_SLOW;
        } else if (getPosition() > target && getPosition() >= 180.0){
            return -SuperConts.ARM_SLOW - SuperConts.ARM_FAST * (-Math.toDegrees(Math.sin(getPosition())));
        } else {
            return SuperConts.ARM_SLOW + SuperConts.ARM_FAST * (Math.toDegrees(Math.sin(getPosition())));
        }
    }
    public boolean atPosition(double position){
        return Math.abs(getPosition() - position) < SuperConts.ARM_THRESHOLD;
    }    
    public boolean pastLift(){
        return getPosition() > 200;
    }
}
