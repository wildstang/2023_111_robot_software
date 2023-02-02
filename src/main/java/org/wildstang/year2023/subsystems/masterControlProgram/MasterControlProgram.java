package org.wildstang.year2023.subsystems.mastercontrolprogram;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
//import org.wildstang.hardware.roborio.outputs.WsPhoenix;
import org.wildstang.year2023.robot.WSInputs;
//custom
import org.wildstang.year2023.subsystems.arm.arm;
import org.wildstang.year2023.subsystems.arm.lift;
import org.wildstang.year2023.subsystems.arm.wrist;


public class MasterControlProgram implements Subsystem {
    
    //inputs
    private DigitalInput highGoal, midGoal, lowGoal, station, cubeMode, coneMode;
    // motors
    

    //custom mechinisems
    private arm armHelper;
    private lift liftHelper;
    private wrist wristHelper;

    //variables
    private double armPosition;
    private double wristPosition;
    private double liftPosition;


    // states
    private enum positions{
        GROUND_FORWARD();
        double lpos;
        double apos;
        double wpos;
        void setPosition(double liftPos, double armPos, double wristPos){
            lpos = liftPos;
            apos = armPos;
            wpos = wristPos;

        }
        double[] returnPositions(){
            double[] list = {lpos,apos,wpos};
            return list;
        }
    }

    //position constents
    private positions ConeLowerPosition;
    private positions CubeLowerPosition;

    private positions ConeMiddlePosition;
    private positions CubeMiddlePosition;

    private positions ConeHighPosition;
    private positions CubeHighPosition;

    private positions StationPosition;


    private enum modes{
        CONE,
        CUBE;
    } //true = cone, false = cube
    private modes CurrentMode = modes.CUBE;

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
        coneMode = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_LEFT);
        coneMode.addInputListener(this);
        cubeMode = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_RIGHT);
        cubeMode.addInputListener(this);

        //position constents ***************** CHANGE WHEN VALUES ARE KNOWN *****************
        ConeLowerPosition.setPosition(0, 0, 0); //temp
        CubeLowerPosition.setPosition(0, 0, 0); //temp

        ConeMiddlePosition.setPosition(50, 50, 50); //temp
        CubeMiddlePosition.setPosition(50, 50, 50); //temp

        ConeHighPosition.setPosition(100, 100, 100); //temp
        CubeHighPosition.setPosition(100, 100, 100); //temp

        StationPosition.setPosition(0, 0, 0); //temp

        //***************** END OF CHANGED VALUES *****************
        //Helper classes
        armHelper = new arm();
        liftHelper = new lift();
        wristHelper = new wrist();
    }

    @Override
    public void resetState() {
    }

    @Override
    public void update() {
        //joints
        if ((!armHelper.isReady())){
            //not at position yet
            armHelper.goToPosition(armPosition);
        }else{
            armHelper.stopMotor();
        }
        if ((!liftHelper.isReady())){
            //not at position yet
            liftHelper.goToPosition(liftPosition);
        }else{
            liftHelper.stopMotor();
        }
        if ((!wristHelper.isReady())){
            //not at position yet
            wristHelper.goToPosition(wristPosition);
        }else{
            wristHelper.stopMotor();
        }
    }

    @Override
    public void inputUpdate(Input source) {

        //*******Behavior*******
        //cone mode
        if (source == coneMode && coneMode.getValue()){
            CurrentMode = modes.CONE;
        }

        //cube mode
        if (source == cubeMode && cubeMode.getValue()){
            CurrentMode = modes.CUBE;
        } 

        //*******armPosition*******
        //lower position
        if (source == lowGoal && lowGoal.getValue() && CurrentMode == modes.CONE){
            //set all positions to lower
            double[] newPositions = ConeLowerPosition.returnPositions();
            liftPosition = newPositions[0];
            armPosition = newPositions[1];
            wristPosition = newPositions[2];
        }else if (source == lowGoal && lowGoal.getValue() && CurrentMode == modes.CUBE){
            //set all positions to lower
            double[] newPositions = CubeLowerPosition.returnPositions();
            liftPosition = newPositions[0];
            armPosition = newPositions[1];
            wristPosition = newPositions[2];
        }

        //middle position
        if (source == midGoal && midGoal.getValue() && CurrentMode == modes.CONE){
            //set all positions to middle
            double[] newPositions = ConeMiddlePosition.returnPositions();
            liftPosition = newPositions[0];
            armPosition = newPositions[1];
            wristPosition = newPositions[2];
        } else if (source == midGoal && midGoal.getValue() && CurrentMode == modes.CUBE){
            //set all positions to middle
            double[] newPositions = CubeMiddlePosition.returnPositions();
            liftPosition = newPositions[0];
            armPosition = newPositions[1];
            wristPosition = newPositions[2];
        }

        //high position
        if (source == highGoal && highGoal.getValue() && CurrentMode == modes.CONE){
            //set all positions to high
            double[] newPositions = ConeHighPosition.returnPositions();
            liftPosition = newPositions[0];
            armPosition = newPositions[1];
            wristPosition = newPositions[2];
        }else if (source == highGoal && highGoal.getValue() && CurrentMode == modes.CUBE){
            //set all positions to high
            double[] newPositions = CubeHighPosition.returnPositions();
            liftPosition = newPositions[0];
            armPosition = newPositions[1];
            wristPosition = newPositions[2];
        }

        if (source == station && station.getValue()){
            //set all positions to station
            double[] newPositions = StationPosition.returnPositions();
            liftPosition = newPositions[0];
            armPosition = newPositions[1];
            wristPosition = newPositions[2];
        }
    }

    @Override
    public String getName() {
        return "MasterControlProgram";
    }

    @Override
    public void selfTest() {
    }
}