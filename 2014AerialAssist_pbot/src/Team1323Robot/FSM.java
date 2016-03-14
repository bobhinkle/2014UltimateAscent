/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Team1323Robot;

import SubSystems.Lights;
import Utilities.Constants;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 *
 * @author Eugene Fang
 */
public class FSM {
    private Robot robot;
    private static FSM instance = null;
    private boolean grabberThread = false;
    // Steady States
    public final static int INIT = 0;
    public final static int DEFAULT = -1;
    public final static int SHOOTING = 100;
    public final static int PICK_UP = 101;
    public final static int CATCHING = 102;
    public final static int FIRE = 103;
    public final static int STOW = 104;
    public final static int SHOOTING2 = 105;
    public final static int AFTERSHOT = 106;
    public final static int PASS_SHOT = 107;
    public final static int PASS_SHOT_FINISH = 108;
    public final static int AUTON_PICK_UP = 109;
    public final static int PICKUP_NO_ROLLER = 110;
    public final static int ROLLER_STOPPED = 111;
    public final static int SHOOTING_ELE_UP = 112;
    public final static int SHOOTING2_ELE_UP = 113;
    public final static int STOW_ELE_UP = 114;
    public final static int AUTON_LEFT_SHOT = 115;
    public final static int ELE_DOWN_NO_WRIST = 116;
    public final static int BALL_DROP = 117;
    public final static int HORIZONTAL = 118;
    private int currentState = INIT;
    private int goalState = DEFAULT;
    private boolean firing = false;
    private grabThread grab;
    private partsUpdate pu;
    private fireThread ft;
    private int clawState = 0;
    public static FSM getInstance()
    {
        if( instance == null )
            instance = new FSM();
        return instance;
    }
        
    public FSM() {
        robot = Robot.getInstance();
        grab = new grabThread();
        pu = new partsUpdate();
        pu.start();
    }

    public void setGoalState(int goal) {
        if(currentState == goal){
            currentState = FSM.DEFAULT;
            goalState = goal;
        }else{
            goalState = goal;
        }
        run();
    }
    public int getGoalState(){
        return goalState;
    }
    private boolean checkStateChange(){
        if(goalState != currentState){
            return true;
       }
        return false;
    }
    public int getCurrentState() {
        return currentState;
    }
    private void stateComplete(int state){
        currentState = state;
    }
    
    private void grabber(){
        if(!grabberThread){
            grab = new grabThread();
            grab.start();
        }
    }
    
