package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class StartOdometryStep extends AutoStep{

    private double x, y, heading;
    private SwerveDrive swerve;
    private final double inToM = 0.0254;

    public StartOdometryStep(double X, double Y, double pathHeading){
        x = X;
        y = Y;
        heading = pathHeading;
    }
    public void update(){
        swerve.setOdo(new Pose2d(new Translation2d(x*inToM, y*inToM), new Rotation2d(Math.toRadians(360.0-heading))));
    }
    public void initialize(){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);
    }
    public String toString(){
        return "Start Odometry";
    }
    
}
