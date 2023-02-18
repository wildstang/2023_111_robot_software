package org.wildstang.framework.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;

import com.pathplanner.lib.PathPlannerTrajectory;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwervePathFollowerStep extends AutoStep {

    private static final double mToIn = 30.3701;
    private SwerveDriveTemplate m_drive;
    private PathPlannerTrajectory pathData;
    private boolean isBlue;

    private Timer timer;

    /** Sets the robot to track a new path
     * finishes after all values have been read to robot
     * @param pathData double[][] that contains path, should be from \frc\paths
     * @param drive the swerveDrive subsystem
     */
    public SwervePathFollowerStep(PathPlannerTrajectory pathData, SwerveDriveTemplate drive, boolean isBlue) {
        this.pathData = pathData;
        m_drive = drive;
        this.isBlue = isBlue;
        timer = new Timer();
    }

    @Override
    public void initialize() {
        //start path
        m_drive.resetDriveEncoders();
        m_drive.setToAuto();
        timer.start();
    }

    @Override
    public void update() {
        if (timer.get() >= pathData.getTotalTimeSeconds()) {
            m_drive.setAutoValues(0, -pathData.getEndState().poseMeters.getRotation().getDegrees());
            setFinished();
        } else {
            SmartDashboard.putNumber("Auto Time", timer.get());
            //update values the robot is tracking to
            m_drive.setAutoValues( getVelocity(),getHeading());
            }
    }

    @Override
    public String toString() {
        return "Swerve Path Follower";
    }

    public double getVelocity(){
        return pathData.sample(timer.get()).velocityMetersPerSecond * mToIn;
    }
    public double getHeading(){
        if (isBlue) return (-pathData.sample(timer.get()).poseMeters.getRotation().getDegrees() + 360)%360;
        else return (pathData.sample(timer.get()).poseMeters.getRotation().getDegrees()+360)%360;
    }
}
