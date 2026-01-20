package org.iottree.ext.ai.yolo;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.MNNodeResCaller;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.ResCaller;
import org.iottree.core.msgnet.MNNode.OutResDef;
import org.iottree.core.util.Convert;
import org.iottree.ext.ai.LLMMsg;
import org.iottree.ext.ai.LLMReq;
import org.iottree.ext.ai.LLMRespOllama;
import org.iottree.ext.ai.LLMToolFunc;
import org.iottree.ext.ai.LLMMsg.Role;
import org.iottree.ext.ai.mn.LLMOllama_M;
import org.iottree.ext.ai.mn.LLMToolFunc_RES;
import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Yolo_NM extends MNNodeMid
{
	long connTO = 3000 ; //ms
	long readTO = 180000 ; //ms
	
	/**
	 * 
	 */
	String serverHost = null ;
	
	int serverPort =5000 ;
	
	@Override
	public String getTP()
	{
		return "yolo_cv";
	}

	@Override
	public String getTPTitle()
	{
		return "Yolo CV";
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
		return "\\uf03d";
	}
	
	@Override
	public int getOutNum()
	{
		return 2;
	}
	
	public String getServerHost()
	{
		if(this.serverHost==null)
			return "" ;
		return this.serverHost ;
	}
	
	public int getServerPort()
	{
		return this.serverPort ;
	}
	

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
//		if(Convert.isNullOrEmpty(this.ollamaHost) || this.ollamaPort<=0)
//		{
//			failedr.append("no valid ollama host:port set") ;
//			return false ;
//		}
		if(Convert.isNullOrEmpty(this.serverHost) || this.serverPort<=0)
		{
			failedr.append("no server host or port set") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("server",this.serverHost) ;
		jo.putOpt("port",this.serverPort) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.serverHost = jo.optString("server") ;
		this.serverPort = jo.optInt("port") ;
		
		synchronized(this)
		{
			httpClient = null ;
		}
	}
	
	@Override
	public String getPmTitle()
	{
		return this.getServerHost()+":"+this.serverPort ;
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
	
	public String getUrl()
	{
		return "http://"+this.serverHost+":"+this.serverPort ;
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
		if(Convert.isNullOrEmpty(this.serverHost))
			return null ;
		
		String url = this.getUrl() ;
		
		JSONObject req_jo = new JSONObject() ;
		RequestBody req_body = RequestBody.create(
				req_jo.toString() //req_str
				,MediaType.get("application/json; charset=utf-8")) ;
		
		Request request = new Request.Builder()
				.url(url).post(req_body)
				.build();
		
		RTOut rto = null ;
		try (Response response = this.getHttpClient().newCall(request).execute())
		{
			if (!response.isSuccessful())
				throw new RuntimeException("Unexpected code " + response);
			String responseBody = response.body().string();
			JSONObject tmpjo = new JSONObject(responseBody) ;
//			//rto.asIdxMsg(1, new MNMsg().asPayload(tmpjo));
//			LLMRespOllama resp = new LLMRespOllama();
//			
//			if(resp.fromJO(tmpjo))
//			{
//				LLMMsg resp_msg = resp.getMessage() ;
//				String content = resp_msg.getContent() ;
//				MNMsg m = new MNMsg().asPayload(content) ;
//				rto.asIdxMsg(2, m) ;
//				//return rto.asIdxMsg(1, m);//.asIdxMsg(1, new MNMsg().asPayload(content));
//				
//				RT_processToolCall(resp_msg) ;
//			}
			
			
		}
		return rto ;
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case 0: return "detail";
		case 1: return "result";
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
