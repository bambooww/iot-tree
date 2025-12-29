package org.iottree.ext.ai;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class LLMModelVLLM extends LLMModel
{
	String id ;
	
	String root ;
	
	long max_model_len ;
	
	public String getId()
	{
		return id ;
	}
	
	@Override
	public String getName()
	{
		return id;
	}

	@Override
	public String getModel()
	{
		return id;
	}
	
	public String getRoot()
	{
		return this.root ;
	}

	@Override
	public long getSize()
	{
		return max_model_len;
	}

	public static LLMModelVLLM fromJO(JSONObject jo)
    {
//    	String n = jo.optString("name") ;
//    	if(Convert.isNullOrEmpty(n))
//    		return null ;
    	LLMModelVLLM ret = new LLMModelVLLM() ;
    	ret.id = jo.optString("id") ;
    	if(Convert.isNullOrEmpty(ret.id) || !"model".equals(jo.optString("object")))
    		return null ;
        
        //"created": 1766570822,
        //"owned_by": "vllm",
        ret.root = jo.optString("root") ; 
        //"parent": null,
        ret.max_model_len = jo.optLong("max_model_len",-1) ;
        
    	return ret ;
    }
}
