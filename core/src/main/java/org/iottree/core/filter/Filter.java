package org.iottree.core.filter;

import org.json.JSONObject;

public abstract class Filter<T>
{
	public abstract boolean accept(T ob) ;
	
	public abstract String getTP() ;
	
	public JSONObject toDefJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("_tp", this.getTP()) ;
		return jo ;
	}
	
	public boolean fromDefJO(JSONObject jo)
	{
		String tp = jo.getString("_tp") ;
		
		return true ;
	}
}
