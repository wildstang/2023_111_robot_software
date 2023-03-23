package org.wildstang.year2023.subsystems.targeting; 

public class LimeConsts {

    public double VERTICAL_APRILTAG_DISTANCE = 37.5; 

    public double HORIZONTAL_APRILTAG_DISTANCE_LEFT = 33.0; 

    public double HORIZONTAL_APRILTAG_DISTANCE_RIGHT = 33.0;

    public double HORIZONTAL_LIMELIGHT_MOUNT = 6.4;

    public double VERT_AUTOAIM_P = 0.01;

    public double HORI_AUTOAIM_P = 0.01;

    public double OFFSET_HORIZONTAL = 10;

    public double OFFSET_VERTICAL = -5;

    public double[] APRILTAG_ABS_OFFSET_X = {1.0275, 1.0275, 1.0275, 0.362, 0.362, 1.0275, 1.0275, 1.0275}; 
    public double[] APRILTAG_ABS_OFFSET_Y = {6.938,  5.268,  3.592,  1.266, 6.75,  4.425,  2.75,   1.072}; 
    
    public final boolean CONE = true;
    public final boolean CUBE = false;
}