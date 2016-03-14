/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SubSystems;

import edu.wpi.first.wpilibj.KinectStick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author mainuser
 */
public class Kinect {
    
    KinectStick leftArm;
    KinectStick rightArm;
    public Kinect(){
        
        leftArm = new KinectStick(1);
        rightArm = new KinectStick(2);
    }
    public void run(){
        SmartDashboard.putNumber("K_LEFT", leftArm.getY());
        SmartDashboard.putNumber("K_RIGHT", rightArm.getY());
    }
    public double getLeft(){
        try{
            return leftArm.getY();
        }catch(Exception e){
            return 0;
        }
        
    }
    public double getRight(){
        try{
            return rightArm.getY();
        }catch(Exception e){
            return 0;
        }
    }
}
