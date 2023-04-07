package org.wildstang.year2023.subsystems.intake;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.year2023.robot.WSInputs;

import org.wildstang.framework.subsystems.Subsystem;

import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.year2023.robot.WSOutputs;
import org.wildstang.year2023.subsystems.superstructure.SuperConts;


import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;

/**
 * Sample Subsystem that controls a motor with a joystick.
 * @author Liam
 * @author James
 */

public class intake implements Subsystem {
    // inputs

    // outputs
    private WsSparkMax intakeMotor;
    private AnalogInput ingest, expel, driverLT, driverRT;
    private DigitalInput driverLB, driverRB, operatorLB, operatorRB;
    

    // states
    private static final double ingestSpeed = 1;
    private static final double expelSpeedCone = -1;
    private static final double expelSpeedCube = -0.3;
    private static final double holdingSpeed = 0.2;
    private static final double deadband = 0.1;
    private static final double motorVelocityDeadband = .1;

    private double speed, in, out;

    private boolean isHolding, gamepiece;

    // private class HapticFeedback{
    //     //just for testing, would likely fit better in roborio inputs
    //     // private final XboxController m_hid1 = new XboxController(1 /* Manipulator */);
    //     private final XboxController m_hid2 = new XboxController(0 /* Driver */);
    
    //     private void WhenAutoPickupFinished(){
    //         //if manipulator trigger down and velocity is deadband
    //         if (hasGrabbed()){
    //             // m_hid1.setRumble(RumbleType.kRightRumble, .5);
    //             // m_hid1.setRumble(RumbleType.kLeftRumble, .5);
    //             m_hid2.setRumble(RumbleType.kRightRumble, .5);
    //             m_hid2.setRumble(RumbleType.kLeftRumble, .5);
    //         } else {
    //             // m_hid1.setRumble(RumbleType.kRightRumble, 0);
    //             // m_hid1.setRumble(RumbleType.kLeftRumble, 0);
    //             m_hid2.setRumble(RumbleType.kRightRumble, 0);
    //             m_hid2.setRumble(RumbleType.kLeftRumble, 0);
    //         }
    //     }
    // }

    @Override
    public void init() {
        intakeMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.INTAKE_MOTOR);
        intakeMotor.setCurrentLimit(30, 30, 0);
        ingest = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_TRIGGER);
        ingest.addInputListener(this);
        expel = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_TRIGGER);
        expel.addInputListener(this);
        driverLT = (AnalogInput) WSInputs.DRIVER_LEFT_TRIGGER.get();
        driverLT.addInputListener(this);
        driverRT = (AnalogInput) WSInputs.DRIVER_RIGHT_TRIGGER.get();
        driverRT.addInputListener(this);
        driverLB = (DigitalInput) WSInputs.DRIVER_LEFT_SHOULDER.get();
        driverLB.addInputListener(this);
        driverRB = (DigitalInput) WSInputs.DRIVER_RIGHT_SHOULDER.get();
        driverRB.addInputListener(this);
        operatorLB = (DigitalInput) WSInputs.MANIPULATOR_LEFT_SHOULDER.get();
        operatorLB.addInputListener(this);
        operatorRB = (DigitalInput) WSInputs.MANIPULATOR_RIGHT_SHOULDER.get();
        operatorRB.addInputListener(this);

        //new HapticFeedback().WhenAutoPickupFinished();

        resetState();
    }

    @Override
    public void resetState() {
        speed = 0;
        isHolding = false;
        gamepiece = SuperConts.CONE;
    }

    @Override
    public void update() {
        intakeMotor.setValue(speed);
    }

    @Override
    public void inputUpdate(Input source) {
        in = Math.abs(ingest.getValue());
        out = Math.abs(expel.getValue());
        if (operatorLB.getValue()) gamepiece = SuperConts.CONE;
        if (operatorRB.getValue()) gamepiece = SuperConts.CUBE;
        if (out > deadband && out > in){
            speed = expelSpeedCone;
            isHolding = false;
        } else if ((in > deadband && in >= out) || (driverLB.getValue() || driverRB.getValue())) {
            speed = ingestSpeed;
            isHolding = true;
        } else if (Math.abs(driverLT.getValue()) > deadband && Math.abs(driverRT.getValue()) > deadband) {
            speed = gamepiece ? expelSpeedCone : expelSpeedCube;
            isHolding = false;
        } else {
            speed = (isHolding? 1.0 : 0.0) * holdingSpeed;
        }
    }

    @Override
    public String getName() {
        return "Intake";
    }

    @Override
    public void selfTest() {
    }
    public void intakeOn(){
        speed = ingestSpeed;
        isHolding = true;
    }
    public void intakeExpel(boolean gamePieceType){
        if (gamePieceType == SuperConts.CONE){
            speed = expelSpeedCone;
        } else {
            speed = expelSpeedCube;
        }
        isHolding = false;
    }
    public void intakeOff(){
        speed = (isHolding? 1.0 : 0.0) * holdingSpeed;
    }
    public boolean hasGrabbed(){
        if (speed < 0.5) return false;
        return intakeMotor.getController().getOutputCurrent() > 15.0;
    }
}