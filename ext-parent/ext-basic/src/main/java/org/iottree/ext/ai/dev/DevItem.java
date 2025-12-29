package org.iottree.ext.ai.dev;

import java.util.LinkedHashMap;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class DevItem
{
	
	String id  ;
	
	String topic ;
	
	String title ;
	
	String desc ;
	
	//LinkedHashMap<String,Argument> name2args = new LinkedHashMap<>() ;

	public String getId()
	{
		return id ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	public String getShowTitle()
	{
		if(Convert.isNotNullEmpty(title))
			return title ;
		return title ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", id) ;
		//jo.put("n", this.name) ;
		jo.put("topic", this.topic) ;
		jo.putOpt("t", this.title) ;
		jo.putOpt("d", this.desc) ;
		return jo ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.id = jo.getString("id") ;
		//this.name = jo.getString("n") ;
		this.topic = jo.getString("topic") ;
		this.title = jo.optString("t","") ;
		this.desc = jo.optString("d","") ;
		return true ;
	}
}
