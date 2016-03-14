/*
 
 * 
 * 
 */
package Team1323Robot;

import IO.TeleController;
import Sensors.I2C_UltraSonic;
import Sensors.LightSensor;
import SubSystems.Claw;
import SubSystems.Compressor;
import SubSystems.DriveTrain;
import SubSystems.Elevator;
import SubSystems.Lights;
import SubSystems.Navigation;
import SubSystems.Plunger;
import SubSystems.Wrist;
import Utilities.Constants;
import Utilities.Ports;
import edu.wpi.first.wpilibj.DigitalInput;

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
    public Robot(){
//        lights = Lights.getInstance();
        dt = DriveTrain.getInstance();
        nav = Navigation.getInstance();
        claw   = Claw.getInstance();
        wrist  = Wrist.getInstance();
        plunger = Plunger.getInstance();
        elevator = Elevator.getInstance();
        comp = new Compressor();
        hotGoal = new LightSensor(Ports.LIGHT_SENSOR);
        ultra2 = new I2C_UltraSonic();
        gyroInit = new DigitalInput(Ports.GYRO_INIT_BUTTON);
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
        if(hotGoal.intensity()>=hotGoalCali-50)
            return Constants.RIGHT_GOAL;
        return Constants.LEFT_GOAL;
    }
    
    public void stopAllMotors(){
        claw.allStop();
    }
}
