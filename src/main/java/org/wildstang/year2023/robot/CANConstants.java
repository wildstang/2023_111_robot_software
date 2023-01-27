package org.wildstang.year2023.robot;

/**
 * CAN Constants are stored here.
 * We primarily use CAN to communicate with Talon motor controllers.
 * These constants must correlate with the IDs set in Phoenix Tuner.
 * Official documentation can be found here:
 * https://phoenix-documentation.readthedocs.io/en/latest/ch08_BringUpCAN.html
 */
public final class CANConstants {

    // Replace these examples.
    // While not independently dangerous if implemented these could have unintended effects.
    public static final int[] EXAMPLE_PAIRED_CONTROLLERS    = {1,2};
    public static final int   EXAMPLE_MOTOR_CONTROLLER      = 3;

    //Gyro and CAN sensor values
    public static final int GYRO = 31;

    //swerve constants
    // public static final int ENC1 = 41;
    // public static final int ENC2 = 42;
    // public static final int ENC3 = 43;
    // public static final int ENC4 = 44;
    public static final int DRIVE1 = 11;
    public static final int ANGLE1 = 12;
    public static final int DRIVE2 = 13;
    public static final int ANGLE2 = 14;
    public static final int DRIVE3 = 15;
    public static final int ANGLE3 = 16;
    public static final int DRIVE4 = 17;
    public static final int ANGLE4 = 18;

    public static final int ARM_ONE = 23; //these need fixing.
    public static final int LIFT_DRIVER = 21;
    public static final int LIFT_FOLLOWER = 22;
    public static final int WRIST = 24;
    
}