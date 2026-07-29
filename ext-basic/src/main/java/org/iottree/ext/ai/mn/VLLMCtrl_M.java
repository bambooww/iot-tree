package org.iottree.ext.ai.mn;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class VLLMCtrl_M extends MNModule
{
	public static class DevItem
	{
		String id  ;
		
		String topic ;
		
		String title ;
		
		String desc ;

		
		public String getShowTitle()
		{
			if(Convert.isNotNullEmpty(title))
				return title ;
			return title ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("id", id) ;
			//jo.put("n", this.name) ;
			jo.put("topic", this.topic) ;
			jo.putOpt("t", this.title) ;
			jo.putOpt("d", this.desc) ;
			return jo ;
		}
		
		public boolean fromJO(JSONObject jo)
		{
			this.id = jo.getString("id") ;
			//this.name = jo.getString("n") ;
			this.topic = jo.getString("topic") ;
			this.title = jo.optString("t","") ;
			this.desc = jo.optString("d","") ;
			return true ;
		}
	}
	
	//ChatModel chatModel = null;
	
	String vllmHost = "localhost" ;
	
	int vllmPort = 8000;
	
	String modelName = null;//"qwen3:4b";

	@Override
	public String getTP()
	{
		return "vllm_ctrl";
	}

	@Override
	public String getTPTitle()
	{
		return g("vllm_ctrl");
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
		return "PK_flw";
	}
	
	public String getVLLMHost()
	{
		if(this.vllmHost==null)
			return "" ;
		return this.vllmHost ;
	}
	
	public int getVLLMPort()
	{
		return this.vllmPort ;
	}
	
	public String getModelName()
	{
		if(this.modelName==null)
			return "" ;
		return this.modelName ;
	}
	
	public String getVLLMUrlBase()
	{
		if(Convert.isNullOrEmpty(this.vllmHost))
			return "" ;
		return "http://"+this.vllmHost+":"+this.vllmPort;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.vllmHost) || this.vllmPort<=0)
		{
			failedr.append("no valid vllm host:port set") ;
			return false ;
		}
		if(Convert.isNullOrEmpty(modelName))
		{
			failedr.append("no model name set") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("vllm_host", this.vllmHost) ;
		jo.put("vllm_port",this.vllmPort) ;
		jo.putOpt("model_name",this.modelName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.vllmHost = jo.optString("vllm_host","localhost") ;
		this.vllmPort = jo.optInt("vllm_port", 8000) ;
		this.modelName = jo.optString("model_name") ;
	}
	
	@Override
	public String getPmTitle()
	{
		String tt = "" ;
		if(Convert.isNotNullEmpty(this.vllmHost))
			tt += this.vllmHost+":"+this.vllmPort;
		if(Convert.isNotNullEmpty(this.modelName))
			tt += " ["+this.modelName+"]" ;
		return tt ;
	}
	
	public static JSONObject RT_readModelList(String host,int port) throws IOException
	{
		String uuu = "http://" + host + ":" + port+"/v1/models"; //this.getUrlConfig();
		if (Convert.isNullOrEmpty(uuu))
			return null;
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpGet git = new HttpGet(uuu);
		// String result = "";
		try (CloseableHttpClient chc = httpClientBuilder.build())
		{
			HttpResponse resp = chc.execute(git);
			InputStream respIs = resp.getEntity().getContent();
			byte[] bs = IOUtils.toByteArray(respIs);

			// result = new String(rbs, this.getEncod());
			// return result;
			String jstr = new String(bs, "UTF-8");
			return new JSONObject(jstr);
		}
		// Convert.
	}
}
