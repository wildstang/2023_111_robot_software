package org.wildstang.year2023.subsystems.superstructure;

import org.wildstang.hardware.roborio.outputs.WsSparkMax;

public class Lift {

    private WsSparkMax motor;

    public Lift(WsSparkMax outputMotor){
        motor = outputMotor;
        motor.setBrake();
        motor.initClosedLoop(SuperConts.LIFT_P, SuperConts.LIFT_I, SuperConts.LIFT_D, 0);
        motor.setCurrentLimit(40, 40, 0);
    }

    public double getPosition(){
        return motor.getPosition();
    }

    public void setPosition(double newPosition){
        if (newPosition < 0) motor.setPosition(0);
        else motor.setPosition(newPosition);
    }
    
}
