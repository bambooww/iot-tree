package org.iottree.driver.zigbee.zspec.util;

/**
 * Zigbee - type
 * 
 * @author jason.zhu
 *
 */
public enum LogicalType
{
	coor(0, "coordinator"), router(1, "router"), end_dev(2, "end_device");

	private final int val;
	private final String title;

	LogicalType(int val, String title)
	{
		this.val = val;
		this.title = title;
	}

	public int getValInt()
	{
		return val;
	}

	public String getTitle()
	{
		return title;
	}
	
	
	public static LogicalType valOfInt(int v)
	{
		switch(v)
		{
		case 0:
			return coor ;
		case 1:
			return router;
		case 2:
			return end_dev;
		default:
			return null ;
		}
	}
}
