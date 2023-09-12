package org.wildstang.year2023.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.auto.Steps.StartOdometryStep;
import org.wildstang.year2023.robot.WsSubsystems;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;

public class Testprogram extends AutoProgram{
    
    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new SetGyroStep(180.0, swerve));
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new PathHeadingStep(180.0, swerve));
        addStep(group1);
        addStep(new StartOdometryStep(3.0, 5.0, 180.0, true));
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Test_100", new PathConstraints(4, 3)),
             swerve, true));
    }

    public String toString(){
        return "Test Program";
    }
}
