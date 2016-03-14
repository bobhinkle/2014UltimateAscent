    
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
    private double distanceGoal;
    public Team1323Main() {
        robot = Robot.getInstance();
        controls = TeleController.getInstance();
        controls.loadSubsystems();
        robot.loadSubsystems();
        fsm = FSM.getInstance();
        turnTh = new turnThread();
        turn = TurnController.getInstance();
        dist = DistanceController.getInstance();
        distTh = new distanceThread(false);
    }
    public void autonomous() {
        int autonSelect = 0; //Select Auton Mode Here
        //0 = 2 Ball
        //1 = 1 Ball from CVR
        //2 = Kinect with Blocker
        robot.enabled();
        robot.nav.resetRobotPosition(0, 0, 0, true);
        robot.dt.highGear();
        switch(autonSelect){
            case 0:
                int hotGoal;//the left goal is 1 and the right goal is 2
                double distanceToNextZone = 60; //60
                double distanceToNextBall = 37; //37
                double totalDistance = 0;
                fsm.setGoalState(FSM.AUTON_PICK_UP);
                int counts = 0;
                while(!robot.wrist.onTargetNow() && isAutonomous() && counts < 100){ //why 100? Why not variable?
                    Timer.delay(0.01);
                    counts++;
                }
                fsm.setGoalState(FSM.PICK_UP);
                robot.claw.allForward();
                System.out.println("1");
                //Distance - Heading - Max Speed - Timout - Tolerance      
                driveDistanceHoldingHeading(18, 0, 0.9, 0.75, 2.0, true, 0);
                System.out.println("2");
                fsm.setGoalState(FSM.SHOOTING2);
                driveDistanceHoldingHeading(75, 0, 0.95, 6.0, 5.0, false, 0);
                System.out.println("3");
                hotGoal = robot.getHotGoal();
                if(hotGoal == Constants.LEFT_GOAL){
                    turnToHotGoal(Constants.RIGHT_GOAL);
                }else{
                    turnToHotGoal(Constants.LEFT_GOAL);
                }
                System.out.println("4");
                //Fire FIRST Shot
                counts = 0;
                double timer_firstshot = 1.2;
                double timer_secondshot = 1.2;
                double timer_ball_pulldown = 2.1;
                
                while(!turn.onTarget() && isAutonomous() && counts < (timer_firstshot/0.01)){
                    Timer.delay(0.01);
                    counts++;
                }
                fsm.setGoalState(FSM.FIRE);
                //Fire SECOND Shot
                while((fsm.getCurrentState() != FSM.ELE_DOWN_NO_WRIST) && isAutonomous()){
                    Timer.delay(0.01);
                }
                fsm.setGoalState(FSM.BALL_DROP);
                Timer.delay(timer_ball_pulldown);
                fsm.setGoalState(FSM.SHOOTING2_ELE_UP);
                counts = 0;
                while(!robot.wrist.onTarget() && isAutonomous() && counts < timer_secondshot/0.01){
                    Timer.delay(0.01);
                    counts++;
                }
                fsm.setGoalState(FSM.FIRE);
                break;
                
            case 1:
                fsm.setGoalState(FSM.SHOOTING_ELE_UP);
                Timer.delay(0.1);
                robot.claw.topForward();
                while((robot.plunger.currentState() != Plunger.ARMED) && isAutonomous()){
                    Timer.delay(0.1);
                }
                robot.claw.topForward();
                fsm.setGoalState(FSM.SHOOTING_ELE_UP);
                Timer.delay(0.1);
                robot.claw.topForward();
                Timer.delay(0.6);

                driveDistanceHoldingHeading(120, 0, 0.80, 4.0, 3, true, 0);
                Timer.delay(2.5);
                robot.claw.allForward();
                Timer.delay(0.2);
                robot.claw.allStop();
                Timer.delay(0.2);
                fsm.setGoalState(FSM.FIRE);
                Timer.delay(0.20);
                break;
            case 2:
                boolean forward = false;
                double distanceCap = 120;
                double driveDistance = 0;
                double left = 0;
                double right = 0;
                driveDistanceHoldingHeadingThreaded(0, 0, 0.95, 200.0, 4.0, false);
                dist.setPID(Constants.DIST_KP_BIG, Constants.DIST_KI_BIG, Constants.DIST_KD_BIG);
                while((robot.kinect.getLeft() >=0 && robot.kinect.getRight() >= 0) && isAutonomous()){
                    System.out.println("waiting");
                    Timer.delay(0.1);
                }
                if(robot.kinect.getLeft() < 0){ //switch to getRight
                    System.out.println("leftUp");
                    distanceGoal -= 80;
                    forward = true;
                }else{
                    System.out.println("rightUp");
                    distanceGoal += 60;
                    forward = false;
                }
                Timer.delay(1.0);
                
                while(isAutonomous()){
                    left = robot.kinect.getLeft();
                    right = robot.kinect.getRight();
                    if(right < -0.2 && left > -0.2){ // flip left and right
                        if(distanceGoal + Constants.DIST_SMALL < distanceCap){
                            dist.setPID(Constants.DIST_KP, Constants.DIST_KI, Constants.DIST_KD);
                            distanceGoal += Constants.DIST_SMALL;
                        }
                        Timer.delay(0.5);
                    }else if(left < -0.2 && right > -0.2){ //flip left and right
                        if(distanceGoal - Constants.DIST_SMALL > -distanceCap){
                            dist.setPID(Constants.DIST_KP, Constants.DIST_KI, Constants.DIST_KD);
                            distanceGoal -= Constants.DIST_SMALL;
                        }
                        Timer.delay(0.5);
                    }else if(robot.kinect.getLeft() < -0.2 && robot.kinect.getRight() < -0.2){
                        if(distanceGoal > 0){
                            dist.setPID(Constants.DIST_KP_BIG, Constants.DIST_KI_BIG, Constants.DIST_KD_BIG);
                            distanceGoal = -80;
                        }else{
                            dist.setPID(Constants.DIST_KP_BIG, Constants.DIST_KI_BIG, Constants.DIST_KD_BIG);
                            distanceGoal = 60;
                        }
                        Timer.delay(1.0);
                    }else{
                        Timer.delay(0.1);
                    }
                }
                break;
        }
    }
    public void operatorControl() {
        robot.enabled();
        robot.dt.highGear();
        fsm.setGoalState(FSM.AFTERSHOT);
        while(true){
           controls.driveUpdate();
           controls.coPilot();
           /*
           if(robot.kinect.getLeft() < -0.2){
               robot.dt.directDrive(0.4, 0.4);
           }else if(robot.kinect.getRight() < -0.2){
               robot.dt.directDrive(-0.4, -0.4);
           }else{
               robot.dt.directDrive(0, 0);
           }
           */
           Timer.delay(0.05);
        }
    }
    public void test() {
        
    }
    
    public void turnToHeading(double heading, double timeout){
        robot.nav.resetRobotPosition(0, 0, 0,false);
        if(turn == null){
            turn = TurnController.getInstance();
        }
        turn.reset();
        turn.setGoal(heading,timeout);
        turnTh = new turnThread();
        turnTh.start();
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
    public double driveDistanceHoldingHeading(double distance, double heading,double maxSpeed,double timeout,double tol, boolean holdSpeed,double breakTime){                
        dist.resetDistance();
        double startDistance = robot.nav.getDistance();
        double distanceChange = distance + startDistance;
        System.out.println("DD: " + startDistance + " " + distanceChange + " " + distance);
        dist.reset();
        dist.setGoal(distanceChange, maxSpeed,heading,timeout,tol,holdSpeed);
        distTh.run();
        return robot.nav.getDistance() - startDistance;
    }
    public void driveDistanceHoldingHeadingThreaded(double distance, double heading,double maxSpeed,double timeout,double tol, boolean holdSpeed){                
        dist.reset();
        dist.setGoal(distance, maxSpeed,heading,timeout,tol,holdSpeed);
        turn.setGoal(0, 0.1);
        distTh = new distanceThread(true);
        distTh.start();
    }
    private class distanceThread extends Thread{
        private boolean keeprunning = false;
        public distanceThread(boolean keeper){
            keeprunning = keeper;
        }
        public void run(){
                if(!keeprunning){
                    while(!dist.onTarget() && isAutonomous()){
                        dist.run();
                        Timer.delay(0.01);
                    }
                }else{
                    while(isAutonomous()){
                        dist.run();
                        dist.resetZero();
                        dist.setSetpoint(distanceGoal);
                        Timer.delay(0.01);
                    }
                }
                System.out.println("done");
                robot.dt.directDrive(0, 0);
                       
        }
    }
    public void turnToHotGoal(int hotGoal){
        double angleToLeftGoal = -22;
        double angleToRightGoal = 22;
        switch(hotGoal){
            case Constants.LEFT_GOAL:
                turnToHeading(angleToLeftGoal, 1.75);
                break;
            case Constants.RIGHT_GOAL:
                turnToHeading(angleToRightGoal, 1.75);
                break;
        }
    }
}
