package org.iottree.ext.conn;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class DesktopEye
{
	public void capScreen() throws Exception
	{
		Robot robot = new Robot() ;
		
		for(int i = 0 ; i < 10 ; i ++)
		{
			robot.delay(3000);
			robot.mouseMove(150*i, 100*i);
		}
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize() ;
		Rectangle rect=  new Rectangle(dim) ;
		
		BufferedImage bi = robot.createScreenCapture(rect) ;
		
		ImageIO.write(bi, "jpg", new File("D:\\tmp\\ssss.jpg")) ;
	}
}
