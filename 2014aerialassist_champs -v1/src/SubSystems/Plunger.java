/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SubSystems;

import Sensors.AnalogPressure;
import Utilities.Constants;
import Utilities.Ports;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author xpsl05x
 */
public class Plunger{
    private static Plunger instance = null;
    private final DigitalInput stopPosition,hardStopPosition;
    private boolean keepRunning = true;
    private final Victor motor;
    private final Solenoid piston;
    public final static int INIT = 0;
    public final static int STOP_LIMIT = 1;
    public final static int HARD_LIMIT = 2;
    public final static int RELOADING = 3;
    public final static int MALFUNCTION = 4;
    public final static int FIRE = 5;
    public final static int FIRING = 6;
    public final static int ARMED = 7;
    public final static boolean PISTON_LOCKED = false;
    private int currentState = Plunger.INIT;
    private final double motor_power = 1.0;
    private final killThread failsafe;
    private final fireThread fire;
    private AnalogPressure airPressure;
    private boolean fireThread = false;
    public static Plunger getInstance()
    {
        if( instance == null )
            instance = new Plunger();
        return instance;
    }
    public int currentState(){
        return currentState;
    }
    private Plunger(){
        stopPosition = new DigitalInput(Ports.PLUNGER_STOP_SENSOR);
        hardStopPosition = new DigitalInput(Ports.PLUNGER_HARDSTOP_SENSOR);
        motor = new Victor(Ports.PLUNGER);
        piston = new Solenoid(Ports.PLUNGER_PISTON);
        failsafe = new killThread();
        fire = new fireThread();
        airPressure = new AnalogPressure(Ports.AIR_PRESSURE,Constants.VOLTS_TO_PSI);
    }
    public void checkPlunger(){
        if(!this.hardStopPosition() || !this.stopPosition()){
            currentState = Plunger.RELOADING;
        }
    }
    private void lockRatchet(){
        piston.set(Plunger.PISTON_LOCKED);
    }
    private void releaseRatchet(){
        piston.set(!Plunger.PISTON_LOCKED);
    }
    private void loadPlunger(){
        motor.set(this.motor_power);
    }
    private void stopPlunger(){
        motor.set(0);
    }
    public void fire(){
        currentState = Plunger.FIRE;
    }
    public void run(){
        try {
            keepRunning = true;
                switch(currentState){
                    case INIT:
                        SmartDashboard.putString("PLUNGER_STATE", "INIT");
                        if(this.hardStopPosition() || this.stopPosition()){
                            currentState = Plunger.ARMED;
                        }else{
                            currentState = Plunger.RELOADING;
                        }
                        break;
                    case STOP_LIMIT:
                        
                        break;
                    case HARD_LIMIT:
                        
                        break;
                    case RELOADING:
                        SmartDashboard.putString("PLUNGER_STATE", "RELOADING");
                        if(this.hardStopPosition() || this.stopPosition()){
                            if(this.hardStopPosition()){
                                SmartDashboard.putString("PLUNGER_HIT", "HARD");
                            }else{
                                SmartDashboard.putString("PLUNGER_HIT", "SOFT");
                            }
                            stopPlunger();
                            currentState = Plunger.ARMED;
                        }else{
                            this.loadPlunger();
                        }
                        break;
                    case MALFUNCTION:
                        stopPlunger();
                        break;
                    case FIRE:
                        fire.run();
                        currentState = Plunger.FIRING;
                        break;
                    case FIRING:
                        if(!fireThread)
                            currentState = Plunger.RELOADING;
                        break;
                    case ARMED:
                        SmartDashboard.putString("PLUNGER_STATE", "ARMED");
                        break;
                    default:
                        SmartDashboard.putString("PLUNGER_STATE", "DEFAULT");
                        break;
                }
                SmartDashboard.putNumber("AIR_PRESSURE", airPressure.getPressure());
                if(this.hardStopPosition()){
                    SmartDashboard.putString("PLUNGER_FRONT", "ARMED");
                }else{
                    SmartDashboard.putString("PLUNGER_FRONT", "SOFT");
                }
                if(this.stopPosition()){
                    SmartDashboard.putString("PLUNGER_BACK", "ARMED");
                }else{
                    SmartDashboard.putString("PLUNGER_BACK", "SOFT");
                }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    private boolean stopPosition(){
        return !this.stopPosition.get();
    }
    private boolean hardStopPosition(){
        return !this.hardStopPosition.get();
    }
    private class fireThread extends Thread{
        
    private final double MAX_FIRE_TIME;

        private fireThread() {
            MAX_FIRE_TIME = 0.1;
        }
        public void run(){
            try {
                fireThread = true;
                SmartDashboard.putString("PLUNGER_STATE", "FIRE2");
                releaseRatchet();
                Timer.delay(0.25);
                lockRatchet();
                SmartDashboard.putString("PLUNGER_STATE", "FIRE3");
                currentState = Plunger.RELOADING;
                fireThread = false;
            }catch(Exception e){
                System.out.println("crash" + e.toString());
            }            
        }
    }
    private class killThread extends Thread{
        
    private final double MAX_RELOAD_TIME;

        private killThread() {
            this.MAX_RELOAD_TIME = 2.0;
        }
        public void run(){
            try {
                System.out.println("killTimer");
                Timer.delay(this.MAX_RELOAD_TIME);
                if(currentState != Plunger.ARMED){
                    SmartDashboard.putString("PLUNGER_STATE", "MALFUNCTION");
                    currentState = Plunger.MALFUNCTION;
                }
                System.out.println("killTimer2");
            }catch(Exception e){
                System.out.println("crash" + e.toString());
            }            
        }
    }
}
