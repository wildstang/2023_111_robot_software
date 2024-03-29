package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.led.LedController;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class StartOdometryStep extends AutoStep{

    private double x, y, heading;
    private SwerveDrive swerve;
    private LedController leds;
    private boolean color;//true for blue, false for red

    public StartOdometryStep(double X, double Y, double pathHeading, boolean allianceColor){
        x = X;
        y = Y;
        heading = pathHeading;
        color = allianceColor;
    }
    public void update(){
        if (color){
            swerve.setOdo(new Pose2d(new Translation2d(x, y), new Rotation2d(Math.toRadians(360.0-heading))));
        } else {
            swerve.setOdo(new Pose2d(new Translation2d(x, 8.016-y), new Rotation2d(Math.toRadians(360.0-heading))));
        }
        leds.turnOff();
        this.setFinished();
    }
    public void initialize(){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);
        leds = (LedController) Core.getSubsystemManager().getSubsystem(WSSubsystems.LED);
    }
    public String toString(){
        return "Start Odometry";
    }
    
}
