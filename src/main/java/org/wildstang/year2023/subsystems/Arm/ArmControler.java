package org.wildstang.year2023.subsystems.Arm;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class ArmControler implements Subsystem{
    //Things it does rn
    //rotate base clockwise and counter clockwise
    //track positition

    private DigitalInput Rotate_Clockwise, Rotate_Counter_Clockwise,SpeedUp,SpeedDown;
    private WsSparkMax BaseMotor;
    

    private int TurnDirection;
    private double BaseSpeed = 0.25;



    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        if (Rotate_Clockwise.getValue()){
            TurnDirection = 1;
        }
        else if (Rotate_Counter_Clockwise.getValue()){
            TurnDirection = -1;
        }
        else{
            TurnDirection = 0;
        }

        if (source == SpeedUp && SpeedUp.getValue()){
            BaseSpeed += 0.05;
        }
        if (source == SpeedDown && SpeedUp.getValue()){
            BaseSpeed -= 0.05;
        }
        
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

        //***********exact imputs are tempeary*********
        //A gives positive, Y gives negative, B speed up, X slow down
        Rotate_Clockwise = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_DOWN);
        Rotate_Clockwise.addInputListener(this);
        Rotate_Counter_Clockwise = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_UP);
        Rotate_Counter_Clockwise.addInputListener(this);
        SpeedUp = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_RIGHT);
        SpeedUp.addInputListener(this);
        SpeedDown = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_LEFT);
        SpeedDown.addInputListener(this);

        BaseMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ARM_ONE);

        BaseMotor.setCurrentLimit(25,25,0);
    }

    @Override
    public void selfTest() {        
    }

    @Override
    public void update() {
        //check to see if movement
        if (TurnDirection != 0){
            BaseMotor.setSpeed(BaseSpeed*TurnDirection);
        }else{
            BaseMotor.stop();
        }
        SmartDashboard.putNumber("Arm Position", BaseMotor.getPosition());
        SmartDashboard.putNumber("Arm Speed", BaseSpeed);
    }

    @Override
    public void resetState() {
        TurnDirection = 0;
        
    }

    @Override
    public String getName() {
        return "Arm";
    }
    
}
