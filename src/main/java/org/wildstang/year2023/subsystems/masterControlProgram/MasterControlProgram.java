package org.wildstang.year2023.subsystems.mastercontrolprogram;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
//import org.wildstang.hardware.roborio.outputs.WsPhoenix;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSSubsystems;
//custom
import org.wildstang.year2023.subsystems.arm.arm;
import org.wildstang.year2023.subsystems.arm.lift;
import org.wildstang.year2023.subsystems.arm.wrist;
import org.wildstang.year2023.subsystems.targeting.AimHelper;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.*;

public class MasterControlProgram implements Subsystem {
    
    //inputs
    private DigitalInput highGoal, midGoal, lowGoal, stationForward, stationReverse, cubeMode, coneMode,groundForward,groundReverse,reset,halt;
    private AnalogInput liftManual;
    // motors
    

    //custom mechinisems
    private arm armHelper;
    private lift liftHelper;
    private wrist wristHelper;

    //variables
    private double armPosition;
    private double wristPosition;
    private double liftPosition;

    private boolean posChanged;
    private boolean haltSignal;
    private boolean liftResetSignal;

    private double liftSpeed;
    private static final double liftDeadband = 0.05;
    private static final double liftSpeedFactor = 0.5;
    private static final double liftResetBound = -0.9;

    private static final double liftFlipPos = 0;
    private static final double wristCarryPos = 0;
    private AimHelper AimHelper;

    private enum modes{
        CONE,
        CUBE,
        FORWARD,
        REVERSE,
        LIFT_MANUAL,
        LIFT_AUTOMATIC,
        HOLDING_ARM,
        HOLDING_LIFT,
        HOLDING_WRIST,
        FREE;
    } //true = cone, false = cube

    private Hashtable<String, position> stringToPosition = new Hashtable<String,position>();

    private enum position{
        HOLDING(0,180,180,modes.FORWARD,"HOLDING","HOLDING"),
        GROUND_FORWARD(10,0,0,modes.FORWARD,"GROUND_FORWARD","GROUND_FORWARD"),
        GROUND_REVERSE(0,0,0,modes.REVERSE,"GROUND_REVERSE","GROUND_REVERSE"),

        CONE_LOW(0,0,0,modes.FORWARD,"CONE_LOW","CUBE_LOW"),
        CONE_MID(0,0,0,modes.FORWARD, "CONE_MID","CUBE_MID"),
        CONE_HIGH(72,240,55,modes.FORWARD,"CONE_HIGH","CUBE_HIGH"),

        CUBE_LOW(0,0,0,modes.FORWARD,"CUBE_LOW","CUBE_LOW"),
        CUBE_MID(0,0,0,modes.FORWARD,"CUBE_MID","CUBE_MID"),
        CUBE_HIGH(72,240,55,modes.FORWARD,"CUBE_HIGH","CUBE_HIGH"),

        STATION_FORWARD(0,0,0,modes.FORWARD,"STATION_FORWARD","STATION_FORWARD"),
        STATION_REVERSE(0,0,0,modes.REVERSE,"STATION_REVERSE","STATION_REVERSE");
        public final double lPos;
        public final double aPos;
        public final double wPos;
        public final String name;
        public final modes mode;
        public final String cube;
        private position(double liftPos, double armPos, double wristPos,modes direction,String posName,String cubName){
            this.lPos = liftPos;
            this.aPos = armPos;
            this.wPos = wristPos;
            this.name = posName;
            this.mode = direction;
            this.cube = cubName;
        }

    }
    private Hashtable<DigitalInput, position> buttonToPosition = new Hashtable<DigitalInput, position>();
    

    private modes currentMode;
    private modes liftState;
    private modes delays;
    private position currentPosition;
    private position lastPosition;

    @Override
    public void init() {
        //inputs
        highGoal = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_UP);
        highGoal.addInputListener(this);
        buttonToPosition.put(highGoal,position.CONE_HIGH);

