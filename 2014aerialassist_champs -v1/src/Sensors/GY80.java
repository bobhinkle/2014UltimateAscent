/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Sensors;

import Utilities.Ports;
import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author xpsl05x
 */
public class GY80 extends SensorBase{
    
    private I2C i2c;
    
    // default address
    private static final byte kAddress = 0x69; 
    
    private static final byte CTRL_REG1 = 0x20;
    private static final byte CTRL_REG2 = 0x21;
    private static final byte CTRL_REG3 = 0x22;
    private static final byte CTRL_REG4 = 0x23;
    private static final byte CTRL_REG5 = 0x24;
    
    private static final byte xMSB_ADDRESS = 0x29;
    private static final byte xLSB_ADDRESS = 0x28;
    private static final byte yMSB_ADDRESS = 0x2B;
    private static final byte yLSB_ADDRESS = 0x2A;
    private static final byte zMSB_ADDRESS = 0x2D;
    private static final byte zLSB_ADDRESS = 0x2C;
    
    private static final byte REG1 = 0x4F;
    private static final byte REG2 = 0x27;
    private static final byte REG3 = 0x08;
    private static final byte REG4_1 = 0x00;
    private static final byte REG4_2 = 0x10;
    private static final byte REG4_3 = 0x30;
    private static final byte REG5 = 0x00;
    
    private double x;
    private double y;
    private double z;
    private static GY80 instance = null;
    //
    // constuctior with slot number parameter
    //
    public GY80() {
        i2c = new I2C( DigitalModule.getInstance(Ports.DIGITAL), kAddress );
        i2c.setCompatabilityMode(true);
        setupL3G4200D(250);
        System.out.println("hello rohi"+i2c.addressOnly());
    }
    public static GY80 getInstance()
    {
        if( instance == null )
            instance = new GY80();
        return instance;
    }
    public void getGyroValues(){

      byte xMSB = readRegister(xMSB_ADDRESS);
      byte xLSB = readRegister(xLSB_ADDRESS);
      int xCombine = ( xLSB & 0xFF ) | ( xMSB << 8 );
      System.out.println(xCombine + "com");
      x = (double) xCombine/ 512.0 ;
      
      byte yMSB = readRegister(yMSB_ADDRESS);
      byte yLSB = readRegister(yLSB_ADDRESS);
      y = ((yMSB << 8) | yLSB);

      byte zMSB = readRegister(zMSB_ADDRESS);
      byte zLSB = readRegister(zLSB_ADDRESS);
      z = ((zMSB << 8) | zLSB);
    }
    
    public void who_am_i(){
        byte[] data = new byte[1];
        i2c.read((byte) 0x0F,(byte) data.length,data);
        System.out.println(data[0] + " wai");
    }
    byte readRegister(byte address){

        // setup array for our data
        byte[] data = new byte[1];
        // read consecutive registers
        
        System.out.println(i2c.read( address, (byte) data.length, data) + " " + data.length + " " + data[0]);
        return data[0];
    }
    
    private void setupL3G4200D(int scale){
      //From  Jim Lindblom of Sparkfun's code

      // Enable x, y, z and turn off power down:
      System.out.println(i2c.write(CTRL_REG1, REG1));

      // If you'd like to adjust/use the HPF, you can edit the line below to configure CTRL_REG2:
      i2c.write(CTRL_REG2, REG2); //0x27

      // Configure CTRL_REG3 to generate data ready interrupt on INT2
      // No interrupts used on INT1, if you'd like to configure INT1
      // or INT2 otherwise, consult the datasheet:
      i2c.write(CTRL_REG3, REG3);

      // CTRL_REG4 controls the full-scale range, among other things:

      if(scale == 250){
        i2c.write(CTRL_REG4, REG4_1);
      }else if(scale == 500){
        i2c.write(CTRL_REG4, REG4_2);
      }else{
        i2c.write(CTRL_REG4, REG4_3);
      }

      // CTRL_REG5 controls high-pass filtering of outputs, use it
      // if you'd like:
      i2c.write(CTRL_REG5, REG5);
      Timer.delay(1.5);
    }
    
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public double getZ(){
        return z;
    }
}
