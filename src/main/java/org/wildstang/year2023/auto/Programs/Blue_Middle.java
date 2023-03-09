package org.wildstang.year2023.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.auto.Steps.AutoBalanceStep;
import org.wildstang.year2023.auto.Steps.StartOdometryStep;
import org.wildstang.year2023.auto.Steps.SuperstructureStep;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.superstructure.SuperPos;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;

public class Blue_Middle extends AutoProgram{

    private boolean color = true;//true for blue, false for red

    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);

        
        addStep(new SetGyroStep(180.0, swerve));
        addStep(new PathHeadingStep(180, swerve));
        addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        addStep(new StartOdometryStep(1.8, 2.8, 180.0, color));
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Middle A", new PathConstraints(2.8, 2.25)),
            swerve, color));
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Middle B", new PathConstraints(2.8, 2.25)),
            swerve, color));
        addStep(new AutoBalanceStep());

    }

    public String toString(){
        return "BLUE Middle";
    }
    
}
