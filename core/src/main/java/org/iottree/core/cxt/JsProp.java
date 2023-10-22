package org.iottree.core.cxt;

import org.json.JSONObject;

public class JsProp
{
	String name = null ;
	
	Class<?> valTp = null ;
	
	String title = null ;
	
	String desc = null ;
	
	public JsProp(String name,Class<?> valtp,String title,String desc)
	{
		this.name = name ;
		this.valTp = valtp;
		this.title = title ;
		this.desc = desc ;
	}

	public String getName()
	{
		return name;
	}

	public Class<?> getValTp()
	{
		return this.valTp ;
	}

	public String getTitle()
	{
		return title;
	}


	public String getDesc()
	{
		return desc;
	}

	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("n", name) ;
		jo.put("tp", this.valTp.getCanonicalName()) ;
		jo.putOpt("t", title) ;
		jo.putOpt("d", desc) ;
		return jo ;
	}
}
