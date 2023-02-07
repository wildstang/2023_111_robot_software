package org.wildstang.year2023.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;
import org.wildstang.year2023.robot.WSSubsystems;

import org.wildstang.year2023.auto.steps.IntakeStep;
import org.wildstang.year2023.auto.steps.MasterControlProgramStep;
import org.wildstang.year2023.auto.steps.AutoBalancingStep;
import org.wildstang.year2023.auto.steps.LimeLightAimingStep;

import com.pathplanner.lib.*;

public class oH1GrabBalance extends AutoProgram{

    private SwerveDrive swerve;

    @Override
    protected void defineSteps() {
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);
        
        // AutoParallelStepGroup setUp = new AutoParallelStepGroup();
        // setUp.addStep(IntakeStep(1));
        // addStep(setUp);

        AutoParallelStepGroup scoreLowCone = new AutoParallelStepGroup();
        scoreLowCone.addStep(new IntakeStep(-1));
        scoreLowCone.addStep(new MasterControlProgramStep("CONE_LOW_FORWARD"));
        scoreLowCone.addStep(new SetGyroStep(-180, swerve));
        scoreLowCone.addStep(new LimeLightAimingStep("AprilTag"));
        addStep(scoreLowCone);

        addStep(new AutoStepDelay(500));

        AutoParallelStepGroup drivePart1 = new AutoParallelStepGroup();
        drivePart1.addStep(new MasterControlProgramStep("HOLDING"));
        drivePart1.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("frc.paths.deploy.pathplanner.oH1GrabBalance", new PathConstraints(4, 3)), swerve));
        addStep(drivePart1);

        addStep(new AutoStepDelay(4000));

        AutoParallelStepGroup pickUpLowCone = new AutoParallelStepGroup();
        pickUpLowCone.addStep(new MasterControlProgramStep("CONE_LOW_FORWARD"));
        pickUpLowCone.addStep(new IntakeStep(1));
        addStep(pickUpLowCone);

        addStep(new AutoStepDelay(500));

        AutoParallelStepGroup drivePart2 = new AutoParallelStepGroup();
        drivePart2.addStep(new MasterControlProgramStep("HOLDING"));
        addStep(drivePart2);

        
        addStep(new AutoStepDelay(1500));

        AutoParallelStepGroup balancing = new AutoParallelStepGroup();


        addStep(balancing);

    }

    @Override
    public String toString() {
        return "oH1 Grab Balance";
    }
}