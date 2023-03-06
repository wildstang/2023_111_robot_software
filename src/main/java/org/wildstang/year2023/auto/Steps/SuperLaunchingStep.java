package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.superstructure.SuperPos;
import org.wildstang.year2023.subsystems.superstructure.Superstructure;

public class SuperLaunchingStep extends AutoStep{

    private Superstructure superStructure;
    private boolean launching;

    public SuperLaunchingStep(boolean isLaunching){
        this.launching = isLaunching;
    }
    public void update(){
        superStructure.autoLaunch(launching);
        this.setFinished();

    }
    public void initialize(){
        superStructure = (Superstructure) Core.getSubsystemManager().getSubsystem(WSSubsystems.SUPERSTRUCTURE);

    }
    public String toString(){
        return "Launch Position";
    }
    
}
