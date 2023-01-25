package org.wildstang.year2023.subsystems.masterControlProgram;

import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
//import org.wildstang.hardware.roborio.outputs.WsPhoenix;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//custom
import org.wildstang.year2023.subsystems.arm.arm;
import org.wildstang.year2023.subsystems.arm.lift;
import org.wildstang.year2023.subsystems.arm.wrist;


public class MasterControlProgram implements Subsystem {
    
    //inputs
    private DigitalInput highGoal, midGoal, lowGoal, station, cubeMode, coneMode, front, back;
    private WsJoystickAxis joystick; //b/c i want to 
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
        GROUND_FORWARD(0,0,0);
        double lpos;
        double apos;
        double wpos;
        positions(double liftPos, double armPos, double wristPos){
            
        }
        

    }
    private enum mode{
        CONE,
        CUBE;
    } //true = cone, false = cube

    @Override
    public void init() {
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
        front = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_UP);
        front.addInputListener(this);
        back = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_DOWN);
        back.addInputListener(this);

        /* Front and back should autoset to ground level,
        so that if nothing else is pressed afterwards,
        they can be used for ground intake. */

        armHelper = new arm();
        liftHelper = new lift();
        wristHelper = new wrist();
    }

    @Override
    public void resetState() {
    }

    @Override
    public void update() {
        if ((armhelper.isReady()!)){
            //not at position yet
            armhelper.goToPosition(armPosition);
        }else{
            armHelper.stopMotor();
        }
        if ((lifthelper.isReady()!)){
            //not at position yet
            lifthelper.goToPosition(armPosition);
        }else{
            liftHelper.stopMotor();
        }
        if ((wristhelper.isReady()!)){
            //not at position yet
            wristhelper.goToPosition(armPosition);
        }else{
            wristHelper.stopMotor();
        }

        
    }

    @Override
    public void inputUpdate(Input source) {

        //control configeration
        //manipulator
        //LDpad switch to cone mode
        //RDpad switch to cube mode 
        //UpDpad switch to front mode
        //DownDpad switch to back mode

        
    }

    @Override
    public String getName() {
        return "MasterControlProgram";
    }

    @Override
    public void selfTest() {
    }
}