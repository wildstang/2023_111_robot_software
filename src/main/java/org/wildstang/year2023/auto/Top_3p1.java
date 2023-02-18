package org.wildstang.year2023.auto;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;

public class Top_3p1 extends AutoProgram{
    
    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);

        addStep(new SetGyroStep(180, swerve));
        addStep(new PathHeadingStep(180.0, swerve));
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 A", new PathConstraints(4, 3)),
            swerve, false));
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 B", new PathConstraints(4, 3)),
            swerve, false));
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 C", new PathConstraints(4, 3)),
             swerve, false));
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 D", new PathConstraints(4, 3)),
             swerve, false));
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+1 E", new PathConstraints(4, 3)),
             swerve, false));
    }

    public String toString(){
        return "Top_3p1";
    }
}