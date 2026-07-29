package org.iottree.ext.ai.mn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.msgnet.MNModule;
import org.iottree.core.util.Convert;
import org.iottree.core.util.UrlUtil;
import org.iottree.ext.ai.LLMModel;
import org.iottree.ext.ai.LLMModelOllama;
import org.json.JSONArray;
import org.json.JSONObject;

public class LLMTooling_M extends MNModule
{
	//ChatModel chatModel = null;

	//ChatModel chatModel = null;
	
	
	@Override
	public String getTP()
	{
		return "llm_tooling";
	}

	@Override
	public String getTPTitle()
	{
		return "LLM Tooling";
	}

	@Override
	public String getColor()
	{
		return "#a349a4";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#eeeeee";
	}

	@Override
	public String getIcon()
	{
		return "\\uf076";
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
//		jo.putOpt("ollama_host", this.ollamaHost) ;
//		jo.put("ollama_port",this.ollamaPort) ;
		//jo.putOpt("model_name",this.modelName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
//		this.ollamaHost = jo.optString("ollama_host") ;
//		this.ollamaPort = jo.optInt("ollama_port", 11434) ;
		//this.modelName = jo.optString("model_name") ;
	}
	
}
