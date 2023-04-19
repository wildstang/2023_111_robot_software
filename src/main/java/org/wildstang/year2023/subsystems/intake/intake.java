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
import edu.wpi.first.wpilibj.Timer;
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
    private DigitalInput driverLB, driverRB, operatorLB, operatorRB, high, mid, low;

    private Timer timer = new Timer();
    

    // states
    private static final double ingestSpeed = 1;
    private static final double expelSpeedCone = -1;
    private static final double expelSpeedCube = -0.25;
    private static final double expelSpeedLow = -0.22;
    private static final double holdingSpeed = 0.2;
    private static final double deadband = 0.1;
    private static final double motorVelocityDeadband = .1;

    private double speed, in, out;

    private boolean isHolding, gamepiece, isLow;

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
        high = (DigitalInput) WSInputs.MANIPULATOR_FACE_UP.get();
        high.addInputListener(this);
        mid = (DigitalInput) WSInputs.MANIPULATOR_FACE_RIGHT.get();
        mid.addInputListener(this);
        low = (DigitalInput) WSInputs.MANIPULATOR_FACE_DOWN.get();
        low.addInputListener(this);

        resetState();
        timer.start();
    }

    @Override
    public void resetState() {
        speed = 0;
        isHolding = false;
        isLow = false;
        gamepiece = SuperConts.CONE;
    }

    @Override
    public void update() {
        //for scoring low - start slowly to get rid of cube, then get faster to get rid of cone
        //this is because the "gamepiece" is not accurate for low, since we select the node based on game piece
        //i.e. for scoring a hybrid node infront of a cube node, the robot will assume we have a cube, but we might
        //  have a cone
        if (timer.hasElapsed(0.1) && speed == expelSpeedLow) speed = expelSpeedCone;
        intakeMotor.setValue(speed);
    }

    @Override
    public void inputUpdate(Input source) {
        //get operator intake overrides
        in = Math.abs(ingest.getValue());
        out = Math.abs(expel.getValue());

        //determine whether it's low or mid/high scoring
        if (source == low && low.getValue()) isLow = true;
        if ((source == mid && mid.getValue()) || (source == high && high.getValue())) isLow = false;

        //get gamepiece
        if (operatorLB.getValue()) gamepiece = SuperConts.CONE;
        if (operatorRB.getValue()) gamepiece = SuperConts.CUBE;

        if (out > deadband && out > in){
            speed = expelSpeedCone;
            isHolding = false;
        } else if ((in > deadband && in >= out) || (driverLB.getValue() || driverRB.getValue())) {
            speed = ingestSpeed;
            isHolding = true;
        } else if (Math.abs(driverLT.getValue()) > deadband && Math.abs(driverRT.getValue()) > deadband) {
            //three speeds - a cube mid/high, a cone mid/high, and a low expel speed
            speed = gamepiece ? (!isLow ? expelSpeedCone : expelSpeedLow) : (!isLow ? expelSpeedCube : expelSpeedLow);
            isHolding = false;
            timer.reset();
        } else {
            //small holding speed if we have a gamepiece, to keep it in the claw
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
    //for auto control
    public void intakeOn(){
        speed = ingestSpeed;
        isHolding = true;
    }
    //for auto control
    public void intakeExpel(boolean gamePieceType){
        if (gamePieceType == SuperConts.CONE){
            speed = expelSpeedCone;
        } else {
            speed = expelSpeedCube;
        }
        isHolding = false;
    }
    //for auto control
    public void intakeOff(){
        speed = (isHolding? 1.0 : 0.0) * holdingSpeed;
    }
    //for leds
    public boolean hasGrabbed(){
        if (speed < 0.5) return false;
        return intakeMotor.getController().getOutputCurrent() > 15.0;
    }
}