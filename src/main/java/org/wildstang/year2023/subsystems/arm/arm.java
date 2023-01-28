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
    private WsSparkMax BaseMotor;
    private AbsoluteEncoder Encoder;
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
        BaseMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ARM_ONE);
        Encoder = BaseMotor.getController().getAbsoluteEncoder(Type.kDutyCycle); //idk if this is actually right
        Encoder.setInverted(false); //or this stuff
        Encoder.setPositionConversionFactor(360.0);
        Encoder.setVelocityConversionFactor(360.0/60.0);
        BaseMotor.initClosedLoop(ArmConstants.ARM_P_HOLDING, ArmConstants.ARM_I_HOLDING, ArmConstants.ARM_D_HOLDING,0, this.Encoder);
        BaseMotor.setCurrentLimit(ArmConstants.ARM_CURRENT_LIMIT, ArmConstants.ARM_CURRENT_LIMIT, 0);
        resetState();
        
    }

    public void stopMotor() {
        BaseMotor.stop();
    }

    public void goToPosition(double pos) {
        if(pos<holdingPosition && currentMode == mode.EXTENDED){
            currentMode = mode.HOLDING;
            BaseMotor.initClosedLoop(ArmConstants.ARM_P_HOLDING, ArmConstants.ARM_I_HOLDING, ArmConstants.ARM_D_HOLDING,0, this.Encoder);
        }
        else if(pos>holdingPosition && currentMode == mode.HOLDING){
            currentMode = mode.EXTENDED;
            BaseMotor.initClosedLoop(ArmConstants.ARM_P_EXTENDED, ArmConstants.ARM_I_EXTENDED, ArmConstants.ARM_D_EXTENDED,0, this.Encoder);
        }
        BaseMotor.setPosition(pos);
        position = pos;
        SmartDashboard.putNumber("Arm target", position);
    }
    
    public double getPosition(){
        return BaseMotor.getPosition();
    }
    
    public boolean isReady(){
        SmartDashboard.putNumber("Arm pos", BaseMotor.getPosition());
        if(Math.abs(BaseMotor.getPosition()-position)<tolerance){
            return true;
        }
        return false;
    }

    public void resetState() {
        position = defaultPosition;
        currentMode = mode.HOLDING;
        BaseMotor.setPosition(position);
    }

    public String getName() {
        return "Arm";
    }

}