package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.intake.intake;

public class IntakeOffStep extends AutoStep{

    private intake intakeSub;
    public IntakeOffStep(){
    }
    public void update(){
        intakeSub.intakeOff();
        this.setFinished();

    }
    public void initialize(){
        intakeSub = (intake) Core.getSubsystemManager().getSubsystem(WSSubsystems.INTAKE);

    }
    public String toString(){
        return "Intake Off";
    }
    
}