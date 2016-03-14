package IO;

import Team1323Robot.Robot;
import Team1323Robot.FSM;
import Utilities.Constants;
import edu.wpi.first.wpilibj.Joystick;

/** Handles the input from an xbox controller in order to calculate what the
 *  forebar angles and claw state should be. It is designed to keep the logic for
 *  deciding what to do apart from the logic for how to do it
 *
 * @author Robotics
 */ 
public class TeleController
{
    public static final double STICK_DEAD_BAND = 0.1;

    private Xbox xbox,xbox2;
    public Joystick right;
    public ThrustMasterWheel left;
    private Robot robot;
    private FSM fsm;
    private static TeleController instance = null;
    private boolean wristTrimTriggered = false;
    private boolean elevatorTrimTriggered = false;
    public TeleController(){
        
        left  = new ThrustMasterWheel(1);
        right = new Joystick(2);
        xbox  = new Xbox(3);
        xbox2 = new Xbox(4);
    }
    public static TeleController getInstance(){
        if(instance == null){
            instance = new TeleController();
        }
        return instance;
    }
    public void loadSubsystems(){
        robot = Robot.getInstance();
        fsm = FSM.getInstance();
    }
    
    public void coPilot(){
        if(xbox.getAButton()){
            //arm down at pickup angle, rollers on, pincher closed
            fsm.setGoalState(FSM.PICK_UP);
            robot.plunger.checkPlunger();
        }
        //////////////////////////////////////////
        if(xbox.getBButton()){
            if((fsm.getCurrentState()==FSM.SHOOTING_ELE_UP) || (fsm.getCurrentState() == FSM.SHOOTING2_ELE_UP) || (fsm.getCurrentState() == FSM.STOW_ELE_UP)){               
                fsm.setGoalState(FSM.STOW_ELE_UP);
                robot.plunger.checkPlunger();
                
            }else{
                fsm.setGoalState(FSM.STOW);
                robot.plunger.checkPlunger();
            }
        }
        ////////////////////////////////////////
        if(xbox.getXButton()){
            if((fsm.getCurrentState()==FSM.SHOOTING_ELE_UP) || (fsm.getCurrentState() == FSM.SHOOTING2_ELE_UP) || (fsm.getCurrentState() == FSM.STOW_ELE_UP)){               
                fsm.setGoalState(FSM.SHOOTING_ELE_UP);
                robot.plunger.checkPlunger();
            }else{
                fsm.setGoalState(FSM.SHOOTING);
                robot.plunger.checkPlunger();
            }
        }
        ///////////////////////////////////////
        if(xbox.getYButton()){
            if((fsm.getCurrentState()==FSM.SHOOTING_ELE_UP) || (fsm.getCurrentState() == FSM.SHOOTING2_ELE_UP) || (fsm.getCurrentState() == FSM.STOW_ELE_UP)){               
                fsm.setGoalState(FSM.SHOOTING2_ELE_UP);
                robot.plunger.checkPlunger();
            }else{
                fsm.setGoalState(FSM.SHOOTING2);
                robot.plunger.checkPlunger();
            }
        }
        /////////////////////////////////////////////

        if(xbox.getRightTrigger()){ 
            fsm.setGoalState(FSM.FIRE);
        }
        //////////////////////////////////
        if(xbox.getRightBumper()) {
           robot.claw.allForward();
           robot.plunger.checkPlunger();
        }
        ///////////////////////////////////////////////////////
        if(xbox.getLeftTrigger()){
           robot.claw.close();
        }
        //////////////////////////////////////////////////////////////////// 
        if(xbox.getLeftBumper()){ 
            robot.claw.allReverse(); //pincher in
        }
        //////////////////////////////////////////////////////
        if(xbox.getBackButton()){  // stop all 
          robot.stopAllMotors();
        }
        ////////////////////////////////////////////////////////
        if(xbox.getStartButton()){
            robot.elevator.setGoal(-20);
        }

        
        if (xbox.getRightStickY() > 0.4) {
            if (!wristTrimTriggered) {
                if(robot.wrist.sensorFail())
                    robot.wrist.manualDown();
                else
                    robot.wrist.trim(-1.5);
                wristTrimTriggered = true;
            }
        } else if (xbox.getRightStickY() < -0.4) {
            if (!wristTrimTriggered) {
                if(robot.wrist.sensorFail())
                    robot.wrist.manualUp();
                else
                    robot.wrist.trim(1.5);
                wristTrimTriggered = true;
            }
        } else {
            
            wristTrimTriggered = false;
            if(robot.wrist.sensorFail()){
                robot.wrist.stop();
                robot.wrist.manualStop();
            }
                
        }
        ///////////////////////////////////////////////
        if (xbox.getLeftStickY() > 0.4) {
            if (!elevatorTrimTriggered) {
                if(!robot.wrist.sensorFail()){
                    switch(fsm.getCurrentState()){
                        case FSM.SHOOTING_ELE_UP:
                            fsm.setGoalState(FSM.SHOOTING);
                        break;
                        case FSM.SHOOTING2_ELE_UP:
                            fsm.setGoalState(FSM.SHOOTING2);
                        break;
                        case FSM.STOW_ELE_UP:
                            fsm.setGoalState(FSM.STOW);
                        break;
                        default:
                            robot.elevator.setGoal(Constants.ELEVATOR_MIN_HEIGHT);
                        break;
                    }
                }
                elevatorTrimTriggered = true;
            }
        } else if (xbox.getLeftStickY() < -0.4) {
            if (!elevatorTrimTriggered) {
                switch(fsm.getCurrentState()){
                        case FSM.SHOOTING:
                            fsm.setGoalState(FSM.SHOOTING_ELE_UP);
                        break;
                        case FSM.SHOOTING2:
                            fsm.setGoalState(FSM.SHOOTING2_ELE_UP);
                        break;
                        case FSM.STOW:
                            fsm.setGoalState(FSM.STOW_ELE_UP);
                        break;
                        default:
                            robot.elevator.setGoal(Constants.ELEVATOR_MAX_HEIGHT);
                        break;
                    }
                elevatorTrimTriggered = true;
            }
        } else {
            elevatorTrimTriggered = false;
        }
        ///////////////////////////////////////////////
        if(xbox.getLeftStick()){
            robot.claw.allStop();
            robot.claw.topForward();
        }     
        ///////////////////////////////////////////////
        if(xbox.getRightStick()) {
            fsm.setGoalState(FSM.AFTERSHOT);
        }
        if(xbox.getDPADX() > 0){
          fsm.setGoalState(FSM.PASS_SHOT);
        }
        if(xbox.getDPADX() < 0){
            
        }        
    }
    
    public void driveUpdate() {

        if(right.getRawButton(4)){
            
        }
        
        if(right.getRawButton(5)){
            
        }
        if(right.getRawButton(10)){
            
        }
        
        if(right.getRawButton(11)){
            
        }
        if(right.getRawButton(8)){ //left 7
         
        }
        if(right.getRawButton(6)){
            
        }
        if (right.getRawButton(1)){robot.dt.lowGear();}
        if(right.getRawButton(3)){robot.dt.highGear(); }
        if(!robot.autoTurn){
            robot.dt.cheesyDrive(left.getX(), -right.getY(), left.getLeftBumper());
            
        }

    }
    
    
}
