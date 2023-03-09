package org.wildstang.year2023.subsystems.superstructure;

import org.wildstang.hardware.roborio.outputs.WsSparkMax;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm {

    private WsSparkMax motor;
    private AbsoluteEncoder absEncoder;
    private Timer timer = new Timer();
    private double lastPosition = 0.0;

    public Arm(WsSparkMax outputMotor){
        motor = outputMotor;
        motor.setBrake();
        timer.reset();timer.start();
        absEncoder = motor.getController().getAbsoluteEncoder(Type.kDutyCycle);
        absEncoder.setPositionConversionFactor(360.0);
        absEncoder.setVelocityConversionFactor(360.0/60.0);
        absEncoder.setInverted(SuperConts.ARM_ENCODER_DIRECTION);
        absEncoder.setZeroOffset(141.3);//54
        motor.initClosedLoop(SuperConts.ARM_P, SuperConts.ARM_I, SuperConts.ARM_D, 0, absEncoder, false);
        motor.setCurrentLimit(25, 25, 0);
    }
    public double getPosition(){
        return (absEncoder.getPosition())%360;
    }
    public void setPosition(double position){
        if (position != lastPosition){
            lastPosition = position;
            timer.reset();
        }
        if (atPosition(position) || timer.hasElapsed(1.0)){
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
            return 0.0;//SuperConts.ARM_SLOW*1.0;
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
