package org.iottree.ext.ai;

import org.json.JSONObject;

public class LLMRespOllama extends LLMResp
{
	String model ;
	
	LLMMsg message ;
	
	boolean done =true;
	
	public LLMMsg getMessage()
	{
		return this.message ;
	}

	public JSONObject toJO()
	{
		JSONObject ret = super.toJO() ;
		
		if(message!=null)
		{
			ret.put("message", message.toJO());
		}
		
		ret.put("done", done) ;
		return ret ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		super.fromJO(jo) ;
		
		JSONObject msg_jo = jo.optJSONObject("message") ;
		if(msg_jo!=null)
		{
			this.message = LLMMsg.transFromJO(msg_jo) ;
		}
		
		return true;
	}
}
