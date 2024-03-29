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
import org.wildstang.year2023.auto.Steps.TagOnStep;
import org.wildstang.year2023.auto.Steps.OuttakeStep;
import org.wildstang.year2023.auto.Steps.StartOdometryStep;
import org.wildstang.year2023.auto.Steps.SuperGamePieceStep;
import org.wildstang.year2023.auto.Steps.SuperLaunchingStep;
import org.wildstang.year2023.auto.Steps.SuperstructureStep;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.superstructure.SuperConts;
import org.wildstang.year2023.subsystems.superstructure.SuperPos;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;

public class Blue_Top_3pE extends AutoProgram{

    private boolean color = true;//true for blue, false for red
    
    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);

        //score preload
        addStep(new SetGyroStep(180.0, swerve));
        addStep(new SuperstructureStep(SuperPos.SCORE_HIGH));
        addStep(new PathHeadingStep(180.0, swerve));
        addStep(new StartOdometryStep(1.83, 4.99, 180.0, color));
        addStep(new AutoStepDelay(1200));
        addStep(new OuttakeStep(true));
        addStep(new AutoStepDelay(300));
        
        //grab first game piece
        addStep(new SuperstructureStep(SuperPos.INTAKE_BACK_LOW));
        addStep(new IntakeOnStep());
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 A", new PathConstraints(4, 3)),
            swerve, color));

        //move and score first pickup piece
        addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        addStep(new IntakeOffStep());
        AutoParallelStepGroup group4 = new AutoParallelStepGroup();
        group4.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 B", new PathConstraints(4, 3)),
            swerve, color));
        AutoSerialStepGroup group4A = new AutoSerialStepGroup();
        group4A.addStep(new AutoStepDelay(1600));
        group4A.addStep(new SuperstructureStep(SuperPos.SCORE_HIGH));
        group4A.addStep(new TagOnStep(true, color));
        group4.addStep(group4A);
        addStep(group4);

        addStep(new AutoStepDelay(300));
        addStep(new OuttakeStep(false));
        addStep(new AutoStepDelay(300));
        addStep(new TagOnStep(false, color));
        addStep(new StartOdometryStep(1.83, 4.4,  180.0, color));

        //grab second game piece
        addStep(new SuperstructureStep(SuperPos.NEUTRAL));
       AutoParallelStepGroup group6 = new AutoParallelStepGroup();
        group6.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 C", new PathConstraints(4, 3)),
            swerve, color));
        AutoSerialStepGroup group6A = new AutoSerialStepGroup();
        group6A.addStep(new AutoStepDelay(200));
        group6A.addStep(new IntakeOffStep());
        group6A.addStep(new AutoStepDelay(1000));
        addStep(new PathHeadingStep(color ? 225 : 135, swerve));
        group6A.addStep(new IntakeOnStep());
        group6A.addStep(new SuperstructureStep(SuperPos.INTAKE_BACK_LOW));
        group6.addStep(group6A);
        addStep(group6);

        //balance
        addStep(new SuperstructureStep(SuperPos.PRETHROW));
        addStep(new IntakeOffStep());
        addStep(new PathHeadingStep(180, swerve));
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e", new PathConstraints(2.8, 2.25)), swerve, color));
        //changed stuff below
        addStep(new SuperLaunchingStep(true));
        AutoParallelStepGroup balance = new AutoParallelStepGroup();
        balance.addStep(new AutoBalanceStep());
        AutoSerialStepGroup throwing = new AutoSerialStepGroup();
        throwing.addStep(new AutoStepDelay(400));
        throwing.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        throwing.addStep(new OuttakeStep(true));
        throwing.addStep(new AutoStepDelay(200));
        throwing.addStep(new IntakeOffStep());
        throwing.addStep(new SuperLaunchingStep(false));
        balance.addStep(throwing);
        addStep(balance);

    }

    public String toString(){
        return "BLUE Top_3pE";
    }
}