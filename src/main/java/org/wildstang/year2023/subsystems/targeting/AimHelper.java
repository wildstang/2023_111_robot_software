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

    public double[] ltarget3D;
    public double[] rtarget3D;
    public double ltid;
    public double rtid;
    public double ltv;
    public double rtv;
    public int ltidInt;
    private int rtidInt;
    private double offsetX;
    private double offsetY;

    public boolean gamepiece;

    private DigitalInput rightBumper, leftBumper;//, dup, ddown;

    public LimeConsts LC;

    ShuffleboardTab tab = Shuffleboard.getTab("Tab");

    public void calcTargetCoords() { //update target coords.
        ltv = NetworkTableInstance.getDefault().getTable("limelight-left").getEntry("tv").getDouble(0);
        rtv = NetworkTableInstance.getDefault().getTable("limelight-right").getEntry("tv").getDouble(0);
        if(ltv > 0.0) {
            ltarget3D = NetworkTableInstance.getDefault().getTable("limelight-left").getEntry("botpose_targetspace").getDoubleArray(new double[6]);
            ltid = NetworkTableInstance.getDefault().getTable("limelight-left").getEntry("tid").getDouble(0);
        }
        if (rtv > 0.0){
            rtarget3D = NetworkTableInstance.getDefault().getTable("limelight-right").getEntry("botpose_targetspace").getDoubleArray(new double[6]);
            rtid = NetworkTableInstance.getDefault().getTable("limelight-right").getEntry("tid").getDouble(0);
        }
    }

    public double[] getAbsolutePosition(boolean isBlue){
        if (isBlue) {
            if (ltid >= 1.0 && ltid <= 8.0){
                offsetX = LC.APRILTAG_ABS_OFFSET_X[ltidInt = -1 + (int) ltid];
                offsetY = LC.APRILTAG_ABS_OFFSET_Y[ltidInt = -1 + (int) ltid];
            }
            return new double[]{-ltarget3D[2]+offsetX, ltarget3D[0]+offsetY};
        } else {
            if (rtid >= 1.0 && rtid <= 8.0){
                offsetX = LC.APRILTAG_ABS_OFFSET_X[rtidInt = -1 + (int) rtid];
                offsetY = LC.APRILTAG_ABS_OFFSET_Y[rtidInt = -1 + (int) rtid];
            }
            
            return new double[]{-rtarget3D[2]+offsetX, rtarget3D[0]+offsetY};
        }
        
    }

    public boolean TargetInView(){
        return ltv > 0.0 || rtv > 0.0;
    }

    //get ySpeed value for auto drive
    public double getScoreY(double offset){
        if (rtv < 1.0){
            return LC.VERT_AUTOAIM_P * (offset*LC.OFFSET_VERTICAL + getLeftVertical());
        } else if (ltv < 1.0){
            return LC.VERT_AUTOAIM_P * (offset*LC.OFFSET_VERTICAL + getRightVertical());
        } else {
            return LC.VERT_AUTOAIM_P * (offset*LC.OFFSET_VERTICAL + (getLeftVertical() + getRightVertical())/2.0);
        }
    }
    private double getLeftVertical(){
        return ltarget3D[2]*mToIn + LC.VERTICAL_APRILTAG_DISTANCE;
    }
    private double getRightVertical(){
        return rtarget3D[2]*mToIn + LC.VERTICAL_APRILTAG_DISTANCE;
    }

    //get xSpeed value for autodrive
    public double getScoreX(double offset){
        if (rtv < 1.0){
            return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + getLeftHorizontal());
        } else if (ltv < 1.0){
            return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + getRightHorizontal());
        } else {
            if (Math.abs(getLeftHorizontal()) < Math.abs(getRightHorizontal())){
                return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + getLeftHorizontal());
            } else {
                return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + getRightHorizontal());
            }
        }
    }

    private double getLeftHorizontal(){
        return ltarget3D[0]*mToIn + (gamepiece ? LC.HORIZONTAL_APRILTAG_DISTANCE : LC.HORIZONTAL_LIMELIGHT_MOUNT);
    }
    private double getRightHorizontal(){
        return rtarget3D[0]*mToIn - (gamepiece ? LC.HORIZONTAL_APRILTAG_DISTANCE : LC.HORIZONTAL_LIMELIGHT_MOUNT);
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

        ltv = NetworkTableInstance.getDefault().getTable("limelight-left").getEntry("tv").getDouble(0);
        rtv = NetworkTableInstance.getDefault().getTable("limelight-right").getEntry("tv").getDouble(0);
        ltarget3D = NetworkTableInstance.getDefault().getTable("limelight-left").getEntry("botpose_targetspace").getDoubleArray(new double[6]);
        rtarget3D = NetworkTableInstance.getDefault().getTable("limelight-right").getEntry("botpose_targetspace").getDoubleArray(new double[6]);
        ltid = NetworkTableInstance.getDefault().getTable("limelight-left").getEntry("tid").getDouble(0);
        rtid = NetworkTableInstance.getDefault().getTable("limelight-right").getEntry("tid").getDouble(0);

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
        SmartDashboard.putNumber("limeleft 3DX", ltarget3D[0]*mToIn);
        SmartDashboard.putNumber("limeleft 3DZ", ltarget3D[2]*mToIn);
        SmartDashboard.putBoolean("limeleft target in view", ltv > 0.0);
        SmartDashboard.putNumber("limeright 3DX", rtarget3D[0]*mToIn);
        SmartDashboard.putNumber("limeright 3DZ", rtarget3D[2]*mToIn);
        SmartDashboard.putBoolean("limeright target in view", rtv > 0.0);
    }

    @Override
    public void resetState() {
        gamepiece = LC.CONE;
        ltid = 1;
        rtid = 1;
        ltv = 0;
        rtv = 0;
        offsetX = 0;
        offsetY = 0;
    }

    @Override
    public String getName() {
        return "Aim Helper";
    }
}