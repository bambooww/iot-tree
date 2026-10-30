package org.iottree.driver.zigbee.zspec.util;

public class PowerLevel
{
	int lvl  ;
	public PowerLevel(int v)
	{
		this.lvl = v ;
	}
	
	/**
	 * 0-100
	 * @return
	 */
	public int getLvlRatio()
	{
		switch(this.lvl)
		{
		case 0x00:
			return 0 ; //Critically Low：电量极低，即将关机
		case 0x04:
			return 33 ;
		case 0x08:
			return 66 ;
		case 0x0C:
			return 100 ; //（接近满电）或市电供电
		default:
			return -1 ;
		}
	}
	
	public String toString()
	{
		int lr = getLvlRatio() ;
		if(lr<0)
			return "-" ;
		return getLvlRatio()+"%" ;
	}
}
