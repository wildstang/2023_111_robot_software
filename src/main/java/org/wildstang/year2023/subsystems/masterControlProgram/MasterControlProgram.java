package org.wildstang.year2023.subsystems.masterControlProgram;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
//import org.wildstang.hardware.roborio.outputs.WsPhoenix;
import org.wildstang.year2023.robot.WSInputs;
//custom
import org.wildstang.year2023.subsystems.arm.arm;
import org.wildstang.year2023.subsystems.arm.lift;
import org.wildstang.year2023.subsystems.arm.wrist;
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

    private enum modes{
        CONE,
        CUBE,
        FORWARD,
        REVERSE,
        LIFT_MANUAL,
        LIFT_AUTOMATIC,
        HOLDING_ARM,
        HOLDING_LIFT,
        FREE;
    } //true = cone, false = cube

    // states
    private enum positions{
        HOLDING(0,0,0,"HOLDING",modes.FORWARD),
        GROUND_FORWARD(0,0,0,"GROUND_FORWARD",modes.FORWARD),
        GROUND_REVERSE(0,0,0,"GROUND_REVERSE",modes.REVERSE),

        CONE_LOW_FORWARD(0,0,0,"CONE_LOW_FORWARD",modes.FORWARD),
        CONE_MID_FORWARD(0,0,0,"CONE_MID_FORWARD",modes.FORWARD),
        //CONE_MID_REVERSE(0,0,0,"CONE_MID_REVERSE"),
        CONE_HIGH_FORWARD(0,0,0,"CONE_HIGH_FORWARD",modes.FORWARD),

        CUBE_LOW_FORWARD(0,0,0,"CUBE_LOW_FORWARD",modes.FORWARD),
        CUBE_MID_FORWARD(0,0,0,"CUBE_MID_FORWARD",modes.FORWARD),
        //CUBE_MID_REVERSE(0,0,0,"CUBE_MID_REVERSE"),
        CUBE_HIGH_FORWARD(0,0,0,"CUBE_HIGH_FORWARD",modes.FORWARD),

        STATION_FORWARD(0,0,0,"STATION_FORWARD",modes.FORWARD),
        STATION_REVERSE(0,0,0,"STATION_REVERSE",modes.REVERSE);
        public final double lpos;
        public final double apos;
        public final double wpos;
        public final String name;
        public final modes mode;
        private positions(double liftPos, double armPos, double wristPos,String Name,modes direction){
            this.lpos = liftPos;
            this.apos = armPos;
            this.wpos = wristPos;
            this.name = Name;
            this.mode = direction;
        }

    }
    private Hashtable<DigitalInput, String> buttonMapping = new Hashtable<DigitalInput, String>();
    

    private modes CurrentMode;
    private modes liftState;
    private modes delays;
    private positions currentPosition;
    private positions lastPosition;

    private String oldQuery;
    @Override
    public void init() {
        //inputs
        highGoal = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_UP);
        highGoal.addInputListener(this);
        buttonMapping.put(highGoal,"HIGH_FORWARD");

        midGoal = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_LEFT);
        midGoal.addInputListener(this);
        buttonMapping.put(midGoal,"MID_FORWARD");

        lowGoal = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_DOWN);
        lowGoal.addInputListener(this);
        buttonMapping.put(lowGoal,"LOW_FORWARD");

        stationForward = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_RIGHT);
        stationForward.addInputListener(this);
        buttonMapping.put(stationForward,"STATION_FORWARD");

        stationReverse = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_LEFT);
        stationReverse.addInputListener(this);
        buttonMapping.put(stationReverse,"STATION_REVERSE");

        groundForward = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_UP);
        groundReverse.addInputListener(this);
        buttonMapping.put(groundForward,"GROUND_FORWARD");

        groundReverse = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_DOWN);
        groundReverse.addInputListener(this);
        buttonMapping.put(groundReverse,"GROUND_REVERSE");


        coneMode = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_SHOULDER);
        coneMode.addInputListener(this);
        cubeMode = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_SHOULDER);
        cubeMode.addInputListener(this);

        reset = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SELECT);
        reset.addInputListener(this);
        halt = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_START); //halt button in case want to stop arm but not rest of robot.
        halt.addInputListener(this);

        liftManual = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_JOYSTICK_Y); 
        liftManual.addInputListener(this);

        armHelper = new arm();
        liftHelper = new lift();
        wristHelper = new wrist();
        resetState();
    }

    @Override
    public void resetState() {
        CurrentMode = modes.CUBE;
        currentPosition = positions.HOLDING;
        lastPosition = positions.HOLDING;
        posChanged = false;
        oldQuery = "";
        haltSignal = false;
        liftResetSignal = false;
        liftState = modes.LIFT_AUTOMATIC;
        liftSpeed = 0;
        delays = modes.FREE;
    }

    @Override
    public void update() {
        if(posChanged && delays == modes.FREE){ //if pos has changed, update targets
            if(lastPosition.mode != currentPosition.mode &&(lastPosition.lpos > liftFlipPos || currentPosition.lpos>liftFlipPos)){
                liftHelper.goToPosition(liftFlipPos);
                delays = modes.HOLDING_ARM;
            }
            else{
                armHelper.goToPosition(currentPosition.apos);
                wristHelper.goToPosition(currentPosition.wpos);
                if(liftState == modes.LIFT_AUTOMATIC){
                    liftHelper.goToPosition(currentPosition.lpos);
                }
                else{
                    liftHelper.setSpeed(liftSpeed,false);
                }
            }
        }
        else if (posChanged){ //if flip manuver inturrpted, act as though flipping out of precaution. this might need fixing if it causes a problem.
            liftHelper.goToPosition(liftFlipPos);
            delays = modes.HOLDING_ARM;
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
                armHelper.goToPosition(currentPosition.apos);
                wristHelper.goToPosition(currentPosition.wpos);
                delays = modes.HOLDING_LIFT;
            }
        }
        if(delays == modes.HOLDING_LIFT && armHelper.isReady()){
            liftHelper.goToPosition(currentPosition.lpos);
            delays = modes.FREE;
        }
        
    }

    @Override
    public void inputUpdate(Input source) { 
        //check for mode change
        if(coneMode.getValue()){ 
            CurrentMode = modes.CONE;
        }
        else if(cubeMode.getValue()){
            CurrentMode = modes.CUBE;
        }

        //this next part is fun. 
        String positionQuery = "";
        boolean goalButton = (source == highGoal || source == midGoal || source == lowGoal);
        if(CurrentMode == modes.CONE && goalButton){ //first, check whether CONE or CUBE. If no goal button pressed, mode does not matter.
            positionQuery += "CONE_";
        }
        else if(CurrentMode == modes.CUBE && goalButton){
            positionQuery += "CUBE_";
        }
        positionQuery += (String) buttonMapping.get(source); //hopefully no error when returns null
        
        if(reset.getValue()){
            positionQuery = "HOLDING"; //override to default position if button pressed
        }
        
        if(!(positionQuery == oldQuery)){
            boolean found = false;
            for (positions position : positions.values()) {  //find the position with the queried name
                if(position.name == positionQuery){
                    found = true;
                    lastPosition = currentPosition;
                    currentPosition = position; //update current position
                }
            }
            if(found){
                oldQuery = positionQuery;
                posChanged = true;
            }
            SmartDashboard.putBoolean("target pos found?", found);
            SmartDashboard.putString("target name", positionQuery);

        }
        //halt button
        if(halt.getValue()){
            haltSignal = true;
        }
        else{
            haltSignal = false;
        }
        //manual lift control
        if(Math.abs(liftManual.getValue())>liftDeadband){
            liftState = modes.LIFT_MANUAL;
            liftSpeed = liftManual.getValue()*liftSpeedFactor;
            if(liftManual.getValue()<liftResetBound){
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
}