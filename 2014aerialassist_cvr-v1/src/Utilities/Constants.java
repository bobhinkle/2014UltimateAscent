/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

/**
 *
 * @author xpsl05x
 */
public class Constants {
    
    public static final boolean LOW_GEAR  = true; // Drivetrain low gear
    public static final double MIN_DT_POWER = 0.2;
    
    public static final double TURN_KP = 0.023; //0.020
    public static final double TURN_KI = 0.000;
    public static final double TURN_KD = 0.0;//0.02
    public static final double TURN_KFV = 0.00;
    public static final double TURN_KFA = 0.00;
    public static final double TURN_ON_TARGET_DEG = 1;
    public static final double TURN_MAX_ACCEL = 90.0;
    public static final double TURN_MAX_VEL = 90.0;
    
     
    public static final double DIST_KP = 0.10; // 0.185
    public static final double DIST_KI = 0.00; // 0.00025
    public static final double DIST_KD = 0.0; // 10
    public static final double STRAIGHT_KP = 0.012;//.012
    public static final double STRAIGHT_KI = 0.0;
    public static final double STRAIGHT_KD = 0.0;
    public static final double DISTANCE_TOLERANCE = 1.0;
    
    public static final double WRIST_TOLERANCE = 1.0;
    public static final double WRIST_MIN_ANGLE = 90;
    public static final double WRIST_MAX_ANGLE = 68 + WRIST_MIN_ANGLE;
    public static final double WRIST_P = 0.043;
    public static final double WRIST_I = 0.0;
    public static final double WRIST_D = 0.04;
    public static final double WRIST_MAX_POWER = 1.0;
    public static final double WRIST_LOADING_POS = 3 + WRIST_MIN_ANGLE;
    public static final double WRIST_SHOT = 61 + WRIST_MIN_ANGLE; // X Shot (Back/White Shot) 39.5
    public static final double WRIST_SHOT2 = 62 + WRIST_MIN_ANGLE; //Y Shot (Middle Shot) 48
    public static final double WRIST_STOW_POS  = 60 + WRIST_MIN_ANGLE; //B Shot (Front Shot) //60
    public static final double WRIST_SHOT_ELE_UP = 56.25 + WRIST_MIN_ANGLE; //X Shot with Elevator 38.25
    public static final double WRIST_SHOT2_ELE_UP = 56.25 + WRIST_MIN_ANGLE;//Y Shot with Elevator
    public static final double WRIST_STOW_POS_ELE_UP  = 68 + WRIST_MIN_ANGLE;//B Shot with Elevator 63
    public static final double WRIST_HORIZONTAL = 32.5 + WRIST_MIN_ANGLE;
    public static final double WRIST_AUTON_LEFT_SHOT = 44 + WRIST_MIN_ANGLE;
    
    public static final double ELEVATOR_MAX_HEIGHT  = 12.5;   // MAXIMUM ELEVATOR HEIGHT 10
    public static final double ELEVATOR_MIN_HEIGHT  = -10;
    public static final double ELEVATOR_P = 0.4;
    public static final double ELEVATOR_I = 0.0;
    public static final double ELEVATOR_D = 0.05;
    public static final double ELEVATOR_DOWN_P = 0.03;
    public static final double ELEVATOR_DOWN_I = 0.0;
    public static final double ELEVATOR_DOWN_D = 0.9;
    public static final double ELEVATOR_AUTON_FLOOR_SHOT = 5.0;
    public static final double ELEVATOR_SHOT = 12.0;
    public static final double ELEVATOR_MIN_POWER = 0.0; //0.0 
    public static final double ELEVATOR_MAX_POWER = 1.0; //0.75
    public static final double ELEVATOR_TOLERANCE = 0.0;
    
    public static final double DRIVE_DISTANCE_PER_PULSE = 0.03420833;
    public static final double ELEVATOR_DISTANCE_PER_PULSE = (13/4561.0)*4.0;
    
    public static final double VOLTS_TO_PSI = 53.18;
    
    public static final int GYRO_INIT = 0;
    public static final int GYRO_READY  = 1;
    public static final int INTAKEON = 3;
    public static final int NOTREADYTOSHOOT = 4;
    public static final int READYTOSHOOT    = 5;
    
    public static final int LEFT_GOAL = 1;
    public static final int RIGHT_GOAL = 2;
    
    public static final double AMBIENT_LIGHT = 350;
}