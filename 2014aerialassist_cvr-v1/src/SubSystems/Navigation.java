package SubSystems;

import Sensors.CustomGyro;
import Sensors.GY80;
import Sensors.SuperEncoder;
import Utilities.Constants;
import Utilities.Ports;
import Utilities.Util;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author jrussell
 */
public class Navigation implements PIDSource
{
    // Sensors
    protected SuperEncoder followerWheel;
    protected CustomGyro gyro;
    protected CustomGyro gyro2;
    
//    protected ADXL345_2 accel;
    private GY80 accel2;
    // Navigational state
    private double x = 0.0; // positive from driver facing center of the field
    private double y = 0.0; // positive from driver looking left
    private static Navigation instance;
    private double basicDistance = 0;
    private double topGoalPosition = 360;
    private boolean topGoalFound = false;
    private boolean twoGyro = true;
    private double angle = 0;
    private Lights lights;
    private Navigation()
    {
        followerWheel = new SuperEncoder(Ports.LEFTENC,Ports.LEFTENC+1,false,1);
        followerWheel.setDistancePerPulse(Constants.DRIVE_DISTANCE_PER_PULSE);
        followerWheel.start();
        SmartDashboard.putString("GYRO_STATUS", "INITIALIZING");
//        lights = Lights.getInstance();
//        lights.setState(Lights.GYRO_INIT);
        gyro = new CustomGyro(Ports.GYRO);
        gyro2 = new CustomGyro(Ports.GYRO2);
//        lights.setState(Lights.GYRO_COMP);
        SmartDashboard.putString("GYRO_STATUS", "READY");
    }
    public static Navigation getInstance()
    {
        if( instance == null )
        {
            instance = new Navigation();
        }
        return instance;
    }
    public void initGyro(){
        SmartDashboard.putString("GYRO_STATUS", "INITIALIZING");
        gyro.initGyro();
        gyro2.initGyro();
        SmartDashboard.putString("GYRO_STATUS", "READY");
    }
    public synchronized void resetRobotPosition(double x, double y, double theta,boolean gyroReset)
    {
        this.x = x;
        this.y = y;
        followerWheel.reset();
        if(gyroReset){
            gyro.reset();
            gyro2.reset();
        }
        basicDistance = 0;
    }
    public void checkForTopGoal(){
        if(SmartDashboard.getBoolean("found", false)){
            double angle = SmartDashboard.getNumber("azimuth", 0);
            if((340 > angle && angle < 360) || (0 > angle && angle < 5)){
                if(Util.getDifferenceInAngleDegrees(angle, SmartDashboard.getNumber("azimuth", 0)) < Util.getDifferenceInAngleDegrees(topGoalPosition, SmartDashboard.getNumber("azimuth", 0))){
                    topGoalPosition = SmartDashboard.getNumber("azimuth", 0);
                }
            }else{
                topGoalFound = false;
            }
        }
    }
    public boolean topGoalFound(){
        return topGoalFound;
    }
    public double topGoalAngle(){
        return Util.getDifferenceInAngleDegrees(topGoalPosition,getHeadingInDegrees());
    }
    public synchronized double getX()
    {
        return x;
    }

    public synchronized double getY()
    {
        return y;
    }

    public double getHeadingInDegrees()
    {
        return Util.boundAngle0to360Degrees(gyro.getAngle());
        
    }
    public double getRawHeading(){
//        return gyro.getAngle();
        return angle;
    }

    public double getPitchInDegrees()
    {
        //return gyro.getAngle();
        return 0;
    }

    public void resetPitch()
    {
        gyro.reset();
    }

    public synchronized void run()
    {
        updatePosition();
        checkForTopGoal();
        SmartDashboard.putNumber("Distance",getDistance());
        SmartDashboard.putNumber("Heading",getHeadingInDegrees());
        SmartDashboard.putNumber("RawHeading",getRawHeading());
    }

    public double getFollowerWheelDistance()
    {
        return followerWheel.getDistance();
    }

    public double getDistance(){
        return basicDistance;
    }
    public void updatePosition()
    {
//        basicDistance = (followerWheel.getDistance() + rightDriveEncoder.getDistance())/2.0;
        basicDistance = followerWheel.getDistance();
        if(twoGyro){
            angle = (gyro.getAngle() + gyro2.getAngle())/2.0;
        }
        /*
        double distanceTravelled = ((followerWheel.getDistance() + rightDriveEncoder.getDistance())/2.0) - distanceLast;
        double timePassed = System.currentTimeMillis() - timeLast;
        speedX = distanceTravelled/timePassed;
        x += distanceTravelled * Math.cos(Math.toRadians(getHeadingInDegrees()));
        y += distanceTravelled * Math.sin(Math.toRadians(getHeadingInDegrees()));

        distanceLast = (followerWheel.getDistance() + rightDriveEncoder.getDistance())/2.0;
        timeLast = System.currentTimeMillis();*/
    }
    public double pidGet() {
        return basicDistance;
    }
    
    public class Distance implements PIDSource {
    
        public double pidGet(){
            return basicDistance = followerWheel.getDistance();
        }
    }
}
