package org.iottree.core.ecp.util;

import org.json.JSONObject;

public class Point
{
	public float x ;
	
	public float y ;
	
	public Point(float x,float y)
	{
		this.x = x ;
		this.y = y ;
	}
	
	public Point(JSONObject jo)
	{
		this.x = jo.getFloat("x") ;
		this.y = jo.getFloat("y") ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().put("x",this.x).put("y", this.y) ;
	}
	
	public static Point parseFrom(JSONObject jo)
	{
		if(jo==null)
			return null ;
		if(!jo.has("x")) return null ;
		if(!jo.has("y")) return null ;
		return new Point(jo) ;
	}
}
