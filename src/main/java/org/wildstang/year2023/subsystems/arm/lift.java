package org.wildstang.year2023.subsystems.arm;
import org.wildstang.year2023.robot.WSOutputs;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.framework.core.Core;
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
    //private int direction;
    //private double BaseSpeed = 5.5;
   // private double EncodedPositition;
    private double Pos;
    private static final double Tolerance = 1;
    private static final double minPosition = 0;
    private static final double maxPosition = 0;
    public void init() {
        //joystick = (WsJoystickAxis) WSInputs.DRIVER_LEFT_JOYSTICK_Y.get();
        Pos = 0;
        liftDriver = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.LIFT_DRIVER);

    }

    public void stopMotor() {
        liftDriver.stop();
    }

    public void goToPosition(double pos) {
        if (pos > minPosition && pos < maxPosition) {
            liftDriver.setPosition(pos);
            Pos = pos;
            SmartDashboard.putNumber("Lift target", pos);
        } else {
            SmartDashboard.putNumber("Lift target", -999);
        }
    }
    
    public double getPosition(){
        return liftDriver.getPosition();
    }
    
    public boolean isReady(){
        SmartDashboard.putNumber("Lift pos", Pos);
        if(Math.abs(liftDriver.getPosition()-Pos)<Tolerance){
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
        return "Lift";
    }

    public void selfTest() {
    }
}
