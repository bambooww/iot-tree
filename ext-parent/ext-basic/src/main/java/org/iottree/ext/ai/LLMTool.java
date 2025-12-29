package org.iottree.ext.ai;

import org.json.JSONObject;

public abstract class LLMTool
{
	public abstract String getTP() ;
	
	
	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject() ;
		ret.put("type", this.getTP()) ;
		return ret;
	}
}
