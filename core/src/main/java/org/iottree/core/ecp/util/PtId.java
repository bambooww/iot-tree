package org.iottree.core.ecp.util;

import org.json.JSONObject;

public class PtId
{
	public float x ;
	
	public float y ;
	
	public String id ; // id for joint or others
	
	public PtId(float x,float y,String id)
	{
		this.x = x ;
		this.y = y ;
		this.id = id ;
	}
	
	public PtId(float x,float y)
	{
		this(x,y,null);
	}
	
	public PtId(JSONObject jo)
	{
		this.x = jo.optFloat("x",0) ;
		this.y = jo.optFloat("y",0) ;
		this.id = jo.optString("id") ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().put("x", this.x).put("y", this.y).putOpt("id", this.id==null?"":this.id);
	}
	
	public static PtId parseFrom(JSONObject jo)
	{
		if(jo==null)
			return null ;
		if(!jo.has("x")) return null ;
		if(!jo.has("y")) return null ;
		return new PtId(jo) ;
	}
}
