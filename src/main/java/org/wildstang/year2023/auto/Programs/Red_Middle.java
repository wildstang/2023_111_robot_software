package org.wildstang.year2023.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.auto.Steps.AutoBalanceStep;
import org.wildstang.year2023.auto.Steps.IntakeOffStep;
import org.wildstang.year2023.auto.Steps.IntakeOnStep;
import org.wildstang.year2023.auto.Steps.OdometryOnStep;
import org.wildstang.year2023.auto.Steps.OuttakeStep;
import org.wildstang.year2023.auto.Steps.StartOdometryStep;
import org.wildstang.year2023.auto.Steps.SuperstructureStep;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.superstructure.SuperPos;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;

public class Red_Middle extends AutoProgram{

    private boolean color = false;//true for blue, false for red

    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);

        //score first piece
        addStep(new SetGyroStep(180.0, swerve));
        addStep(new PathHeadingStep(180, swerve));
        addStep(new SuperstructureStep(SuperPos.SCORE_HIGH));
        addStep(new StartOdometryStep(1.83, 2.19, 180.0, color));
        addStep(new AutoStepDelay(1200));
        addStep(new OuttakeStep());
        addStep(new AutoStepDelay(300));
        addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        addStep(new AutoStepDelay(500));

        //cross and grab first piece
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Middle A", new PathConstraints(1.75, 2.25)),
            swerve, color));
        AutoSerialStepGroup group1A = new AutoSerialStepGroup();
        group1A.addStep(new AutoStepDelay(3000));
        group1A.addStep(new IntakeOnStep());
        group1A.addStep(new SuperstructureStep(SuperPos.INTAKE_BACK_LOW));
        group1.addStep(group1A);
        addStep(group1);

        //return and score
        AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        group2.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Middle B", new PathConstraints(1.75, 2.25)),
            swerve, color));
        AutoSerialStepGroup group2A = new AutoSerialStepGroup();
        group2A.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        group2A.addStep(new IntakeOffStep());
        group2A.addStep(new AutoStepDelay(3000));
        group2A.addStep(new SuperstructureStep(SuperPos.SCORE_HIGH));
        group2A.addStep(new OdometryOnStep(true, color));
        group2.addStep(group2A);
        addStep(group2);
        
        addStep(new OuttakeStep());
        addStep(new AutoStepDelay(200));
        addStep(new OdometryOnStep(false, color));
        addStep(new StartOdometryStep(1.83, 2.75, 180.0, color));
        

        //move and balance
        addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Middle C", new PathConstraints(1.75, 2.25)),
            swerve, color));
        addStep(new AutoBalanceStep());

        // //alt
        // addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        // addStep(new IntakeOffStep());
        // addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Middle B alt", new PathConstraints(1.75, 2.25)),
        //     swerve, color));
        // addStep(new AutoBalanceStep());

    }

    public String toString(){
        return "RED Middle";
    }
    
}
