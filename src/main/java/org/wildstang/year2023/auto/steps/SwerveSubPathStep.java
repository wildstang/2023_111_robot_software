package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;

import com.pathplanner.lib.PathPlannerTrajectory;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveSubPathStep extends AutoStep {

    //litterally just swervepathfollowstep without the stop at the end.
    private static final double mToIn = 30.3701;
    private SwerveDriveTemplate m_drive;
    private PathPlannerTrajectory pathData;
    private boolean isBlue;

    private double xOffset, yOffset;
    private Pose2d localAutoPose, localRobotPose;

    private Timer timer;

    /** Sets the robot to track a new path
     * finishes after all values have been read to robot
     * @param pathData double[][] that contains path, should be from \frc\paths
     * @param drive the swerveDrive subsystem
     */
    public SwerveSubPathStep(PathPlannerTrajectory pathData, SwerveDriveTemplate drive, boolean isBlue) {
        this.pathData = pathData;
        m_drive = drive;
        this.isBlue = isBlue;
        timer = new Timer();
    }

    @Override
    public void initialize() {
        //start path
        m_drive.setToAuto();
        timer.start();
    }

    @Override
    public void update() {
        if (timer.get() >= pathData.getTotalTimeSeconds()) {
            //m_drive.setAutoValues(0, -pathData.getEndState().poseMeters.getRotation().getDegrees(),0,0);
            //keep driving dont stop
            setFinished();
        } else {
            SmartDashboard.putNumber("Auto Time", timer.get());
            //update values the robot is tracking to

            localRobotPose = m_drive.returnPose();
            localAutoPose = pathData.sample(timer.get()).poseMeters;
            yOffset = -(localRobotPose.getX() - localAutoPose.getX());
            if (isBlue){
                xOffset = localRobotPose.getY() - localAutoPose.getY();
            } else {
                xOffset = localRobotPose.getY() - (8.016 - localAutoPose.getY());
            }

            m_drive.setAutoValues( getVelocity(),getHeading(), xOffset, yOffset);
            }
    }

    @Override
    public String toString() {
        return "Swerve (sub)Path Follower";
    }

    public double getVelocity(){
        return pathData.sample(timer.get()).velocityMetersPerSecond * mToIn;
    }
    public double getHeading(){
        if (isBlue) return (-pathData.sample(timer.get()).poseMeters.getRotation().getDegrees() + 360)%360;
        else return (pathData.sample(timer.get()).poseMeters.getRotation().getDegrees()+360)%360;
    }
}
