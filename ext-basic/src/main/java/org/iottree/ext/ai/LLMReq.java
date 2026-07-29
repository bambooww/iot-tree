package org.iottree.ext.ai;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class LLMReq
{
	String model ;
	
	ArrayList<LLMMsg> messages = new ArrayList<>() ;
	
	Float temperature = null;
	
	Long max_tokens = null ;
	
	ArrayList<LLMTool> tools = null ;
	
	/**
	 * 
	 */
	JSONObject openai_guided_json = null ;
	
	JSONObject structured_output_json_schema = null ;
	
	public LLMReq asModel(String model)
	{
		this.model = model;
		return this;
	}
	
	public LLMReq asTemperature(Float temp)
	{
		this.temperature = temp;
		return this;
	}
	
	public LLMReq asMaxTokens(Long max_tk)
	{
		this.max_tokens = max_tk;
		return this;
	}

	/**
	 * openai api JSON Schema
	 * @param jo
	 * @return
	 */
	public LLMReq asOpenAIGuidedJson(JSONObject jo)
	{
		this.openai_guided_json = jo ;
		return this ;
	}
	
	public LLMReq asStructuredOutputJsonSchema(JSONObject jo)
	{
		this.structured_output_json_schema = jo ;
		return this ;
	}
	
	public List<LLMMsg> getMessages()
	{
		return this.messages ;
	}
	
	public LLMReq addMessage(LLMMsg msg)
	{
		this.messages.add(msg) ;
		return this ;
	}
	
	public LLMReq addMessages(List<LLMMsg> msgs)
	{
		this.messages.addAll(msgs) ;
		return this ;
	}
	
	public LLMReq addMessage(LLMMsg.Role role,String content)
	{
		this.messages.add(new LLMMsg(role,content)) ;
		return this ;
	}
	
	public LLMReq addMessageSystem(String content)
	{
		this.messages.add(new LLMMsg(LLMMsg.Role.system,content)) ;
		return this ;
	}

	public LLMReq addMessageUser(String content)
	{
		this.messages.add(new LLMMsg(LLMMsg.Role.user,content)) ;
		return this ;
	}
	
	public LLMReq addMessageAssistsnt(String content)
	{
		this.messages.add(new LLMMsg(LLMMsg.Role.assistant,content)) ;
		return this ;
	}
	
	public LLMReq addTool(LLMTool tool)
	{
		if(tools==null)
			tools = new ArrayList<>() ;
		
		tools.add(tool) ;
		return this ;
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject() ;
		ret.putOpt("model", model) ;
		
		JSONArray jarr = new JSONArray() ;
		if(messages!=null)
		{
			for(LLMMsg m:messages)
			{
				jarr.put(m.toJO()) ;
			}
		}
		ret.put("messages", jarr);
		ret.put("stream", false) ;
		ret.putOpt("temperature",this.temperature) ;
		ret.putOpt("max_tokens", this.max_tokens) ;
		
		if(tools!=null&&tools.size()>0)
		{
			JSONArray tool_jarr = new JSONArray() ;
			ret.put("tools", tool_jarr) ;
			for(LLMTool t:tools)
				tool_jarr.put(t.toJO()) ;
			ret.put("tool_choice", "auto") ;
		}
		
		if(openai_guided_json!=null)
		{
			JSONObject extra_body = new JSONObject() ;
			extra_body.put("guided_json",this.openai_guided_json) ;
			ret.put("extra_body",extra_body) ;
		}
		
//		if(structured_output_json_schema!=null)
//		{
//			JSONObject extra_body = new JSONObject() ;
//			ret.put("extra_body",extra_body) ;
//			
//			JSONObject outputs_config = new JSONObject() ;
//			extra_body.put("structured_outputs",outputs_config);
//			
//			outputs_config.put("type","json") ;
//			JSONObject json_jo = new JSONObject() ;
//			outputs_config.put("json",json_jo) ;
//
//			json_jo.put("strict",true) ;
//			json_jo.put("response_format","json_schema") ;
//			json_jo.put("schema", this.structured_output_json_schema) ;
//		}
		
		if(structured_output_json_schema!=null)
		{//vLLM test ok
			JSONObject response_format = new JSONObject() ;
			ret.put("response_format",response_format) ;
			
			response_format.put("type","json_schema") ;
			JSONObject json_sch = new JSONObject() ;
			response_format.put("json_schema",json_sch) ;
			json_sch.put("name", "n1") ;
			json_sch.put("schema",this.structured_output_json_schema) ;
		}
		return ret ;
	}
	
	public static LLMReq fromJO(JSONObject jo)
	{
		LLMReq req = new LLMReq() ;
		
		//ret.
		
		JSONArray jarr = jo.optJSONArray("messages") ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				LLMMsg msg = new LLMMsg();
				if(msg.fromJO(tmpjo))
					req.messages.add(msg) ;
			}
		}
		
		return req;
	}
}
