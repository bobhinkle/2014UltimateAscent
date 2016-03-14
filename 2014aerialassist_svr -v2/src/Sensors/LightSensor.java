/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Sensors;

import edu.wpi.first.wpilibj.AnalogChannel;

/**
 *
 * @author mainuser
 */
public class LightSensor {
    private AnalogChannel sensor;
    
    public LightSensor(int port){
        sensor = new AnalogChannel(port);
    }
    public double intensity(){
        return Math.floor(sensor.getVoltage()/.01);
    }
}
