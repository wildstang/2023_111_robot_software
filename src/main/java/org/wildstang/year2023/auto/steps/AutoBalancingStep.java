package org.wildstang.year2023.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;

public class AutoBalancingStep extends AutoStep {
    private SwerveDrive swerve;

    /** Set Intake Speed
     * 
     */
    public AutoBalancingStep(){
        
    }

    @Override
    public void update(){
        swerve.setToAutoBalance();
        this.setFinished(true);
    }
    
    @Override
    public String toString() {
        return "Auto Balancing Step";
    }
    
    @Override
    public void initialize(){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);
    }

}