        midGoal = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_LEFT);
        midGoal.addInputListener(this);
        buttonToPosition.put(midGoal,position.CONE_MID);

        lowGoal = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_DOWN);
        lowGoal.addInputListener(this);
        buttonToPosition.put(lowGoal,position.CONE_LOW);

        stationForward = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_RIGHT);
        stationForward.addInputListener(this);
        buttonToPosition.put(stationForward,position.STATION_FORWARD);

        stationReverse = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_LEFT);
        stationReverse.addInputListener(this);
        buttonToPosition.put(stationReverse,position.STATION_REVERSE);

        groundForward = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_UP);
        groundForward.addInputListener(this);
        buttonToPosition.put(groundForward,position.GROUND_FORWARD);

        groundReverse = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_DOWN);
        groundReverse.addInputListener(this);
        buttonToPosition.put(groundReverse,position.GROUND_REVERSE);


        coneMode = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_SHOULDER);
        coneMode.addInputListener(this);
        cubeMode = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_SHOULDER);
        cubeMode.addInputListener(this);

        reset = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SELECT);
        reset.addInputListener(this);
        buttonToPosition.put(reset,position.HOLDING);

        halt = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_START); //halt button in case want to stop arm but not rest of robot.
        halt.addInputListener(this);

        liftManual = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_JOYSTICK_Y); 
        liftManual.addInputListener(this);

        AimHelper = (AimHelper) Core.getSubsystemManager().getSubsystem(WSSubsystems.AIM_HELPER);

        armHelper = new arm();
        liftHelper = new lift();
        wristHelper = new wrist();
        resetState();
        initStringToPosition();
    }

    @Override
    public void resetState() {
        currentMode = modes.CUBE;
        currentPosition = position.HOLDING;
        lastPosition = position.HOLDING;
        posChanged = false;
        haltSignal = false;
        liftResetSignal = false;
        liftState = modes.LIFT_AUTOMATIC;
        liftSpeed = 0;
        delays = modes.FREE;
    }

    @Override
    public void update() {
        if(posChanged && delays == modes.FREE){ //if pos has changed, update targets
            //hight limit stuff
            if(lastPosition.mode != currentPosition.mode &&(lastPosition.lPos > liftFlipPos || currentPosition.lPos>liftFlipPos) && liftState == modes.LIFT_AUTOMATIC){
                liftHelper.goToPosition(liftFlipPos);
                wristHelper.goToPosition(wristCarryPos);
                delays = modes.HOLDING_ARM;
            }
            //everything else
            else{
                armHelper.goToPosition(currentPosition.aPos);
                if(lastPosition.mode != currentPosition.mode){
                    wristHelper.goToPosition(wristCarryPos);
                    delays = modes.HOLDING_WRIST;
                }
                else{
                    wristHelper.goToPosition(currentPosition.wPos);
                }
                if(liftState == modes.LIFT_AUTOMATIC){
                    liftHelper.goToPosition(currentPosition.lPos);
                }
                else{
                    //manual controol
                    liftHelper.setSpeed(liftSpeed,false);
                }
            }
            //dont update until next pos change
            posChanged = false;
        }
        else if (posChanged){ //if flip manuver inturrpted, act as though flipping out of precaution. this might need fixing if it causes a problem.
            liftHelper.goToPosition(liftFlipPos);
            wristHelper.goToPosition(wristCarryPos);
            delays = modes.HOLDING_ARM;
            posChanged = false;
        }
        if(liftResetSignal == true){
            liftHelper.resetEncoder();
            liftResetSignal = false;
        }

        if(haltSignal){
            armHelper.stopMotor();
            liftHelper.stopMotor();
            wristHelper.stopMotor();
        }

        if(delays == modes.HOLDING_ARM){
            if(liftHelper.isReady()){ //if lift finished moving to position, move arm
                armHelper.goToPosition(currentPosition.aPos);
                delays = modes.HOLDING_LIFT;
            }
        }
        if(delays == modes.HOLDING_LIFT && armHelper.isReady()){
            liftHelper.goToPosition(currentPosition.lPos);
            wristHelper.goToPosition(currentPosition.wPos);
            delays = modes.FREE;
        }
        if(delays == modes.HOLDING_WRIST && armHelper.isReady()){
            wristHelper.goToPosition(currentPosition.wPos);
        }
        if(liftState == modes.LIFT_MANUAL){
            liftHelper.setSpeed(liftSpeed,false);
        }
        
    }

    @Override
    public void inputUpdate(Input source) { 
        //check for mode change
        if(coneMode.getValue()){ 
            currentMode = modes.CONE;
            AimHelper.changePipeline("reflective");
        }
        else if(cubeMode.getValue()){
            currentMode = modes.CUBE;
            AimHelper.changePipeline("AprilTag");
        }

        if(buttonToPosition.containsKey(source)){
            lastPosition = currentPosition;
            currentPosition = buttonToPosition.get(source);
            if(currentMode == modes.CUBE){
                currentPosition = stringToPosition.get(currentPosition.cube);
            }
            SmartDashboard.putString("currentPosition",currentPosition.name);
        }
        
        
        //halt button
        if(halt.getValue())
        {
            haltSignal = true;
        }
        else{
            haltSignal = false;
        }
        //manual lift control
        if(Math.abs(liftManual.getValue())>liftDeadband){
            liftState = modes.LIFT_MANUAL;
            liftSpeed = liftManual.getValue()*liftSpeedFactor;
            if(liftManual.getValue() < liftResetBound){
                liftResetSignal = true;
            }
        }
        else{
            liftState = modes.LIFT_AUTOMATIC;
        }

    }

    @Override
    public String getName() {
        return "Master Control Program";
    }

    @Override
    public void selfTest() {
    }
    public void initStringToPosition() {
        for (position pos : position.values()){
            stringToPosition.put(pos.name,pos);
        }

    }
}