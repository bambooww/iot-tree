package org.iottree.driver.zigbee.zspec.util;

import org.iottree.core.util.Lan;

/**
 * 供电来源
 */
public enum PowerSrc
{
	unknown(0x00,"Unknown","未知"),
	mains1(1, "Mains (single phase)","单相交流市电"), 
	mains3(2, "Mains (three phase)","三相交流市电"),
	battery(3, "Battery","电池供电"),
	dc(4, "DC Source","直流电源"),
	em_mains_const(5, "Emergency Mains Constantly Powered","应急市电持续供电"),
	em_mains_switch(6, "Emergency Mains And Transfer Switch","应急电源切换供电")
	;

	private final int val;
	private final String title_en;
	private final String title_cn;

	PowerSrc(int val, String title_en,String title_cn)
	{
		this.val = val;
		this.title_en = title_en;
		this.title_cn = title_cn ;
	}
	
	public boolean isNoLimit()
	{
		return this.val!=3 && this.val!=6 ;
	}

	public int getValInt()
	{
		return val;
	}

	public String getTitle()
	{
		if("cn".equals(Lan.getUsingLang()))
				return title_cn;
		else
			return title_en ;
	}
	
	public static PowerSrc valOfInt(int v)
	{
		for(PowerSrc ps:PowerSrc.values())
		{
			if(ps.val==v)
				return ps ;
		}
		return null ;
	}
}
