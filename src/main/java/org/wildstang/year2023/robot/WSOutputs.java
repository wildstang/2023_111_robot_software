package org.wildstang.year2023.robot;

// expand this and edit if trouble with Ws
import org.wildstang.framework.core.Core;
import org.wildstang.framework.core.Outputs;
import org.wildstang.framework.hardware.OutputConfig;
import org.wildstang.framework.io.outputs.Output;
import org.wildstang.hardware.roborio.outputs.config.WsServoConfig;
import org.wildstang.hardware.roborio.outputs.config.WsPhoenixConfig;
import org.wildstang.hardware.roborio.outputs.config.WsRemoteAnalogOutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsI2COutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsMotorControllers;
import org.wildstang.hardware.roborio.outputs.config.WsDigitalOutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSparkMaxConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSparkMaxFollowerConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSolenoidConfig;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

/**
 * Output mappings are stored here.
 * Below each Motor, PWM, Digital Output, Solenoid, and Relay is enumerated with their appropriated IDs.
 * The enumeration includes a name, output type, and output config object.
 */
public enum WSOutputs implements Outputs {

    // ********************************
    // PWM Outputs
    // ********************************
    // ---------------------------------
    // Motors
    // ---------------------------------
    ARM_ONE("Arm", new WsSparkMaxConfig(CANConstants.ARM, true)),
    //rename this String, and it shouldn't be a follower, but a normal motor

    DRIVE1("Module 1 Drive Motor", new WsSparkMaxConfig(CANConstants.DRIVE1, true)),
    ANGLE1("Module 1 Angle Motor", new WsSparkMaxConfig(CANConstants.ANGLE1, true)),
    DRIVE2("Module 2 Drive Motor", new WsSparkMaxConfig(CANConstants.DRIVE2, true)),
    ANGLE2("Module 2 Angle Motor", new WsSparkMaxConfig(CANConstants.ANGLE2, true)),
    DRIVE3("Module 3 Drive Motor", new WsSparkMaxConfig(CANConstants.DRIVE3, true)),
    ANGLE3("Module 3 Angle Motor", new WsSparkMaxConfig(CANConstants.ANGLE3, true)),
    DRIVE4("Module 4 Drive Motor", new WsSparkMaxConfig(CANConstants.DRIVE4, true)),
    ANGLE4("Module 4 Angle Motor", new WsSparkMaxConfig(CANConstants.ANGLE4, true)),
    
    LIFT_DRIVER("Lift Driver", new WsSparkMaxConfig(CANConstants.LIFT, true)),
    LIFT_Follower("Lift Follower", new WsSparkMaxFollowerConfig("Lift Driver", CANConstants.LIFT_FOLLOWER, true, true)),
    WRIST("Wrist", new WsSparkMaxConfig(CANConstants.WRIST, true)),
    INTAKE_MOTOR("Intake Motor", new WsSparkMaxConfig(CANConstants.INTAKE, true)),
    // ---------------------------------
    // Servos
    // ---------------------------------
    //TEST_SERVO("Test Servo", new WsServoConfig(0, 0)),

    // ********************************
    // DIO Outputs
    // ********************************
    //DIO_O_0("Test Digital Output 0", new WsDigitalOutputConfig(0, true)), // Channel 0, Initially Low

    // ********************************
    // Solenoids
    // ********************************
    //TEST_SOLENOID("Test Solenoid", new WsSolenoidConfig(PneumaticsModuleType.REVPH, 0, false)),
    
    // ********************************
    // Relays
    // ********************************

    // ********************************
    // NetworkTables
    // ********************************
    // LL_MODE("camMode", new WsRemoteAnalogOutputConfig("limelight", 0)),
    // LL_LEDS("ledMode", new WsRemoteAnalogOutputConfig("limelight", 0)),

    // ********************************
    // Others ...
    // ********************************
    LED("LEDs", new WsI2COutputConfig(I2C.Port.kMXP, 0x10));

    ; // end of enum

    /**
     * Do not modify below code, provides template for enumerations.
     * We would like to have a super class for this structure, however,
     * Java does not support enums extending classes.
     */

    private String m_name;
    private OutputConfig m_config;

    /**
     * Initialize a new Output.
     * @param p_name Name, must match that in class to prevent errors.
     * @param p_config Corresponding configuration for OutputType.
     */
    WSOutputs(String p_name, OutputConfig p_config) {
        m_name = p_name;
        m_config = p_config;
    }

    /**
     * Returns the name mapped to the Output.
     * @return Name mapped to the Output.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the config of Output for the enumeration.
     * @return OutputConfig of enumeration.
     */
    public OutputConfig getConfig() {
        return m_config;
    }

    /**
     * Returns the actual Output object from the OutputManager
     * @return The corresponding output.
     */
    public Output get() {
        return Core.getOutputManager().getOutput(this);
    }
}