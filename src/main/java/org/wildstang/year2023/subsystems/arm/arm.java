package org.wildstang.year2023.subsystems.arm;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.year2023.robot.WSOutputs;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.wildstang.framework.core.Core;

/**
 * Sample Subsystem that controls a motor with a joystick.
 * @author Liam
 */
public class arm {
    // inputs
   // WsJoystickAxis joystick;

    // outputs

    // states
    //double speed;
    //private DigitalInput Rotate_Clockwise, Rotate_Counter_Clockwise;
    private WsSparkMax BaseMotor;
    //private int direction;
    //private double BaseSpeed = 5.5;
    private double EncodedPositition;
    private double Pos;
    private static final double Tolerance = 0;
    public void init() {
        //joystick = (WsJoystickAxis) WSInputs.DRIVER_LEFT_JOYSTICK_Y.get();
        Pos = 0;
        BaseMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ARM_ONE);
    }

    public void stopMotor() {
        BaseMotor.stop();
    }

    public void goToPosition(double pos) {
        BaseMotor.setPosition(pos);
        Pos = pos;
        SmartDashboard.putNumber("Arm target", pos);
    }
    
    public double getPosition(){
        return BaseMotor.getPosition();
    }
    
    public boolean isReady(){
        SmartDashboard.putNumber("Arm pos", Pos);
        if(Math.abs(BaseMotor.getPosition()-Pos)<Tolerance){
            return true;
        }
        return false;
    }

    public void resetState() {
        Pos = 0;
    }

    public void update() {
    
    }


    public String getName() {
        return "Arm";
    }

    public void selfTest() {
    }
}