package org.wildstang.year2023.subsystems.wrist;

import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
//import org.wildstang.hardware.roborio.outputs.WsPhoenix;
import org.wildstang.year2023.robot.WSInputs;
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
    private WsSparkMax BaseMotor;
    private AbsoluteEncoder Encoder;
    //private int direction;
    //private double BaseSpeed = 5.5;
   // private double EncodedPositition;
    private double position;
    private static final double tolerance = 1;
    private static final double minPosition = 0;
    private static final double maxPosition = 0;
    public void init() {
        //joystick = (WsJoystickAxis) WSInputs.DRIVER_LEFT_JOYSTICK_Y.get();
        position = minPosition;
        BaseMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.WRIST);
        Encoder = BaseMotor.getController().getAbsoluteEncoder(Type.kDutyCycle); //idk if this is actually right
        Encoder.setInverted(false); //or this stuff
        Encoder.setPositionConversionFactor(360.0);
        Encoder.setVelocityConversionFactor(360.0/60.0);
        BaseMotor.initClosedLoop(ArmConstants.WRIST_P, ArmConstants.WRIST_I, ArmConstants.WRIST_D,0, this.Encoder);
        BaseMotor.setCurrentLimit(ArmConstants.WRIST_CURRENT_LIMIT, ArmConstants.WRIST_CURRENT_LIMIT, 0);
        resetState();
    }

    public void stopMotor() {
        BaseMotor.stop();
    }

    public void goToPosition(double pos) {
        if (pos > minPosition && pos < maxPosition) {
            BaseMotor.setPosition(pos);
            position = pos;
            SmartDashboard.putNumber("Wrist target", pos);
        } else {
            SmartDashboard.putNumber("Wrist target", -999);
        }
    }
    
    public double getPosition(){
        return BaseMotor.getPosition();
    }
    
    public boolean isReady(){
        SmartDashboard.putNumber("Wrist pos", position);
        if(Math.abs(BaseMotor.getPosition()-position)<tolerance){
            return true;
        }
        return false;
    }

    public void resetState() {
        position = minPosition;
        BaseMotor.setPosition(position);
    }

    public String getName() {
        return "Wrist";
    }

}