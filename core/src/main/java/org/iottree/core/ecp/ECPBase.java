package org.iottree.core.ecp;

import org.json.JSONObject;

public abstract class ECPBase
{
	//private final ECPNet net;
	
	private final String id ;
	
	private String title ;
	
	private String desc = null ;
	
	public ECPBase(String id)
	{
		this.id = id ;
	}
	
	
	
	public final String getId()
	{
		return id ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public final void setTitle(String t)
	{
		this.title = t; 
	}
	
	public String getDesc()
	{
		return this.desc ;
	}
	
	public final void setBasic(String t,String d)
	{
		this.title = t;
		this.desc = d ;
	}
	
	public abstract boolean removeMe() ;
	
	public JSONObject toJO()
	{
		return new JSONObject().put("id",this.id).put("title", this.title).putOpt("d", desc);
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.title = jo.optString("title") ;
		this.desc = jo.optString("desc") ;
		return true;
	}
}
