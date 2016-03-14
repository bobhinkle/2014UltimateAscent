package SubSystems;

import Sensors.MA3;
import Team1323Robot.Robot;
import Utilities.Constants;
import Utilities.Ports;
import Utilities.Util;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Jared Russell
 */
public final class Wrist extends SynchronousPID implements Controller
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
    private final boolean powerReversed = true;
    private double wrist_up_p,wrist_up_i,wrist_up_d;
    private double wrist_down_p,wrist_down_i,wrist_down_d;
    private double power = 0;
    public static Wrist getInstance()
    {
        if( instance == null )
            instance = new Wrist();
        return instance;
    }
    

    private Wrist()
    {
        wrist_up_p = Constants.WRIST_P_UP;
        wrist_up_i = Constants.WRIST_I_UP;
        wrist_up_d = Constants.WRIST_D_UP;
        wrist_down_p = Constants.WRIST_P_DOWN;
        wrist_down_i = Constants.WRIST_I_DOWN;
        wrist_down_d = Constants.WRIST_D_DOWN;
        armEncoder = new MA3(Ports.WRIST_MA3);
        drive = new Victor(Ports.WRIST);
        setGoal(armEncoder.getAngle());
    }
    public void upP(){
        double p = this.getP();
        double d = this.getD();
        double i = this.getI();
         
        p += 0.01;
        this.setPID(p, i, d);
        SmartDashboard.putNumber("WRIST_P", p);
        SmartDashboard.putNumber("WRIST_I", i);
        SmartDashboard.putNumber("WRIST_D", d);
        System.out.println(p);
    }
    public void downP(){
        double p = this.getP();
        double d = this.getD();
        double i = this.getI();
        p -= 0.01;
        this.setPID(p, i, d);
        SmartDashboard.putNumber("WRIST_P", p);
        SmartDashboard.putNumber("WRIST_I", i);
        SmartDashboard.putNumber("WRIST_D", d);
        System.out.println(p);
    }
    public void upD(){
        double p = this.getP();
        double d = this.getD();
        double i = this.getI();
        d += 0.01;
        this.setPID(p, i, d);
        SmartDashboard.putNumber("WRIST_P", p);
        SmartDashboard.putNumber("WRIST_I", i);
        SmartDashboard.putNumber("WRIST_D", d);
        System.out.println(d);
    }
    public void downD(){
        double p = this.getP();
        double d = this.getD();
        double i = this.getI();
        d -= 0.01;
        this.setPID(p, i, d);
        SmartDashboard.putNumber("WRIST_P", p);
        SmartDashboard.putNumber("WRIST_I", i);
        SmartDashboard.putNumber("WRIST_D", d);
        System.out.println(d);
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
       return (armEncoder.getAngle() > (Constants.WRIST_MAX_ANGLE+8)) || (armEncoder.getAngle() < (Constants.WRIST_MIN_ANGLE-8));
    }
    public synchronized void setGoal(double goalDistance)
    {
        reset();
        if(this.getCurrent() < goalDistance){
            this.setPID(wrist_up_p, wrist_up_i, wrist_up_d);
        }else{
            this.setPID(wrist_down_p, wrist_down_i, wrist_down_d);
        }
        this.setSetpoint(goalDistance);
        goalPosition = goalDistance;
        
        SmartDashboard.putNumber("WRIST_P", this.getP());
        SmartDashboard.putNumber("WRIST_I", this.getI());
        SmartDashboard.putNumber("WRIST_D", this.getD());
    }
    public void setUpPIDValues(double p, double i, double d){
        wrist_up_p = p;
        wrist_up_i = i;
        wrist_up_d = d;
    }
    public void setDownPIDValues(double p, double i, double d){
        wrist_down_p = p;
        wrist_down_i = i;
        wrist_down_d = d;
    }
    public double getAngle(){
        double angle = armEncoder.getAngle();
        if(angle < Constants.WRIST_MIN_ANGLE)
            return Constants.WRIST_MIN_ANGLE;        
        return angle;
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
        double current = 0;
        if(robot == null){
            robot = Robot.getInstance();
        }
        
        if(Math.abs(this.getSetpoint() - armEncoder.getAngle()) < 1.0){
            current = Util.buffer(getAngle(), lastValue, 80);
        }else{
            current = getAngle();
        }
        lastValue = current;
        
        if(inRange(current))
        {
            if(onTarget())
                isOnTarget = true;
            onTargetCounter--;
        }
        else
        {
           power = this.calculate(current);
           if((current < Constants.WRIST_MIN_ANGLE && !upPower(power)) || ((current > Constants.WRIST_MAX_ANGLE) && !downPower(power))){
                power = 0;
           }
            onTargetCounter = onTargetThresh;
            isOnTarget = false;
        }
      if(!sensorFail())
        applyPower(power);
      
      SmartDashboard.putNumber("WRIST_ANGLE", current - Constants.WRIST_HORIZONTAL_CAL);
      SmartDashboard.putNumber("WRIST_NO_FILTER",armEncoder.getAngle());
      SmartDashboard.putNumber("WRIST_GOAL", this.getSetpoint() - Constants.WRIST_HORIZONTAL_CAL);
      SmartDashboard.putNumber("WRIST_POWER", power);
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
