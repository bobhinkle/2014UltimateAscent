package SubSystems;

import Team1323Robot.Robot;
import Utilities.Constants;
import Utilities.TrajectorySmoother;
import Utilities.Util;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Jared Russell
 */
public class TurnController extends FeedforwardPIV implements Controller
{
    private Robot robot;
    private double goalPosition;
    private boolean isOnTarget = false;
    private static final int onTargetThresh = 25;
    private int onTargetCounter = onTargetThresh;
    public static double kOnTargetToleranceDegrees = Constants.TURN_ON_TARGET_DEG;
    public static final double kLoopRate = 200.0;
    private double timeout = 0;
    private double startTime = 0;
    private double lastHeading = 0;
    private static TurnController instance = null;
    private double maxPower = 1.0;
    private double minPower = 0.15;
    private boolean force = false;
    private TrajectorySmoother trajectory;
    public static TurnController getInstance()
    {
        if( instance == null )
            instance = new TurnController();
        return instance;
    }
    public void loadParts(){
        robot = Robot.getInstance();
    }
    private TurnController()
    {
        loadProperties();
    }

    double lastDistance = 0;
    double lastCheck = 0;
    public void setTime(){
        lastCheck = System.currentTimeMillis();
    }
    public boolean checkStall(){
        double distanceTraveled = Math.abs(robot.nav.getDistance()) - Math.abs(lastDistance);
        double timeSinceCheck = System.currentTimeMillis() - lastCheck;
        if((distanceTraveled < 0.2) && (timeSinceCheck > 10)){
            lastDistance = distanceTraveled;
            lastCheck = System.currentTimeMillis();
            return true;
        }
//        System.out.println("CS: " + distanceTraveled + " " + timeSinceCheck);
        lastDistance = distanceTraveled;
        lastCheck = System.currentTimeMillis();
        return false;
    }
    public synchronized void setGoal(double goalAngle, double timeout)
    {
        this.setSetpoint(goalAngle);
        goalPosition = goalAngle;
        startTime = System.currentTimeMillis();
        this.timeout = (timeout * 1000) + System.currentTimeMillis() ;
        force = false;
        setTime();
    }
    
    public synchronized void reset()
    {
        lastHeading = robot.nav.getRawHeading();
        setGoal(lastHeading,0);
        onTargetCounter = onTargetThresh;
    }

    public synchronized void run()
    {
        double current = robot.nav.getRawHeading();
        double position = Util.getDifferenceInAngleDegrees(this.getSetpoint(), current);
        double velocity = Util.getDifferenceInAngleDegrees(current, lastHeading) * kLoopRate;
        trajectory.update(position, velocity, 0.0, 1.0/kLoopRate);
//        System.out.println(trajectory.getPosition() + " " + trajectory.getVelocity() + " " + trajectory.getAcceleration());
//        double output = calculate(trajectory, position, velocity, 1.0/kLoopRate);
        double output = this.calculate(this.getSetpoint(), 90, 90, current, velocity, 1.0/kLoopRate);
        getError();
        //output = Util.pidPower(output, -minPower, -maxPower, minPower, maxPower);
        if(timeout > System.currentTimeMillis() && !Util.onTarget(this.getSetpoint(),current,kOnTargetToleranceDegrees))
        {
            robot.dt.directDrive(output, -output);
            onTargetCounter = onTargetThresh;
            isOnTarget = false;
            //System.out.println("Driving");
        }
        else
        {
            //System.out.println("Stop");
            if(onTarget() || checkStall()){
                isOnTarget = true;
            }
            onTargetCounter--;
            robot.dt.directDrive(0, 0);
        }
        //System.out.println("TS: " + " " + output + " " + getError());
        lastHeading = current;
//        SmartDashboard.putNumber("turnPower", output);
//        SmartDashboard.putNumber("onTargetCounter", onTargetCounter);
    }

    public synchronized boolean onTarget()
    {
        return onTargetCounter <= 0 ;
    }
    public double getError(){
//        SmartDashboard.putNumber("error", lastHeading - this.getSetpoint());
        return lastHeading - this.getSetpoint();
    }
    public double getAbsError()
    {
        double absError = Math.abs(lastHeading - this.getSetpoint());
//        SmartDashboard.putNumber("ABS_ERROR", absError);
        return absError;
    }
    public final void loadProperties()
    {
        double kp = Constants.TURN_KP;
        double ki = Constants.TURN_KI;
        double kd = Constants.TURN_KD;
        double kfa = Constants.TURN_KFA;
        double kfv = Constants.TURN_KFV;
        setParams(kp, ki, kd, kfv, kfa);
        trajectory = new TrajectorySmoother(Constants.TURN_MAX_ACCEL,Constants.TURN_MAX_VEL);
    }
}
