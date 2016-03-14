    
package Team1323Robot;


import IO.TeleController;
import SubSystems.DistanceController;
import SubSystems.Plunger;
import SubSystems.TurnController;
import Utilities.Constants;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;


public class Team1323Main extends SimpleRobot {
    private FSM fsm;
    private Robot robot;
    private TeleController controls;
    private turnThread turnTh;
    private distanceThread distTh;
    public TurnController turn; 
    public DistanceController dist;
    private boolean plungerCheck = false;
    
    public Team1323Main() {
        robot = Robot.getInstance();
        controls = TeleController.getInstance();
        controls.loadSubsystems();
        robot.loadSubsystems();
        fsm = FSM.getInstance();
        fsm.start();
        turnTh = new turnThread();
        distTh = new distanceThread();
        turn = TurnController.getInstance();
        dist = DistanceController.getInstance();
        turn.loadParts();
    }
    public void autonomous() {
        robot.enabled();
        robot.nav.resetRobotPosition(0, 0, 0, true);
        robot.dt.highGear();
//        Timer.delay(5);
//        driveDistanceHoldingHeading(84, 0, 0.8, 3.0, 3, true, 0,false);
        
        int hotGoal;//the left goal is 1 and the right goal is 2
        double distanceToNextZone = 60;
        double distanceToNextBall = 37;
        double totalDistance = 0;
        fsm.setGoalState(FSM.SHOOTING_ELE_UP);
        Timer.delay(0.1);
        robot.claw.topForward();
        while((robot.plunger.currentState() != Plunger.ARMED) && isAutonomous()){
            Timer.delay(0.1);
        }
        robot.claw.topForward();
//        while((robot.wrist.getAngle() > (50+Constants.WRIST_MIN_ANGLE)) && isAutonomous()){
//           Timer.delay(0.1);
//        }
        fsm.setGoalState(FSM.SHOOTING_ELE_UP);
        Timer.delay(0.1);
        robot.claw.topForward();
        Timer.delay(0.6);
/*        
        robot.claw.allForward();
        Timer.delay(0.2);
        robot.claw.allStop();
        Timer.delay(0.2);
        fsm.setGoalState(FSM.FIRE);
        Timer.delay(0.20);
        fsm.setGoalState(FSM.PICKUP_NO_ROLLER);
        boolean checkTurn = true;
        while(robot.plunger.currentState() != Plunger.ARMED){
          Timer.delay(0.1);
        }
        fsm.setGoalState(FSM.AUTON_PICK_UP);
        totalDistance += driveDistanceHoldingHeading(12, 0, 0.85,1.2, 1, true, 0,false);
        fsm.setGoalState(FSM.SHOOTING);
        driveDistanceHoldingHeading(60, 0, 0.78, 2.0, 3, true, 0,false);
        while(!robot.wrist.onTargetNow()){
            if(isAutonomous())
                Timer.delay(0.01);
        }
        if(robot.claw.hasBall())
            fsm.setGoalState(FSM.FIRE);
        if(isAutonomous())
           Timer.delay(0.275);
        fsm.setGoalState(FSM.AFTERSHOT);*/
        driveDistanceHoldingHeading(120, 0, 0.80, 4.0, 3, true, 0,false);
        Timer.delay(2.5);
        robot.claw.allForward();
        Timer.delay(0.2);
        robot.claw.allStop();
        Timer.delay(0.2);
        fsm.setGoalState(FSM.FIRE);
        Timer.delay(0.20);
    }
    public void operatorControl() {
        robot.enabled();
        robot.dt.highGear();
        fsm.setGoalState(FSM.AFTERSHOT);
        while(true){
           controls.driveUpdate();
           controls.coPilot();
           Timer.delay(0.025);
        }
    }
    public void test() {
        
    }
    
    public void turnToHeading(double heading, double timeout){
        robot.nav.resetRobotPosition(0, 0, 0,false);
        turn.reset();
        turn.setGoal(heading,timeout);
        turnTh.run();
    }
    private class turnThread extends Thread{
        
        public void run(){
            try {
                while(!turn.onTarget() && isAutonomous()){
                    turn.run();
                    Timer.delay(0.01);
                }
                System.out.println("done");
                robot.dt.directDrive(0, 0);
            }catch(Exception e){
                System.out.println("crash" + e.toString());
            }            
        }
    }
    public double driveDistanceHoldingHeading(double distance, double heading,double maxSpeed,double timeout,double tol, boolean holdSpeed,double breakTime,boolean plunger){                
        plungerCheck = plunger;
        dist.resetDistance();
        double startDistance = robot.nav.getDistance();
        double distanceChange = distance + startDistance;
        System.out.println("DD: " + startDistance + " " + distanceChange + " " + distance);
        dist.reset();
        dist.setGoal(distanceChange, maxSpeed,heading,timeout,tol,holdSpeed);
        distTh.run();
        return robot.nav.getDistance() - startDistance;
    }
    private class distanceThread extends Thread{
        boolean done;
        public void run(){
            try {
                done = false;
                while(!dist.onTarget() && isAutonomous() && !done){
                    dist.run();
                    if(robot.claw.hasBall())
                        robot.claw.allStop();
                    Timer.delay(0.01);
                }
                System.out.println("done");
                robot.dt.directDrive(0, 0);
            }catch(Exception e){
                System.out.println("crash" + e.toString());
            }            
        }
    }
    public void turnToHotGoal(int hotGoal){
        double angleToLeftGoal = -25;
        double angleToRightGoal = 0;
        switch(hotGoal){
            case Constants.LEFT_GOAL:
                turnToHeading(angleToLeftGoal, 0.8);
                break;
            case Constants.RIGHT_GOAL:
                turnToHeading(angleToRightGoal, 0.8);
                break;
        }
    }
}
