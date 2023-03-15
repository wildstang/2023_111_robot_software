package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.superstructure.SuperConts;
import org.wildstang.year2023.subsystems.superstructure.Superstructure;
import org.wildstang.year2023.subsystems.targeting.AimHelper;

public class SuperGamePieceStep extends AutoStep{

    private Superstructure superStructure;
    private AimHelper aimHelper;
    private boolean gamepiece;

    public SuperGamePieceStep(boolean piece){
        this.gamepiece = piece;
        if (gamepiece = SuperConts.CONE){
            aimHelper.changePipeline(1);
        } else if (gamepiece = SuperConts.CONE){
            aimHelper.changePipeline(2);
        }
    }
    public void update(){
        superStructure.setGamepiece(gamepiece);
        this.setFinished();

    }
    public void initialize(){
        superStructure = (Superstructure) Core.getSubsystemManager().getSubsystem(WSSubsystems.SUPERSTRUCTURE);
        aimHelper = (AimHelper) Core.getSubsystemManager().getSubsystem(WSSubsystems.AIM_HELPER);

    }
    public String toString(){
        return "Game Piece";
    }
    
}
