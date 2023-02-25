package org.wildstang.year2023.subsystems.superstructure;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Superstructure implements Subsystem{
    
    private enum modes {SIMPLE, ARMDELAY, LIFTDELAY, WRIST, WAITING}
    private enum score {HIGH, MID, LOW}
    private String[] scoreString = new String[]{"High", "Mid", "Low"};
    private enum intake {UPRIGHT, TIPPED, FRONT}
    private String[] intakeString = new String[]{"Upright", "Tipped", "Front"};
    private enum station {DOUBLE, SINGLE}
    private String[] stationString = new String[]{"Double", "Single"};

    private DigitalInput leftBumper, rightBumper, A, X, Y, B, dUp, dDown, dLeft, dRight, select, start, driverLB, driverRB;
    private AnalogInput driverLT;
    private modes motion;
    private score scoring;
    private intake intaking;
    private station stationing;
    private SuperPos currentPos, lastPos;
    public Arm arm;
    public Lift lift;
    public Wrist wrist;
    private Timer timer = new Timer();
    private double liftMod;
    private SwerveDrive swerve;

    private boolean gamepiece, wristWait, armWait, liftWait, swerveWait;


    @Override
    public void inputUpdate(Input source) {
        if (source == leftBumper && leftBumper.getValue()){
            gamepiece = SuperConts.CONE;
        }
        if (source == rightBumper && rightBumper.getValue()){
            gamepiece = SuperConts.CUBE;
        }
        if (start.getValue() && source == dLeft && dLeft.getValue()) liftMod--;
        if (start.getValue() && source == dRight && dRight.getValue()) liftMod++;
        if (start.getValue() && select.getValue() && (source == start || source == select)) {
            if (currentPos != SuperPos.STOWED) currentPos = SuperPos.STOWED;
            else currentPos = SuperPos.NEUTRAL;
        }
        if (source == A && A.getValue()) scoring = score.LOW;
        if (source == B && B.getValue()) scoring = score.MID;
        if (source == Y && Y.getValue()) scoring = score.HIGH;
        if (source == dUp && dUp.getValue()) intaking = intake.UPRIGHT;
        if (source == dDown && dDown.getValue()) intaking = intake.TIPPED;
        if (source == X && X.getValue()) intaking = intake.FRONT;
        if (source == dRight && dRight.getValue()) stationing = station.DOUBLE;
        //if (source == dLeft && dLeft.getValue()) stationing = station.SINGLE;

        if (timer.hasElapsed(0.25) && currentPos != SuperPos.STOWED){
            if (Math.abs(driverLT.getValue()) > 0.25){
                if (scoring == score.HIGH) currentPos = SuperPos.SCORE_HIGH;
                if (scoring == score.MID) currentPos = SuperPos.SCORE_MID;
                if (scoring == score.LOW) currentPos = SuperPos.SCORE_LOW;
                if (lastPos != currentPos) timer.reset();
                swerveWait = true;
            } else if (driverLB.getValue()){
                if (stationing == station.DOUBLE) currentPos = SuperPos.HP_STATION_DOUBLE;
                //if (stationing == station.SINGLE) currentPos = SuperPos.HP_STATION_SINGLE;
                if (lastPos != currentPos) timer.reset();
                swerveWait = true;
            } else if (driverRB.getValue()){
                if (intaking == intake.UPRIGHT) currentPos = SuperPos.INTAKE_BACK;
                if (intaking == intake.TIPPED) currentPos = SuperPos.INTAKE_BACK_LOW;
                if (intaking == intake.FRONT) currentPos = SuperPos.INTAKE_FRONT;
                if (lastPos != currentPos) timer.reset();
            } else {
                currentPos = SuperPos.NEUTRAL;
                if (lastPos != currentPos) timer.reset();
            }
        }

        // if (source == A && A.getValue()){
        //     currentPos = SuperPos.SCORE_LOW;
        // }if (source == B && B.getValue()){
        //     currentPos = SuperPos.SCORE_MID;
        // }if (source == X && X.getValue()){
        //     currentPos = SuperPos.NEUTRAL;
        // }if (source == Y && Y.getValue()){
        //     currentPos = SuperPos.SCORE_HIGH;
        // }if (source == dUp && dUp.getValue()){
        //     currentPos = SuperPos.HP_STATION_FRONT;
        // }if (source == dRight && dRight.getValue()){
        //     currentPos = SuperPos.INTAKE_BACK;
        // }if (source == dLeft && dLeft.getValue()){
        //     currentPos = SuperPos.INTAKE_FRONT;
        // }if (source ==  dDown&& dDown.getValue()){
        //     currentPos = SuperPos.INTAKE_BACK_LOW;
        // }
        
        if (currentPos != lastPos){
            determineMotion();
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
        start = (DigitalInput) WSInputs.MANIPULATOR_START.get();
        start.addInputListener(this);
        driverLT = (AnalogInput) WSInputs.DRIVER_LEFT_TRIGGER.get();
        driverLT.addInputListener(this);
        driverLB = (DigitalInput) WSInputs.DRIVER_LEFT_SHOULDER.get();
        driverLB.addInputListener(this);
        driverRB = (DigitalInput) WSInputs.DRIVER_RIGHT_SHOULDER.get();
        driverRB.addInputListener(this);

        arm = new Arm((WsSparkMax) WSOutputs.ARM_ONE.get());
        lift = new Lift((WsSparkMax) WSOutputs.LIFT_DRIVER.get());
        wrist = new Wrist((WsSparkMax) WSOutputs.WRIST.get());
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);

        resetState();
    }

    @Override
    public void update() {
        if (swerveWait && Math.abs(swerve.getGyroAngle() - swerve.getRotTarget()) < 15.0){
            swerveWait = false;
        }
        if (timer.hasElapsed(0.5)) swerveWait = false;
        if (motion == modes.ARMDELAY){
            //armWait = true;
            wristWait = true;
            motion = modes.SIMPLE;
        }
        if (motion == modes.LIFTDELAY){
            wristWait = true;
            liftWait = true;
            motion = modes.SIMPLE;
        }
        if (motion == modes.WRIST){
            wristWait = true;
            motion = modes.SIMPLE;
        }
        if (motion == modes.SIMPLE){
            if (liftWait || swerveWait){
                if (arm.notScooping() && !swerveWait){
                    lift.setPosition(liftMod + currentPos.getL(gamepiece));
                    liftWait = false;
                } else {
                    lift.setPosition(lift.getPosition());
                }
            } else {
                lift.setPosition(liftMod + currentPos.getL(gamepiece));
            }  

            if (armWait || swerveWait){
                if (lift.getPosition() < SuperConts.LIFTSTAGE && !swerveWait){
                    arm.setPosition(currentPos.getA(gamepiece));
                    armWait = false;
                } else {
                    arm.setPosition(arm.getPosition());
                }
            } else {
                arm.setPosition(currentPos.getA(gamepiece));
            }

            if (wristWait || swerveWait){
                if (arm.pastLift() && !armWait || !swerveWait){
                    wrist.setPosition(currentPos.getW(gamepiece));
                    wristWait = false;
                } else {
                    //wrist.setFollow((360-arm.getPosition())%360, arm.getSpeed(currentPos.getA(gamepiece)));
                    wrist.setPosition((360-arm.getPosition())%360);
                }
            } else {
                wrist.setPosition(currentPos.getW(gamepiece));
            }
        }
        displayNumbers();
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
        scoring = score.HIGH;
        intaking = intake.UPRIGHT;
        stationing = station.SINGLE;
        timer.reset(); timer.start();
        liftMod = 0.0;
        swerveWait = false;
    }

    @Override
    public void selfTest() {
    }

    @Override
    public String getName() {
        return "Superstructure";
    }   
    private void determineMotion(){
        if (lastPos.getA(gamepiece) < SuperConts.ANTISCOOP){
            this.motion = modes.LIFTDELAY;
        } else if (lastPos.getDirection() != currentPos.getDirection()){
            this.motion = modes.WRIST;
        } else {
            this.motion = modes.SIMPLE;
        }
    }
    private void displayNumbers(){
        SmartDashboard.putNumber("Arm Target", currentPos.getA(gamepiece));
        SmartDashboard.putNumber("Lift Target", currentPos.getL(gamepiece));
        SmartDashboard.putNumber("Wrist Target", currentPos.getW(gamepiece));
        SmartDashboard.putNumber("Arm Position", arm.getPosition());
        SmartDashboard.putNumber("Lift Encoder", lift.getPosition());
        SmartDashboard.putNumber("Wrist Position", wrist.getPosition());
        SmartDashboard.putString("Cone or Cube", gamepiece? "Cone" : "Cube");
        SmartDashboard.putString("Score Level", scoreString[scoring.ordinal()]);
        SmartDashboard.putString("Intake Level", intakeString[intaking.ordinal()]);
        SmartDashboard.putString("Station level", stationString[stationing.ordinal()]);
        SmartDashboard.putNumber("Lift Modifier", liftMod);
    }
    public void goToPosition(SuperPos position){
        currentPos = position;
        if (currentPos != lastPos) determineMotion();
        lastPos = currentPos;
    }
    public void setGamepiece(boolean newGamePiece){
        this.gamepiece = newGamePiece;
    }
}