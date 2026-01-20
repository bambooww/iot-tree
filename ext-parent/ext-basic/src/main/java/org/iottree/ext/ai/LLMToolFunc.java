package org.iottree.ext.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import org.iottree.core.msgnet.MNNodeResCaller;
import org.iottree.core.msgnet.ResCaller;
import org.iottree.core.util.*;

public class LLMToolFunc extends LLMTool
{
	public static class Param
	{
		public String name ;
		
		public String tp ; //
		
		public String desc ;
		
		public boolean required = false;
		
		private JSONObject toTpDescJO()
		{
			JSONObject jo = new JSONObject().putOpt("type",tp).putOpt("description",desc) ;
			//jo.put("enum", value)
			return jo ;
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
	
	HashMap<String,Param> paramsMap = null;//new HashMap<>() ;
	
	JSONObject paramJsonSchema = null ;
	
	public LLMToolFunc(String name,String desc,List<Param> params)
	{
		this.name = name ;
		this.desc = desc ;
		for(Param pm:params)
			paramsMap.put(pm.name,pm) ;
	}
	
	public LLMToolFunc(String name,String desc,JSONObject param_jsonschema)
	{
		this.name = name ;
		this.desc = desc ;
		this.paramJsonSchema = param_jsonschema;
	}
	
	public static LLMToolFunc parseFromResCaller(ResCaller rc)
	{
		if(rc==null)
			return null ;

		return new LLMToolFunc(rc.getCallerName(),rc.getCallerTitle(),rc.getParamJsonSchema()) ;
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
		if(this.paramsMap!=null)
		{
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
		}
		
		if(this.paramJsonSchema!=null)
		{
			JSONObject pm_jo = new JSONObject(this.paramJsonSchema.toString()) ;
			pm_jo.remove("$schema") ;
			ret.put("parameters", pm_jo) ;
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
