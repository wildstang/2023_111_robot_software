package org.wildstang.year2023.subsystems.targeting; 

public class LimeConsts {

    public double VERTICAL_APRILTAG_DISTANCE = 20.0; 

    public double HORIZONTAL_APRILTAG_DISTANCE = 22.0; 

    public double VERT_AUTOAIM_P = 0.01;

    public double HORI_AUTOAIM_P = 0.01;

    public double OFFSET_HORIZONTAL = 10;

    public double OFFSET_VERTICAL = -5;

    public double[] APRILTAG_ABS_OFFSET_X = {1.0275, 1.0275, 1.0275, 0.362, 0.362, 1.0275, 1.0275, 1.0275}; 
    public double[] APRILTAG_ABS_OFFSET_Y = {6.938,  5.268,  3.592,  1.266, 6.75,  4.425,  2.75,   1.072}; 
    
    public final boolean CONE = true;
    public final boolean CUBE = false;
}