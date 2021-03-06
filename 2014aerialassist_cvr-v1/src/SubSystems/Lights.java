/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SubSystems;

import Utilities.Ports;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Relay;

/**
 *
 * @author Rohi Zacharia
 */
public class Lights {
    private Relay state1;
    private Relay state2;
    private Relay state3;
    private DigitalOutput gyroInit;
    
    private static Lights instance = null;
    private int lastState = -1;
    private int currentState = 0;
    private boolean setToTransmit = false;
    public final static int GYRO_INIT = 0;
    public final static int GYRO_COMP = 1;
    public Lights(){
        state1 = new Relay(Ports.STATE1, Relay.Direction.kForward);
        state2 = new Relay(Ports.STATE2, Relay.Direction.kForward);
        state3 = new Relay(Ports.STATE3, Relay.Direction.kForward);
        gyroInit = new DigitalOutput(12);
    }
    public static Lights getInstance()
    {
        if( instance == null )
            instance = new Lights();
        return instance;
    }
    public void setState(int state){
        currentState = state;
    }
    public void stateComplete(int state){
        lastState = state;
    }
    public void updateLights(){
        switch(currentState){
            case Lights.GYRO_INIT:
                state1.set(Relay.Value.kOff);
                state2.set(Relay.Value.kOff);
                state3.set(Relay.Value.kOff);
                gyroInit.set(true);
                System.out.println("case 0");
                break;
            case Lights.GYRO_COMP:
                state1.set(Relay.Value.kOn);
                state2.set(Relay.Value.kOff);
                state3.set(Relay.Value.kOff);
                gyroInit.set(false);
                System.out.println("case 1");
                break;
            case 2:
                state1.set(Relay.Value.kOff);
                state2.set(Relay.Value.kOn);
                state3.set(Relay.Value.kOff);
                break;
            case 3:
                state1.set(Relay.Value.kOn);
                state2.set(Relay.Value.kOn);
                state3.set(Relay.Value.kOff);
                break;
            case 4:
                state1.set(Relay.Value.kOff);
                state2.set(Relay.Value.kOff);
                state3.set(Relay.Value.kOn);
                break;
            case 5:
                state1.set(Relay.Value.kOn);
                state2.set(Relay.Value.kOff);
                state3.set(Relay.Value.kOn);
                break;
            case 6:
                state1.set(Relay.Value.kOff);
                state2.set(Relay.Value.kOn);
                state3.set(Relay.Value.kOn);
                break;
            case 7:
                state1.set(Relay.Value.kOn);
                state2.set(Relay.Value.kOn);
                state3.set(Relay.Value.kOn);
                break;
        }
    }
}