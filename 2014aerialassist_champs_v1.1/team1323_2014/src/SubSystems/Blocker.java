/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SubSystems;

import Utilities.Ports;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author xpsl05x
 */
public class Blocker {
   
    Victor motor;
    
    public Blocker(){
        motor = new Victor(Ports.BLOCKER_MOTOR);
    }
    public void manualUp(){
        motor.set(-0.75);
    }
    public void manualDown(){
        motor.set(0.75);
    }
    public void stop(){
        motor.set(0);
    }
    
}
