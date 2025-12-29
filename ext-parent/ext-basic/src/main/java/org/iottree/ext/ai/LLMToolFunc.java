package org.iottree.ext.ai;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import org.iottree.core.util.*;

public class LLMToolFunc extends LLMTool
{
	public static class Param
	{
		public String name ;
		
		public String tp ;
		
		public String desc ;
		
		public boolean required = false;
		
		private JSONObject toTpDescJO()
		{
			return new JSONObject().putOpt("type",tp).putOpt("description",desc) ;
		}
		
		public JSONObject toJO()
		{
			return new JSONObject().putOpt("tp",tp).putOpt("desc",desc) 
					.putOpt("name", name).put("required",required) ;
		}
		
		public static Param formJO(JSONObject jo)
		{
			String n = jo.optString("name") ;
			if(Convert.isNullOrEmpty(n))
				return null ;
			String tp = jo.optString("tp") ;
			String desc = jo.optString("desc") ;
			return new Param(n,tp,desc,jo.optBoolean("required",false));
		}
		
		private Param(String name,String tp,String desc,boolean required)
		{
			this.name =name ;
			this.tp = tp ;
			this.desc = desc ;
			this.required = required ;
		}
	}
	
	String name ; //func name
	
	String desc ;
	
	HashMap<String,Param> paramsMap = new HashMap<>() ;
	
	public LLMToolFunc(String name,String desc,List<Param> params)
	{
		this.name = name ;
		this.desc = desc ;
		for(Param pm:params)
			paramsMap.put(pm.name,pm) ;
	}
	
	@Override
	public String getTP()
	{
		return "function";
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getDescription()
	{
		return this.desc ;
	}
	
	public HashMap<String,Param> getParamMap()
	{
		return this.paramsMap ;
	}
	
	public boolean isValid(StringBuilder failedr)
	{
		
		return true ;
	}
	/**
	 * 
	 * @param name function name
	 * @param desc You must clarify under what circumstances this function is called
	 * @return
	 */
	public LLMToolFunc setBasic(String name,String desc)
	{
		this.name =name ;
		this.desc = desc ;
		return this ;
	}
	
	public LLMToolFunc setParam(String name,String tp,String desc,boolean required)
	{
		Param pm = new Param(name,tp,desc,required);
		this.paramsMap.put(pm.name,pm) ;
		return this ;
	}
	
	private JSONObject toFuncJO()
	{
		JSONObject ret = new JSONObject() ;
		ret.put("name", this.name) ;
		ret.put("description", this.desc) ;
		JSONObject pm_jo = new JSONObject() ;
		ret.put("parameters", pm_jo) ;
		
		pm_jo.put("type", "object") ;
		JSONObject ps_jo = new JSONObject() ;
		JSONArray req_jarr = new JSONArray() ;
		pm_jo.put("properties", ps_jo) ;
		pm_jo.put("required", req_jarr) ;
		
		for(Param pm:this.paramsMap.values())
		{
			ps_jo.put(pm.name, pm.toTpDescJO()) ;
		}
		return ret ;
	}

	public JSONObject toJO()
	{
		JSONObject ret = super.toJO() ;
		ret.put("function",toFuncJO()) ;
		return ret ;
	}
	

	 
	
}
