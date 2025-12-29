package org.iottree.ext.ai;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class LLMResp
{
	String model ;
	
	public String getModel()
	{
		return this.model ;
	}
	
	public abstract LLMMsg getMessage();

	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject() ;
		ret.putOpt("model", model) ;
		return ret ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.model = jo.optString("model") ;
		return true;
	}
	
}
