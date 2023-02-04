package org.wildstang.year2023.subsystems.superstructure;

import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Superstructure implements Subsystem{
    
    private enum modes {SIMPLE, ARMDELAY, LIFTDELAY, WRIST, WAITING}

    private DigitalInput leftBumper, rightBumper, A, X, Y, B, dUp, dDown, dLeft, dRight, select;
    private modes motion;
    private SuperPos currentPos, lastPos;
    public Arm arm;
    public Lift lift;
    public Wrist wrist;

    private boolean gamepiece, wristWait, armWait, liftWait;


    @Override
    public void inputUpdate(Input source) {
        if (source == leftBumper && leftBumper.getValue()){
            gamepiece = SuperConts.CONE;
            if (!wristWait) wrist.setPosition(currentPos.getW(gamepiece));
            if (!armWait) arm.setPosition(currentPos.getA(gamepiece));
            if (!liftWait) lift.setPosition(currentPos.getL(gamepiece));
        }
        if (source == rightBumper && rightBumper.getValue()){
            gamepiece = SuperConts.CUBE;
            if (!wristWait) wrist.setPosition(currentPos.getW(gamepiece));
            if (!armWait) arm.setPosition(currentPos.getA(gamepiece));
            if (!liftWait) lift.setPosition(currentPos.getL(gamepiece));
        }
        if (source == A && A.getValue()){
            currentPos = SuperPos.SCORE_LOW;
        }if (source == B && B.getValue()){
            currentPos = SuperPos.SCORE_MID;
        }if (source == X && X.getValue()){
            currentPos = SuperPos.NEUTRAL;
        }if (source == Y && Y.getValue()){
            currentPos = SuperPos.SCORE_HIGH;
        }if (source == dUp && dUp.getValue()){
            currentPos = SuperPos.HP_STATION_BACK;
        }if (source == dRight && dRight.getValue()){
            currentPos = SuperPos.INTAKE_BACK;
        }if (source == dLeft && dLeft.getValue()){
            currentPos = SuperPos.INTAKE_FRONT;
        }if (source ==  dDown&& dDown.getValue()){
            currentPos = SuperPos.INTAKE_FRONT_LOW;
        }
        
        if (currentPos != lastPos){
            if (lastPos.getDirection() == currentPos.getDirection()){
                motion = modes.SIMPLE;
            } else if (currentPos.getL(gamepiece) > SuperConts.LIFTSTAGE){
                motion = modes.LIFTDELAY;
            } else if (lastPos.getL(gamepiece) > SuperConts.LIFTSTAGE){
                motion = modes.ARMDELAY;
            } else {
                motion = modes.WRIST;
            }
        }
        lastPos = currentPos;
    }

    @Override
    public void init() {
        leftBumper = (DigitalInput) WSInputs.MANIPULATOR_LEFT_SHOULDER.get();
        leftBumper.addInputListener(this);
        rightBumper = (DigitalInput) WSInputs.MANIPULATOR_RIGHT_SHOULDER.get();
        rightBumper.addInputListener(this);
        A = (DigitalInput) WSInputs.MANIPULATOR_FACE_DOWN.get();
        A.addInputListener(this);
        B = (DigitalInput) WSInputs.MANIPULATOR_FACE_RIGHT.get();
        B.addInputListener(this);
        X = (DigitalInput) WSInputs.MANIPULATOR_FACE_LEFT.get();
        X.addInputListener(this);
        Y = (DigitalInput) WSInputs.MANIPULATOR_FACE_UP.get();
        Y.addInputListener(this);
        dDown = (DigitalInput) WSInputs.MANIPULATOR_DPAD_DOWN.get();
        dDown.addInputListener(this);
        dLeft = (DigitalInput) WSInputs.MANIPULATOR_DPAD_LEFT.get();
        dLeft.addInputListener(this);
        dRight = (DigitalInput) WSInputs.MANIPULATOR_DPAD_RIGHT.get();
        dRight.addInputListener(this);
        dUp = (DigitalInput) WSInputs.MANIPULATOR_DPAD_UP.get();
        dUp.addInputListener(this);
        select = (DigitalInput) WSInputs.MANIPULATOR_SELECT.get();
        select.addInputListener(this);

        arm = new Arm((WsSparkMax) WSOutputs.ARM_ONE.get());
        lift = new Lift((WsSparkMax) WSOutputs.LIFT_DRIVER.get());
        wrist = new Wrist((WsSparkMax) WSOutputs.WRIST.get());
    }

    @Override
    public void update() {
        if (motion == modes.SIMPLE){
            lift.setPosition(currentPos.getL(gamepiece));
            arm.setPosition(currentPos.getA(gamepiece));
            wrist.setPosition(currentPos.getW(gamepiece));
            motion = modes.WAITING;
        }
        if (motion == modes.ARMDELAY){
            lift.setPosition(currentPos.getL(gamepiece));
            armWait = true;
            wristWait = true;
            motion = modes.WAITING;
        }
        if (motion == modes.LIFTDELAY){
            arm.setPosition(currentPos.getA(gamepiece));
            wristWait = true;
            liftWait = true;
            motion = modes.WAITING;
        }
        if (motion == modes.WRIST){
            lift.setPosition(currentPos.getL(gamepiece));
            arm.setPosition(currentPos.getA(gamepiece));
            wristWait = true;
            motion = modes.WAITING;
        }

        if (liftWait){
            if (arm.atPosition(currentPos.getA(gamepiece))){
                lift.setPosition(currentPos.getL(gamepiece));
                liftWait = false;
            } else {
                lift.setPosition(SuperConts.LIFTSTAGE);
            }
        }
        if (armWait){
            if (lift.getPosition() < SuperConts.LIFTSTAGE){
                arm.setPosition(currentPos.getA(gamepiece));
                armWait = false;
            }
        }
        if (wristWait){
            if (arm.atPosition(currentPos.getA(gamepiece)) && !armWait){
                wrist.setPosition(currentPos.getW(gamepiece));
                wristWait = false;
            } else {
                wrist.setPosition((360.0-arm.getPosition())%360);
            }
        }
        SmartDashboard.putString("Current Mode", currentPos.toString());
        SmartDashboard.putNumber("Arm Field Target", currentPos.getA(gamepiece));
        SmartDashboard.putNumber("Lift Target", currentPos.getL(gamepiece));
        SmartDashboard.putNumber("Wrist Field Target", currentPos.getW(gamepiece));
        SmartDashboard.putNumber("Arm Field Position", arm.getPosition());
        SmartDashboard.putNumber("Arm raw pos", arm.getRawPosition());
        SmartDashboard.putNumber("Lift Encoder", lift.getPosition());
        SmartDashboard.putNumber("Wrist Field Position", wrist.getPosition());
        SmartDashboard.putNumber("Wrist raw pos", wrist.getRawPosition());
        SmartDashboard.putBoolean("Cone or Cube", gamepiece);
    }

    @Override
    public void resetState() {
        gamepiece = SuperConts.CONE;
        wristWait = false;
        armWait = false;
        liftWait = false;
        motion = modes.WAITING;
        currentPos = SuperPos.NEUTRAL;
        lastPos = SuperPos.NEUTRAL;
    }

    @Override
    public void selfTest() {
    }

    @Override
    public String getName() {
        return "Superstructure";
    }   
}