/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Sensors;

import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author mainuser
 */
public class I2C_UltraSonic {
    private I2C comm;
    public static final int RANGE_COMMAND = 0x70;
    public I2C_UltraSonic(){
        DigitalModule module = DigitalModule.getInstance(1);
        comm = new I2C(module, 0xE0);
        comm.setCompatabilityMode(true);
    }
    public short getRange(){
        comm.write(0xE0, RANGE_COMMAND);
        Timer.delay(0.1);
        byte[] rawAccel = new byte[2];
        comm.read(0xE1, 2, rawAccel);
        return twoBytesToShort(rawAccel[0],rawAccel[1]);
    }
    public static short twoBytesToShort(byte b1, byte b2) {
          return (short) ((b1 << 8) | (b2 & 0xFF));
    }
}
