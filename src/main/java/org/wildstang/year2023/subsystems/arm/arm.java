package org.wildstang.year2023.subsystems.arm;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.year2023.robot.WSOutputs;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.wildstang.framework.core.Core;
import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;

import org.wildstang.year2023.subsystems.arm.ArmConstants;

/**
 * not Sample Subsystem that controls a motor with a joystick.
 * @author not Liam
 */
public class arm {
    // inputs
   // WsJoystickAxis joystick;

    // outputs

    // states
    //double speed;
    //private DigitalInput Rotate_Clockwise, Rotate_Counter_Clockwise;
    private WsSparkMax baseMotor;
    private AbsoluteEncoder encoder;
    //private int direction;
    //private double BaseSpeed = 5.5;
    private double position;
    private static final double defaultPosition = 0;
    private static final double tolerance = 0;
    private static final double holdingPosition = 0;

    private enum mode {
        EXTENDED,
        HOLDING;
    }
    private mode currentMode;
    public void init() {
        //joystick = (WsJoystickAxis) WSInputs.DRIVER_LEFT_JOYSTICK_Y.get()
        baseMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ARM_ONE);
        encoder = baseMotor.getController().getAbsoluteEncoder(Type.kDutyCycle); //idk if this is actually right
        encoder.setInverted(false); //or this stuff
        encoder.setPositionConversionFactor(360.0);
        encoder.setVelocityConversionFactor(360.0 / 60.0);
        baseMotor.initClosedLoop(ArmConstants.ARM_P_HOLDING, ArmConstants.ARM_I_HOLDING, ArmConstants.ARM_D_HOLDING,0, this.encoder);
        encoder.setZeroOffset(29.3);
        baseMotor.setCurrentLimit(ArmConstants.ARM_CURRENT_LIMIT, ArmConstants.ARM_CURRENT_LIMIT, 0);
        resetState();
        
    }

    public void stopMotor() {
        baseMotor.stop();
    }

    public void goToPosition(double pos) {
        if(pos < holdingPosition && currentMode == mode.EXTENDED){
            currentMode = mode.HOLDING;
            baseMotor.initClosedLoop(ArmConstants.ARM_P_HOLDING, ArmConstants.ARM_I_HOLDING, ArmConstants.ARM_D_HOLDING,0, this.encoder);
        }
        else if(pos>holdingPosition && currentMode == mode.HOLDING){
            currentMode = mode.EXTENDED;
            baseMotor.initClosedLoop(ArmConstants.ARM_P_EXTENDED, ArmConstants.ARM_I_EXTENDED, ArmConstants.ARM_D_EXTENDED,0, this.encoder);
        }
        baseMotor.setPosition(pos);
        position = pos;
        SmartDashboard.putNumber("Arm target", position);
    }
    
    public double getPosition(){
        return baseMotor.getPosition();
    }
    
    public boolean isReady(){
        SmartDashboard.putNumber("Arm pos", baseMotor.getPosition());
        if(Math.abs(baseMotor.getPosition() - position) < tolerance){
            return true;
        }
        return false;
    }

    public void resetState() {
        position = defaultPosition;
        currentMode = mode.HOLDING;
        baseMotor.setPosition(position);
    }

    public String getName() {
        return "Arm";
    }

}