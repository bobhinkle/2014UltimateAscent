/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Sensors;

import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.I2C;

/**
 *
 * @author mainuser
 */
public class ADXL345_2 {
    

	private I2C comm;

	public static final int AXIS_X = 0x00;
	public static final int AXIS_Y = 0x02;
	public static final int AXIS_Z = 0x04;

	public ADXL345_2()
	{
		DigitalModule module = DigitalModule.getInstance(1);
		comm = new I2C(module, 0x3A);
                comm.setCompatabilityMode(true);
		comm.write(0x2D, 0x08);	//turn on measurements
		comm.write(0x31, 0x08); //set the data format to 00
	}
        public class Point{
            double x,y,z;
            
            public Point(double x1, double y1, double z1){
                x = x1;
                y = y1;
                z = z1;
            }
        }
	public double getAccel(int axis)
	{
		byte[] rawAccel = new byte[2];
		comm.read(0x32 + axis, 2, rawAccel);
		short tempLow = (short) (rawAccel[0] & 0xff);
		short tempHigh = (short) ((rawAccel[1] << 8) & 0xff00);

		return ((double) (tempHigh | tempLow) * 0.00390625) * 9.8;
	}

	public Point getAccel()
	{
		return new Point(
			getAccel(AXIS_X),
			getAccel(AXIS_Y),
			getAccel(AXIS_Z)
		);
	}

	public void getAccel(Point pt)
	{
		pt.x = getAccel(AXIS_X);
		pt.y = getAccel(AXIS_Y);
		pt.z = getAccel(AXIS_Z);
	}
}