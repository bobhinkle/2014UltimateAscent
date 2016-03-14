package SubSystems;

import Sensors.UltraSonic;
import Team1323Robot.FSM;
import Utilities.Ports;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
public class Claw {
    private Victor topIntake,sideIntake,sideIntake2;
    private static Claw instance = null;
    private Solenoid piston1,piston2;
    private final double topForward = 1.0;
    private final double bottomForward = 1.0;
    public UltraSonic ultrasonic;
    private final static int CLOSED = 1;
    private final static int OPEN = 2;
    private final static int NEUTRAL = 3;
    private int state = Claw.CLOSED;
    private FSM fsm;
    private boolean hasBall = true;
    private boolean rollersOn = false;
    public static Claw getInstance()
    {
        if( instance == null )
            instance = new Claw();
        return instance;
    }
    public Claw(){
        topIntake = new Victor(Ports.CLAW_TOP);
        sideIntake = new Victor(Ports.CLAW_INTAKE);
        sideIntake2 = new Victor(Ports.CLAW_INTAKE_2);
        piston1 = new Solenoid(Ports.CLAW_PISTON1);
        piston2 = new Solenoid(Ports.CLAW_PISTON2);
        ultrasonic = new UltraSonic(Ports.ULTRA_SONIC);
    }
    public void neutral(){
        piston1.set(true);
        piston2.set(false);
        state = Claw.NEUTRAL;
    }
    public void close(){
        piston1.set(false);
        piston2.set(false);
        state = Claw.CLOSED;
    }
    public void open(){
        piston1.set(true);
        piston2.set(true);
        state = Claw.OPEN;
    }
    public void allForward(){
        topIntake.set(topForward);
        sideIntake.set(bottomForward); //port 3
        sideIntake2.set(bottomForward); //port 8
        rollersOn = true;
    }
    public void allStop(){
        topIntake.set(0);
        sideIntake.set(0);
        sideIntake2.set(0);
        rollersOn = false;
    }
    public void allReverse(){
        topIntake.set(topForward);
        sideIntake.set(-bottomForward);
        sideIntake2.set(-bottomForward);
        rollersOn = true;
    }
    public void sideForward(){
        sideIntake.set(bottomForward); //port 3
        sideIntake2.set(bottomForward); //port 8
        rollersOn = true;
    }
    public void stopSide(){
        sideIntake.set(0);
        sideIntake2.set(0);
    }
    public void topForward(){
        topIntake.set(topForward);
        rollersOn = true;
    }
    public boolean hasBall(){
        return ultrasonic.getDistance() <= 7.0;
    }
    public boolean rollersOn(){
        return rollersOn;
    }
    public void ballFired(){
        hasBall = false;
    }
    public void run(){
        if(fsm == null){
            fsm = FSM.getInstance();
        }
        if(hasBall()){
            if((fsm.getCurrentState() == FSM.AUTON_PICK_UP)){
                allStop();
                hasBall = true;
            }
        }
    }
}
