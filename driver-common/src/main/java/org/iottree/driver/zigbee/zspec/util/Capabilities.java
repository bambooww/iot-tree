package org.iottree.driver.zigbee.zspec.util;

import java.util.ArrayList;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class Capabilities
{
	public static final int MT_CAP_SYS =0x0001;
	public static final int MT_CAP_MAC =0x0002;
	public static final int MT_CAP_NWK =0x0004;
	public static final int MT_CAP_AF =0x0008;
	public static final int MT_CAP_ZDO =0x0010;
	public static final int MT_CAP_SAPI =0x0020;
	public static final int MT_CAP_UTIL =0x0040;
	public static final int MT_CAP_DEBUG =0x0080;
	public static final int MT_CAP_APP =0x0100;
	public static final int MT_CAP_ZOAD =0x1000;
	
	private static String[] OPTS = new String[] {
			"SYS", //bit 0
			"MAC",
			"NWK",
			"AF",
			"ZDO",
			"SAPI",
			"UTIL",
			"DEBUG",
			"APP",
			"ZOAD" //bit 9
	};
	
	int cap ;
	
	public Capabilities(int cap)
	{
		this.cap = cap ;
	}
	
	public int getCapVal()
	{
		return this.cap ;
	}
	
	public boolean isCapSYS()
	{
		return (this.cap & MT_CAP_SYS) >0 ;
	}
	
	public boolean isCapMAC()
	{
		return (this.cap & MT_CAP_MAC) >0 ;
	}
	
	public boolean isCapNWK()
	{
		return (this.cap & MT_CAP_NWK) >0 ;
	}
	
	public boolean isCapAF()
	{
		return (this.cap & MT_CAP_AF) >0 ;
	}
	
	public boolean isCapZDO()
	{
		return (this.cap & MT_CAP_ZDO) >0 ;
	}
	
	public boolean isCapSAPI()
	{
		return (this.cap & MT_CAP_SAPI) >0 ;
	}
	
	public boolean isCapUTIL()
	{
		return (this.cap & MT_CAP_UTIL) >0 ;
	}
	
	public boolean isCapDEBUG()
	{
		return (this.cap & MT_CAP_DEBUG) >0 ;
	}
	
	public boolean isCapAPP()
	{
		return (this.cap & MT_CAP_APP) >0 ;
	}
	
	public boolean isCapZOAD()
	{
		return (this.cap & MT_CAP_ZOAD) >0 ;
	}
	
	public ArrayList<String> getOptionsList()
	{
		ArrayList<String> rets = new ArrayList<>() ;
		for(int i = 0 ; i < OPTS.length ; i ++)
		{
			int v = 1<<i ;
			if((this.cap & v)>0)
				rets.add(OPTS[i]) ;
		}
		return rets ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().put("cap_val", this.getCapVal()).put("caps", this.getOptionsList()) ;
	}
	
	public String toString()
	{
		return Convert.combineWith(this.getOptionsList(), ',');
	}
}
