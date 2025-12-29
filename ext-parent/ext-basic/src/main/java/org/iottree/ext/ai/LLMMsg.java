package org.iottree.ext.ai;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class LLMMsg
{
	public static enum Role
	{
		system,
		user,
		assistant
	}
	
	
	Role role = Role.user ;
	
	String content ;
	
	// List<ToolCall>
	
	String thinking ;
	
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
