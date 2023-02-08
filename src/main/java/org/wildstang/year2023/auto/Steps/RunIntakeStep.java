package org.wildstang.year2023.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.mastercontrolprogram.MasterControlProgram;
import org.wildstang.year2023.subsystems.intake.Intake;

public class RunIntakeStep extends AutoStep {
    private Intake Intake;

    private double intakeSpeed;

    /** Set Intake Speed
     * 
     */
    public RunIntakeStep(double speed){
        this.intakeSpeed = speed;
    }

    @Override
    public void update(){
        Intake.intakeObject(intakeSpeed);
        this.setFinished(true);
    }
    
    @Override
    public String toString() {
        return "Run Intake Step";
    }
    
    @Override
    public void initialize(){
        Intake = (Intake) Core.getSubsystemManager().getSubsystem(WSSubsystems.INTAKE);
    }
}