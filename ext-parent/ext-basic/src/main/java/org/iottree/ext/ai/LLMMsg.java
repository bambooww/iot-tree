package org.iottree.ext.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import org.iottree.core.util.*;

public class LLMMsg
{
	public static enum Role
	{
		system,
		user,
		assistant
	}
	
	public static class ToolCallFunc
	{
		public String id ;
		
		public String name ;
		
		public int index = 0 ; 
		
		//public HashMap<String,String> arguments = new HashMap<>() ;
		public JSONObject arguments = null ;
		
		public static ToolCallFunc fromJO(JSONObject jo)
		{
			JSONObject func_jo = jo.optJSONObject("function") ;
			if(func_jo==null)
				return null ;
			
			ToolCallFunc tcf = new ToolCallFunc() ;
			tcf.id = jo.optString("id") ;
			tcf.name =func_jo.optString("name") ;
			if(Convert.isNullOrEmpty(tcf.name))
				return null ;
			tcf.index = func_jo.optInt("index",0) ;
			tcf.arguments = func_jo.optJSONObject("arguments") ;
//			JSONObject arg_jo = func_jo.optJSONObject("arguments") ;
//			if(arg_jo!=null)
//			{
//				for(String n : arg_jo.keySet())
//				{
//					Object v = arg_jo.get(n) ;
//					if(v==null)
//						continue ;
//					tcf.arguments.put(n,v.toString()) ;
//				}
//			}
			return tcf ;
		}
	}
	
	Role role = Role.user ;
	
	String content ;
	
	// List<ToolCall>
	
	String thinking ;
	
	ArrayList<ToolCallFunc> toolCallFuncs = null;
	
	public LLMMsg()
	{}
	
	public LLMMsg(Role r,String content)
	{
		if(r==null)
			throw new IllegalArgumentException("role cannot null") ;
		
		this.role = r ;
		this.content = content ;
	}
	
	public Role getRole()
	{
		return this.role ;
	}
	
	public String getContent()
	{
		return this.content ;
	}
	
	public List<ToolCallFunc> getToolCallFunctions()
	{
		return this.toolCallFuncs;
	}
	
	public JSONObject toJO()
	{
		JSONObject retjo = new JSONObject() ;
		retjo.put("role",role.name()) ;
		retjo.putOpt("content",content) ;
		retjo.putOpt("thinking",this.thinking) ;
		return retjo ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.role = Role.valueOf(jo.optString("role","user")) ;
		this.content = jo.optString("content") ;
		this.thinking = jo.optString("thinking") ;
		
		
		if(jo.has("tool_calls"))
		{//check ollama tool_calls
			JSONArray jarr = jo.getJSONArray("tool_calls") ;
			int n = jarr.length() ;
			this.toolCallFuncs = new ArrayList<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				ToolCallFunc tcf = ToolCallFunc.fromJO(tmpjo) ;
				if(tcf==null)
					continue ;
				this.toolCallFuncs.add(tcf) ;
			}
		}
		return true ;
	}
	
	public static LLMMsg transFromJO(JSONObject jo)
	{
		LLMMsg ret = new LLMMsg() ;
		if(ret.fromJO(jo))
			return ret;
		else
			return null ;
	}
	
	public static List<LLMMsg> transFromJArr(JSONArray jarr)
	{
		if(jarr==null)
			return null ;
		int n = jarr.length() ;
		ArrayList<LLMMsg> rets = new ArrayList<>() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			LLMMsg mm = transFromJO(jo) ;
			if(mm==null)
				continue ;
			rets.add(mm) ;
		}
		return rets ;
	}
}
