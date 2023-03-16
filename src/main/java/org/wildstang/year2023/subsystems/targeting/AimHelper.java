package org.wildstang.year2023.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsRemoteAnalogInput;
import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.year2023.robot.WSInputs;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AimHelper implements Subsystem {
    
    private static final double mToIn = 39.3701;

    private WsRemoteAnalogInput ltv;
    private WsRemoteAnalogInput rtv;

    public double[] ltarget3D;
    public double[] rtarget3D;
    public double ltid;
    public double rtid;
    public int ltidInt;
    private int rtidInt;
    private double offsetX;
    private double offsetY;

    public boolean gamepiece;

    private DigitalInput rightBumper, leftBumper;//, dup, ddown;

    public LimeConsts LC;

    ShuffleboardTab tab = Shuffleboard.getTab("Tab");

    public void calcTargetCoords() { //update target coords.
        if(ltv.getValue() == 1) {
            ltarget3D = NetworkTableInstance.getDefault().getTable("limeleft").getEntry("camerapose_targetspace").getDoubleArray(new double[6]);
            ltid = NetworkTableInstance.getDefault().getTable("limeleft").getEntry("tid").getDouble(0);
        }
        if (rtv.getValue() == 1){
            rtarget3D = NetworkTableInstance.getDefault().getTable("limeright").getEntry("camerapose_targetspace").getDoubleArray(new double[6]);
            rtid = NetworkTableInstance.getDefault().getTable("limeright").getEntry("tid").getDouble(0);
        }
    }

    public double[] getAbsoluteLeftPosition(){
        if (ltid >= 1.0 && ltid <= 8.0){
            offsetX = LC.APRILTAG_ABS_OFFSET_X[ltidInt = -1 + (int) ltid];
            offsetY = LC.APRILTAG_ABS_OFFSET_Y[ltidInt = -1 + (int) ltid];
        }
        
        return new double[]{-ltarget3D[2]+offsetX, ltarget3D[0]+offsetY};
    }
    public double[] getAbsoluteRightPosition(){
        if (rtid >= 1.0 && rtid <= 8.0){
            offsetX = LC.APRILTAG_ABS_OFFSET_X[rtidInt = -1 + (int) rtid];
            offsetY = LC.APRILTAG_ABS_OFFSET_Y[rtidInt = -1 + (int) rtid];
        }
        
        return new double[]{-rtarget3D[2]+offsetX, rtarget3D[0]+offsetY};
    }

    public boolean TargetInView(){
        return ltv.getValue()==1 || rtv.getValue()==1;
    }

    public double getScoreY(double offset){
        if (rtv.getValue() == 0){
            return LC.VERT_AUTOAIM_P * (offset*LC.OFFSET_VERTICAL + ltarget3D[2] - LC.VERTICAL_APRILTAG_DISTANCE);
        } else if (ltv.getValue() == 0){
            return LC.VERT_AUTOAIM_P * (offset*LC.OFFSET_VERTICAL + rtarget3D[2] - LC.VERTICAL_APRILTAG_DISTANCE);
        } else {
            return LC.VERT_AUTOAIM_P * (offset*LC.OFFSET_VERTICAL + (rtarget3D[2]+ltarget3D[2])/2 - LC.VERTICAL_APRILTAG_DISTANCE);
        }
    }

    public double getScoreX(double offset){
        if (rtv.getValue() == 0){
            return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + ltarget3D[0] - (gamepiece ? LC.HORIZONTAL_APRILTAG_DISTANCE : 0.0));
        } else if (ltv.getValue() == 0){
            return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + rtarget3D[0] + (gamepiece ? LC.HORIZONTAL_APRILTAG_DISTANCE : 0.0));
        } else {
            if (gamepiece){
                if (Math.abs(ltarget3D[0] + LC.HORIZONTAL_APRILTAG_DISTANCE) < Math.abs(rtarget3D[0] - LC.HORIZONTAL_APRILTAG_DISTANCE)){
                    return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + ltarget3D[0] + LC.HORIZONTAL_APRILTAG_DISTANCE);
                } else {
                    return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + rtarget3D[0] - LC.HORIZONTAL_APRILTAG_DISTANCE);
                }
            } else {
                if (Math.abs(ltarget3D[0]) < Math.abs(rtarget3D[0])){
                    return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + ltarget3D[0]);
                } else {
                    return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + rtarget3D[0]);
                }
            }
        }
    }

    @Override
    public void inputUpdate(Input source) {
        if (rightBumper.getValue())
        {
            gamepiece = LC.CUBE;
        }
        if (leftBumper.getValue()){
            gamepiece = LC.CONE;
        }
    }

    @Override
    public void init() {
        LC = new LimeConsts();

        ltv = (WsRemoteAnalogInput) WSInputs.LL_TV.get();
        rtv = (WsRemoteAnalogInput) WSInputs.LR_TV.get();
        ltarget3D = NetworkTableInstance.getDefault().getTable("limeleft").getEntry("botpose_targetspace").getDoubleArray(new double[6]);
        rtarget3D = NetworkTableInstance.getDefault().getTable("limeright").getEntry("botpose_targetspace").getDoubleArray(new double[6]);
        ltid = NetworkTableInstance.getDefault().getTable("limeleft").getEntry("tid").getDouble(0);
        rtid = NetworkTableInstance.getDefault().getTable("limeright").getEntry("tid").getDouble(0);

        rightBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_SHOULDER);
        rightBumper.addInputListener(this);
        leftBumper = (DigitalInput) WSInputs.MANIPULATOR_LEFT_SHOULDER.get();
        leftBumper.addInputListener(this);

        resetState();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        calcTargetCoords();
        SmartDashboard.putNumber("limeleft 3DX", ltarget3D[0]);
        SmartDashboard.putNumber("limeleft 3DY", ltarget3D[1]);
        SmartDashboard.putNumber("limeleft 3DZ", ltarget3D[2]);
        SmartDashboard.putBoolean("limeleft target in view", ltv.getValue() == 1);
        SmartDashboard.putNumber("limeright 3DX", rtarget3D[0]);
        SmartDashboard.putNumber("limeright 3DY", rtarget3D[1]);
        SmartDashboard.putNumber("limeright 3DZ", rtarget3D[2]);
        SmartDashboard.putBoolean("limeright target in view", rtv.getValue() == 1);
    }

    @Override
    public void resetState() {
        gamepiece = LC.CONE;
        ltid = 1;
        rtid = 1;
        offsetX = 0;
        offsetY = 0;
    }

    @Override
    public String getName() {
        return "Aim Helper";
    }
}