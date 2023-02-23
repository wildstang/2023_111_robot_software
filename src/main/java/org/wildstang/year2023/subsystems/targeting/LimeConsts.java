package org.wildstang.year2023.subsystems.targeting; 

public class LimeConsts {
    
    public double CAMERA_ANGLE_OFFSET = 40; //angular offset between ground and center of camera in degrees

    public double TARGET_HEIGHT = 104-32; //In inches, between limelight and top of target. Not the height of the target

    public double LIMELIGHT_DISTANCE_OFFSET = 9.0; //12.2 inches to meters, distance from limelight to front of front bumper

    public double DESIRED_APRILTAG_DISTANCE = 10.0; //13.8 inches to m, distance from front bumper to april tag

    public double APRILTAG_HORIZONTAL_OFFSET = 22;//22 in to meters

    public double[] AUTO_ALIGN_PID_X = {0.01,0,0}; // TODO: Needs to be tuned, kP, kI, kD

    public double[] AUTO_ALIGN_PID_Y = {0.01,0,0}; // TODO: Needs to be tuned, kP, kI, kD

    public double[] APRILTAG_ABS_OFFSET_X = {5,10,15,20,20,15,10,5}; //TODO: Change offset constants using onshape measure
    public double[] APRILTAG_ABS_OFFSET_Y = {1.01599,1.01599,1.01599,0.3556,16.84655-0.3556,16.84655-1.01599,16.84655-1.01599,16.84655-1.01599}; //TODO: Change offset constants using onshape measure
    
    public double DEFAULT_POSITION_X = 0;
    public double DEFAULT_POSITION_Y = 0;
    
    public final boolean CONE = true;
    public final boolean CUBE = false;


}