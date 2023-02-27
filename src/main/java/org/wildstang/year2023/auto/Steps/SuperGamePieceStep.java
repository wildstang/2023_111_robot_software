package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.superstructure.Superstructure;

public class SuperGamePieceStep extends AutoStep{

    private Superstructure superStructure;
    private boolean gamepiece;

    public SuperGamePieceStep(boolean piece){
        this.gamepiece = piece;
    }
    public void update(){
        superStructure.setGamepiece(gamepiece);
        this.setFinished();

    }
    public void initialize(){
        superStructure = (Superstructure) Core.getSubsystemManager().getSubsystem(WSSubsystems.SUPERSTRUCTURE);

    }
    public String toString(){
        return "Game Piece";
    }
    
}
