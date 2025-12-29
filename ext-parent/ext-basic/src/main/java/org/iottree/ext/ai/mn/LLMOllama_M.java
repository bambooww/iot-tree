package org.iottree.ext.ai.mn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.msgnet.MNModule;
import org.iottree.ext.ai.LLMModel;
import org.iottree.ext.ai.LLMModelOllama;
import org.iottree.ext.ai.LLMModelVLLM;
import org.iottree.ext.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import dev.langchain4j.model.chat.ChatModel;
import org.iottree.core.util.*;

public class LLMOllama_M extends MNModule
{
	//ChatModel chatModel = null;

	//ChatModel chatModel = null;
	
	String ollamaHost = "localhost" ;
	
	int ollamaPort = 11434;
	
	@Override
	public String getTP()
	{
		return "llm_ollama";
	}

	@Override
	public String getTPTitle()
	{
		return "LLM Ollama";
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
		return "PK_ollama";
	}
	
	public String getOllamaHost()
	{
		if(this.ollamaHost==null)
			return "" ;
		return this.ollamaHost ;
	}
	
	public int getOllamaPort()
	{
		return this.ollamaPort ;
	}
	
	public String getOllamaUrl()
	{
		if(Convert.isNullOrEmpty(this.ollamaHost))
		{
			//RT_DEBUG_ERR.fire("model", "model is not ready");
			return null;
		}
		return "http://"+this.ollamaHost+":"+this.ollamaPort ;
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.ollamaHost) || this.ollamaPort<=0)
		{
			failedr.append("no valid ollama host:port set") ;
			return false ;
		}
		
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("ollama_host", this.ollamaHost) ;
		jo.put("ollama_port",this.ollamaPort) ;
		//jo.putOpt("model_name",this.modelName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.ollamaHost = jo.optString("ollama_host") ;
		this.ollamaPort = jo.optInt("ollama_port", 11434) ;
		//this.modelName = jo.optString("model_name") ;
	}
	
	@Override
	public String getPmTitle()
	{
		if(Convert.isNullOrEmpty(this.ollamaHost))
			return "" ;
		return this.ollamaHost+":"+this.ollamaPort;
	}
	

	private List<LLMModel> modelItems = null ;
	
	public List<LLMModel> listModelItems(boolean brefresh) throws IOException
	{
		if(!brefresh && modelItems!=null)
			return this.modelItems ;
		String url = getOllamaUrl() +"/api/tags";
		if(Convert.isNullOrEmpty(url))
			return null ;
		String txtres = UrlUtil.doGetToStr(url, "UTF-8") ;
		if(Convert.isNullOrEmpty(txtres))
			return null ;
		JSONObject jo = new JSONObject(txtres) ;
		JSONArray jarr = jo.optJSONArray("models") ;
		ArrayList<LLMModel> mds = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				//LLMModel m = JsonUtil.fromJson(tmpjo.toString(),LLMModel.class) ;
				LLMModelOllama m = LLMModelOllama.fromJO(tmpjo);//JsonUtil.fromJson(tmpjo.toString(),LLMModel.class) ;
				if(m==null)
					continue ;
				mds.add(m) ;
			}
		}
		return this.modelItems = mds ;
	}
}
