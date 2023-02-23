package org.wildstang.year2023.auto.Programs;

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
import org.wildstang.year2023.auto.Steps.SuperstructureStep;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.superstructure.SuperPos;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;

public class Blue_Top_3pE extends AutoProgram{
    
    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);

        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new SetGyroStep(180.0, swerve));
        group1.addStep(new PathHeadingStep(180.0, swerve));
        group1.addStep(new StartOdometryStep(1.78, 4.96, 180.0));
        group1.addStep(new SuperstructureStep(SuperPos.SCORE_HIGH));
        group1.addStep(new AutoStepDelay(600));
        addStep(group1);

        AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        group2.addStep(new AutoStepDelay(200));
        group2.addStep(new OuttakeStep());
        addStep(group2);
        
        AutoParallelStepGroup group3 = new AutoParallelStepGroup();
        group3.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        group3.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 A", new PathConstraints(4, 3)),
            swerve, true));
        AutoSerialStepGroup group3A = new AutoSerialStepGroup();
        group3A.addStep(new AutoStepDelay(200));
        AutoParallelStepGroup group3B = new AutoParallelStepGroup();
        group3B.addStep(new AutoStepDelay(1700));
        group3B.addStep(new IntakeOffStep());
        group3A.addStep(group3B);
        AutoParallelStepGroup group3C = new AutoParallelStepGroup();
        group3C.addStep(new IntakeOnStep());
        group3C.addStep(new SuperstructureStep(SuperPos.INTAKE_BACK));
        group3A.addStep(group3C);
        group3.addStep(group3A);
        addStep(group3);

        AutoParallelStepGroup group4 = new AutoParallelStepGroup();
        group4.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        group4.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 B", new PathConstraints(4, 3)),
            swerve, true));
        group4.addStep(new IntakeOffStep());
        AutoSerialStepGroup group4A = new AutoSerialStepGroup();
        group4A.addStep(new AutoStepDelay(2000));
        group4A.addStep(new SuperstructureStep(SuperPos.SCORE_HIGH));
        group4.addStep(group4A);
        addStep(group4);

        AutoParallelStepGroup group5 = new AutoParallelStepGroup();
        group5.addStep(new AutoStepDelay(200));
        group5.addStep(new OuttakeStep());
        addStep(group5);

        AutoParallelStepGroup group6 = new AutoParallelStepGroup();
        group6.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        group6.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 C", new PathConstraints(4, 3)),
            swerve, true));
        AutoSerialStepGroup group6A = new AutoSerialStepGroup();
        group6A.addStep(new AutoStepDelay(200));
        AutoParallelStepGroup group6B = new AutoParallelStepGroup();
        group6B.addStep(new AutoStepDelay(1900));
        group6B.addStep(new IntakeOffStep());
        group6A.addStep(group6B);
        AutoParallelStepGroup group6C = new AutoParallelStepGroup();
        group6C.addStep(new IntakeOnStep());
        group6C.addStep(new SuperstructureStep(SuperPos.INTAKE_BACK));
        group6A.addStep(group6C);
        group6.addStep(group6A);
        addStep(group6);
        
        AutoParallelStepGroup group7 = new AutoParallelStepGroup();
        group7.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        group7.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 D", new PathConstraints(4, 3)),
            swerve, true));
        group7.addStep(new IntakeOffStep());
        AutoSerialStepGroup group7A = new AutoSerialStepGroup();
        group7A.addStep(new AutoStepDelay(2100));
        group7A.addStep(new SuperstructureStep(SuperPos.SCORE_HIGH));
        group7.addStep(group7A);
        addStep(group7);

        AutoParallelStepGroup group8 = new AutoParallelStepGroup();
        group8.addStep(new AutoStepDelay(200));
        group8.addStep(new OuttakeStep());
        addStep(group8);

        AutoParallelStepGroup group9 = new AutoParallelStepGroup();
        group9.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        group9.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e E", new PathConstraints(4, 3)),
        swerve, true));

        //autobalance step

    }

    public String toString(){
        return "BLUE Top_3pE";
    }
}