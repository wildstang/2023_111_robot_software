package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;
import org.wildstang.year2023.subsystems.targeting.AimHelper;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class OdometryOnStep extends AutoStep{

    private SwerveDrive swerve;
    private AimHelper limelight;
    private boolean color, on;//true for blue, false for red

    public OdometryOnStep(boolean isOn, boolean isBlue){
        color = isBlue;
        on = isOn;
    }
    public void update(){
        swerve.setAutoOdo(on, color);
        limelight.setGamePiece(false);
        this.setFinished();
    }
    public void initialize(){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);
        limelight = (AimHelper) Core.getSubsystemManager().getSubsystem(WSSubsystems.AIM_HELPER);
    }
    public String toString(){
        return "Odometry On";
    }
    
}
