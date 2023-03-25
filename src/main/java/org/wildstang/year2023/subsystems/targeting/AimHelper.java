package org.wildstang.year2023.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsRemoteAnalogInput;
import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.year2023.robot.WSInputs;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AimHelper implements Subsystem {
    
    private static final double mToIn = 39.3701;

    public NetworkTable limeleft = NetworkTableInstance.getDefault().getTable("limelight-left");
    public NetworkTable limeright = NetworkTableInstance.getDefault().getTable("limelight-right");

    public LimelightHelpers.Results lresult;
    public LimelightHelpers.Results rresult;

    public double[] ltarget3D;
    public double[] rtarget3D;
    public double[] lblue3D;
    public double[] lred3D;
    public double[] rblue3D;
    public double[] rred3D;
    public double ltid;
    public double rtid;
    public double ltv;
    public double rtv;

    public boolean valid;

    public Timer timer = new Timer();

    public int lnumtargets;
    public int rnumtargets;

    public boolean gamepiece;

    private DigitalInput rightBumper, leftBumper;//, dup, ddown;

    public LimeConsts LC;

    ShuffleboardTab tab = Shuffleboard.getTab("Tab");

    public void calcTargetCoords() { //update target coords.
        ltv = limeleft.getEntry("tv").getDouble(0);
        rtv = limeright.getEntry("tv").getDouble(0);
        if(ltv > 0.0) {
            ltarget3D = limeleft.getEntry("botpose_targetspace").getDoubleArray(new double[6]);
            lblue3D = limeleft.getEntry("botpose_wpiblue").getDoubleArray(new double[7]);
            lred3D = limeleft.getEntry("botpose_wpired").getDoubleArray(new double[7]);
            ltid = limeleft.getEntry("tid").getDouble(0);
            lnumtargets = lresult.targets_Fiducials.length;
        }
        if (rtv > 0.0){
            rtarget3D = limeright.getEntry("botpose_targetspace").getDoubleArray(new double[6]);
            rblue3D = limeright.getEntry("botpose_wpiblue").getDoubleArray(new double[7]);
            rred3D = limeright.getEntry("botpose_wpired").getDoubleArray(new double[7]);
            rtid = limeright.getEntry("tid").getDouble(0);
            rnumtargets = rresult.targets_Fiducials.length;
        }
    }

    public double[] getAbsolutePosition(boolean isBlue){
        calcTargetCoords();
        if (isBlue){
            if (ltv > 0.0 && rtv < 1.0){
                return new double[]{lblue3D[0], lblue3D[1]};
            } else if (ltv < 1.0 && rtv > 0.0){
                return new double[]{rblue3D[0], rblue3D[1]};
            } else {
                if (rnumtargets > lnumtargets){
                    return new double[]{rblue3D[0], rblue3D[1]};
                } else {
                    return new double[]{lblue3D[0], lblue3D[1]};
                }
            }
        } else {
            if (rtv > 0.0 && ltv < 1.0){
                return new double[]{rred3D[0], rred3D[1]};
            } else if (ltv > 0.0 && rtv < 1.0) {
                return new double[]{lred3D[0], lred3D[1]};
            } else {
                if (lnumtargets > rnumtargets){
                    return new double[]{lred3D[0], lred3D[1]};
                } else {
                    return new double[]{rred3D[0], rred3D[1]};
                }
            }
        }        
    }

    public boolean dataValid(boolean isBlue){
        if (getAbsolutePosition(isBlue)[0] < 2.5) return true;
        return false;
        // if (!valid) return false;
        // if (lnumtargets < 2 && rnumtargets < 2) return false;
        // return true;
    }

    public boolean TargetInView(){
        return ltv > 0.0 || rtv > 0.0;
    }

    //get ySpeed value for auto drive
    public double getScoreY(double offset){
        if (rtv > 0.0 && ltv > 0.0){
            return LC.VERT_AUTOAIM_P * (offset*LC.OFFSET_VERTICAL + (getLeftVertical() + getRightVertical())/2.0);
        } else if (ltv > 0.0){
            return (getLeftVertical()+offset*LC.OFFSET_VERTICAL) * LC.VERT_AUTOAIM_P;
        } else {
            return (getRightVertical()+offset*LC.OFFSET_VERTICAL) * LC.VERT_AUTOAIM_P;
        }
        // if (rtv < 1.0){
        //     return LC.VERT_AUTOAIM_P * (offset*LC.OFFSET_VERTICAL + getLeftVertical());
        // } else if (ltv < 1.0){
        //     return LC.VERT_AUTOAIM_P * (offset*LC.OFFSET_VERTICAL + getRightVertical());
        // } else {
        //     return LC.VERT_AUTOAIM_P * (offset*LC.OFFSET_VERTICAL + (getLeftVertical() + getRightVertical())/2.0);
        // }
    }
    private double getLeftVertical(){
        if (ltid > 4.5){
            return -lblue3D[0]*mToIn + (LC.VERTICAL_APRILTAG_DISTANCE); 
        } else {
            return -lred3D[0]*mToIn + (LC.VERTICAL_APRILTAG_DISTANCE);
        }
        //return ltarget3D[2]*mToIn + LC.VERTICAL_APRILTAG_DISTANCE;
    }
    private double getRightVertical(){
        if (rtid > 4.5){
            return -rblue3D[0]*mToIn + (LC.VERTICAL_APRILTAG_DISTANCE); 
        } else {
            return -rred3D[0]*mToIn + (LC.VERTICAL_APRILTAG_DISTANCE);
        }
        //return rtarget3D[2]*mToIn + LC.VERTICAL_APRILTAG_DISTANCE;
    }

    //get xSpeed value for autodrive
    public double getScoreX(double offset){
        if (rtv > 0.0 && ltv > 0.0){
            return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + (getLeftHorizontal() + getRightHorizontal())/2.0);
        } else if (ltv > 0.0){
            return (getLeftHorizontal()+offset*LC.OFFSET_HORIZONTAL) * LC.HORI_AUTOAIM_P;
        } else {
            return (getRightHorizontal()+offset*LC.OFFSET_HORIZONTAL) * LC.HORI_AUTOAIM_P;
        }
        // if (rtv < 1.0){
        //     return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + getLeftHorizontal());
        // } else if (ltv < 1.0){
        //     return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + getRightHorizontal());
        // } else {
        //     if (Math.abs(getLeftHorizontal()) < Math.abs(getRightHorizontal())){
        //         return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + getLeftHorizontal());
        //     } else {
        //         return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + getRightHorizontal());
        //     }
        // }
    }

    private double getLeftHorizontal(){
        if (ltid > 4.5){
            if (gamepiece) return getCone(lblue3D[1]*mToIn, true);
            else return getCube(lblue3D[1]*mToIn, true);
        } else {
            if (gamepiece) return getCone(lred3D[1]*mToIn, false);
            else return getCube(lred3D[1]*mToIn, false);
        }
        //return ltarget3D[0]*mToIn + (gamepiece ? LC.HORIZONTAL_APRILTAG_DISTANCE_LEFT : LC.HORIZONTAL_LIMELIGHT_MOUNT);
    }
    private double getRightHorizontal(){
        if (rtid > 4.5){
            if (gamepiece) return getCone(rblue3D[1]*mToIn, true);
            else return getCube(rblue3D[1]*mToIn, true);
        } else {
            if (gamepiece) return getCone(rred3D[1]*mToIn, false);
            else return getCube(rred3D[1]*mToIn, false);
        }
        //return rtarget3D[0]*mToIn - (gamepiece ? LC.HORIZONTAL_APRILTAG_DISTANCE_RIGHT : LC.HORIZONTAL_LIMELIGHT_MOUNT);
    }
    private double getCone(double target, boolean color){
        int i = color ? 6 : 0;
        double minimum = 1000;
        while (i < (color ? 12 : 6)){
            if (Math.abs(minimum) > Math.abs(target - LC.CONES[i]*mToIn)){
                minimum = target - LC.CONES[i]*mToIn;
            }
            i++;
        }
        return minimum;
    }
    private double getCube(double target, boolean color){
        int i = color ? 3 : 0;
        double minimum = 1000;
        while (i < (color ? 6 : 3)){
            if (Math.abs(minimum) > Math.abs(target - LC.CUBES[i]*mToIn)){
                minimum = target - LC.CUBES[i]*mToIn;
            }
            i++;
        }
        return minimum;
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

        ltv = limeleft.getEntry("tv").getDouble(0);
        rtv = limeright.getEntry("tv").getDouble(0);
        ltarget3D = limeleft.getEntry("botpose_targetspace").getDoubleArray(new double[6]);
        rtarget3D = limeright.getEntry("botpose_targetspace").getDoubleArray(new double[6]);
        lblue3D = limeleft.getEntry("botpose_wpiblue").getDoubleArray(new double[7]);
        lred3D = limeleft.getEntry("botpose_wpired").getDoubleArray(new double[7]);
        rblue3D = limeright.getEntry("botpose_wpiblue").getDoubleArray(new double[7]);
        rred3D = limeright.getEntry("botpose_wpired").getDoubleArray(new double[7]);
        ltid = limeleft.getEntry("tid").getDouble(0);
        rtid = limeright.getEntry("tid").getDouble(0);

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
        lresult = LimelightHelpers.getLatestResults("limelight-left").targetingResults;
        rresult = LimelightHelpers.getLatestResults("limelight-right").targetingResults;
        calcTargetCoords();
        if (lnumtargets < 2 && rnumtargets < 2) timer.reset();
        valid = timer.hasElapsed(0.5);
        //SmartDashboard.putNumber("limeleft 3DX", ltarget3D[0]*mToIn);
        //SmartDashboard.putNumber("limeleft 3DZ", ltarget3D[2]*mToIn);
        SmartDashboard.putBoolean("limeleft tiv", ltv > 0.0);
        SmartDashboard.putNumber("limeleft tid", ltid);
        SmartDashboard.putNumber("limeleft red X", lred3D[0]*mToIn);
        SmartDashboard.putNumber("limeleft red Y", lred3D[1]*mToIn);
        SmartDashboard.putNumber("limeleft blue X", lblue3D[0]*mToIn);
        SmartDashboard.putNumber("limeleft blue Y", lblue3D[1]*mToIn);
        //SmartDashboard.putNumber("limeright 3DX", rtarget3D[0]*mToIn);
        //SmartDashboard.putNumber("limeright 3DZ", rtarget3D[2]*mToIn);
        SmartDashboard.putBoolean("limeright tiv", rtv > 0.0);
        SmartDashboard.putNumber("limeright tid", rtid);
        SmartDashboard.putNumber("limeright red X", rred3D[0]*mToIn);
        SmartDashboard.putNumber("limeright red Y", rred3D[1]*mToIn);
        SmartDashboard.putNumber("limeright blue X", rblue3D[0]*mToIn);
        SmartDashboard.putNumber("limeright blue Y", rblue3D[1]*mToIn);
        SmartDashboard.putBoolean("limelight target in view", TargetInView());
        SmartDashboard.putBoolean("limelight valid data", dataValid(rtv > 1.0 ? rtid > 4.5 : ltid > 4.5));
    }

    @Override
    public void resetState() {
        gamepiece = LC.CONE;
        ltid = 1;
        rtid = 1;
        ltv = 0;
        rtv = 0;
        lnumtargets = 0;
        rnumtargets = 0;
        valid = false;
        timer.reset();timer.start();
    }

    @Override
    public String getName() {
        return "Aim Helper";
    }
}