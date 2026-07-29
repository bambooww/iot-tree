package org.iottree.ext.ai.mn;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNNode.OutResDef;
import org.iottree.core.util.Convert;
import org.iottree.ext.ai.LLMMsg;
import org.iottree.ext.ai.LLMReq;
import org.iottree.ext.ai.LLMResp;
import org.iottree.ext.ai.LLMRespVLLM;
import org.iottree.ext.ai.LLMToolFunc;
import org.iottree.ext.ai.LLMMsg.Role;
import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VLLMChat_NM extends MNNodeMid
{
	String modelName = null;//"qwen3:4b";
	
	long connTO = 3000 ; //ms
	long readTO = 180000 ; //ms
	
	/**
	 * set fixed system message
	 */
	String systemMsg = null ;
	
	String lastUserMsg =null ;
	
	String lastAssistantMsg = null ;
	
	JSONObject jsonSchema = null ;
	
	long maxTokens = 5120;
	
	
	@Override
	public String getTP()
	{
		return "llm_vllm_chat";
	}

	@Override
	public String getTPTitle()
	{
		return "vLLM Chat";
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
	
	@Override
	public int getOutNum()
	{
		return 4;
	}
	
	private static HashMap<Integer,OutResDef> OUT2RES =new HashMap<>() ;
	static
	{
		OUT2RES.put(3,new OutResDef(LLMToolFunc_RES.class,false)) ;
	}
	
	@Override
	public Map<Integer,OutResDef> getOut2Res()
	{
		return OUT2RES ;
	}
	

	public List<LLMToolFunc_RES> listToolFuncs()
	{
		return this.findSubsequentNodes(LLMToolFunc_RES.class) ;
	}

	public String getModelName()
	{
		if(this.modelName==null)
			return "" ;
		return this.modelName ;
	}
	
	public String getSystemMsg()
	{
		if(this.systemMsg==null)
			return "" ;
		return this.systemMsg ;
	}
	
	public String getLastUserMsg()
	{
		if(this.lastUserMsg==null)
			return "" ;
		return this.lastUserMsg ;
	}
	
	public String getLastAssistantMsg()
	{
		if(this.lastAssistantMsg==null)
			return "" ;
		return this.lastAssistantMsg ;
	}
	
	public JSONObject getJsonSchema()
	{
		return this.jsonSchema ;
	}
	
	public long getMaxTokens()
	{
		return this.maxTokens ;
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
		if(this.maxTokens<=0)
		{
			failedr.append("max tokens must >0") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("model_name",this.modelName) ;
		jo.putOpt("sys_msg",this.systemMsg) ;
		jo.putOpt("last_user_msg",this.lastUserMsg) ;
		jo.putOpt("last_assistant_msg",this.lastAssistantMsg) ;
		jo.putOpt("max_tokens", this.maxTokens) ;
		jo.putOpt("json_schema", this.jsonSchema) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.modelName = jo.optString("model_name") ;
		this.systemMsg = jo.optString("sys_msg") ;
		this.lastUserMsg = jo.optString("last_user_msg") ;
		this.lastAssistantMsg = jo.optString("last_assistant_msg") ;
		this.maxTokens = jo.optLong("max_tokens");
		Object json_ob = jo.opt("json_schema") ;
		if(json_ob!=null&&!"".equals(json_ob))
		{
			if(json_ob instanceof String)
				this.jsonSchema = new JSONObject((String)json_ob) ;
			else if(json_ob instanceof JSONObject)
				this.jsonSchema = (JSONObject)json_ob ;
		}
		
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
	
	private LLMReq RT_getOrCreateReq(MNMsg msg)
	{
		Object pld = msg.getPayload() ;
		LLMReq req = null;
		
		if(pld!=null&&!"".equals(pld))
		{
			req = new LLMReq() ;
			if(Convert.isNotNullEmpty(this.systemMsg))
				req.addMessage(Role.system, systemMsg) ;
			if(Convert.isNotNullEmpty(lastUserMsg) && Convert.isNotNullEmpty(this.lastAssistantMsg))
			{
				req.addMessageUser(lastUserMsg) ;
				req.addMessageAssistsnt(this.lastAssistantMsg) ;
			}
			
			if(pld instanceof String)
			{
				String pldstr = (String)pld ;
				req.addMessageUser(pldstr) ; //default user message
			}
			else if(pld instanceof JSONObject)
			{
				LLMMsg mmm = LLMMsg.transFromJO((JSONObject)pld) ;
				if(mmm==null)
					return null ;
				req.addMessage(mmm) ;
			}
			else if(pld instanceof JSONArray)
			{
				List<LLMMsg> mms = LLMMsg.transFromJArr((JSONArray)pld) ;
				req.addMessages(mms) ;
			}
			else
			{
				return null ;
			}
			
			req.asStructuredOutputJsonSchema(jsonSchema) ;
		}
		else
		{
			Object extob = msg.getExtObj() ;
			if(extob!=null && extob instanceof LLMReq)
				req = (LLMReq)extob;
		}
		
		if(req==null)
			return null ;
		
		// model params
		if(this.maxTokens>0)
			req.asMaxTokens(this.maxTokens) ;
		req.asModel(this.modelName) ;
		
		return req;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		if(Convert.isNullOrEmpty(this.modelName))
			return null ;
		
		VLLM_M owner = (VLLM_M)this.getOwnRelatedModule() ;
		
		LLMReq req = RT_getOrCreateReq(msg) ;
		if(req==null)
			return null ;
		
		List<LLMToolFunc_RES> tfs = listToolFuncs();
		if(tfs!=null&&tfs.size()>0)
		{
			for(LLMToolFunc_RES tf : tfs)
			{
				LLMToolFunc llmtf = tf.toToolFunc() ;
				if(llmtf==null)
					continue;
				req.addTool(llmtf) ;
			}
		}
		//req.asMaxTokens()
		String url = owner.getUrl() + "/v1/chat/completions";
		JSONObject req_jo = req.toJO() ;
		String req_jstr = req_jo.toString(2) ;
		//System.out.println(" req jo==="+req_jstr) ;
		
		MNMsg m = new MNMsg().asPayloadJO(req_jo) ;//resp.getMessage().getContent());
		RTOut rto = RTOut.createOutIdx().asIdxMsg(0, m) ;
		
		RequestBody req_body = RequestBody.create(
				req_jstr
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
			
			LLMRespVLLM resp = new LLMRespVLLM();
			if(resp.fromJO(tmpjo))
			{
				m = new MNMsg().asPayloadJO(tmpjo) ;//resp.getMessage().getContent());
				rto.asIdxMsg(1, m) ;
				
				String content = resp.getMessage().getContent() ;
				if(jsonSchema!=null && Convert.isNotNullEmpty(content))
				{
					JSONObject ccjo = new JSONObject(content) ;
					return rto.asIdxMsg(2, new MNMsg().asPayload(ccjo)) ;
				}
				rto.asIdxMsg(2, new MNMsg().asPayload(content));
			}
		}
		return rto ;
	}

	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case 0: return "request";
		case 1: return "response";
		case 2: return "return";
		case 3: return "Tools" ;
		default:
			return null ;
		}
	}
	
	@Override
	public boolean getShowOutTitleDefault()
	{
		return true;
	}
}
