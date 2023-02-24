package org.wildstang.year2023.subsystems.swerve;

import com.ctre.phoenix.sensors.Pigeon2;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;
import org.wildstang.year2023.robot.CANConstants;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;
import org.wildstang.year2023.robot.WSSubsystems;
import org.wildstang.year2023.subsystems.targeting.AimHelper;
import org.wildstang.year2023.subsystems.targeting.LimeConsts;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**Class: SwerveDrive
 * inputs: driver left joystick x/y, right joystick x, right trigger, right bumper, select, face buttons all, gyro
 * outputs: four swerveModule objects
 * description: controls a swerve drive for four swerveModules through autonomous and teleoperated control
 */
public class SwerveDrive extends SwerveDriveTemplate {

    private AnalogInput leftStickX;//translation joystick x
    private AnalogInput leftStickY;//translation joystick y
    private AnalogInput rightStickX;//rot joystick
    private AnalogInput rightTrigger;//thrust
    private AnalogInput leftTrigger;//aiming
    // private DigitalInput rightBumper;//robot centric control
    private DigitalInput leftBumper;//intake
    private DigitalInput select;//gyro reset
    private DigitalInput start;//snake mode
    private DigitalInput faceUp;//rotation lock 0 degrees
    private DigitalInput faceRight;//rotation lock 90 degrees
    private DigitalInput faceLeft;//rotation lock 270 degrees
    private DigitalInput faceDown;//rotation lock 180 degrees
    private DigitalInput dpadLeft;//defense mode

    private double xSpeed;
    private double ySpeed;
    private double rotSpeed;
    private double thrustValue;
    private boolean rotLocked;
    private boolean isSnake;
    private boolean isFieldCentric;
    private double rotTarget;
    private double pathVel;
    private double pathHeading;
    private double pathTarget;
    private double aimOffset;
    private double lastOffset;
    private boolean lastInView;
    private boolean startingLL;
    public double xAbsPos;
    public double yAbsPos;
    private double pathXOffset = 0;
    private double pathYOffset = 0;
    
    private final double mToIn = 39.37;
    private final double inToM = 1.0/mToIn;

    //private final AHRS gyro = new AHRS(SerialPort.Port.kUSB);
    private final Pigeon2 gyro = new Pigeon2(CANConstants.GYRO);
    public SwerveModule[] modules;
    private SwerveSignal swerveSignal;
    private WSSwerveHelper swerveHelper = new WSSwerveHelper();
    private SwerveDriveOdometry odometry;
    private Pose2d robotPose;

    private AimHelper limelight;
    private LimeConsts LC;
    private PIDController LLpidX;// = new PIDController(LC.AUTO_ALIGN_PID_X[1], LC.AUTO_ALIGN_PID_X[2], LC.AUTO_ALIGN_PID_X[3]);
    private PIDController LLpidY;// = new PIDController(LC.AUTO_ALIGN_PID_Y[1], LC.AUTO_ALIGN_PID_Y[2], LC.AUTO_ALIGN_PID_Y[3]);

    public enum driveType {TELEOP, AUTO, CROSS, LL};
    public driveType driveState;

