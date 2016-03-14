/*
 
 * 
 * 
 */
package Team1323Robot;

import IO.TeleController;
import Sensors.I2C_UltraSonic;
import Sensors.LightSensor;
import SubSystems.Blocker;
import SubSystems.Claw;
import SubSystems.Compressor;
import SubSystems.DistanceController;
import SubSystems.DriveTrain;
import SubSystems.Elevator;
import SubSystems.Kinect;
import SubSystems.Lights;
import SubSystems.Navigation;
import SubSystems.Plunger;
import SubSystems.TurnController;
import SubSystems.Wrist;
import Utilities.Constants;
import Utilities.Ports;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;

public class Robot{
    
    private static Robot instance = null;
    public DriveTrain dt;
    public Navigation nav;
    public Lights lights;
    private TeleController control;
    public Compressor comp;
    public Claw claw;
    public Wrist wrist;
    public boolean autoTurn = false;
    public Plunger plunger;
    public Elevator elevator;
    public LightSensor hotGoal;
    public I2C_UltraSonic ultra2;
    public DigitalInput gyroInit;
    public double hotGoalCali = 0;
    private boolean enabled = false;
    public Kinect kinect;
    public Blocker blocker;
    public int autonMode;
    public Relay arm;
    private  distanceThread distTh;
    public TurnController turn; 
    public DistanceController dist;
    public Robot(){
        lights = Lights.getInstance();
        dt = DriveTrain.getInstance();
        nav = Navigation.getInstance();
        claw   = Claw.getInstance();
        wrist  = Wrist.getInstance();
        plunger = Plunger.getInstance();
        elevator = Elevator.getInstance();
        comp = new Compressor();        
        blocker = new Blocker();
        hotGoal = new LightSensor(Ports.LIGHT_SENSOR);
        ultra2 = new I2C_UltraSonic();
        gyroInit = new DigitalInput(Ports.GYRO_INIT_BUTTON);
        kinect = new Kinect();
        arm = new Relay(Ports.ARM_RELAY);
        dist = DistanceController.getInstance();
        distTh = new distanceThread(false);
    }
    public static Robot getInstance()
    {
        if( instance == null )
            instance = new Robot();
        return instance;
    }
    public void loadSubsystems(){
        control = TeleController.getInstance();
    }
    public void enabled(){
        enabled = true;
    }
    public boolean isEnabled(){
        return enabled;
    }
    public int getHotGoal(){
        if(kinect.getLeft() < -0.3){
            return Constants.LEFT_GOAL;
        }
        return Constants.RIGHT_GOAL;
    }
    public void armOut(){
        arm.set(Relay.Value.kOn);
    }
    public void armIn(){
        arm.set(Relay.Value.kOff);
    }
    public void stopAllMotors(){
        claw.allStop();
    }
    public double driveDistanceHoldingHeading(double distance, double heading,double maxSpeed,double timeout,double tol, boolean holdSpeed,double breakTime){                
        dist.resetDistance();
        double startDistance = 0;
        double distanceChange = distance + startDistance;
        System.out.println("DD: " + startDistance + " " + distanceChange + " " + distance);
        dist.reset();
        dist.setGoal(distanceChange, maxSpeed,heading,timeout,tol,holdSpeed);
        distTh.run();
        return nav.getDistance() - startDistance;
    }
    private class distanceThread extends Thread{
        private boolean keeprunning = false;
        public distanceThread(boolean keeper){
            keeprunning = keeper;
        }
        public void run(){
                if(!keeprunning){
                    while(!dist.onTarget()){
                        dist.run();
                        Timer.delay(0.01);
                    }
                }else{
                   
                }
                System.out.println("done");
                dt.directDrive(0, 0);
                       
        }
    }
}
