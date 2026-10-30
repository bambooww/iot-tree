package org.iottree.driver.zigbee.zspec.util;

public enum PowerMode
{
	NODEPWR_CONTINIOUS(0,"Receiver On When Idle","市电供电的路由器、协调器、智能插座"),
	NODECURPWR_RCVR_AUTO(1, "Periodic Wakeup","定时上报的温湿度传感器、智能水表"), 
	NODECURPWR_RCVR_STIM(2, "Stimulated Wakeup","无线开关、门磁、人体传感器"); //接收机仅在外部刺激（如按键、传感器阈值触发）时唤醒

	private final int val;
	private final String title;
	private final String desc;

	PowerMode(int val, String title,String desc)
	{
		this.val = val;
		this.title = title;
		this.desc = desc ;
	}

	public int getValInt()
	{
		return val;
	}

	public String getTitle()
	{
		return title;
	}
	
	public String getDesc()
	{
		return this.desc ;
	}
	
	public String toTitleString()
	{
		return val+":"+this.title ;
	}
	
	public static PowerMode valOfInt(int v)
	{
		switch(v)
		{
		case 0:
			return NODEPWR_CONTINIOUS ;
		case 1:
			return NODECURPWR_RCVR_AUTO;
		case 2:
			return NODECURPWR_RCVR_STIM;
		default:
			return null ;
		}
	}
}
