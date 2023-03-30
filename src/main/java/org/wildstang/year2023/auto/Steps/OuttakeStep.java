package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.intake.intake;

public class OuttakeStep extends AutoStep{
    private boolean gamePiece; //true is cone

    private intake intakeSub;

    /**runs the intake backwards 
     * @param gamePieceType the game piece, where true is cone and false is cube
    */
    public OuttakeStep(Boolean gamePieceType){
        gamePiece = gamePieceType;
    }
    public void update(){
        intakeSub.intakeExpel(gamePiece);
        this.setFinished();

    }
    public void initialize(){
        intakeSub = (intake) Core.getSubsystemManager().getSubsystem(WSSubsystems.INTAKE);

    }
    public String toString(){
        return "Outtake Game Piece";
    }
    
}