package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive.driveType;

public class AutoBalanceStep extends AutoStep{ //TODO: Make an autobalance step
    
    private SwerveDrive swerve;

    public AutoBalanceStep(){
    }

    public void update(){
        swerve.driveState = driveType.BALANCE;// uncomment when charge station exists
        this.setFinished();

    }

    public void initialize(){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);
    }
    public String toString(){
        return "Auto Balance Step";
    }
    
}