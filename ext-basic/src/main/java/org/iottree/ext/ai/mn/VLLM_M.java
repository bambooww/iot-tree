package org.iottree.ext.ai.mn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.msgnet.MNModule;
import org.iottree.core.util.Convert;
import org.iottree.core.util.UrlUtil;
import org.iottree.ext.ai.LLMModel;
import org.iottree.ext.ai.LLMModelVLLM;
import org.iottree.ext.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class VLLM_M extends MNModule
{
	//ChatModel chatModel = null;

	//ChatModel chatModel = null;
	
	String vllmHost = "localhost" ;
	
	int vllmPort = 8000;
	
	@Override
	public String getTP()
	{
		return "llm_vllm";
	}

	@Override
	public String getTPTitle()
	{
		return "vLLM";
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
		return "PK_vllm";
	}
	
	public String getHost()
	{
		if(this.vllmHost==null)
			return "" ;
		return this.vllmHost ;
	}
	
	public int getPort()
	{
		return this.vllmPort ;
	}
	
	public String getUrl()
	{
		if(Convert.isNullOrEmpty(this.vllmHost))
		{
			//RT_DEBUG_ERR.fire("model", "model is not ready");
			return null;
		}
		return "http://"+this.vllmHost+":"+this.vllmPort ;
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.vllmHost) || this.vllmPort<=0)
		{
			failedr.append("no valid host:port set") ;
			return false ;
		}
		
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("host", this.vllmHost) ;
		jo.put("port",this.vllmPort) ;
		//jo.putOpt("model_name",this.modelName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.vllmHost = jo.optString("host") ;
		this.vllmPort = jo.optInt("port", 8000) ;
		//this.modelName = jo.optString("model_name") ;
	}
	
	@Override
	public String getPmTitle()
	{
		if(Convert.isNullOrEmpty(this.vllmHost))
			return "" ;
		return this.vllmHost+":"+this.vllmPort;
	}
	
private List<LLMModel> modelItems = null ;
	
	public List<LLMModel> listModelItems(boolean brefresh) throws IOException
	{
		if(!brefresh && modelItems!=null)
			return this.modelItems ;
		
		String url = getUrl() +"/v1/models";
		if(Convert.isNullOrEmpty(url))
			return null ;
		String txtres = UrlUtil.doGetToStr(url, "UTF-8") ;
		if(Convert.isNullOrEmpty(txtres))
			return null ;
		JSONObject jo = new JSONObject(txtres) ;
		if(!"list".equals(jo.optString("object")))
			return null ;
		JSONArray jarr = jo.optJSONArray("data") ;
		ArrayList<LLMModel> mds = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				LLMModelVLLM m = LLMModelVLLM.fromJO(tmpjo);//JsonUtil.fromJson(tmpjo.toString(),LLMModel.class) ;
				if(m==null)
					continue ;
				mds.add(m) ;
			}
		}
		return this.modelItems = mds ;
	}
	

}
