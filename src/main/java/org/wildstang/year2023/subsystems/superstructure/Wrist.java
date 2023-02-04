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
        motor.initClosedLoop(SuperConts.WRIST_P, SuperConts.WRIST_I, SuperConts.WRIST_D, 0, absEncoder);
        motor.setCurrentLimit(40, 40, 0);
        
    }
    public double getPosition(){
        return absEncoder.getPosition();
    }
    public void setPosition(double position){
        motor.setPosition(position);
    }
    
}
