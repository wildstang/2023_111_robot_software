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
    private DigitalInput ingest, expel;
    

    // states
    private static final double ingestSpeed = 1;
    private static final double expelSpeed = -1;

    private double speed;
    @Override
    public void init() {

        intakeMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.INTAKE_MOTOR);
        ingest = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_SHOULDER);
        ingest.addInputListener(this);
        expel = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_SHOULDER);
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
        if (ingest.getValue()) {
            speed = ingestSpeed;
        } else if (expel.getValue()) {
            speed = expelSpeed;
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