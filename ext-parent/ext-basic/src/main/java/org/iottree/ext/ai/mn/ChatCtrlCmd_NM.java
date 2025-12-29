package org.iottree.ext.ai.mn;

import java.util.List;
import java.util.Map;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatCtrlCmd_NM extends MNNodeMid
{
	@Override
	public String getTP()
	{
		return "chat_ctrl_cmd";
	}

	@Override
	public String getTPTitle()
	{
		return g("chat_ctrl_cmd");
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
	
//	public String getOllamaHost()
//	{
//		if(this.ollamaHost==null)
//			return "" ;
//		return this.ollamaHost ;
//	}
//	
//	public int getOllamaPort()
//	{
//		return this.ollamaPort ;
//	}
//	
//	public String getModelName()
//	{
//		if(this.modelName==null)
//			return "" ;
//		return this.modelName ;
//	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
//		if(Convert.isNullOrEmpty(this.ollamaHost) || this.ollamaPort<=0)
//		{
//			failedr.append("no valid ollama host:port set") ;
//			return false ;
//		}
//		if(Convert.isNullOrEmpty(modelName))
//		{
//			failedr.append("no model name set") ;
//			return false;
//		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
//		jo.putOpt("ollama_host", this.ollamaHost) ;
//		jo.put("ollama_port",this.ollamaPort) ;
//		jo.putOpt("model_name",this.modelName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
//		this.ollamaHost = jo.optString("ollama_host") ;
//		this.ollamaPort = jo.optInt("ollama_port", 11434) ;
//		this.modelName = jo.optString("model_name") ;
	}
	
	@Override
	public String getPmTitle()
	{
//		if(Convert.isNullOrEmpty(this.ollamaHost))
//			return "" ;
//		return this.ollamaHost+":"+this.ollamaPort;
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		String sys = "你是一名智能家居管家，只能从下面工具列表里挑选，输出 JSON。\r\n" + 
				"工具列表：\r\n" + 
				"[\r\n" + 
				"  {\"name\":\"bedroomLight\",\"desc\":\"主卧灯\",\"state\":\"on|off\",\"args\":{\"action\":\"on|off\"}},\r\n" + 
				"  {\"name\":\"ac\",\"desc\":\"空调\",\"args\":{\"action\":\"on|off|set_temp\",\"temp\":16-30}}\r\n" + 
				"]\r\n" + 
				
				"当用户询问当前设备状态，请输出：{\"state\":{\"bedroomLight\":\"{{on|off}}\",\"temp\":{{state}}\r\n" +
				"当用需要调整设备状态，请输出："+"{\"plan\":[{\"tool\":\"bedroomLight\",\"action\":\"on\"}]}\r\n"+
				
				"当前状态：{\"bedroomLight_state\":\"off\",\"ac\":\"off\",\"temp_state\":25\"}\r\n";
		
		String pld = msg.getPayloadStr() ;
		if(Convert.isNullOrEmpty(pld))
			return null ;
		
		OkHttpClient client = new OkHttpClient();
		
		JSONObject req_jo = new JSONObject() ;
		JSONArray msg_jarr = new JSONArray() ;
		msg_jarr.put(Map.of("role", "system", "content", sys)) ;
		
		req_jo.put("model","./Qwen2.5-0.5B-Instruct/")
			.put("messages",List.of(
                Map.of("role", "system", "content", sys),
                Map.of("role", "user", "content", pld)
            ))
			.put("max_tokens",5120)
			.put("temperature",0.1);
		

	        // 2. 发送请求
	        Request request = new Request.Builder()
	                .url("http://localhost:8000/v1/chat/completions")
	                .post(RequestBody.create(req_jo.toString(), MediaType.get("application/json")))
	                .build();
	        try (Response response = client.newCall(request).execute()) {
	            if (!response.isSuccessful()) throw new RuntimeException("Unexpected code " + response);
	            String responseBody = response.body().string();
	          //System.out.println() ;
				MNMsg m = new MNMsg().asPayloadJO(responseBody) ;
						 
			return RTOut.createOutIdx().asIdxMsg(0, m);
	        }
	        //return null ;
	}
	
	protected RTOut RT_onMsgIn0(MNConn in_conn, MNMsg msg) throws Exception
	{
		String pld = msg.getPayloadStr() ;
		if(Convert.isNullOrEmpty(pld))
			return null ;
		
		
		OpenAiChatModel model = OpenAiChatModel.builder()
			    .baseUrl("http://172.20.226.189:8000/v1") // 关键：指向你的vLLM服务
			    .apiKey("no-api-key-needed") // vLLM不需要key，但需填一个非空值
			    .modelName("./Qwen2.5-0.5B-Instruct/") // 必须与启动服务时指定的名称一致
			    .temperature(0.1)
			    .maxTokens(5500)
			    .logRequests(true) // 开启日志，便于调试
			    .logResponses(true)
			    .build();

		ResponseFormat rf = ResponseFormat.builder()
				.type(ResponseFormatType.JSON)
				//.jsonSchema(JsonSchema.builder().name("abc").build())
				.build();
		UserMessage umsg = UserMessage.from(pld);
		//umsg.
		ChatRequest cr = ChatRequest.builder()
				.responseFormat(rf)
				.messages(umsg)
				.build() ;
			ChatResponse response = model.chat(cr) ;
			
			JSONObject resp_jo = new JSONObject(response.aiMessage().text()) ;
			//System.out.println() ;
			MNMsg m = new MNMsg().asPayloadJO(resp_jo) ;
					 
		return RTOut.createOutIdx().asIdxMsg(0, m);
	}
	
//	@Override
//	protected void RT_onBeforeNetRun()
//	{
////		if(Convert.isNullOrEmpty(this.ollamaHost))
////			return ;
////		String url = "http://"+this.ollamaHost+":"+this.ollamaPort ;
////		chatModel = OllamaChatModel.builder().baseUrl(url).modelName(modelName).build();
//	}
//	
//	
//	@Override
//	protected void RT_onAfterNetStop()
//	{
//		
//	}
}
