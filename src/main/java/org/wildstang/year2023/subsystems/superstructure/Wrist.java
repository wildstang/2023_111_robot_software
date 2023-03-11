package org.wildstang.year2023.subsystems.superstructure;

import org.wildstang.hardware.roborio.outputs.WsSparkMax;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;

public class Wrist {

    private WsSparkMax motor;
    private AbsoluteEncoder absEncoder;

    public Wrist(WsSparkMax outputMotor){
        motor = outputMotor;
        motor.setBrake();
        absEncoder = motor.getController().getAbsoluteEncoder(Type.kDutyCycle);
        absEncoder.setPositionConversionFactor(360.0);
        absEncoder.setVelocityConversionFactor(360.0/60.0);
        absEncoder.setInverted(SuperConts.WRIST_ENCODER_DIRECTION);
        absEncoder.setZeroOffset(95.1);//310
        motor.initClosedLoop(SuperConts.WRIST_P, SuperConts.WRIST_I, SuperConts.WRIST_D, 0, absEncoder, false);
        motor.setCurrentLimit(15, 15, 0);
        
    }
    public double getPosition(){
        return (absEncoder.getPosition())%360;
    }
    public void setPosition(double position){
        motor.setPosition((position)%360);
    }
    // public void setFollow(double position, double speed){
    //     motor.setSpeed(-speed + SuperConts.WRIST_P * (position - getPosition()));
    // }
    
}
