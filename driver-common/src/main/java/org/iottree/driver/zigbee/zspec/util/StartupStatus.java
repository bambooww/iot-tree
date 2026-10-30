package org.iottree.driver.zigbee.zspec.util;

import org.json.JSONObject;

public class StartupStatus
{
	private static String[] OPTS = new String[] {
			"Restored network state", // 0
			"New network state", //1
			"Leave and not Started" //2
	};
	
	private int st ;
	
	public StartupStatus(int v)
	{
		this.st = v ;
	}

	public int getStatus()
	{
		return this.st ;
	}
	
	public String getStatusT()
	{
		if(this.st>=0 && this.st<=2)
			return OPTS[this.st] ;
		return "" ;
	}
	
	public boolean isStatusOk()
	{
		return this.st==0 ||  this.st==1 ;
	}
	
	public JSONObject toJO()
	{
		JSONObject r = new JSONObject() ;
		r.put("status", this.getStatus()).put("status_ok", this.isStatusOk()).putOpt("status_t", this.getStatusT()) ;
		return r ;
	}
	
	public String toString()
	{
		return " Status="+getStatus()+":"+this.getStatusT() ;
	}
}
