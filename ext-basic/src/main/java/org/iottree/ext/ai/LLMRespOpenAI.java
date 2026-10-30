package org.iottree.ext.ai;

import org.json.JSONArray;
import org.json.JSONObject;

public class LLMRespOpenAI extends LLMResp
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
		
		JSONArray ch_jarr  = jo.optJSONArray("choices") ;
		if(ch_jarr!=null && ch_jarr.length()>0)
		{
			JSONObject jo_ch = ch_jarr.getJSONObject(0) ;
			JSONObject msg_jo = jo_ch.optJSONObject("message") ;
			if(msg_jo!=null)
			{
				this.message = LLMMsg.transFromJO(msg_jo) ;
			}
		}
		return true;
	}
}
