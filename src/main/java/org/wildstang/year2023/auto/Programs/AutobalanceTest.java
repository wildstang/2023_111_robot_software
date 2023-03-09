package org.wildstang.year2023.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2023.auto.Steps.AutoBalanceStep;
import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.auto.Steps.IntakeOffStep;
import org.wildstang.year2023.auto.Steps.IntakeOnStep;
import org.wildstang.year2023.auto.Steps.OuttakeStep;
import org.wildstang.year2023.auto.Steps.StartOdometryStep;
import org.wildstang.year2023.auto.Steps.SuperGamePieceStep;
import org.wildstang.year2023.auto.Steps.SuperstructureStep;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.superstructure.SuperConts;
import org.wildstang.year2023.subsystems.superstructure.SuperPos;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;
import org.wildstang.year2023.auto.Steps.AutoBalanceStep;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
public class AutobalanceTest extends AutoProgram{

    public void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);
        
        addStep(new SetGyroStep(225.0, swerve));
        addStep(new PathHeadingStep(180.0, swerve));
        addStep(new StartOdometryStep(6.83, 3.77, 225.0, true));
        
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e", new PathConstraints(4, 3)), swerve, true));
        addStep(new AutoBalanceStep());

        //or
        // addStep(new SetGyroStep(180.0, swerve));
        // addStep(new PathHeadingStep(180.0, swerve));
        // addStep(new AutoBalanceStep());
    }
    public String toString(){
        return "autobalance";
    }
    
}
