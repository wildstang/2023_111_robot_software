package org.wildstang.year2023.subsystems.superstructure;

public final class SuperConts {

    public static final boolean CONE = true;
    public static final boolean CUBE = false;

    public static final double LIFTSTAGE = 36.0;

    public static final boolean ARM_ENCODER_DIRECTION = false;
    public static final double ARM_THRESHOLD = 5.0;
    public static final double ARM_SLOW = 0.2;
    public static final double ARM_FAST = 1.0-ARM_SLOW;
    
    public static final boolean WRIST_ENCODER_DIRECTION = false;

    public static final double LIFT_P = 0.1;
    public static final double LIFT_I = 0.0;
    public static final double LIFT_D = 0.0;
    public static final double ARM_P = 0.015;
    public static final double ARM_I = 0.0;
    public static final double ARM_D = 0.0;
    public static final double WRIST_P = 0.1;
    public static final double WRIST_I = 0.0;
    public static final double WRIST_D = 0.1;

    
}
