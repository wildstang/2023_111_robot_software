package org.wildstang.year2023.subsystems.targeting; 

public class LimeConsts {
    
    public double CAMERA_ANGLE_OFFSET = 40; //angular offset between ground and center of camera in degrees

    public double TARGET_HEIGHT = 104-32; //In inches, between limelight and top of target. Not the height of the target

    public double LIMELIGHT_DISTANCE_OFFSET = 9.0; //12.2 inches to meters, distance from limelight to front of front bumper

    public double DESIRED_APRILTAG_DISTANCE = 10.0; //13.8 inches to m, distance from front bumper to april tag

    public double APRILTAG_HORIZONTAL_OFFSET = 22;//22 in to meters

    public double[] AUTO_ALIGN_PID_X = {0.01,0,0}; // TODO: Needs to be tuned, kP, kI, kD

    public double[] AUTO_ALIGN_PID_Y = {0.01,0,0}; // TODO: Needs to be tuned, kP, kI, kD
    
    public final boolean CONE = true;
    public final boolean CUBE = false;


}