package org.iottree.ext.ai.mn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.iottree.core.ai.AIApiKey;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.util.Convert;
import org.iottree.core.util.UrlUtil;
import org.iottree.ext.ai.LLMModel;
import org.iottree.ext.ai.LLMModelOllama;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LLMOnline_M extends MNModule
{
	String apikeyName;
	
	String model ;

	@Override
	public String getTP()
	{
		return "llm_online";
	}

	@Override
	public String getTPTitle()
	{
		return "LLM Online";
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
		return "PK_openai";
	}

	public AIApiKey getApiKey()
	{
		if (Convert.isNullOrEmpty(this.apikeyName))
			return null;

		return AIApiKey.getApiKey(this.apikeyName);
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if (Convert.isNullOrEmpty(this.apikeyName))
		{
			failedr.append("no api key set");
			return false;
		}
		
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("apikey_n", this.apikeyName);
		jo.putOpt("model", this.model) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.apikeyName = jo.optString("apikey_n");
		this.model = jo.optString("model") ;
	}

	@Override
	public String getPmTitle()
	{
		AIApiKey ak = getApiKey();
		if (ak == null)
			return "";
		return ak.getTitle() + "[" + ak.getName() + "]";
	}

	private OkHttpClient client;
	private ObjectMapper mapper;
	
	private OkHttpClient getClient()
	{
		if(client!=null)
			return client ;
		return this.client = new OkHttpClient();// .Builder().build();
	}

	public String chat(String model, String prompt) throws Exception
	{
		AIApiKey aak = this.getApiKey();
		if (aak == null)
			return null;
		
		this.mapper = new ObjectMapper();

		String url = aak.getChatUrl(); // "https://"+aak.getApiHost() + "/chat/completions";
		Map<String, Object> requestBody = Map.of("model", model, "messages",
				List.of(Map.of("role", "user", "content", prompt)));

		Request request = new Request.Builder().url(url).addHeader("Authorization", "Bearer " + aak.getApiKey())
				.post(RequestBody.create(mapper.writeValueAsString(requestBody), MediaType.get("application/json")))
				.build();

		try (Response response = getClient().newCall(request).execute())
		{
			return response.body().string();
		}
	}
	// private UniChatClient chatClient = null ;
	//
	
	private List<String> models = null ;
	
	public synchronized List<String> listModels(boolean brefresh) throws Exception
	{
		if(!brefresh && models!=null)
			return this.models ;
		
		AIApiKey aak = this.getApiKey();
		if (aak == null)
			return null;
		String url = aak.getModelListUrl();//"https://"+aak.getApiHost() + "/v1/models";
		if(Convert.isNullOrEmpty(url))
			return null ;
		
		//Map<String, Object> requestBody = Map.of();

		Request request = new Request.Builder().url(url).addHeader("Authorization", "Bearer " + aak.getApiKey())
				.get()
				//.post(RequestBody.create(mapper.writeValueAsString(requestBody), MediaType.get("application/json")))
				.build();

		ArrayList<String> ss = new ArrayList<>() ;
		try (Response response = getClient().newCall(request).execute())
		{
			String resptxt = response.body().string();
			JSONObject resp_jo = new JSONObject(resptxt) ;
			JSONArray jarr  =resp_jo.optJSONArray("data") ;
			if(jarr!=null)
			{
				for(int i = 0 ; i < jarr.length() ; i ++)
				{
					JSONObject tmpjo = jarr.getJSONObject(i) ;
					String md = tmpjo.optString("id") ;
					if(Convert.isNullOrEmpty(md))
						continue ;
					ss.add(md) ;
				}
			}
		}
		
		return models=ss ;
	}
}
