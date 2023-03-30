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
import org.wildstang.year2023.auto.Steps.OdometryOnStep;
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

public class Red_Bot_3 extends AutoProgram{

    private boolean color = false;//true for blue, false for red
    
    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);

        //score preload
        addStep(new SetGyroStep(180.0, swerve));
        addStep(new SuperstructureStep(SuperPos.SCORE_HIGH));
        addStep(new PathHeadingStep(180.0, swerve));
        addStep(new StartOdometryStep(1.83, .51, 180.0, color));
        addStep(new AutoStepDelay(1200));
        addStep(new OuttakeStep());
        addStep(new AutoStepDelay(300));
        
        //grab first game piece
        addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        addStep(new IntakeOnStep());
        addStep(new AutoStepDelay(500));
        AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        group2.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Bot 3 A", new PathConstraints(2.0, 2.25)),
            swerve, color));
        AutoSerialStepGroup group2A = new AutoSerialStepGroup();
        group2A.addStep(new PathHeadingStep(color ? 150 : 210, swerve));
        group2A.addStep(new AutoStepDelay(2000));
        group2A.addStep(new SuperstructureStep(SuperPos.INTAKE_BACK_LOW));
        group2A.addStep(new PathHeadingStep(180.0, swerve));
        group2.addStep(group2A);
        addStep(group2);

        //move and score first pickup piece
        addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        addStep(new IntakeOffStep());
        AutoParallelStepGroup group4 = new AutoParallelStepGroup();
        group4.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Bot 3 B", new PathConstraints(2.0, 2.25)),
            swerve, color));
        AutoSerialStepGroup group4A = new AutoSerialStepGroup();
        group4A.addStep(new PathHeadingStep(color ? 150 : 210, swerve));
        group4A.addStep(new AutoStepDelay(2000));
        group4A.addStep(new PathHeadingStep(180.0, swerve));
        group4A.addStep(new AutoStepDelay(1000));
        group4A.addStep(new OdometryOnStep(true, color));
        group4.addStep(group4A);
        addStep(group4);

        addStep(new SuperstructureStep(SuperPos.SCORE_HIGH));
        addStep(new AutoStepDelay(1200));
        addStep(new OuttakeStep());
        addStep(new AutoStepDelay(300));
        addStep(new OdometryOnStep(false, color));
        addStep(new StartOdometryStep(1.83, 1.07, 180.0, color));

        //grab second game piece
        addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        AutoParallelStepGroup group6 = new AutoParallelStepGroup();
        group6.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Bot 3 C", new PathConstraints(2.0, 2.25)),
            swerve, color));
        AutoSerialStepGroup group6A = new AutoSerialStepGroup();
        group6A.addStep(new PathHeadingStep(color ? 150 : 210, swerve));
        group6A.addStep(new AutoStepDelay(2000));
        group6A.addStep(new PathHeadingStep(color ? 90.0 : 270.0, swerve));
        group6.addStep(group6A);
        addStep(group6);

    }

    public String toString(){
        return "RED Bot_3";
    }
} 
