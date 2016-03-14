/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

/**
 *
 * @author xpsl05x
 */
public class Ports {
    // Digital Sidecar 
    public static final int LEFTENC     = 1; //USES 2 Ports ( 1-2)
    
    public static final int ELEVATOR_ENC = 5;
    public static final int PLUNGER_STOP_SENSOR      = 7;
    public static final int PLUNGER_HARDSTOP_SENSOR  = 8;
    public static final int ELEVATOR_BOTTOM_LIMIT    = 9 ;
    
    public static final int GYRO_INIT_BUTTON         = 12;
    public static final int COMPRESSOR_SWITCH        = 14;
    
    //Digital Sidecar 1 I2C
    public static final int ACCEL = 2;
    
    //Digital Sidecar 1 PWM
    
    public static final int RIGHTDT              = 1; 
    public static final int LEFTDT               = 2; 
    public static final int CLAW_INTAKE          = 9; //3
    public static final int PLUNGER              = 4;
    public static final int CLAW_TOP             = 5;
    public static final int WRIST                = 6;
    public static final int ELEVATOR             = 7;
    public static final int CLAW_INTAKE_2        = 8;
    public static final int BLOCKER_MOTOR        = 10;
    
    //Modules
    public static final int ANALOG   = 0;
    public static final int DIGITAL  = 1;
    public static final int SILENOID = 2;
    
    // Solenoids
    public static final int SHIFTER     = 1;
    public static final int PLUNGER_PISTON = 2;
    
    public static final int CLAW_PISTON1 = 3;
    public static final int CLAW_PISTON2 = 4;
    // Analog
    public static final int GYRO            = 1;
    public static final int GYRO2           = 2;
    public static final int AIR_PRESSURE    = 3;
    public static final int ULTRA_SONIC     = 4;
    public static final int WRIST_MA3       = 6;
    public static final int LIGHT_SENSOR    = 7;
    //cRio Port
    public static final int CRIOPORT     = 1;
    
    //Relay
    public static final int COMPRESSOR_SPIKE = 1;
    public static final int STATE1      = 2;
    public static final int STATE2      = 3; 
    public static final int STATE3      = 4;
    
}
