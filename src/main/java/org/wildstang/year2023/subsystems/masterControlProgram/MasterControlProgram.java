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


public class MasterControlProgram implements Subsystem {
    
    //inputs
    private DigitalInput highGoal, midGoal, lowGoal, station, cubeMode, coneMode,forward,reverse,reset,halt;
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
    // states
    private enum positions{
        HOLDING(0,0,0,"HOLDING"),
        GROUND_FORWARD(0,0,0,"GROUND_FORWARD"),
        GROUND_REVERSE(0,0,0,"GROUND_REVERSE"),
        CONE_MID_FORWARD(0,0,0,"CONE_MID_FORWARD"),
        CONE_MID_REVERSE(0,0,0,"CONE_MID_REVERSE"),
        CONE_HIGH_FORWARD(0,0,0,"CONE_HIGH_FORWARD"),
        CUBE_MID_FORWARD(0,0,0,"CUBE_MID_FORWARD"),
        CUBE_MID_REVERSE(0,0,0,"CUBE_MID_REVERSE"),
        CUBE_HIGH_FORWARD(0,0,0,"CUBE_HIGH_FORWARD"),
        STATION_FORWARD(0,0,0,"STATION_FORWARD"),
        STATION_REVERSE(0,0,0,"STATION_REVERSE");
        public final double lpos;
        public final double apos;
        public final double wpos;
        public final String name;
        private positions(double liftPos, double armPos, double wristPos,String Name){
            this.lpos = liftPos;
            this.apos = armPos;
            this.wpos = wristPos;
            this.name = Name;
        }
    }

    private enum modes{
        CONE,
        CUBE,
        FORWARD,
        REVERSE,
        LIFT_MANUAL,
        LIFT_AUTOMATIC;
    } //true = cone, false = cube
    private modes CurrentMode;
    private modes currentDirection;
    private modes liftState;
    private positions currentPosition;

    private String oldQuery;
    @Override
    public void init() {
        //inputs
        highGoal = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_UP);
        highGoal.addInputListener(this);
        midGoal = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_LEFT);
        midGoal.addInputListener(this);
        lowGoal = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_DOWN);
        lowGoal.addInputListener(this);
        station = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_RIGHT);
        station.addInputListener(this);
        coneMode = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_SHOULDER);
        coneMode.addInputListener(this);
        cubeMode = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_SHOULDER);
        cubeMode.addInputListener(this);
        forward = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_UP);
        forward.addInputListener(this);
        reverse = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_DOWN);
        reverse.addInputListener(this);
        reset = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SELECT);
        reset.addInputListener(this);
        halt = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_START); //halt button in case want to stop arm but not rest of robot.
        halt.addInputListener(this);

        liftManual = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_JOYSTICK_Y); //halt button in case want to stop arm but not rest of robot.
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
        currentDirection = modes.FORWARD;
        posChanged = false;
        oldQuery = "";
        haltSignal = false;
        liftResetSignal = false;
        liftState = modes.LIFT_AUTOMATIC;
        liftSpeed = 0;
    }

    @Override
    public void update() {
        if(posChanged){ //if pos has changed, update targets
            armHelper.goToPosition(currentPosition.apos);
            wristHelper.goToPosition(currentPosition.wpos);
            if(liftState == modes.LIFT_AUTOMATIC){
            liftHelper.goToPosition(currentPosition.lpos);
            }
            else{
                liftHelper.setSpeed(liftSpeed,false);
            }
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
        
    }

    @Override
    public void inputUpdate(Input source) { 
        //check for mode changes
        if(forward.getValue()){ //check if forward
            currentDirection = modes.FORWARD;
        }
        else if(reverse.getValue()){
            currentDirection = modes.REVERSE;
        }

        //this next part is fun. 
        String positionQuery = "";
        if(CurrentMode == modes.CONE && (highGoal.getValue()||midGoal.getValue())){ //first, check whether CONE or CUBE. If no goal button pressed, mode does not matter.
            positionQuery += "CONE_";
        }
        else if(CurrentMode == modes.CUBE && (highGoal.getValue()||midGoal.getValue())){
            positionQuery += "CUBE_";
        }

        if(highGoal.getValue()){ //then, check whether HIGH, MID, LOW, or STATION
            positionQuery += "HIGH_";
        }
        else if (midGoal.getValue()){
            positionQuery += "MID_";
        }
        else if (station.getValue()){
            positionQuery += "STATION_";
        }
        else if (lowGoal.getValue()){
            positionQuery += "GROUND_";
        }

        if(currentDirection == modes.FORWARD || highGoal.getValue()){ //then, check whether FORWARD or REVERSE. HIGH is only FORWARD
            positionQuery += "FORWARD";
        }
        else{
            positionQuery += "REVERSE";
        }

        if(reset.getValue()){
            positionQuery = "HOLDING"; //override to default position if button pressed
        }
        
        if(!(positionQuery == oldQuery)){
            boolean found = false;
            for (positions position : positions.values()) {  //find the position with the queried name
                if(position.name == positionQuery){
                    found = true;
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