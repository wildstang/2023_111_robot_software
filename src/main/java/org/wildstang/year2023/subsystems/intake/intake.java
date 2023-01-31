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
    private static final double deadband = 0.05;

    private double speed;
    @Override
    public void init() {

        intakeMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.INTAKE_MOTOR);
        ingest = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_TRIGGER);
        ingest.addInputListener(this);
        expel = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_TRIGGER);
        expel.addInputListener(this);
        speed = 0;
    }

    @Override
    public void resetState() {
        speed = 0;
    }

    @Override
    public void update() {
        intakeMotor.setValue(speed);
    }

    @Override
    public void inputUpdate(Input source) {
    double in = Math.abs(ingest.getValue());
    double out = Math.abs(expel.getValue());
    if (in > deadband && in > out) {
            speed = ingestSpeed * in;
        } else if (out > deadband && out > in) {
            speed = expelSpeed * out;
        } else {
            speed = 0;
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