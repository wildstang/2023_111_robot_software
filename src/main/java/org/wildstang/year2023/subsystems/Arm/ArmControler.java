package org.wildstang.year2023.subsystems.Arm;

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



public class ArmControler implements Subsystem{

    private DigitalInput Rotate_Positive, Rotate_Negative,SpeedUp,SpeedDown;
    private WsSparkMax BaseMotor;
    private AbsoluteEncoder absEncoder;
    

    private int TurnDirection;
    private int slowDirection;
    private double BaseSpeed = 1.0;
    private double slowSpeed = 0.25;



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

        if (SpeedUp.getValue()){
            slowDirection = 1;
        } else if (SpeedDown.getValue()){
            slowDirection = -1;
        } else {
            slowDirection = 0;
        }
        
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

        //***********exact imputs are tempeary*********
        //Y gives positive, A gives negative, B speed up, X slow down
        Rotate_Positive = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_UP);
        Rotate_Positive.addInputListener(this);
        Rotate_Negative = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_DOWN);
        Rotate_Negative.addInputListener(this);
        SpeedUp = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_RIGHT);
        SpeedUp.addInputListener(this);
        SpeedDown = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_LEFT);
        SpeedDown.addInputListener(this);

        BaseMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ARM_ONE);
        this.absEncoder = BaseMotor.getController().getAbsoluteEncoder(Type.kDutyCycle);
        this.absEncoder.setPositionConversionFactor(360.0);
        this.absEncoder.setVelocityConversionFactor(360.0/60.0);
        BaseMotor.getController().getPIDController().setFeedbackDevice(absEncoder);
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
        //     //BaseMotor.setSpeed(BaseSpeed*TurnDirection);
        //     if (TurnDirection<0 && getPosition() <= 180.0){
        //         BaseMotor.setSpeed(-0.1);
        //     } else if (TurnDirection>0 && getPosition() >= 180.0){
        //         BaseMotor.setSpeed(0.1);
        //     } else if (TurnDirection<0 && getPosition() >= 180.0){
        //         BaseMotor.setSpeed(-0.1 - 0.2 * (-Math.toDegrees(Math.sin(getPosition()))));
        //     } else {
        //         BaseMotor.setSpeed(0.1 + 0.2 * (Math.toDegrees(Math.sin(getPosition()))));
        //     }
        // }else if (slowDirection != 0){
        //     BaseMotor.setSpeed(slowSpeed*slowDirection);
        // } else {
        //     BaseMotor.stop();
        // }
        SmartDashboard.putNumber("Arm Position*", getPosition());
        SmartDashboard.putNumber("Arm Speed", BaseSpeed);
    }
    public double getPosition(){
        return (absEncoder.getPosition()+54.0)%360;
    }

    @Override
    public void resetState() {
        TurnDirection = 0;
        slowDirection = 0;
    }

    @Override
    public String getName() {
        return "Arm";
    }
    
}
