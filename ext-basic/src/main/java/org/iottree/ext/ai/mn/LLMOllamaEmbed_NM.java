package org.iottree.ext.ai.mn;

import java.time.Duration;
import java.util.List;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.ext.ai.LLMMsg;
import org.iottree.ext.ai.LLMReq;
import org.iottree.ext.ai.LLMRespOllama;
import org.iottree.ext.ai.LLMMsg.Role;
import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LLMOllamaEmbed_NM extends MNNodeMid
{
	String modelName = null;//"qwen3:4b";
	
	long connTO = 3000 ; //ms
	long readTO = 180000 ; //ms
	
	
	@Override
	public String getTP()
	{
		return "llm_ollama_embed";
	}

	@Override
	public String getTPTitle()
	{
		return "Ollama Embed";
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
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	public String getModelName()
	{
		if(this.modelName==null)
			return "" ;
		return this.modelName ;
	}
	

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
//		if(Convert.isNullOrEmpty(this.ollamaHost) || this.ollamaPort<=0)
//		{
//			failedr.append("no valid ollama host:port set") ;
//			return false ;
//		}
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
		jo.putOpt("model_name",this.modelName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.modelName = jo.optString("model_name") ;
		
		synchronized(this)
		{
			httpClient = null ;
		}
	}
	
	@Override
	public String getPmTitle()
	{
		return this.modelName ;
	}
	
	private transient OkHttpClient httpClient = null ; 
			
	private synchronized OkHttpClient getHttpClient()
	{
		if(httpClient!=null)
			return httpClient ;
		
		return httpClient = new OkHttpClient.Builder()
				//.callTimeout(Duration.ofMinutes(3))
				.connectTimeout(Duration.ofSeconds(connTO))
				.readTimeout(Duration.ofMillis(readTO))
				.build() ;
	}
	
//	
//	
//	@Override
//	protected void RT_onAfterNetStop()
//	{
//		
//	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		if(Convert.isNullOrEmpty(this.modelName))
			return null ;
		
		LLMOllama_M owner = (LLMOllama_M)this.getOwnRelatedModule() ;
		
		Object pld = msg.getPayload() ;
		if(pld==null||"".equals(pld))
			return null;
		
		
		JSONArray inputs = null;
		if(pld instanceof String)
		{
			String pldstr = (String)pld ;
			inputs = new JSONArray() ;
			inputs.put(pldstr) ;
		}
		else if(pld instanceof JSONArray)
		{
			inputs = (JSONArray)pld ;
		}
		else
		{
			return null ;
		}
		
		JSONObject req_jo = new JSONObject() ;
		req_jo.put("model",this.modelName) ;
		
		//inputs.put("hello") ;
		req_jo.put("input", inputs) ;

		String url = owner.getOllamaUrl() + "/v1/embeddings";
		
		RequestBody req_body = RequestBody.create(
				req_jo.toString()
				,MediaType.get("application/json; charset=utf-8")) ;
		
		Request request = new Request.Builder()
				.url(url).post(req_body)
				.build();
		try (Response response = this.getHttpClient().newCall(request).execute())
		{
			if (!response.isSuccessful())
				throw new RuntimeException("Unexpected code " + response);
			String responseBody = response.body().string();
			JSONObject tmpjo = new JSONObject(responseBody) ;
			JSONArray data_jarr = tmpjo.optJSONArray("data") ;
			//System.out.println("resp ="+tmpjo.toString(2));
//			LLMRespOllama resp = new LLMRespOllama();
//			
			if(data_jarr!=null)
			{
				MNMsg m = new MNMsg().asPayloadJO(data_jarr) ;//resp.getMessage().getContent());
				return RTOut.createOutIdx().asIdxMsg(0, m);
			}
		}
		return null ;
	}
}