    @Override
    public void inputUpdate(Input source) {
        //determine if we are in cross or teleop
        if (driveState != driveType.AUTO && dpadLeft.getValue()) {
            driveState = driveType.CROSS;
            for (int i = 0; i < modules.length; i++) {
                modules[i].setDriveBrake(true);
            }
            this.swerveSignal = new SwerveSignal(new double[]{0, 0, 0, 0 }, swerveHelper.setCross().getAngles());
        }
        else if (driveState != driveType.AUTO) {
            driveState = driveType.TELEOP;
            for (int i = 0; i < modules.length; i++) {
                modules[i].setDriveBrake(false);
            }
        }
        //get x and y speeds
        xSpeed = swerveHelper.scaleDeadband(leftStickX.getValue(), DriveConstants.DEADBAND);
        ySpeed = swerveHelper.scaleDeadband(leftStickY.getValue(), DriveConstants.DEADBAND);
        
        //reset gyro
        if (source == select && select.getValue()) {
            gyro.setYaw(0.0);
        }

        //determine snake or pid locks
        if (start.getValue() && (Math.abs(xSpeed) > 0.1 || Math.abs(ySpeed) > 0.1)) {
            rotLocked = true;
            isSnake = true;
            rotTarget = swerveHelper.getDirection(xSpeed, ySpeed);
        }
        else {
            isSnake = false;
        }
        if ((source == faceUp && faceUp.getValue()) || leftBumper.getValue()){
            rotTarget = 0.0;
            rotLocked = true;
        }
        if (source == faceLeft && faceLeft.getValue()){
            rotTarget = 270.0;
            rotLocked = true;
        }
        if ((source == faceDown && faceDown.getValue()) || Math.abs(leftTrigger.getValue()) > 0.15){
            rotTarget = 180.0;
            rotLocked = true;
        }
        if (source == faceRight && faceRight.getValue()){
            rotTarget = 90.0;
            rotLocked = true;
        }

        //get rotational joystick
        rotSpeed = rightStickX.getValue()*Math.abs(rightStickX.getValue());
        rotSpeed = swerveHelper.scaleDeadband(rotSpeed, DriveConstants.DEADBAND);
        if (Math.abs(leftTrigger.getValue()) > 0.15 || leftBumper.getValue()) rotSpeed = 0;
        //if the rotational joystick is being used, the robot should not be auto tracking heading
        if (rotSpeed != 0) {
            rotLocked = false;
        }
        
        //assign thrust
        thrustValue = 1 - DriveConstants.DRIVE_THRUST + DriveConstants.DRIVE_THRUST * Math.abs(rightTrigger.getValue());
        xSpeed *= thrustValue;
        ySpeed *= thrustValue;
        rotSpeed *= thrustValue;
        

        // if (Math.abs(leftTrigger.getValue()) > 0.15){
        //     xSpeed /=2.0;
        //     ySpeed /=2.0;
        //     //rotTarget = 180.0;
        //     //rotLocked = true;
        // }
        //use the limelight for tracking
        if (Math.abs(leftTrigger.getValue())>0.15 && driveState != driveType.CROSS) {
            driveState = driveType.LL;
            startingLL = true;
        }
        else {
            if (driveState == driveType.LL) {
                driveState = driveType.TELEOP;
                rotLocked = false;
            }
        }
        aimOffset = swerveHelper.scaleDeadband(leftStickX.getValue(), DriveConstants.DEADBAND);

    }
 
    @Override
    public void init() {
        initInputs();
        initOutputs();
        resetState();
        gyro.setYaw(0.0);
    }

