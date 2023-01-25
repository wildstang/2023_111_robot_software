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
    //private int direction;
    //private double BaseSpeed = 5.5;
   // private double EncodedPositition;
    private double Pos;
    private static final double Tolerance = 1;
    private static final double minPosition = 0;
    private static final double maxPosition = 0;
    public void init() {
        //joystick = (WsJoystickAxis) WSInputs.DRIVER_LEFT_JOYSTICK_Y.get();
        Pos = minPosition;
        BaseMotor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.WRIST);
    }

    public void stopMotor() {
        BaseMotor.stop();
    }

    public void goToPosition(double pos) {
        if (pos > minPosition && pos < maxPosition) {
            BaseMotor.setPosition(pos);
            Pos = pos;
            SmartDashboard.putNumber("Wrist target", pos);
        } else {
            SmartDashboard.putNumber("Wrist target", -999);
        }
    }
    
    public double getPosition(){
        return BaseMotor.getPosition();
    }
    
    public boolean isReady(){
        SmartDashboard.putNumber("Wrist pos", Pos);
        if(Math.abs(BaseMotor.getPosition()-Pos)<Tolerance){
            return true;
        }
        return false;
    }

    public void resetState() {
        Pos = minPosition;
    }

    public void update() {
    
    }


    public String getName() {
        return "Wrist";
    }

    public void selfTest() {
    }
}