    public boolean raisedState(){
        return (getCurrentState()==FSM.STOW_ELE_UP) || ((getCurrentState()==FSM.STOW) && (robot.elevator.getHeight()>8));
    }
    public void run(){
            if(checkStateChange()){
                switch(goalState){
                    case INIT:
                        SmartDashboard.putString("FSM_STATE", "INIT");
                        stateComplete(FSM.INIT);
                        break;
                    case FIRE:
                        /*
                        SmartDashboard.putString("FSM_STATE", "FIRE");
                        robot.plunger.fire();
                        Timer.delay(0.1);
                        robot.claw.open();
                        robot.claw.ballFired();
                        robot.claw.allStop();
                        setGoalState(FSM.ELE_DOWN_NO_WRIST); */
                        if(!firing){
                            ft = new fireThread();
                            ft.start();
                            stateComplete(FSM.FIRE);
                        }
                        break;
                    case ELE_DOWN_NO_WRIST:
                        SmartDashboard.putString("FSM_STATE", "ELE_DOWN");
                        robot.elevator.downGain();
//                        robot.claw.open();
                        robot.claw.allStop();
                        robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                        robot.wrist.setGoal(Constants.WRIST_B_SHOT);
                        stateComplete(FSM.ELE_DOWN_NO_WRIST);
                        break;
                    case PICK_UP:
                        SmartDashboard.putString("FSM_STATE", "PICK_UP");
                        robot.elevator.downGain();
                        robot.wrist.setGoal(Constants.WRIST_LOADING_POS);
                        robot.claw.close();
                        robot.claw.allForward();
                        robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                        stateComplete(FSM.PICK_UP);
                        break;
                    case AUTON_PICK_UP:
                        SmartDashboard.putString("FSM_STATE", "AUTON_PICK_UP");
                        robot.elevator.downGain();
                        robot.wrist.setGoal(Constants.WRIST_LOADING_POS);
                        robot.claw.open();
                        robot.claw.allForward();
                        robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                        stateComplete(FSM.AUTON_PICK_UP);
                        break;
                    case PICKUP_NO_ROLLER:
                        SmartDashboard.putString("FSM_STATE", "AUTON_PICK_UP_NO");
                        robot.elevator.downGain();
                        robot.wrist.setGoal(Constants.WRIST_LOADING_POS);
                        robot.claw.open();
                        robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                        stateComplete(FSM.PICKUP_NO_ROLLER);
                        break;
                    case SHOOTING: 
                        SmartDashboard.putString("FSM_STATE", "SHOOTING");
                        grabber();
                        robot.elevator.downGain();
                        robot.wrist.setGoal(Constants.WRIST_X_SHOT);
                        robot.claw.close();
                        robot.claw.allStop();
                        robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                        clawState = 0; //neutral
                        stateComplete(FSM.SHOOTING);
                        break;
                    case SHOOTING2:
                        SmartDashboard.putString("FSM_STATE", "SHOOTING2");
                        grabber();
                        robot.elevator.downGain();
                        robot.wrist.setGoal(Constants.WRIST_Y_SHOT);
                        robot.claw.close();
                        robot.claw.allStop();
                        robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                        clawState = 0; //neutral
                        stateComplete(FSM.SHOOTING2);
                        break;
                    case STOW:
                        SmartDashboard.putString("FSM_STATE", "STOW");
                        grabber();
                        robot.elevator.downGain();
                        robot.wrist.setGoal(Constants.WRIST_B_SHOT);
                        robot.claw.close();
                        robot.claw.allStop();
                        robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                        clawState = 1; //open
                        stateComplete(FSM.STOW);
                        break;
                    case SHOOTING_ELE_UP: 
                        SmartDashboard.putString("FSM_STATE", "SHOOTING");
                        robot.elevator.upGain();
                        robot.wrist.setGoal(Constants.WRIST_X_SHOT_ELE_UP);
                        robot.claw.close();
                        robot.claw.allStop();
                        robot.elevator.setGoal(Constants.ELEVATOR_MAX_HEIGHT);
                        clawState = 0; //neutral
                        grabber();
                        stateComplete(FSM.SHOOTING_ELE_UP);
                        break;
                    case SHOOTING2_ELE_UP:
                        SmartDashboard.putString("FSM_STATE", "SHOOTING2");
                        robot.elevator.upGain();
                        robot.wrist.setGoal(Constants.WRIST_Y_SHOT_ELE_UP);
                        robot.claw.close();
                        robot.claw.allStop();
                        robot.elevator.setGoal(Constants.ELEVATOR_MAX_HEIGHT);
                        clawState = 0; //neutral
                        grabber();
                        stateComplete(FSM.SHOOTING2_ELE_UP);
                        break;
                    case STOW_ELE_UP:
                        SmartDashboard.putString("FSM_STATE", "STOW");
                        robot.elevator.upGain();
                        robot.wrist.setGoal(Constants.WRIST_B_SHOT_ELE_UP);
                        robot.claw.close();
                        robot.claw.allStop();
                        robot.elevator.setGoal(Constants.ELEVATOR_MAX_HEIGHT);
                        clawState = 0; //neutral
                        grabber();
                        stateComplete(FSM.STOW_ELE_UP);
                        break;
                    case BALL_DROP:
                        SmartDashboard.putString("FSM_STATE", "BALL_DROP");
                        robot.elevator.upGain();
                        robot.wrist.setGoal(Constants.WRIST_B_SHOT_ELE_UP);
                        robot.claw.close();
                        robot.claw.stopSide();
                        robot.claw.topForward();
                        robot.elevator.setGoal(Constants.ELEVATOR_MAX_HEIGHT);
                        stateComplete(FSM.BALL_DROP);
                        break;
                    case AUTON_LEFT_SHOT:
                        SmartDashboard.putString("FSM_STATE", "AUTON_LEFT");

                        robot.elevator.downGain();
                        robot.wrist.setGoal(Constants.WRIST_AUTON_LEFT_SHOT);
                        robot.claw.close();
                        robot.claw.allStop();
                        robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                        grabber();
                        stateComplete(FSM.AUTON_LEFT_SHOT);
                        break;
                    case AFTERSHOT:
                        SmartDashboard.putString("FSM_STATE", "AFTERSHOT");
                        robot.elevator.downGain();
                        robot.wrist.setGoal(Constants.WRIST_B_SHOT);
                        robot.claw.close();
                        robot.claw.allStop();
                        robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                        stateComplete(FSM.AFTERSHOT);
                        break;
                    case HORIZONTAL:
                        SmartDashboard.putString("FSM_STATE", "HORIZONTAL");
                        robot.elevator.downGain();
                        robot.wrist.setGoal(Constants.WRIST_HORIZONTAL);
                        robot.claw.close();
                        robot.claw.allStop();
                        robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                        stateComplete(FSM.AFTERSHOT);
                        break;
                    case PASS_SHOT:
                        SmartDashboard.putString("FSM_STATE", "PASS_SHOT");
                        robot.wrist.setGoal(Constants.WRIST_B_SHOT);
                        robot.claw.close();
                        if(robot.wrist.onTargetNow()){
                            setGoalState(FSM.PASS_SHOT_FINISH);
                        }
                        break;
                    case PASS_SHOT_FINISH:
                        SmartDashboard.putString("FSM_STATE", "PASS_SHOT_FINISH");
                        robot.plunger.fire();
//                            robot.claw.allForward();
                        setGoalState(FSM.AFTERSHOT);                            
                        break;
                    case ROLLER_STOPPED:
                        robot.claw.allStop();
                        stateComplete(FSM.ROLLER_STOPPED);
                        break;
                    default:
                        SmartDashboard.putString("FSM_STATE", "DEFAULT");
                        stateComplete(FSM.DEFAULT);
                        break;
                    }
            }
    }
    private class fireThread extends Thread{
        private FSM fsm;
        public void fireThread(){
            fsm = FSM.getInstance();
        }
        public void run(){
            firing = true;
            SmartDashboard.putString("FSM_STATE", "FIRE");  
            if(clawState == 0){
                robot.claw.neutral();
            }else{
                robot.claw.open();
            }
            Timer.delay(0.05);
            robot.plunger.fire();
            Timer.delay(0.2);
            robot.claw.ballFired();
            robot.claw.allStop();
            Timer.delay(0.5);
            robot.claw.close();
            setGoalState(FSM.ELE_DOWN_NO_WRIST);
            if(fsm == null){
                fsm = FSM.getInstance();
            }
            fsm.run();
            firing = false;
        }
    }
    private class grabThread extends Thread{
        private double timestart = 0;
        private double timeend   = 0;
        public void run(){            
            grabberThread = true;
            Timer.delay(0.2);
            robot.claw.sideForward();
            Timer.delay(0.2);
            robot.claw.topForward();
            robot.claw.stopSide();
            Timer.delay(0.5);
            robot.claw.allStop();
            grabberThread = false;            
        }
    }
    private class partsUpdate extends Thread{
        
