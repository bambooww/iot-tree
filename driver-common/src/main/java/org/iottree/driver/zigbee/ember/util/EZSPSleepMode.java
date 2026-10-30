package org.iottree.driver.zigbee.ember.util;


public class EZSPSleepMode
{
	private static String[] OPTS = new String[] {
			"Idle", // 0
			"Deep sleep", //1 
			"Power down", //2
			"Reserved", //3
	};
	
	private int v ;
	
	public EZSPSleepMode(int v)
	{
		if(v<0||v>3)
			throw new IllegalArgumentException("v must in [0-3]") ;
		this.v = v ;
	}

	public int getVal()
	{
		return this.v ;
	}
	
	public String getTitle()
	{
		return OPTS[this.v] ;
	}
	
	public String toString()
	{
		return v+"="+this.getTitle() ;
	}
}
