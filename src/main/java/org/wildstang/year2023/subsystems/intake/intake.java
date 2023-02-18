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

/**
 * Sample Subsystem that controls a motor with a joystick.
 * @author Liam
 */
public class intake implements Subsystem {
    // inputs

    // outputs
    private WsSparkMax intakeMotor;
    private AnalogInput ingest, expel, driverLT, driverRT;
    private DigitalInput driverLB, driverRB;
    

    // states
    private static final double ingestSpeed = 1;
    private static final double expelSpeed = -1;
    private static final double holdingSpeed = 0.2;
    private static final double deadband = 0.1;

    private double speed, in, out;

    private boolean isHolding;
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
        resetState();
    }

    @Override
    public void resetState() {
        speed = 0;
        isHolding = false;
    }

    @Override
    public void update() {
        intakeMotor.setValue(speed);
    }

    @Override
    public void inputUpdate(Input source) {
        in = Math.abs(ingest.getValue());
        out = Math.abs(expel.getValue());
        if ((in > deadband && in >= out) || (driverLB.getValue() || driverRB.getValue())) {
            speed = ingestSpeed * 1;
            isHolding = true;
        } else if ((out > deadband && out > in) || (Math.abs(driverLT.getValue()) > deadband && Math.abs(driverRT.getValue()) > deadband)) {
            speed = expelSpeed * 1;
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
    public void intakeExpel(){
        speed = expelSpeed;
        isHolding = false;
    }
    public void intakeOff(){
        speed = (isHolding? 1.0 : 0.0) * holdingSpeed;
    }
}