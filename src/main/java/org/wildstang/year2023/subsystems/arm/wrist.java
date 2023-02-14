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
public class wrist {
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
   // private double EncodedPositition;
    private double position;
    private static final double tolerance = 4;
    private static final double minPosition = 0;
    private static final double maxPosition = 180;
    public void init() {
        //joystick = (WsJoystickAxis) WSInputs.DRIVER_LEFT_JOYSTICK_Y.get();
        position = minPosition;
        baseMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.WRIST);
        encoder = baseMotor.getController().getAbsoluteEncoder(Type.kDutyCycle); //idk if this is actually right
        encoder.setInverted(false); //or this stuff
        encoder.setPositionConversionFactor(360.0);
        encoder.setVelocityConversionFactor(360.0 / 60.0);
        encoder.setZeroOffset(206.7);
        baseMotor.initClosedLoop(ArmConstants.WRIST_P, ArmConstants.WRIST_I, ArmConstants.WRIST_D, 0, this.encoder);
        baseMotor.setCurrentLimit(ArmConstants.WRIST_CURRENT_LIMIT, ArmConstants.WRIST_CURRENT_LIMIT, 0);
        resetState();
    }

    public void stopMotor() {
        baseMotor.stop();
    }

    public void goToPosition(double pos) {
        if (pos > minPosition && pos < maxPosition) {
            baseMotor.setPosition(pos);
            position = pos;
            SmartDashboard.putNumber("Wrist target", pos);
        } else {
            SmartDashboard.putNumber("Wrist target", -999);
        }
    }
    
    public double getPosition(){
        return baseMotor.getPosition();
    }
    
    public boolean isReady(){
        SmartDashboard.putNumber("Wrist pos", position);
        if(Math.abs(baseMotor.getPosition() - position) < tolerance){
            return true;
        }
        return false;
    }

    public void resetState() {
        position = minPosition;
    }

    public String getName() {
        return "Wrist";
    }

}