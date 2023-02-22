package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.superstructure.SuperPos;
import org.wildstang.year2023.subsystems.superstructure.Superstructure;

public class SuperstructureStep extends AutoStep{

    private Superstructure superStructure;
    private SuperPos target;

    public SuperstructureStep(SuperPos target){
        this.target = target;
    }
    public void update(){
        superStructure.goToPosition(target);
        this.setFinished();

    }
    public void initialize(){
        superStructure = (Superstructure) Core.getSubsystemManager().getSubsystem(WSSubsystems.SUPERSTRUCTURE);

    }
    public String toString(){
        return "Move superstructure";
    }
    
}
