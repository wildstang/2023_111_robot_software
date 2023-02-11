package org.wildstang.year2023.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsRemoteAnalogInput;
import org.wildstang.hardware.roborio.outputs.WsRemoteAnalogOutput;

import java.util.HashMap;
import java.util.Map;

import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.wildstang.year2023.subsystems.swerve.DriveConstants;
import org.wildstang.year2023.subsystems.swerve.WSSwerveHelper;

public class AimHelper implements Subsystem {

    private WsRemoteAnalogInput ty; // y angle
    private WsRemoteAnalogInput tx; // x angle
    private WsRemoteAnalogInput tv;
    private WsRemoteAnalogOutput ledModeEntry;
    private WsRemoteAnalogOutput llModeEntry;

    public double x;
    public double y;

    //private double modifier;

    public boolean TargetInView;
    private boolean ledState;

    private double TargetAbsoluteDistance;
    private double TargetNormalDistance;
    private double TargetParallelDistance;


    private DigitalInput rightBumper, leftBumper;//, dup, ddown;

    public LimeConsts LC;

    private int dataLifeSpan = 5;
    private int dataLife = 0; 

    public int currentPipeline;
    private Map<String, Integer> pipelineStringToInt = new HashMap<String, Integer>() {{
        put("aprilTag", 0);
        put("reflective", 1);
    }};

    ShuffleboardTab tab = Shuffleboard.getTab("Tab");

    public void changePipeline(String pipelineString) {
        currentPipeline = pipelineStringToInt.get(pipelineString);

        NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(currentPipeline);
    }

    public void calcTargetCoords() { //update target coords.
        if(tv.getValue() == 1) {
            TargetInView = true;
            x = tx.getValue();
            y = ty.getValue();
            dataLife = 0;
        }
        else {
            TargetInView = false;
            // accounting for unreliable readings
            dataLife ++;
            if (dataLife <= dataLifeSpan){
                return;
            } else{
                x = 0; //no target case
                y = 0;
            }
        }
    }
    
    /** 
     * @return Get the shortest distance from robot to the target
     */
    public double getDistance() {
        TargetAbsoluteDistance = LC.TARGET_HEIGHT / Math.tan(Math.toRadians(this.y + LC.CAMERA_ANGLE_OFFSET));//+36?
        //return TargetDistance;
        return TargetAbsoluteDistance;
    }

    /** 
     * @return Get the x distance from robot to target (2023 game)
     */
    public double getNormalDistance() {
        TargetNormalDistance = getDistance()*Math.cos(Math.toRadians(this.x));

        return TargetNormalDistance;
    }

    /** 
     * @return Get the y distance from robot to target (2023 game)
     */
    public double getParallelDistance() {
        TargetParallelDistance = getDistance()*Math.sin(Math.toRadians(this.x));

        return TargetParallelDistance;
    }

    public double getRotPID() {
        calcTargetCoords();
        return (this.x) * -0.015;
    }

    public void turnOnLED(boolean onState) {
        if (onState) {
            ledModeEntry.setValue(3);
            llModeEntry.setValue(0);
        }
        else {
            ledModeEntry.setValue(1);
            llModeEntry.setValue(1);
        }
    }

    @Override
    public void inputUpdate(Input source) {
        if (rightBumper.getValue())
        {
            ledState = false;
            changePipeline("aprilTag");
        }
        if (leftBumper.getValue()){
            // always on
            ledState = true;
            changePipeline("reflective");
        }
        // if (source == dup && dup.getValue()) {
        //     modifier++;
        // }
        // if (source == ddown && ddown.getValue()) {
        //     modifier--;
        // }
    }

    @Override
    public void init() {
        LC = new LimeConsts();
        x = 0;  //x and y angular offsets from limelight. Only updated when calcTargetCoords is called.
        y = 0;
        TargetInView = false; //is the target in view? only updated when calcTargetCoords is called.
        TargetAbsoluteDistance = 0; //distance to target in feet. Only updated when calcTargetCoords is called.
        TargetNormalDistance = 0; 
        TargetParallelDistance = 0; 

        ty = (WsRemoteAnalogInput) WSInputs.LL_TY.get();
        tx = (WsRemoteAnalogInput) WSInputs.LL_TX.get();
        tv = (WsRemoteAnalogInput) WSInputs.LL_TV.get();
        ledModeEntry = (WsRemoteAnalogOutput) WSOutputs.LL_LEDS.get();
        llModeEntry = (WsRemoteAnalogOutput) WSOutputs.LL_MODE.get();

        rightBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_SHOULDER);
        rightBumper.addInputListener(this);
        leftBumper = (DigitalInput) WSInputs.MANIPULATOR_LEFT_SHOULDER.get();
        leftBumper.addInputListener(this);
        // dup = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_UP);
        // dup.addInputListener(this);
        // ddown = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_DOWN);
        // ddown.addInputListener(this);
        resetState();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        turnOnLED(ledState);
        calcTargetCoords();
        SmartDashboard.putNumber("limelight distance", getDistance());
        SmartDashboard.putNumber("limelight tx", tx.getValue());
        SmartDashboard.putNumber("limelight ty", ty.getValue());
        SmartDashboard.putBoolean("limelight target in view", tv.getValue() == 1);
        //SmartDashboard.putNumber("Distance Modifier", modifier);
    }

    @Override
    public void resetState() {
        ledState = true;
        //modifier = 0;
    }

    @Override
    public String getName() {
        return "Aim Helper";
    }
}