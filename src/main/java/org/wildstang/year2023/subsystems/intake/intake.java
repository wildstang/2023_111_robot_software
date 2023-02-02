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
    private AnalogInput ingest, expel;
    

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
        intakeMotor.setCurrentLimit(40, 40, 0);
        ingest = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_TRIGGER);
        ingest.addInputListener(this);
        expel = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_TRIGGER);
        expel.addInputListener(this);
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
        if (in > deadband && in >= out) {
            speed = ingestSpeed * in;
            isHolding = true;
        } else if (out > deadband && out > in) {
            speed = expelSpeed * out;
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
}