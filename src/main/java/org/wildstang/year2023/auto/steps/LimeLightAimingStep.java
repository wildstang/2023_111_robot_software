package org.wildstang.year2023.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.targeting.AimHelper;

import org.wildstang.year2023.subsystems.swerve.SwerveDrive;

public class LimeLightAimingStep extends AutoStep {
    private AimHelper AimHelper;
    private SwerveDrive swerve;
    private Boolean gamePiece;



    /** Set Intake Speed
     * 
     */
    public LimeLightAimingStep(String gamePiece){
        if (gamePiece == "CONE"){
            this.gamePiece = true;}
        else if (gamePiece == "CUBE"){
            this.gamePiece = false;
        }
    }

    @Override
    public void update(){
        swerve.setAiming(this.gamePiece);
        this.setFinished(true);
    }
    
    @Override
    public String toString() {
        return "Lime Light Aiming Step";
    }
    
    @Override
    public void initialize(){
        AimHelper = (AimHelper) Core.getSubsystemManager().getSubsystem(WSSubsystems.AIM_HELPER);
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);
    }
}