        public void run(){
            while(true){
                try{
                    robot.comp.run();
                    robot.wrist.run();
                    robot.plunger.run();
                    robot.elevator.run();
                    robot.nav.run();
                    robot.claw.run();
                    displayAutonMode();
                    SmartDashboard.putNumber("ULTRASONIC", robot.claw.ultrasonic.getDistance());
                    SmartDashboard.putNumber("HOTGOAL", robot.hotGoal.intensity());
                    SmartDashboard.putNumber("WRIST_ERROR", robot.wrist.error());
                    SmartDashboard.putNumber("ELE_ERROR", robot.elevator.error());
                    
//                    robot.autonMode = robot.prefs.getInt("autonSelect", 1);
//                    SmartDashboard.putNumber("AutonSelect", robot.autonMode);
                    if(robot.claw.rollersOn()){
                        robot.lights.setState(2);
                    }else if(robot.wrist.onTargetNow()){
                        robot.lights.setState(Lights.OFF_TARGET);
                    }else{
                        robot.lights.setState(Lights.ON_TARGET);
                    }
                    robot.lights.updateLights();
                    if(robot.gyroInit.get()){
                        robot.nav.initGyro();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try{
                    robot.kinect.run();
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
                Timer.delay(0.02);
            }
        }
    }
    private void displayAutonMode(){
        //0 = 2 Ball - Turn with Kinect
        //1 = 1 Ball from CVR
        //2 = Kinect with Blocker
        //3 = 2 Ball - Straight
        switch(Constants.autonSelect){
            case 0:
                SmartDashboard.putString("AUTON", "2BALL Kinect");
                break;
            case 1:
                SmartDashboard.putString("AUTON", "1BALL");
                break;
            case 2:
                SmartDashboard.putString("AUTON", "KinBlocker");
                break;
            case 3:
                SmartDashboard.putString("AUTON", "2BALL Straight");
                break;
        }
    }
}