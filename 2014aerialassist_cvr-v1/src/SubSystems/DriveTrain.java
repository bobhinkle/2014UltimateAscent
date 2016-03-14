package SubSystems;

import Utilities.Constants;
import Utilities.Ports;
import Utilities.Util;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class DriveTrain
{
    public Victor leftdt, rightdt;
    private Solenoid shifter;
    public boolean gear = Constants.LOW_GEAR;
    private static DriveTrain instance = null;
    
    public DriveTrain()
    {
        leftdt  = new Victor(Ports.LEFTDT);
        rightdt = new Victor(Ports.RIGHTDT);
        shifter = new Solenoid(Ports.SHIFTER);
    }
    public static DriveTrain getInstance()
    {
        if( instance == null )
            instance = new DriveTrain();
        return instance;
    }
    public void directDrive(double left, double right)
    {
        leftdt.set(left);
        rightdt.set(-right);
    }
    public void directArcadeDrive(double x, double y)
    {
        x = Util.limit(x, -1.0, 1.0);
        y = Util.limit(y, -1.0, 1.0);
        double left = y + x;
        double right = y - x;
        left = Util.limit(left, -1.0, 1.0);
        right = Util.limit(right, -1.0, 1.0);
        directDrive(left, right);
    }
    public void driveSpeedTurn(double speed, double turn)
    {
        double left = speed + turn;
        double right = speed - turn;
        directDrive(left, right);
    }
    
    public void setGear(boolean gear) {this.gear = gear; shifter.set(gear); }
    public void lowGear(){ shifter.set(Constants.LOW_GEAR);}
    public void highGear(){ shifter.set(!Constants.LOW_GEAR);}
    public boolean getGear(){ return shifter.get(); }
    public boolean inLowGear() { return shifter.get() == Constants.LOW_GEAR;}
    private double old_wheel = 0.0;
    private double neg_inertia_accumulator = 0.0;
    private double oldWheel = 0.0;
    private double quickStopAccumulator;
    private double throttleDeadband = 0.02;
    private double wheelDeadband = 0.02;
    /*
    public void cheesyDrive(double wheel, double throttle, boolean isQuickTurn){
        double wheelNonLinearity;
        double negInertia = wheel - oldWheel;
        oldWheel = wheel;
 
        if (!inLowGear()) {
          wheelNonLinearity = 0.6;
          // Apply a sin function that's scaled to make it feel better.
          wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) /
              Math.sin(Math.PI / 2.0 * wheelNonLinearity);
          wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) /
              Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        } else {
          wheelNonLinearity = 0.5;
          // Apply a sin function that's scaled to make it feel better.
          wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) /
              Math.sin(Math.PI / 2.0 * wheelNonLinearity);
          wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) /
              Math.sin(Math.PI / 2.0 * wheelNonLinearity);
          wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) /
              Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        }

        double leftPwm, rightPwm, overPower;
        double sensitivity = 1.7;

        double angularPower;
        double linearPower;

        // Negative inertia!
        double negInertiaAccumulator = 0.0;
        double negInertiaScalar;
        if (!inLowGear()) {
          negInertiaScalar = 5.0;
          sensitivity = Constants.sensitivityHigh;
        } else {
          if (wheel * negInertia > 0) {
            negInertiaScalar = 2.5;
          } else {
            if (Math.abs(wheel) > 0.65) {
              negInertiaScalar = 5.0;
            } else {
              negInertiaScalar = 3.0;
            }
          }
          sensitivity = Constants.sensitivityLow;

          if (Math.abs(throttle) > 0.1) {
           // sensitivity = 1.0 - (1.0 - sensitivity) / Math.abs(throttle);
          }
        }
        double negInertiaPower = negInertia * negInertiaScalar;
        negInertiaAccumulator += negInertiaPower;

        wheel = wheel + negInertiaAccumulator;
        if (negInertiaAccumulator > 1) {
          negInertiaAccumulator -= 1;
        } else if (negInertiaAccumulator < -1) {
          negInertiaAccumulator += 1;
        } else {
          negInertiaAccumulator = 0;
        }
        linearPower = throttle;

        // Quickturn!
        if (isQuickTurn) {
          if (Math.abs(linearPower) < 0.2) {
            double alpha = 0.1;
            quickStopAccumulator = (1 - alpha) * quickStopAccumulator + alpha *
                Util.limit(wheel, 1.0) * 5;
          }
          overPower = 1.0;
          if (!inLowGear()) {
            sensitivity = 1.0;
          } else {
            sensitivity = 1.0;
          }
          angularPower = wheel;
        } else {
          overPower = 0.0;
          angularPower = Math.abs(throttle) * wheel * sensitivity - quickStopAccumulator;
          if (quickStopAccumulator > 1) {
            quickStopAccumulator -= 1;
          } else if (quickStopAccumulator < -1) {
            quickStopAccumulator += 1;
          } else {
            quickStopAccumulator = 0.0;
          }
        }

        rightPwm = leftPwm = linearPower;
        leftPwm += angularPower;
        rightPwm -= angularPower;

        if (leftPwm > 1.0) {
          rightPwm -= overPower * (leftPwm - 1.0);
          leftPwm = 1.0;
        } else if (rightPwm > 1.0) {
          leftPwm -= overPower * (rightPwm - 1.0);
          rightPwm = 1.0;
        } else if (leftPwm < -1.0) {
          rightPwm += overPower * (-1.0 - leftPwm);
          leftPwm = -1.0;
        } else if (rightPwm < -1.0) {
          leftPwm += overPower * (-1.0 - rightPwm);
          rightPwm = -1.0;
        }
        directDrive(leftPwm,rightPwm);
      
    }*/

    public void cheesyDrive(double wheel, double throttle, boolean quickturn)
    {
        double left_pwm,right_pwm,overPower;
        double sensitivity = 1.1;
        double angular_power;
        double linear_power;
        double wheelNonLinearity;

        double neg_inertia = wheel - old_wheel;
        old_wheel = wheel;

        if (!inLowGear()) {
                wheelNonLinearity = 0.995; // used to be csvReader->TURN_NONL 0.9
                // Apply a sin function that's scaled to make it feel bette
                wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
                wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        } else {
                wheelNonLinearity = 0.8; // used to be csvReader->TURN_NONL higher is less sensitive
                // Apply a sin function that's scaled to make it feel bette
                wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
                wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
                wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        }

        double neg_inertia_scalar;
        if (!inLowGear()) {
                neg_inertia_scalar = 1.25; // used to be csvReader->NEG_INT 11
                sensitivity = 1.06; // used to be csvReader->SENSE_HIGH  1.15
                if (Math.abs(throttle) > 0.1) { // used to be csvReader->SENSE_
                        sensitivity = .9 - (.9 - sensitivity) / Math.abs(throttle);
                }
        } else {
                if (wheel * neg_inertia > 0) {
                        neg_inertia_scalar = 1; // used to be csvReader->NE 5
                } else {
                        if (Math.abs(wheel) > 0.65) {
                                neg_inertia_scalar = 1;// used to be csvRe 10
                        } else {
                                neg_inertia_scalar = 1; // used to be csvRe 3
                        }
                }
                sensitivity = 1.07; // used to be csvReader->SENSE_LOW lower is less sensitive

                if (Math.abs(throttle) > 0.1) { // used to be csvReader->SENSE_
                        sensitivity = .9 - (.9 - sensitivity) / Math.abs(throttle);
                }
        }
        double neg_inertia_power = neg_inertia * neg_inertia_scalar;
        if (Math.abs(throttle) >= 0.05 || quickturn) neg_inertia_accumulator += neg_inertia_power;
        
        wheel = wheel + neg_inertia_accumulator;
        if (neg_inertia_accumulator > 1)
                neg_inertia_accumulator -= 0.25;
        else if (neg_inertia_accumulator < -1)
                neg_inertia_accumulator += 0.25;
        else
                neg_inertia_accumulator = 0;

        linear_power = throttle;

        if ((!Util.inRange(throttle, -0.15,0.15) || !(Util.inRange(wheel, -0.4, 0.4))) && quickturn) {
                overPower = 1.0;
                if (gear == !Constants.LOW_GEAR) {
                        sensitivity = 1.0; // default 1.0
                } else {
                        sensitivity = 1.0;
                }
                angular_power = wheel * sensitivity;
        } else {
                overPower = 0.0;
                angular_power = Math.abs(throttle) * wheel * sensitivity;
        }
//        System.out.println("NA " + neg_inertia_accumulator + " AP " + angular_power + " wheel " + wheel + " throttle" + throttle + " NAP " + neg_inertia_power);
        right_pwm = left_pwm = linear_power;
        left_pwm += angular_power;
        right_pwm -= angular_power;

        if (left_pwm > 1.0) {
                right_pwm -= overPower*(left_pwm - 1.0);
                left_pwm = 1.0;
        } else if (right_pwm > 1.0) {
                left_pwm -= overPower*(right_pwm - 1.0);
                right_pwm = 1.0;
        } else if (left_pwm < -1.0) {
                right_pwm += overPower*(-1.0 - left_pwm);
                left_pwm = -1.0;
        } else if (right_pwm < -1.0) {
                left_pwm += overPower*(-1.0 - right_pwm);
                right_pwm = -1.0;
        }
//        directDrive(Util.deadBand(Util.victorLinearize(left_pwm),DRIVE_TRAIN_DEADBAND),Util.deadBand(Util.victorLinearize(right_pwm),DRIVE_TRAIN_DEADBAND));
        directDrive(Util.victorLinearize(left_pwm),Util.victorLinearize(right_pwm));
    }
    
    double powerToReduce = 0.0;
    int lastDirection    = 0;
    public void driveHoldHeading(double headingToHold, double currentHeading,double maxSpeed){
        if(currentHeading < headingToHold){
            if(lastDirection != 1)
                powerToReduce = 0;
            if((Math.abs(maxSpeed) - powerToReduce) > 0)
                powerToReduce = powerToReduce + 0.05;
            SmartDashboard.putString("driveHolding", "turn right");
            lastDirection = 1;
            directArcadeDrive(maxSpeed  , maxSpeed - powerToReduce);
        }else if(currentHeading > headingToHold){
            if(lastDirection != -1)
                powerToReduce = 0;
            if((Math.abs(maxSpeed) - powerToReduce) > 0)
                powerToReduce = powerToReduce + 0.05;
            directArcadeDrive(maxSpeed - powerToReduce, maxSpeed );
            lastDirection = -1;
            SmartDashboard.putString("driveHolding", "turn left");
        }else{
            powerToReduce = 0.0;
            SmartDashboard.putString("driveHolding", "straight");
            lastDirection = 0;
            directArcadeDrive(maxSpeed, maxSpeed);
        }
        SmartDashboard.putNumber("POWER_TO_REDUCE", powerToReduce);
        SmartDashboard.putNumber("POWER_REDUCTION",maxSpeed-powerToReduce);
    }
    
    
}
