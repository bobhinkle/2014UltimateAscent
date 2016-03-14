/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Team1323Robot;

import Utilities.Constants;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 *
 * @author Eugene Fang
 */
public class FSM extends Thread {
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
    private boolean keepRunning = true;
    private int currentState = INIT;
    private int goalState = DEFAULT;
    private boolean fromA;
    private grabThread grab;
    public static FSM getInstance()
    {
        if( instance == null )
            instance = new FSM();
        return instance;
    }
        
    public FSM() {
        robot = Robot.getInstance();
        fromA = false;
        grab = new grabThread();
    }

    public void kill() {
        keepRunning = false;
    }
    public void setGoalState(int goal) {
        if(currentState == goal){
            currentState = FSM.DEFAULT;
            goalState = goal;
        }else{
            goalState = goal;
        }
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
        if(fromA && !grabberThread){
            grab.run();
        }
    }
    public void setFromA(){
        fromA = true;
    }
    public boolean raisedState(){
        return (getCurrentState()==FSM.STOW_ELE_UP) || ((getCurrentState()==FSM.STOW) && (robot.elevator.getHeight()>8));
    }
    public void run(){
        try {
            keepRunning = true;
            while(keepRunning){
                if(checkStateChange()){
                    switch(goalState){
                        case INIT:
                            SmartDashboard.putString("FSM_STATE", "INIT");
                            stateComplete(FSM.INIT);
                            break;
                        case FIRE:
                            SmartDashboard.putString("FSM_STATE", "FIRE");
                            robot.plunger.fire();
                            Timer.delay(0.1);
                            robot.claw.open();
                            robot.claw.ballFired();
                            robot.claw.allStop();
                            setGoalState(FSM.ELE_DOWN_NO_WRIST);    
                            break;
                        case ELE_DOWN_NO_WRIST:
                            SmartDashboard.putString("FSM_STATE", "ELE_DOWN");
                            robot.elevator.downGain();
                            robot.claw.close();
                            robot.claw.allStop();
                            robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                            fromA = true;
                            stateComplete(FSM.ELE_DOWN_NO_WRIST);
                            break;
                        case PICK_UP:
                            SmartDashboard.putString("FSM_STATE", "PICK_UP");
                            robot.elevator.downGain();
                            robot.wrist.setGoal(Constants.WRIST_LOADING_POS);
                            robot.claw.close();
                            robot.claw.allForward();
                            robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                            fromA = true;
                            stateComplete(FSM.PICK_UP);
                            break;
                        case AUTON_PICK_UP:
                            SmartDashboard.putString("FSM_STATE", "AUTON_PICK_UP");
                            robot.elevator.downGain();
                            robot.wrist.setGoal(Constants.WRIST_LOADING_POS);
                            robot.claw.close();
                            robot.claw.allForward();
                            robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                            fromA = true;
                            stateComplete(FSM.PICK_UP);
                            break;
                        case PICKUP_NO_ROLLER:
                            SmartDashboard.putString("FSM_STATE", "AUTON_PICK_UP_NO");
                            robot.elevator.downGain();
                            robot.wrist.setGoal(Constants.WRIST_LOADING_POS);
                            robot.claw.open();
                            robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                            fromA = true;
                            stateComplete(FSM.PICKUP_NO_ROLLER);
                            break;
                        case SHOOTING: 
                            SmartDashboard.putString("FSM_STATE", "SHOOTING");
                            grabber();
                            robot.elevator.downGain();
                            robot.wrist.setGoal(Constants.WRIST_SHOT);
                            robot.claw.close();
                            robot.claw.allStop();
                            robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                            fromA = false;
                            stateComplete(FSM.SHOOTING);
                            break;
                        case SHOOTING2:
                            SmartDashboard.putString("FSM_STATE", "SHOOTING2");
                            grabber();
                            robot.elevator.downGain();
                            robot.wrist.setGoal(Constants.WRIST_SHOT2);
                            robot.claw.close();
                            robot.claw.allStop();
                            robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                            fromA = false;
                            stateComplete(FSM.SHOOTING2);
                            break;
                        case STOW:
                            SmartDashboard.putString("FSM_STATE", "STOW");
                            grabber();
                            robot.elevator.downGain();
                            robot.wrist.setGoal(Constants.WRIST_STOW_POS);
                            robot.claw.close();
                            robot.claw.allStop();
                            robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                            fromA = false;
                            stateComplete(FSM.STOW);
                            break;
                        case SHOOTING_ELE_UP: 
                            SmartDashboard.putString("FSM_STATE", "SHOOTING");
                            grabber();
                            robot.elevator.upGain();
                            robot.wrist.setGoal(Constants.WRIST_SHOT_ELE_UP);
                            robot.claw.close();
                            robot.claw.allStop();
                            robot.elevator.setGoal(Constants.ELEVATOR_MAX_HEIGHT);
                            fromA = false;
                            stateComplete(FSM.SHOOTING_ELE_UP);
                            break;
                        case SHOOTING2_ELE_UP:
                            SmartDashboard.putString("FSM_STATE", "SHOOTING2");
                            grabber();
                            robot.elevator.upGain();
                            robot.wrist.setGoal(Constants.WRIST_SHOT2_ELE_UP);
                            robot.claw.close();
                            robot.claw.allStop();
                            robot.elevator.setGoal(Constants.ELEVATOR_MAX_HEIGHT);
                            fromA = false;
                            stateComplete(FSM.SHOOTING2_ELE_UP);
                            break;
                        case STOW_ELE_UP:
                            SmartDashboard.putString("FSM_STATE", "STOW");
                            grabber();
                            robot.elevator.upGain();
                            robot.wrist.setGoal(Constants.WRIST_STOW_POS_ELE_UP);
                            robot.claw.close();
                            robot.claw.allStop();
                            robot.elevator.setGoal(Constants.ELEVATOR_MAX_HEIGHT);
                            fromA = false;
                            stateComplete(FSM.STOW_ELE_UP);
                            break;
                        case AUTON_LEFT_SHOT:
                            SmartDashboard.putString("FSM_STATE", "AUTON_LEFT");
                            grabber();
                            robot.elevator.downGain();
                            robot.wrist.setGoal(Constants.WRIST_AUTON_LEFT_SHOT);
                            robot.claw.close();
                            robot.claw.allStop();
                            robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                            fromA = false;
                            stateComplete(FSM.AUTON_LEFT_SHOT);
                            break;
                        case AFTERSHOT:
                            SmartDashboard.putString("FSM_STATE", "AFTERSHOT");
                            robot.elevator.downGain();
                            robot.wrist.setGoal(Constants.WRIST_HORIZONTAL);
                            robot.claw.close();
                            robot.claw.allStop();
                            robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                            fromA = true;
                            stateComplete(FSM.AFTERSHOT);
                            break;
                        case PASS_SHOT:
                            SmartDashboard.putString("FSM_STATE", "PASS_SHOT");
                            robot.wrist.setGoal(Constants.WRIST_STOW_POS);
                            robot.claw.close();
                            if(robot.wrist.onTargetNow()){
                                setGoalState(FSM.PASS_SHOT_FINISH);
                            }
                            fromA = true;
                            break;
                        case PASS_SHOT_FINISH:
                            SmartDashboard.putString("FSM_STATE", "PASS_SHOT_FINISH");
                            robot.plunger.fire();
//                            robot.claw.allForward();
                            fromA = true;
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
                partsUpdate();
            }

        }catch(Exception e){

        }
    }
    private class grabThread extends Thread{
        
        public void run(){
            grabberThread = true;
            Timer.delay(.5);
            robot.claw.allForward();
            Timer.delay(0.1);
            robot.claw.allStop();
            grabberThread = false;            
        }
    }
    private void partsUpdate(){
        robot.comp.run();
        robot.wrist.run();
        robot.plunger.run();
        robot.elevator.run();
        robot.nav.run();
        robot.claw.run();
        if(robot.gyroInit.get() && !robot.isEnabled()){
            robot.nav.initGyro();
            robot.hotGoalCali = robot.hotGoal.intensity();
        }
        SmartDashboard.putNumber("ULTRASONIC", robot.claw.ultrasonic.getDistance());
        SmartDashboard.putNumber("HOTGOAL", robot.hotGoal.intensity());
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
            System.out.println("crash" + ex.toString());
        }
    }
}