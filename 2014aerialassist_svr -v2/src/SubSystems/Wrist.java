package SubSystems;

import Sensors.MA3;
import Team1323Robot.Robot;
import Utilities.Constants;
import Utilities.Ports;
import Utilities.Util;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Jared Russell
 */
public class Wrist extends SynchronousPID implements Controller
{
    private MA3 armEncoder;
    private Victor drive;
    private Robot robot;
    private double goalPosition;
    private boolean isOnTarget = false;
    private static final int onTargetThresh = 25;
    private int onTargetCounter = onTargetThresh;
    public static double kOnTargetToleranceInches = Constants.WRIST_TOLERANCE;
    public static final double kLoopRate = 200.0;
    double lastValue = 0;
    private static Wrist instance = null;
    private boolean powerReversed = true;
    public static Wrist getInstance()
    {
        if( instance == null )
            instance = new Wrist();
        return instance;
    }
    public void upP(){
        double p = this.getP();
        double d = this.getD();
        double i = this.getI();
         
        p += 0.01;
        this.setPID(p, i, d);
        SmartDashboard.putNumber("TURN_P", p);
        SmartDashboard.putNumber("TURN_I", i);
        SmartDashboard.putNumber("TURN_D", d);
        System.out.println(p);
    }
    public void downP(){
        double p = this.getP();
        double d = this.getD();
        double i = this.getI();
        p -= 0.01;
        this.setPID(p, i, d);
        SmartDashboard.putNumber("TURN_P", p);
        SmartDashboard.putNumber("TURN_I", i);
        SmartDashboard.putNumber("TURN_D", d);
        System.out.println(p);
    }
    public void upD(){
        double p = this.getP();
        double d = this.getD();
        double i = this.getI();
        d += 0.01;
        this.setPID(p, i, d);
        SmartDashboard.putNumber("TURN_P", p);
        SmartDashboard.putNumber("TURN_I", i);
        SmartDashboard.putNumber("TURN_D", d);
        System.out.println(d);
    }
    public void downD(){
        double p = this.getP();
        double d = this.getD();
        double i = this.getI();
        d -= 0.01;
        this.setPID(p, i, d);
        SmartDashboard.putNumber("TURN_P", p);
        SmartDashboard.putNumber("TURN_I", i);
        SmartDashboard.putNumber("TURN_D", d);
        System.out.println(d);
    }

    private Wrist()
    {
        loadProperties();
        armEncoder = new MA3(Ports.WRIST_MA3);
        drive = new Victor(Ports.WRIST);
        setGoal(armEncoder.getAngle());
    }

    public void manualUp(){
        drive.set(0.4);
    }
    public void manualDown(){
        drive.set(-0.4);
    }
    public void manualStop(){
        drive.set(0);
    }
    public boolean sensorFail(){
       return (armEncoder.getAngle() > (Constants.WRIST_MAX_ANGLE+4)) || (armEncoder.getAngle() < (Constants.WRIST_MIN_ANGLE-8));
    }
    public synchronized void setGoal(double goalDistance)
    {
        reset();
        if(this.getCurrent() < goalDistance){
            this.setPID(Constants.WRIST_P_UP, Constants.WRIST_I_UP, Constants.WRIST_D_UP);
        }else{
            this.setPID(Constants.WRIST_P_DOWN, Constants.WRIST_I_DOWN, Constants.WRIST_D_DOWN);
        }
        SmartDashboard.putNumber("WRIST_P", this.getP());
        SmartDashboard.putNumber("WRIST_I", this.getI());
        SmartDashboard.putNumber("WRIST_D", this.getD());
      //  System.out.println("P: " + this.getP() + " I: " + this.getI() + " I: " + this.getD());
        
        this.setSetpoint(goalDistance);
        goalPosition = goalDistance;
    }
    public void loadParts(){
        
        robot = Robot.getInstance();
    }
    public double getAngle(){
        double current = armEncoder.getAngle();
        if(current < Constants.WRIST_MIN_ANGLE){
            current = Constants.WRIST_MIN_ANGLE;
        }
        return current;
    }
    public double getCurrent(){
        return lastValue;
    }
    public synchronized void reset()
    {
        super.reset();
        isOnTarget = false;
        onTargetCounter = onTargetThresh;
    }
    public void stop(){this.setGoal(getAngle());}
    private boolean inRange(double current){
        return Util.onTarget(this.getSetpoint(),current,kOnTargetToleranceInches);
    }
    public synchronized void run()
    {
        if(robot == null){
            robot = Robot.getInstance();
        }
        double current = 0;
        double power = 0;
        if(Math.abs(this.getSetpoint() - armEncoder.getAngle()) < 1.0){
            current = Util.buffer(getAngle(), lastValue, 80);
        }else{
            current = getAngle();
        }
        if(current < Constants.WRIST_MIN_ANGLE){
            current = Constants.WRIST_MIN_ANGLE;
        }
        lastValue = current;
        
        SmartDashboard.putNumber("WRIST_ANGLE", current - Constants.WRIST_MIN_ANGLE);
        SmartDashboard.putNumber("WRIST_NO_FILTER",armEncoder.getAngle());
        SmartDashboard.putNumber("WRIST_GOAL",  this.getSetpoint() - Constants.WRIST_MIN_ANGLE);
        if(inRange(current))
        {
            if(onTarget())
                isOnTarget = true;
            onTargetCounter--;
        }
        else
        {
           power = this.calculate(current);
           if(this.getSetpoint() == Constants.WRIST_MIN_ANGLE && current <= Constants.WRIST_MIN_ANGLE && upPower(power)){
                power = 0;
           }else if((current >= Constants.WRIST_MAX_ANGLE) && downPower(power)){
                power = 0;
            }else{
               
            }
            onTargetCounter = onTargetThresh;
            isOnTarget = false;
        }
      if(!sensorFail())
        applyPower(power);
      SmartDashboard.putNumber("WRIST_POWR", power);
    }
    private void applyPower(double power){
        if(powerReversed){
            drive.set(-power);
        }else{
            drive.set(power);
        }
    }
    public boolean upPower(double power){
        if(powerReversed){
            return power < 0;
        }else{
            return power > 0;
        }        
    }
    public boolean downPower(double power){
        return !upPower(power);
    }
    public void trim(double angle){
            this.setGoal(Util.limit(this.goalPosition + angle, Constants.WRIST_MIN_ANGLE, Constants.WRIST_MAX_ANGLE));
        }
    public synchronized boolean onTarget()
    {
        return onTargetCounter <= 0;
    }
    public double error(){
        return lastValue - this.getSetpoint();
    }
    public synchronized boolean onTargetNow(){
        return Util.onTarget(this.getSetpoint(), lastValue, Constants.WRIST_TOLERANCE + Constants.WRIST_LIGHT_TOLERANCE) || onTarget();
    }
    public final void loadProperties()
    {
        double kp = Constants.WRIST_P_UP;
        double ki = Constants.WRIST_I_UP;
        double kd = Constants.WRIST_D_UP;
        this.setInputRange(Constants.WRIST_MIN_ANGLE, Constants.WRIST_MAX_ANGLE);
        this.setOutputRange(-Constants.WRIST_MAX_POWER, Constants.WRIST_MAX_POWER);
        this.setPID(kp, ki, kd);
        kOnTargetToleranceInches = Constants.WRIST_TOLERANCE;
    } 
}
