package org.iottree.core.basic;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class NameTitle
{
	String name ;
	
	String title ;
	
	public NameTitle(String n,String t)
	{
		this.name = n ;
		this.title = t ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("n",this.name) ;
		jo.putOpt("t", this.title) ;
		return jo ;
	}
	
	public static NameTitle fromJO(JSONObject jo)
	{
		String n = jo.optString("n") ;
		if(Convert.isNullOrEmpty(n))
			return null ;
		String t =jo.optString("t") ;
		return new NameTitle(n,t) ;
	}
}
