package org.iottree.ext.ai;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class LLMRespVLLM extends LLMResp
{
	public static class Usage
	{
		public int completion_tokens ;
		public int prompt_tokens ;
		public String prompt_tokens_details ;
		public int total_tokens ;
	}
	
	public static class Choice
	{
		public int index = 0 ;
		public String finish_reason ;
		public String stop_reason ;
		public LLMMsg message ;
	}
	
	String id ;
	
	Usage usage ;
	
	ArrayList<Choice> choices ;
	
	public String getId()
	{
		return this.id ;
	}
	
	@Override
	public LLMMsg getMessage()
	{
		if(this.choices==null||this.choices.size()<=0)
			return null ;
		return this.choices.get(0).message;
	}

	public JSONObject toJO()
	{
		JSONObject ret = super.toJO() ;
//		if(message!=null)
//		{
//			ret.put("message", message.toJO());
//		}
//		
//		ret.put("done", done) ;
		return ret ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		super.fromJO(jo) ;
		this.id = jo.optString("id") ;
		
		this.usage = parseUsage(jo.optJSONObject("usage")) ;
		JSONArray jarr = jo.optJSONArray("choices") ;
		choices = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				Choice ch = parseChoice(tmpjo) ;
				if(ch==null)
					continue ;
				choices.add(ch) ;
			}
		}
		return true;
	}
	
	static Usage parseUsage(JSONObject jo)
	{
		if(jo==null)
			return null ;
		Usage ret = new Usage() ;
		ret.completion_tokens = jo.optInt("completion_tokens",-1) ;
		ret.prompt_tokens = jo.optInt("prompt_tokens",-1) ;
		ret.total_tokens = jo.optInt("total_tokens",-1) ;
		ret.prompt_tokens_details = jo.optString("prompt_tokens_details") ;
		return ret ;
	}
	
	static Choice parseChoice(JSONObject jo)
	{
		Choice ch = new Choice() ;
		ch.index = jo.optInt("index",0) ;
		ch.finish_reason = jo.optString("finish_reason") ;
		ch.stop_reason = jo.optString("stop_reason") ;
		JSONObject msg_jo = jo.optJSONObject("message") ;
		ch.message = LLMMsg.transFromJO(msg_jo) ;
		
		return ch ;
	}
}
