package IO;

import Team1323Robot.Robot;
import Team1323Robot.FSM;
import Utilities.Constants;
import Utilities.Prefs;
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
    private Prefs prefs;
    public TeleController(){
        
        left  = new ThrustMasterWheel(1);
        right = new Joystick(2);
        xbox  = new Xbox(3);
        xbox2 = new Xbox(4);
        prefs = Prefs.getInstance();
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
            System.out.println("A " + System.currentTimeMillis());
            //arm down at pickup angle, rollers on, pincher closed
            fsm.setGoalState(FSM.PICK_UP);
            robot.plunger.checkPlunger();
            robot.claw.close();
        }
        //////////////////////////////////////////
        if(xbox.getBButton()){
            System.out.println("B " + System.currentTimeMillis());
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
            //fsm.setGoalState(FSM.FIRE);
            
            System.out.println("X " + System.currentTimeMillis());
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
            System.out.println("Y " + System.currentTimeMillis());
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
            System.out.println("rightTrigger " + System.currentTimeMillis());
        }
        //////////////////////////////////
        if(xbox.getRightBumper()) {
           robot.claw.close(); 
           robot.claw.allForward();
           robot.plunger.checkPlunger();
           System.out.println("rightBumper " + System.currentTimeMillis());
        }
        ///////////////////////////////////////////////////////
        if(xbox.getLeftTrigger()){
           robot.claw.open();
           robot.claw.allForward();
           System.out.println("leftTrigger " + System.currentTimeMillis());
        }
        //////////////////////////////////////////////////////////////////// 
        if(xbox.getLeftBumper()){ //reverse rollers
            robot.claw.allReverse();
            robot.claw.neutral();
            System.out.println("leftBumper " + System.currentTimeMillis());
        }
        //////////////////////////////////////////////////////
        if(xbox.getBackButton()){  // stop all 
          robot.stopAllMotors();
          System.out.println("back " + System.currentTimeMillis());
        }
        ////////////////////////////////////////////////////////
        if(xbox.getStartButton()){
            robot.elevator.setGoal(-20);
            System.out.println("start " + System.currentTimeMillis());
        }

        
        if (xbox.getRightStickY() > 0.4) {
            System.out.println("rightStickY high " + System.currentTimeMillis());
            if (!wristTrimTriggered) {
                if(robot.wrist.sensorFail())
                    robot.wrist.manualDown();
                else
                    robot.wrist.trim(-1.5);
                wristTrimTriggered = true;
            }
        } else if (xbox.getRightStickY() < -0.4) {
            System.out.println("rightStickY low " + System.currentTimeMillis());
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
            System.out.println("leftStickY high " + System.currentTimeMillis());
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
            System.out.println("leftStickY low " + System.currentTimeMillis());
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
            System.out.println("leftStick " + System.currentTimeMillis());
        }     
        ///////////////////////////////////////////////
        if(xbox.getRightStick()) {
            fsm.setGoalState(FSM.HORIZONTAL);
            System.out.println("rightStick " + System.currentTimeMillis());
        }
        if(xbox.getDPADX() > 0){
          robot.blocker.manualDown();
        }else if(xbox.getDPADX() < 0){
          robot.blocker.manualDown();
        }else{
            robot.blocker.stop();
        }
    }
    
    public void driveUpdate() {

        if(right.getRawButton(4)){
            prefs.getPrefs();
        }
        
        if(right.getRawButton(5)){
//            robot.driveDistanceHoldingHeading(115 , 0, 0.85, 4, 1.0, false, 0); //75
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
            robot.dt.cheesyDrive(left.getX(), -right.getY(), left.getLeftBumper() || left.getRightBumper());
            
        }

    }
    
    
}
