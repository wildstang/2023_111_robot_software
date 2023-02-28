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
import org.wildstang.year2023.auto.Steps.SwerveSubPathStep;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.superstructure.SuperConts;
import org.wildstang.year2023.subsystems.superstructure.SuperPos;
import org.wildstang.year2023.subsystems.swerve.SwerveDrive;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;

public class Blue_Top_3pBalance extends AutoProgram{

    private boolean color = true;//true for blue, false for red 

    private boolean isCube = true; //true for true, false for false

    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.SWERVE_DRIVE);
        AutoParallelStepGroup Score1 = new AutoParallelStepGroup();
        Score1.addStep(new SuperstructureStep(SuperPos.SCORE_HIGH));
        Score1.addStep(new SetGyroStep(180.0, swerve));
        Score1.addStep(new PathHeadingStep(180.0, swerve));
        Score1.addStep(new StartOdometryStep(1.78, 4.96, 180.0, color));
        Score1.addStep(new AutoStepDelay(600));

        AutoParallelStepGroup Vomit1 = new AutoParallelStepGroup();
        Vomit1.addStep(new AutoStepDelay(200));
        Vomit1.addStep(new OuttakeStep());
        

        AutoParallelStepGroup StartToWaypoint1 = new AutoParallelStepGroup();
        StartToWaypoint1.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        StartToWaypoint1.addStep(new SwerveSubPathStep(PathPlanner.loadPath("Top to 1", new PathConstraints(4, 3)),
            swerve, color));
        AutoParallelStepGroup StartToWaypoint1AndStop = new AutoParallelStepGroup();
        StartToWaypoint1.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        StartToWaypoint1.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("Top to 1", new PathConstraints(4, 3)),
            swerve, color));

        AutoParallelStepGroup IntakeOnBit = new AutoParallelStepGroup();
        if(isCube){
            IntakeOnBit.addStep(new SuperstructureStep(SuperPos.INTAKE_FRONT));
        }
        else{
            IntakeOnBit.addStep(new SuperstructureStep(SuperPos.INTAKE_FRONT_LOW));
        }
        IntakeOnBit.addStep(new IntakeOnStep());
        IntakeOnBit.addStep(new AutoStepDelay(200));

       /* AutoParallelStepGroup IntakeOffBit = new AutoParallelStepGroup();
        IntakeOffBit.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        IntakeOffBit.addStep(new IntakeOffStep());
        IntakeOffBit.addStep(new AutoStepDelay(200));*/
        
        AutoParallelStepGroup Waypoint1ToStart = new AutoParallelStepGroup();
        StartToWaypoint1.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        StartToWaypoint1.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("1 to Top", new PathConstraints(4, 3)),
            swerve, color));

        AutoParallelStepGroup Score2 = new AutoParallelStepGroup();
        Score2.addStep(new SuperstructureStep(SuperPos.SCORE_MID));
       /* Score1.addStep(new SetGyroStep(180.0, swerve));
        Score1.addStep(new PathHeadingStep(180.0, swerve));
        Score1.addStep(new StartOdometryStep(1.78, 4.96, 180.0, color));*/
        Score2.addStep(new AutoStepDelay(600));

        AutoParallelStepGroup Waypoint1To2 = new AutoParallelStepGroup();
        Waypoint1To2.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        Waypoint1To2.addStep(new SwervePathFollowerStep(PathPlanner.loadPath("1 to 2", new PathConstraints(4, 3)),
            swerve, color));
        AutoParallelStepGroup Waypoint2To1 = new AutoParallelStepGroup();
        Waypoint2To1.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        Waypoint2To1.addStep(new SwerveSubPathStep(PathPlanner.loadPath("2 to 1", new PathConstraints(4, 3)),
            swerve, color));

        AutoParallelStepGroup Score3 = new AutoParallelStepGroup();
        Score2.addStep(new SuperstructureStep(SuperPos.SCORE_LOW));
       /* Score1.addStep(new SetGyroStep(180.0, swerve));
        Score1.addStep(new PathHeadingStep(180.0, swerve));
        Score1.addStep(new StartOdometryStep(1.78, 4.96, 180.0, color));*/
        Score2.addStep(new AutoStepDelay(600));

        AutoParallelStepGroup Waypoint1ToStation = new AutoParallelStepGroup();
        Waypoint1ToStation.addStep(new SuperstructureStep(SuperPos.NEUTRAL));
        Waypoint1ToStation.addStep(new SwerveSubPathStep(PathPlanner.loadPath("1 to Station", new PathConstraints(4, 3)),
            swerve, color));

        addStep(Score1);
        addStep(Vomit1);

        addStep(StartToWaypoint1AndStop);
        addStep(IntakeOnBit);
      //  addStep(IntakeOffBit);
        addStep(Waypoint1ToStart);
        addStep(Score2);
        addStep(Vomit1);

        addStep(StartToWaypoint1);
        addStep(Waypoint1To2);
        addStep(IntakeOnBit);
        addStep(Waypoint2To1);
        addStep(Waypoint1ToStart);
        addStep(Score3);
        addStep(Vomit1);

        addStep(Waypoint1ToStart);
        addStep(Waypoint1ToStation);
        //autobalance here

    }
    public String toString(){
        return "BLUE Top_3pAndBalance";
    }
}