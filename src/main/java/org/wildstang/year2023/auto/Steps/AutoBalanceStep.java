package org.wildstang.year2023.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2023.robot.WSSubsystems;

public class AutoBalanceStep extends AutoStep{ //TODO: Make an autobalance step

    public AutoBalanceStep(){
    }

    public void update(){
        this.setFinished();

    }

    public void initialize(){

    }
    public String toString(){
        return "Auto Balance Step";
    }
    
}