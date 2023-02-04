package org.wildstang.year2023.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.mastercontrolprogram.MasterControlProgram;
import org.wildstang.year2023.subsystems.intake.Intake;

public class MasterControlProgramStep extends AutoStep {
    private MasterControlProgram MasterControlProgram;

    private String MCPPosition;

    /** Set Intake Speed
     * 
     */
    public MasterControlProgramStep(String position){
        this.MCPPosition = position;
    }

    @Override
    public void update(){
        MasterControlProgram.goToPosition(MCPPosition);
        this.setFinished(true);
    }
    
    @Override
    public String toString() {
        return "Master Control Program Step";
    }
    
    @Override
    public void initialize(){
        MasterControlProgram = (MasterControlProgram) Core.getSubsystemManager().getSubsystem(WSSubsystems.MASTER_CONTROL_PROGRAM);
        resetState();
    }
}