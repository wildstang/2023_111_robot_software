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
    
    private static final double mToIn = 30.3701;

    private WsRemoteAnalogInput ty; // y angle
    private WsRemoteAnalogInput tx; // x angle
    private WsRemoteAnalogInput tv;

    public double x;
    public double y;
    public double[] target3D;
    public double tid;
    private Integer tidInt;
    private double[] targetOffset;

    public boolean TargetInView;
    public boolean gamepiece;
    private double TargetAbsoluteDistance;

    private DigitalInput rightBumper, leftBumper;//, dup, ddown;

    public LimeConsts LC;

    private int dataLifeSpan = 10;
    private int dataLife = 0; 

    ShuffleboardTab tab = Shuffleboard.getTab("Tab");

    public void calcTargetCoords() { //update target coords.
        if(tv.getValue() == 1) {
            TargetInView = true;
            x = tx.getValue();
            y = ty.getValue();
            target3D = NetworkTableInstance.getDefault().getTable("limelight").getEntry("camerapose_targetspace").getDoubleArray(new double[6]);
            tid = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tid").getDouble(0);
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
                target3D = new double[] {0,0,0,0,0,0};
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
        //TargetNormalDistance = getDistance()*Math.cos(Math.toRadians(this.x));
        return get3DZ();
        //return TargetNormalDistance;
    }

    /** 
     * @return Get the y distance from robot to target (2023 game)
     */
    public double getParallelDistance() {
        //TargetParallelDistance = getDistance()*Math.sin(Math.toRadians(this.x));
        return get3DX();
        //return TargetParallelDistance;
    }
    public double getParallelSetpoint(){
        if (gamepiece == LC.CONE) return LC.APRILTAG_HORIZONTAL_OFFSET * Math.signum(get3DX());
        else return 0.0;
    }

    public double[] getAbsolutePosition(){
        double offsetX = LC.APRILTAG_ABS_OFFSET_X[tidInt = (int) tid];
        double offsetY = LC.APRILTAG_ABS_OFFSET_Y[tidInt = (int) tid];
        
        double[] newArray = target3D;
        newArray[0] = target3D[0] + offsetX;
        newArray[2] = target3D[2] + offsetY;
        
        return newArray;
    }

    public double getRotPID() {
        calcTargetCoords();
        return (this.x) * -0.015;
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
        x = 0;  //x and y angular offsets from limelight. Only updated when calcTargetCoords is called.
        y = 0;
        TargetInView = false; //is the target in view? only updated when calcTargetCoords is called.
        TargetAbsoluteDistance = 0; //distance to target in feet. Only updated when calcTargetCoords is called.

        ty = (WsRemoteAnalogInput) WSInputs.LL_TY.get();
        tx = (WsRemoteAnalogInput) WSInputs.LL_TX.get();
        tv = (WsRemoteAnalogInput) WSInputs.LL_TV.get();
        target3D = NetworkTableInstance.getDefault().getTable("limelight").getEntry("camerapose_targetspace").getDoubleArray(new double[6]);
        tid = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tid").getDouble(0);

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
        SmartDashboard.putNumber("limelight distance", getDistance());
        SmartDashboard.putNumber("limelight tx", tx.getValue());
        SmartDashboard.putNumber("limelight ty", ty.getValue());
        SmartDashboard.putNumber("limelight 3DX", get3DX());
        SmartDashboard.putNumber("limelight 3DY", get3DY());
        SmartDashboard.putNumber("limelight 3DZ", get3DZ());
        SmartDashboard.putBoolean("limelight target in view", tv.getValue() == 1);
    }

    @Override
    public void resetState() {
        gamepiece = LC.CONE;
    }

    @Override
    public String getName() {
        return "Aim Helper";
    }
    public double get3DX(){
        return target3D[0]*mToIn;
    }
    public double get3DY(){
        return target3D[1]*mToIn;
    }
    public double get3DZ(){
        return -target3D[2]*mToIn;
    }
}