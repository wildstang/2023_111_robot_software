package org.wildstang.year2023.subsystems.superstructure;

import org.wildstang.hardware.roborio.outputs.WsSparkMax;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;

import edu.wpi.first.wpilibj.Timer;
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
        absEncoder.setZeroOffset(89.5);//54
        motor.initClosedLoop(SuperConts.ARM_P, SuperConts.ARM_I, SuperConts.ARM_D, 0, absEncoder, false);
        motor.setCurrentLimit(30, 30, 0);
    }
    public double getPosition(){
        return (absEncoder.getPosition())%360;
    }
    public void setPosition(double position){
        if (atPosition(position)){
            motor.setPosition((position)%360);
        } else {
           motor.setSpeed(getSpeed(position%360));
        }
    }
    public double getSpeed(double target){
        if (getPosition() > target && getPosition() <= 180.0){
            return Math.signum(target - getPosition()) * SuperConts.ARM_SLOW;
        } else if (getPosition() > target && getPosition() >= 180.0){
            return Math.signum(target - getPosition()) * SuperConts.ARM_SLOW;
        } else if (getPosition() < target && getPosition() >= 180.0){
            return Math.signum(target - getPosition()) * SuperConts.ARM_SLOW*0.5;
        } else if (getPosition() < target && getPosition() >= 150.0){
            return Math.signum(target - getPosition()) * SuperConts.ARM_SLOW * 0.8;
        } else {
            return SuperConts.ARM_SLOW*1.0;
        }
    }
    public boolean atPosition(double position){
        return Math.abs(getPosition() - position) < SuperConts.ARM_THRESHOLD;
    }    
    public boolean pastLift(){
        return getPosition() > 200;
    }
    public boolean notScooping(){
        return getPosition() > SuperConts.ANTISCOOP;
    }
}
