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
    private DigitalInput highGoal, midGoal, lowGoal, stationForward, stationReverse, cubeMode, coneMode,groundForward,groundReverse,reset,halt,deploy;
    private AnalogInput liftManual,armAjuster,wristAjuster;
    // motors
    

    //custom mechinisems
    private arm armHelper;
    private lift liftHelper;
    private wrist wristHelper;

    //variables
    private double armPosition;
    private double wristPosition;
    private double liftPosition;

    private double armAjust;
    private double wristAjust;

    private boolean posChanged;
    private boolean haltSignal;
    private boolean liftResetSignal;

    private double liftSpeed;
    private static final double liftDeadband = 0.05;
    private static final double liftSpeedFactor = 0.2;
    private static final double liftResetBound = -0.9;

    private static final double liftFlipPos = 30;
    private static final double wristOffset = 25;

    private static final double armAjustFactor = 5;
    private static final double wristAjustFactor = 5;
    private static final double ajustDeadband = 0.1;

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
        HOLDING(0,180,180,modes.FORWARD,"HOLDING","HOLDING",false),
        GROUND_FORWARD(13.5,283,52,modes.FORWARD,"GROUND_FORWARD","GROUND_FORWARD",false),
        GROUND_REVERSE(26.7,56.4,302.0,modes.REVERSE,"GROUND_REVERSE","GROUND_REVERSE",false),

        CONE_LOW(0,270,135,modes.FORWARD,"CONE_LOW","CUBE_LOW",true),
        CONE_MID(31.2,233.4,44.5,modes.FORWARD, "CONE_MID","CUBE_MID",true),
        CONE_HIGH(68,245.4,51.8,modes.FORWARD,"CONE_HIGH","CUBE_HIGH",true),

        CUBE_LOW(0,270,135,modes.FORWARD,"CUBE_LOW","CUBE_LOW",true),
        CUBE_MID(31.2,233.4,44.5,modes.FORWARD,"CUBE_MID","CUBE_MID",true),
        CUBE_HIGH(68,245.4,51.8,modes.FORWARD,"CUBE_HIGH","CUBE_HIGH",true),

        STATION_FORWARD(25,203,58,modes.FORWARD,"STATION_FORWARD","STATION_FORWARD",false),
        STATION_REVERSE(40,140,310,modes.REVERSE,"STATION_REVERSE","STATION_REVERSE",false);
        public final double lPos;
        public final double aPos;
        public final double wPos;
        public final String name;
        public final modes mode;
        public final String cube;
        public final boolean isScoring;
        private position(double liftPos, double armPos, double wristPos,modes direction,String posName,String cubName,boolean score){
            this.lPos = liftPos;
            this.aPos = armPos;
            this.wPos = wristPos;
            this.name = posName;
            this.mode = direction;
            this.cube = cubName;
            this.isScoring = score;
        }

    }
    private Hashtable<DigitalInput, position> buttonToPosition = new Hashtable<DigitalInput, position>();
    

    private modes currentMode;
    private modes liftState;
    private modes delays;
    private position currentPosition;
    private position lastPosition;
    private position stashedPosition;

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
        wristAjuster = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_JOYSTICK_X); 
        wristAjuster.addInputListener(this);
        armAjuster = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_JOYSTICK_Y); 
        armAjuster.addInputListener(this);

        deploy = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_SHOULDER); 
        deploy.addInputListener(this);

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
        stashedPosition = position.HOLDING;
        posChanged = false;
        haltSignal = false;
        liftResetSignal = false;
        liftState = modes.LIFT_AUTOMATIC;
        liftSpeed = 0;
        delays = modes.FREE;
        armAjust = 0;
        wristAjust = 0;
    }

    @Override
    public void update() {
        if(posChanged && delays == modes.FREE){ //if pos has changed, update targets
            //hight limit stuff
            if(lastPosition.mode != currentPosition.mode &&(lastPosition.lPos > liftFlipPos || currentPosition.lPos>liftFlipPos) && liftState == modes.LIFT_AUTOMATIC){ //if in danger of violating height limit. This happens if flipping with either start or finish pos above safe lift flip position
                liftHelper.goToPosition(liftFlipPos);
                delays = modes.HOLDING_ARM;
            }
            //everything else
            else{
                armHelper.goToPosition(currentPosition.aPos+armAjust,currentPosition.isScoring); 
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
            delays = modes.HOLDING_ARM;
            posChanged = false;
        }

        if(liftResetSignal == true){ //if left joystick all the way down, reset lift encoder.
            liftHelper.resetEncoder();
            liftResetSignal = false;
        }

        if(haltSignal){ //if halt button pressed, stop everything as well as automatic movements.
            armHelper.stopMotor();
            liftHelper.stopMotor();
            wristHelper.stopMotor();
            delays = modes.FREE;
        }

        if(delays == modes.HOLDING_ARM && liftHelper.isReady()){
             //if lift finished moving to position, move arm
            armHelper.goToPosition(currentPosition.aPos+armAjust,currentPosition.isScoring);
            delays = modes.HOLDING_LIFT;
            
        }
        //if frozen and arm has finished, move lift and wrist to correct pos
        if(delays == modes.HOLDING_LIFT && armHelper.isReady()){
            liftHelper.goToPosition(currentPosition.lPos);
            //wristHelper.goToPosition(currentPosition.wPos+wristAjust);
            delays = modes.FREE;
        }
        //manual mode directly controls speed
        if(liftState == modes.LIFT_MANUAL){
            liftHelper.setSpeed(liftSpeed,false);
        }
        //update arm/wrist ajustment even if preset not changed
        if(delays == modes.FREE){
            armHelper.goToPosition(currentPosition.aPos+armAjust,currentPosition.isScoring);
        }
        setWrist(currentPosition.wPos+wristAjust);
        //smartdashboard
        SmartDashboard.putNumber("arm pos",armHelper.getPosition());
        SmartDashboard.putNumber("wrist pos",wristHelper.getPosition());
        SmartDashboard.putNumber("lift pos",liftHelper.getPosition());
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
        //get position corresponding to button
        if(buttonToPosition.containsKey(source) && getDigitalInput(source).getValue()){
            stashedPosition = buttonToPosition.get(source);
            if(currentMode == modes.CUBE){
                stashedPosition = stringToPosition.get(currentPosition.cube);
            }
            SmartDashboard.putString("currentPosition",currentPosition.name);
        }
        if(deploy.getValue()){
            lastPosition = currentPosition;
            currentPosition = stashedPosition;
        }
        else{
            lastPosition = currentPosition;
            currentPosition = position.HOLDING;
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
        //right joystick ajust
        if(Math.abs(armAjuster.getValue())>ajustDeadband){
            armAjust = armAjuster.getValue()*armAjustFactor;
        }
        else{
            armAjust = 0;
        }
        if(Math.abs(wristAjuster.getValue())>ajustDeadband){
            wristAjust = wristAjuster.getValue()*wristAjustFactor;
        }
        else{
            wristAjust = 0;
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
        for (position pos : position.values()){ //construct string to position dictionary on init.
            stringToPosition.put(pos.name,pos);
        }

    }  
    private DigitalInput getDigitalInput(Input source){
        for (DigitalInput digimon:buttonToPosition.keySet()){
            if(source == digimon){
                return digimon;
            }
        }
        return buttonToPosition.keySet().iterator().next();
    }

    public void autoSetPosition(String pos){ //auto override code. get position corresponding to string.
        if(stringToPosition.containsKey(pos)){
            lastPosition = currentPosition;
            currentPosition = stringToPosition.get(pos);
            SmartDashboard.putString("currentPosition",currentPosition.name);
        }
        else{
            SmartDashboard.putString("Error: autoSetPosition recived invalid String. Recived: "+pos,currentPosition.name);
        }
    }
    
    private void setWrist(double pos){
        pos = pos>armHelper.getPosition()+wristOffset?armHelper.getPosition()+wristOffset:pos;
        pos = pos<armHelper.getPosition()-wristOffset?armHelper.getPosition()-wristOffset:pos;
        wristHelper.goToPosition(pos);
        
    }
}