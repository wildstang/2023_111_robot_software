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
import org.wildstang.year2023.auto.Steps.SuperGamePieceStep;
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
        addStep(new StartOdometryStep(1.78, 4.96, 180.0, color));
        addStep(new AutoStepDelay(1000));
        addStep(new OuttakeStep());
        addStep(new AutoStepDelay(300));
        
        //grab first game piece
        addStep(new SuperstructureStep(SuperPos.INTAKE_BACK));
        addStep(new IntakeOnStep());
        //AutoParallelStepGroup group3 = new AutoParallelStepGroup();
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 A", new PathConstraints(4, 3)),
            swerve, color));
        //AutoSerialStepGroup group3A = new AutoSerialStepGroup();
        //group3A.addStep(new AutoStepDelay(200));
        //group3A.addStep(new IntakeOffStep());
        //group3A.addStep(new AutoStepDelay(900));
        //group3A.addStep(new IntakeOnStep());
        //group3A.addStep(new SuperstructureStep(SuperPos.INTAKE_BACK));
        // group3.addStep(group3A);
        // addStep(group3);

        //move and score first pickup piece
        addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        addStep(new IntakeOffStep());
        AutoParallelStepGroup group4 = new AutoParallelStepGroup();
        group4.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 B", new PathConstraints(4, 3)),
            swerve, color));
        AutoSerialStepGroup group4A = new AutoSerialStepGroup();
        group4A.addStep(new AutoStepDelay(1600));
        group4A.addStep(new SuperstructureStep(SuperPos.SCORE_HIGH));
        group4.addStep(group4A);
        addStep(group4);

        addStep(new OuttakeStep());
        addStep(new AutoStepDelay(300));

        //grab second game piece
        addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        //addStep(new SuperGamePieceStep(SuperConts.CUBE));
        //addStep(new PathHeadingStep(225, swerve));
        //addStep(new IntakeOnStep());
        //addStep(new SuperstructureStep(SuperPos.INTAKE_BACK_LOW));
       AutoParallelStepGroup group6 = new AutoParallelStepGroup();
        group6.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 C", new PathConstraints(4, 3)),
            swerve, color));
        AutoSerialStepGroup group6A = new AutoSerialStepGroup();
        group6A.addStep(new AutoStepDelay(200));
        group6A.addStep(new IntakeOffStep());
        group6A.addStep(new AutoStepDelay(1000));
        addStep(new PathHeadingStep(225, swerve));
        group6A.addStep(new IntakeOnStep());
        group6A.addStep(new SuperstructureStep(SuperPos.INTAKE_BACK_LOW));
        group6.addStep(group6A);
        addStep(group6);
        
        //score second pickup piece
        addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        addStep(new IntakeOffStep());
        addStep(new PathHeadingStep(180, swerve));
        AutoParallelStepGroup group7 = new AutoParallelStepGroup();
        group7.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e or 3+1 D", new PathConstraints(4, 3)),
            swerve, color));
        AutoSerialStepGroup group7A = new AutoSerialStepGroup();
        group7A.addStep(new AutoStepDelay(2100));
        group7A.addStep(new SuperstructureStep(SuperPos.SCORE_HIGH));
        group7.addStep(group7A);
        addStep(group7);

        addStep(new OuttakeStep());
        addStep(new AutoStepDelay(200));

        addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top 3+e E", new PathConstraints(4, 3)),
        swerve, color));

        //autobalance step

    }

    public String toString(){
        return "BLUE Top_3pE";
    }
}