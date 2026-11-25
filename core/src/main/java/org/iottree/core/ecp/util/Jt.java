package org.iottree.core.ecp.util;

import org.json.JSONObject;

public abstract class Jt
{
	public static final String JT_TP_IO = "io" ;
	public static final String JT_TP_CONN = "c" ;
	
	
	protected String jt_id ; // id for joint or others
	
	public Jt(String jt_id)
	{
		this.jt_id = jt_id ;
	}
	
	public String getJtId()
	{
		return this.jt_id ;
	}

	//public abstract Point getXY();
	
//	public Jt(JSONObject jo)
//	{
//		this.x = jo.optFloat("x",0) ;
//		this.y = jo.optFloat("y",0) ;
//		this.id = jo.optString("id") ;
//	}
	
//	public JSONObject toJO()
//	{
//		return new JSONObject().put("x", this.x).put("y", this.y).putOpt("jt_id", this.jt_id);
//	}
}
