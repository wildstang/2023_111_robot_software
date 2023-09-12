package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WsSubsystems;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;
import org.wildstang.year2023.subsystems.targeting.WsVision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class TagOnStep extends AutoStep{

    private SwerveDrive swerve;
    private WsVision limelight;
    private boolean color, on;//true for blue, false for red

    public TagOnStep(boolean isOn, boolean isBlue){
        color = isBlue;
        on = isOn;
    }
    public void update(){
        swerve.setAutoTag(on, color);
        limelight.setGamePiece(false);
        this.setFinished();
    }
    public void initialize(){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        limelight = (WsVision) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_VISION);
    }
    public String toString(){
        return "Tag Align On";
    }
    
}
