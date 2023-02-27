package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.intake.intake;

public class IntakeOnStep extends AutoStep{

    private intake intakeSub;
    public IntakeOnStep(){
    }
    public void update(){
        intakeSub.intakeOn();
        this.setFinished();

    }
    public void initialize(){
        intakeSub = (intake) Core.getSubsystemManager().getSubsystem(WSSubsystems.INTAKE);

    }
    public String toString(){
        return "Intake On";
    }
    
}