    public void initInputs() {
        LC = new LimeConsts();
        LLpidX = new PIDController(LC.AUTO_ALIGN_PID_X[0], LC.AUTO_ALIGN_PID_X[1], LC.AUTO_ALIGN_PID_X[2]);
        LLpidY = new PIDController(LC.AUTO_ALIGN_PID_Y[0], LC.AUTO_ALIGN_PID_Y[1], LC.AUTO_ALIGN_PID_Y[2]);


        leftStickX = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_X);
        leftStickX.addInputListener(this);
        leftStickY = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_Y);
        leftStickY.addInputListener(this);
        rightStickX = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_JOYSTICK_X);
        rightStickX.addInputListener(this);
        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_TRIGGER);
        rightTrigger.addInputListener(this);
        leftTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_TRIGGER);
        leftTrigger.addInputListener(this);
        // rightBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_SHOULDER);
        // rightBumper.addInputListener(this);
        leftBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_SHOULDER);
        leftBumper.addInputListener(this);
        select = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_SELECT);
        select.addInputListener(this);
        start = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_START);
        start.addInputListener(this);
        faceUp = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_FACE_UP);
        faceUp.addInputListener(this);
        faceLeft = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_FACE_LEFT);
        faceLeft.addInputListener(this);
        faceRight = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_FACE_RIGHT);
        faceRight.addInputListener(this);
        faceDown = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_FACE_DOWN);
        faceDown.addInputListener(this);
        dpadLeft = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_DPAD_LEFT);
        dpadLeft.addInputListener(this);
    }

    public void initOutputs() {
        //create four swerve modules
        modules = new SwerveModule[]{
            new SwerveModule((WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.DRIVE1), 
                (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ANGLE1), DriveConstants.FRONT_LEFT_OFFSET),
            new SwerveModule((WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.DRIVE2), 
                (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ANGLE2), DriveConstants.FRONT_RIGHT_OFFSET),
            new SwerveModule((WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.DRIVE3), 
                (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ANGLE3), DriveConstants.REAR_LEFT_OFFSET),
            new SwerveModule((WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.DRIVE4), 
                (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ANGLE4), DriveConstants.REAR_RIGHT_OFFSET)
        };
        //create default swerveSignal
        swerveSignal = new SwerveSignal(new double[]{0.0, 0.0, 0.0, 0.0}, new double[]{0.0, 0.0, 0.0, 0.0});
        limelight = (AimHelper) Core.getSubsystemManager().getSubsystem(WSSubsystems.AIM_HELPER);
        odometry = new SwerveDriveOdometry(new SwerveDriveKinematics(new Translation2d(0.2794, 0.2794), new Translation2d(0.2794, -0.2794),
            new Translation2d(-0.2794, 0.2794), new Translation2d(-0.2794, -0.2794)), odoAngle(), odoPosition(), new Pose2d());
    }
    
    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        if (limelight.TargetInView && driveState != driveType.AUTO && (driveState != driveType.LL||startingLL)){
            odometry.resetPosition(odoAngle(), odoPosition(), new Pose2d(new Translation2d(-limelight.target3D[2], limelight.target3D[0]), odoAngle()));
        } 
        robotPose = odometry.update(odoAngle(), odoPosition());
        xAbsPos = odometry.getPoseMeters().getX();
        yAbsPos = odometry.getPoseMeters().getY();

        if (driveState == driveType.CROSS) {
            //set to cross - done in inputupdate
            this.swerveSignal = swerveHelper.setCross();
            drive();
        }
        if (driveState == driveType.TELEOP) {
            if (rotLocked){
                //if rotation tracking, replace rotational joystick value with controller generated one
                rotSpeed = swerveHelper.getRotControl(rotTarget, getGyroAngle());
                if (isSnake) {
                    if (Math.abs(rotSpeed) < 0.05) {
                        rotSpeed = 0;
                    }
                    else {
                        rotSpeed *= 4;
                        if (Math.abs(rotSpeed) > 1) rotSpeed = 1.0 * Math.signum(rotSpeed);
                    }
                    
                } 
            }
            this.swerveSignal = swerveHelper.setDrive(xSpeed, ySpeed, rotSpeed, getGyroAngle());
            SmartDashboard.putNumber("FR signal", swerveSignal.getSpeed(0));
            drive();
        }
        if (driveState == driveType.AUTO) {
            //get controller generated rotation value
            rotSpeed = Math.max(-0.2, Math.min(0.2, swerveHelper.getRotControl(pathTarget, getGyroAngle())));
            //ensure rotation is never more than 0.2 to prevent normalization of translation from occuring
            
            //update where the robot is, to determine error in path
            this.swerveSignal = swerveHelper.setAuto(swerveHelper.getAutoPower(pathVel), pathHeading, rotSpeed, getGyroAngle(), pathXOffset, pathYOffset);
            drive();        
        }
        if (driveState == driveType.LL) {
            if (rotLocked){
                rotSpeed = swerveHelper.getRotControl(rotTarget, getGyroAngle());
            }

            if (!limelight.gamepiece || (limelight.TargetInView && startingLL)){
                xSpeed = -LLpidX.calculate(limelight.getParallelDistance(), limelight.getParallelSetpoint() - 5.0*aimOffset);
                ySpeed = LLpidY.calculate(limelight.getNormalDistance(), LC.DESIRED_APRILTAG_DISTANCE + LC.LIMELIGHT_DISTANCE_OFFSET);
                if (Math.abs(xSpeed) > 0.2) xSpeed = Math.signum(xSpeed) * 0.2;
                if (Math.abs(ySpeed) > 0.2) ySpeed = Math.signum(ySpeed) * 0.2;    
            } else {
                startingLL = false;
                ySpeed = 0.01 * -(robotPose.getX()*mToIn - (LC.DESIRED_APRILTAG_DISTANCE + LC.LIMELIGHT_DISTANCE_OFFSET));
                if (robotPose.getY()>0.0){
                    if (robotPose.getY()<=1.5*LC.APRILTAG_HORIZONTAL_OFFSET){
                        xSpeed = 0.01 * (robotPose.getY()*mToIn - (LC.APRILTAG_HORIZONTAL_OFFSET - 10.0*aimOffset));
                    } else {
                        xSpeed = 0.01 * (robotPose.getY()*mToIn - (2.0*LC.APRILTAG_HORIZONTAL_OFFSET - 10.0*aimOffset));
                    }
                } else {
                    if (robotPose.getY()>-1.5*LC.APRILTAG_HORIZONTAL_OFFSET){
                        xSpeed = 0.01 * (robotPose.getY()*mToIn + (LC.APRILTAG_HORIZONTAL_OFFSET + 10.0*aimOffset));
                    } else {
                        xSpeed = 0.01 * (robotPose.getY()*mToIn + (2.0*LC.APRILTAG_HORIZONTAL_OFFSET + 10.0*aimOffset));
                    }
                }
                if (Math.abs(ySpeed)<0.05) ySpeed = 0.0;
                if (Math.abs(xSpeed)<0.05) xSpeed = 0.0;
                if (Math.abs(xSpeed) > 0.2) xSpeed = Math.signum(xSpeed) * 0.2;
                if (Math.abs(ySpeed) > 0.2) ySpeed = Math.signum(ySpeed) * 0.2; 
            }
            this.swerveSignal = swerveHelper.setDrive(xSpeed, ySpeed, rotSpeed, getGyroAngle());            
            drive();
        }   
        SmartDashboard.putNumber("Align robotY", robotPose.getY()*mToIn);
        SmartDashboard.putNumber("Align robotX", robotPose.getX()*mToIn);
        SmartDashboard.putNumber("Align limeX", -limelight.target3D[2]*mToIn);
        SmartDashboard.putNumber("Align limeY", limelight.target3D[0]*mToIn);
        SmartDashboard.putNumber("Gyro Reading", getGyroAngle());
        SmartDashboard.putNumber("X speed", xSpeed);
        SmartDashboard.putNumber("Y speed", ySpeed);
        SmartDashboard.putNumber("rotSpeed", rotSpeed);
        SmartDashboard.putString("Drive mode", driveState.toString());
        SmartDashboard.putBoolean("rotLocked", rotLocked);
        SmartDashboard.putNumber("Auto velocity", pathVel);
        SmartDashboard.putNumber("Auto translate direction", pathHeading);
        SmartDashboard.putNumber("Auto rotation target", pathTarget);
        SmartDashboard.putNumber("Absolute X Position", xAbsPos);
        SmartDashboard.putNumber("Absolute Y Distance", yAbsPos);
    }
    
    @Override
    public void resetState() {
        xSpeed = 0;
        ySpeed = 0;
        rotSpeed = 0;
        //gyro.reset();
        setToTeleop();
        rotLocked = false;
        rotTarget = 0.0;
        pathVel = 0.0;
        pathHeading = 0.0;
        pathTarget = 0.0;
        aimOffset = 0.0;
        lastOffset = 0.0;
        lastInView = true;
        startingLL = true;

        isFieldCentric = true;
        isSnake = false;
        robotPose = new Pose2d();
    }

    @Override
    public String getName() {
        return "Swerve Drive";
    }

    /** resets the drive encoders on each module */
    public void resetDriveEncoders() {
        for (int i = 0; i < modules.length; i++) {
            modules[i].resetDriveEncoders();
        }
    }

    /** sets the drive to teleop/cross, and sets drive motors to coast */
    public void setToTeleop() {
        driveState = driveType.TELEOP;
        for (int i = 0; i < modules.length; i++) {
            modules[i].setDriveBrake(false);
        }
        rotSpeed = 0;
        xSpeed = 0;
        ySpeed = 0;
        pathHeading = 0;
        pathVel = 0;
        rotLocked = false;
    }

    /**sets the drive to autonomous */
    public void setToAuto() {
        driveState = driveType.AUTO;
        resetDriveEncoders();
    }

    /**drives the robot at the current swerveSignal, and displays information for each swerve module */
    private void drive() {
        if (driveState == driveType.CROSS) {
            for (int i = 0; i < modules.length; i++) {
                modules[i].runCross(swerveSignal.getSpeed(i), swerveSignal.getAngle(i));
                modules[i].displayNumbers(DriveConstants.POD_NAMES[i]);
            }
        }
        else {
            for (int i = 0; i < modules.length; i++) {
                modules[i].run(swerveSignal.getSpeed(i), swerveSignal.getAngle(i));
                modules[i].displayNumbers(DriveConstants.POD_NAMES[i]);
            }
        }
    }

    /**sets autonomous values from the path data file */
    public void setAutoValues(double velocity, double heading, double xOffset, double yOffset) {
        pathVel = velocity;
        pathHeading = heading;
        pathXOffset = xOffset;
        pathYOffset = yOffset;
    }

    /**sets the autonomous heading controller to a new target */
    public void setAutoHeading(double headingTarget) {
        pathTarget = headingTarget;
    }

    public void setAiming() {
        driveState = driveType.LL;
    }

    /**
     * Resets the gyro, and sets it the input number of degrees
     * Used for starting the match at a non-0 angle
     * @param degrees the current value the gyro should read
     */
    public void setGyro(double degrees) {
        resetState();
        setToAuto();
        gyro.setYaw(degrees);
    }

    public double getGyroAngle() {
        if (!isFieldCentric) return 0;
        //limelight.setGyroValue((gyro.getYaw() + 360)%360);
        return (359.99 - gyro.getYaw()+360)%360;
    }  
    public Rotation2d odoAngle(){
        return new Rotation2d(Math.toRadians(360-getGyroAngle()));
    }
    public SwerveModulePosition[] odoPosition(){
        return new SwerveModulePosition[]{modules[0].odoPosition(), modules[1].odoPosition(), modules[2].odoPosition(), modules[3].odoPosition()};
    }
    public void setOdo(Pose2d starting){
        odometry.resetPosition(odoAngle(), odoPosition(), starting);
    }
    public Pose2d returnPose(){
        return odometry.getPoseMeters();
    }
    public double getRotTarget(){
        return rotTarget;
    }
}
