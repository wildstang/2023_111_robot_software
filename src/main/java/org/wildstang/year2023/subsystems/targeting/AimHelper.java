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

    //private SwerveDrive swerve;
    private WSSwerveHelper helper;

    public double x;
    public double y;

    private double modifier;

    public boolean TargetInView;
    private boolean ledState;

    private double TargetAbsoluteDistance;
    private double TargetNormalDistance;
    private double TargetParallelDistance;
    private double xSpeed, ySpeed;


    private DigitalInput rightBumper, dup, ddown;
    private AnalogInput leftStickX, leftStickY;

    public LimeConsts LC;

    private double gyroValue;

    private double distanceFactor = 30;
    private double angleFactor = 15;
    public static double FenderDistance = 60;

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
            TargetInView = false;
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


    private double getGyroAngle() {
        return gyroValue;
    }

    public void setGyroValue(double toSet) {
        gyroValue = toSet;
    }
    
    /** 
     * @return Get the shortest distance from robot to the target
     */
    public double getDistance() {
        TargetAbsoluteDistance = (modifier *  12) + 36 + LC.TARGET_HEIGHT / Math.tan(Math.toRadians(this.y + LC.CAMERA_ANGLE_OFFSET));
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
        //return tx.getDouble(0) * -0.015;
        return (tx.getValue()) * -0.015;
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
            ledState = true;
        }
        else
        {
            // always on
            ledState = true;
        }
        if (source == dup && dup.getValue()) {
            modifier++;
        }
        if (source == ddown && ddown.getValue()) {
            modifier--;
        }
        xSpeed = leftStickX.getValue();
        ySpeed = leftStickY.getValue();
        if (Math.abs(leftStickX.getValue()) < DriveConstants.DEADBAND) {
            xSpeed = 0;
        }
        if (Math.abs(leftStickY.getValue()) < DriveConstants.DEADBAND) {
            ySpeed = 0;
        }
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

        helper = new WSSwerveHelper();

        rightBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_SHOULDER);
        rightBumper.addInputListener(this);
        leftStickX = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_X);
        leftStickX.addInputListener(this);
        leftStickY = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_Y);
        leftStickY.addInputListener(this);
        dup = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_UP);
        dup.addInputListener(this);
        ddown = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_DOWN);
        ddown.addInputListener(this);
        resetState();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        turnOnLED(ledState);
        calcTargetCoords();
        //distanceFactor = distance.getEntry().getDouble(0);
        //angleFactor = angle.getEntry().getDouble(0);
        SmartDashboard.putNumber("limelight distance", getDistance());
        SmartDashboard.putNumber("limelight tx", tx.getValue());
        SmartDashboard.putNumber("limelight ty", ty.getValue());
        SmartDashboard.putBoolean("limelight target in view", tv.getValue() == 1);
        SmartDashboard.putNumber("Distance Modifier", modifier);
    }

    @Override
    public void resetState() {
        ledState = true;
        modifier = 0;
        xSpeed = 0;
        ySpeed = 0;
        gyroValue = 0;
    }

    @Override
    public String getName() {
        return "Aim Helper";
    }
}