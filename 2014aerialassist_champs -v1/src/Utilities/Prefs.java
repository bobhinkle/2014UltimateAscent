/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Utilities;

import SubSystems.DistanceController;
import Team1323Robot.Robot;
import edu.wpi.first.wpilibj.Preferences;

/**
 *
 * @author xpsl05x
 */
public class Prefs {
    
    
    public Preferences prefs;
    private Robot robot;
    private DistanceController dist;
    public Prefs(){
        robot = Robot.getInstance();
        dist  = DistanceController.getInstance();
    }
    public void initPrefs(){
        try{
            prefs.putDouble("DISTANCE_P_2BALL", Constants.DIST_KP_2BALL);
            prefs.putDouble("DISTANCE_I_2BALL", Constants.DIST_KI_2BALL);
            prefs.putDouble("DISTANCE_D_2BALL", Constants.DIST_KD_2BALL);

            prefs.putDouble("WRIST_UP_P", Constants.WRIST_P_UP);
            prefs.putDouble("WRIST_UP_I", Constants.WRIST_I_UP);
            prefs.putDouble("WRIST_UP_D", Constants.WRIST_D_UP);
            prefs.putDouble("WRIST_DOWN_P", Constants.WRIST_P_DOWN);
            prefs.putDouble("WRIST_DOWN_I", Constants.WRIST_I_DOWN);
            prefs.putDouble("WRIST_DOWN_D", Constants.WRIST_D_DOWN);
        }catch(Exception e){
            System.out.println(e);
        }
    }
    public void getPrefs(){
        if(robot == null){
            robot = Robot.getInstance();
        }
        if(dist == null){
            dist = DistanceController.getInstance();
        }
        try{
            double p,i,d = 0;
            p = prefs.getDouble("WRIST_UP_P", Constants.WRIST_P_UP);
            i = prefs.getDouble("WRIST_UP_I", Constants.WRIST_I_UP);
            d = prefs.getDouble("WRIST_UP_D", Constants.WRIST_D_UP);
            robot.wrist.setUpPIDValues(p, i, d);
            p = prefs.getDouble("WRIST_DOWN_P", Constants.WRIST_P_DOWN);
            i = prefs.getDouble("WRIST_DOWN_I", Constants.WRIST_I_DOWN);
            d = prefs.getDouble("WRIST_DOWN_D", Constants.WRIST_D_DOWN);
            robot.wrist.setDownPIDValues(p, i, d);
            p = prefs.getDouble("WRIST_DOWN_P", Constants.WRIST_P_DOWN);
            i = prefs.getDouble("WRIST_DOWN_I", Constants.WRIST_I_DOWN);
            d = prefs.getDouble("WRIST_DOWN_D", Constants.WRIST_D_DOWN);
            robot.wrist.setDownPIDValues(p, i, d);
            p = prefs.getDouble("DISTANCE_P_2BALL", Constants.DIST_KP_2BALL);
            i = prefs.getDouble("DISTANCE_I_2BALL", Constants.DIST_KI_2BALL);
            d = prefs.getDouble("DISTANCE_D_2BALL", Constants.DIST_KD_2BALL);
            dist.setDistPIDValues(p, i, d);
            dist.updatePIDValues();
        }catch(Exception e){
            System.out.println(e);
        }
        
    }
}
