package org.wildstang.year2023.subsystems.arm;
import org.wildstang.year2023.robot.WSOutputs;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.framework.core.Core;
import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;

import org.wildstang.year2023.subsystems.arm.ArmConstants;
/**
 * Sample Subsystem that controls a motor with a joystick.
 * @author Liam
 */
public class lift {
    // inputs
   // WsJoystickAxis joystick;

    // outputs

    // states
    //double speed;
    //private DigitalInput Rotate_Clockwise, Rotate_Counter_Clockwise;
    private WsSparkMax liftDriver;
    private AbsoluteEncoder encoder;
    //private int direction;
    //private double BaseSpeed = 5.5;
   // private double EncodedPositition;
    private double position;
    private static final double tolerance = 1;
    private static final double minPosition = 0;
    private static final double maxPosition = 75;
    private static final double holdingPosition = 40;
    private enum mode {
        EXTENDED,
        HOLDING;
    }
    private mode currentMode;
    public void init() {
        //joystick = (WsJoystickAxis) WSInputs.DRIVER_LEFT_JOYSTICK_Y.get();
        currentMode = mode.HOLDING;
        liftDriver = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.LIFT_DRIVER);
        liftDriver.initClosedLoop(ArmConstants.LIFT_P_HOLDING, ArmConstants.LIFT_I_HOLDING, ArmConstants.LIFT_D_HOLDING,0, this.encoder);
        liftDriver.setCurrentLimit(ArmConstants.LIFT_CURRENT_LIMIT, ArmConstants.LIFT_CURRENT_LIMIT, 0);
        resetState();
    }

    public void stopMotor() {
        liftDriver.stop();
    }

    public void goToPosition(double pos) {
        if (pos > minPosition && pos < maxPosition) {
            /*if(pos < holdingPosition && currentMode == mode.EXTENDED){
                currentMode = mode.HOLDING;
                liftDriver.initClosedLoop(ArmConstants.LIFT_P_HOLDING, ArmConstants.LIFT_I_HOLDING, ArmConstants.LIFT_D_HOLDING,0, this.encoder);
                //BaseMotor.setCurrentLimit(ArmConstants.LIFT_CURRENT_LIMIT, ArmConstants.LIFT_CURRENT_LIMIT, 0);
            }
            else if(pos > holdingPosition && currentMode == mode.HOLDING){
                currentMode = mode.EXTENDED;
                liftDriver.initClosedLoop(ArmConstants.LIFT_P_EXTENDED, ArmConstants.LIFT_I_EXTENDED, ArmConstants.LIFT_D_EXTENDED,0, this.encoder);
            }
            */
            liftDriver.setPosition(pos);
            position = pos;
            SmartDashboard.putNumber("Lift target", pos);
        } else {
            SmartDashboard.putNumber("Lift target", -999);
        }
    }
    
    public double getPosition(){
        return liftDriver.getPosition();
    }
    
    public boolean isReady(){
        SmartDashboard.putNumber("Lift pos", position);
        if(Math.abs(liftDriver.getPosition() - position) < tolerance){
            return true;
        }
        return false;
    }

    public void resetEncoder(){
        liftDriver.resetEncoder();
    }
    public void setSpeed(double speed, boolean useHoldingPID){
        liftDriver.setSpeed(speed);
    }
    public void resetState() {
        position = minPosition;
        liftDriver.setPosition(position);
    }
    public String getName() {
        return "Lift";
    }

}
