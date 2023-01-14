package org.wildstang.year2023.subsystems.arm;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.core.Inputs;
import org.wildstang.framework.hardware.InputConfig;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.JoystickConstants;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.year2023.robot.WSInputs;
import org.wildstang.year2023.robot.WSOutputs;

/*Arm Subsystem
     * Left Joystick Y: controls speed of arm. Forward is positive speed, backward is negative speed
     * Nothing else, prototype
     * @author: Shane
     */

public class Arm implements Subsystem {

    WsSparkMax arm_motor;


    WsJoystickAxis right_joystick_y;


    double arm_position;
    private enum State {OVERRIDE, IDLE};
    private State state;


    private final double ABS_MAX = 50.0;
    private final double ABS_MIN = -50.0;

    @Override
    public void init(){
        arm_motor = (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.TEST_MOTOR);//prototype arm runs off of test motor
        arm_motor.initClosedLoop(1.0, 0.0, 0.1, 0.0);

        right_joystick_y = (WsJoystickAxis) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_JOYSTICK_Y);
        right_joystick_y.addInputListener(this);

        resetState();
    }

    @Override
    public void inputUpdate(Input source) {
        if (source == right_joystick_y && right_joystick_y.getValue() != 0.0){
            state = State.OVERRIDE;
        }else{
            state = State.IDLE;
        }
        
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update() {
        if (state == State.OVERRIDE){

            if(right_joystick_y.getValue() < 0){
                if(arm_motor.getPosition() > ABS_MIN){
                    arm_motor.setSpeed(-1.0);
                }else{
                    state = State.IDLE;
                }
                //when joystick is pulled backwards
            }else if(right_joystick_y.getValue() > 0){
                if(arm_motor.getPosition() < ABS_MAX){
                    arm_motor.setSpeed(1.0);
                }else{
                    state = State.IDLE;
                }
                //when joystick is pushed forward
            }
        }
        if (state == State.IDLE){
            arm_motor.setSpeed(0.0);
        }
    }

    @Override
    public void resetState() {
        arm_motor.resetEncoder();
        state = State.IDLE;
        
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Arm";
    }
}
