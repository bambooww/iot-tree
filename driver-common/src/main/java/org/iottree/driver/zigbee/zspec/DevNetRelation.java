package org.iottree.driver.zigbee.zspec;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class DevNetRelation
{
	String ieee_addr ;
	
	int short_addr ;
	
	int parent_shot_addr ;
	
	public DevNetRelation()
	{}
	
	public DevNetRelation(String ieee,int addr,int p_addr)
	{
		this.ieee_addr = ieee ;
		this.short_addr = addr ;
		this.parent_shot_addr = p_addr ;
	}
	
	public String getIEEEAddr()
	{
		return this.ieee_addr ;
	}
	
	public int getShortAddr()
	{
		return this.short_addr ;
	}
	
	public int getShortAddrParent()
	{
		return this.parent_shot_addr ;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj==null||!(obj instanceof DevNetRelation))
			return false;
		DevNetRelation nr = (DevNetRelation)obj ;
		if(!this.ieee_addr.equals(nr.ieee_addr))
			return false;
		if(this.short_addr!=nr.short_addr)
			return false;
		if(this.parent_shot_addr!=nr.parent_shot_addr)
			return false;
		return true ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().putOpt("ieee_addr",this.ieee_addr).put("short_addr", this.short_addr)
				.put("parent_short_addr", this.parent_shot_addr) ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.ieee_addr = jo.optString("ieee_addr") ;
		if(Convert.isNullOrEmpty(this.ieee_addr))
			return false;
		this.short_addr = jo.optInt("short_addr",-1) ;
		if(this.short_addr<0)
			return false;
		this.parent_shot_addr = jo.optInt("parent_short_addr",0) ;
		return true ;
	}
}
