package org.iottree.ext.ai;

import org.json.JSONObject;

import org.iottree.core.util.*;

public class LLMModelOllama extends LLMModel
{
	String name ;
	
    String model ;
	
    long size ;
	
    @Override
	public String getName()
	{
		return this.name ;
	}
	
    @Override
	public String getModel()
	{
		return this.model ;
	}
	
    @Override
	public long getSize()
	{
		return this.size ;
	}
    
    public static LLMModelOllama fromJO(JSONObject jo)
    {
    	String n = jo.optString("name") ;
    	if(Convert.isNullOrEmpty(n))
    		return null ;
    	LLMModelOllama ret = new LLMModelOllama() ;
    	ret.name = n ;
    	ret.model = jo.optString("model") ;
    	ret.size = jo.optLong("size",-1) ;
    	return ret ;
    }
}
