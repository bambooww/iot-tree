package org.iottree.ext.ai.mn;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.ext.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

//import com.openai.models.completions.CompletionChoice;
//
//import dev.langchain4j.data.message.ChatMessage;
//import dev.langchain4j.data.message.SystemMessage;
//import dev.langchain4j.data.message.UserMessage;
//import dev.langchain4j.model.chat.request.ChatRequest;
//import dev.langchain4j.model.chat.request.ResponseFormat;
//import dev.langchain4j.model.chat.request.ResponseFormatType;
//import dev.langchain4j.model.chat.response.ChatResponse;
//import dev.langchain4j.model.openai.OpenAiChatModel;
//import dev.langchain4j.model.openai.internal.chat.ChatCompletionRequest;
//import dev.langchain4j.model.openai.internal.chat.ChatCompletionResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VLLMCtrlCmd_NM extends MNNodeMid
{
	int maxToken = 1024;

	@Override
	public String getTP()
	{
		return "vllm_ctrl_cmd";
	}

	@Override
	public String getTPTitle()
	{
		return g("vllm_ctrl_cmd");
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

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		// if(Convert.isNullOrEmpty(this.ollamaHost) || this.ollamaPort<=0)
		// {
		// failedr.append("no valid ollama host:port set") ;
		// return false ;
		// }
		// if(Convert.isNullOrEmpty(modelName))
		// {
		// failedr.append("no model name set") ;
		// return false;
		// }
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		// jo.putOpt("ollama_host", this.ollamaHost) ;
		// jo.put("ollama_port",this.ollamaPort) ;
		// jo.putOpt("model_name",this.modelName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		// this.ollamaHost = jo.optString("ollama_host") ;
		// this.ollamaPort = jo.optInt("ollama_port", 11434) ;
		// this.modelName = jo.optString("model_name") ;
	}

	@Override
	public int getOutNum()
	{
		// Optional<T>.ofNullable(value)
		return 1;
	}
	
//	protected RTOut RT_onMsgIn1(MNConn in_conn, MNMsg msg) throws Exception
//	{
//		String pld = msg.getPayloadStr() ;
//		if(Convert.isNullOrEmpty(pld))
//			return null ;
//		
//		String SYSTEM_PROMPT = "你是一名智能家居管家，只能从下面工具列表里挑选，输出 JSON。\r\n" + 
//				"工具列表：\r\n" + 
//				"[\r\n" + 
//				"  {\"name\":\"bedroomLight\",\"desc\":\"主卧灯\",\"state\":\"on|off\",\"args\":{\"action\":\"on|off\"}},\r\n" + 
//				"  {\"name\":\"ac\",\"desc\":\"空调\",\"args\":{\"action\":\"on|off|set_temp\",\"temp\":16-30}}\r\n" + 
//				"]\r\n" + 
//				
//				"当用户询问当前设备状态，请输出：{\"state\":{\"bedroomLight\":\"{{on|off}}\",\"temp\":{{state}}\r\n" +
//				"当用需要调整设备状态，请输出："+"{\"plan\":[{\"tool\":\"bedroomLight\",\"action\":\"on\"}]}\r\n"+
//				
//				"当前状态：{\"bedroomLight_state\":\"off\",\"ac\":\"off\",\"temp_state\":25\"}\r\n";
//		
//		VLLMCtrl_M owner = (VLLMCtrl_M) this.getOwnRelatedModule();
//		StringBuilder failedr = new StringBuilder();
//		if (!owner.isParamReady(failedr))
//		{
//			RT_DEBUG_ERR.fire("owner", "Owner Module Param Not Ready:" + failedr);
//			return null;
//		}
//
//		String url_base = owner.getVLLMUrlBase();
//		
//		OpenAiChatModel model = OpenAiChatModel.builder()
//			    .baseUrl(url_base+"/v1") // 关键：指向你的vLLM服务
//			    .apiKey("no-api-key-needed") // vLLM不需要key，但需填一个非空值
//			    .modelName("./Qwen2.5-0.5B-Instruct/") // 必须与启动服务时指定的名称一致
//			    .temperature(0.1)
//			    .maxTokens(5500)
//			    .logRequests(true) // 开启日志，便于调试
//			    .logResponses(true)
//			    .build();
//
//		ResponseFormat rf = ResponseFormat.builder()
//				.type(ResponseFormatType.JSON)
//				//.jsonSchema(JsonSchema.builder().name("abc").build())
//				.build();
//		
//		List<ChatMessage> messages = Arrays.asList(
//                SystemMessage.from(SYSTEM_PROMPT), // 第一段：系统角色
//                UserMessage.from("当前设备状态：\n" + getCurrentDeviceStatus()), // 第二段：状态
//                UserMessage.from("用户指令：" + pld) // 第三段：本次指令
//        );
//		
//			ChatResponse response = model.chat(messages) ;
//			
//			JSONObject resp_jo = new JSONObject(response.aiMessage().text()) ;
//			//System.out.println() ;
//			MNMsg m = new MNMsg().asPayloadJO(resp_jo) ;
//					 
//		return RTOut.createOutIdx().asIdxMsg(0, m);
//	}
	
	private String getCurrentDeviceStatus() {
        // 返回结构化的状态，例如JSON字符串
        return "{\r\n" + 
        		"                  \"devices\": {\r\n" + 
        		"                    \"living_room_light\": { \"state\": \"off\", \"brightness\": 0 },\r\n" + 
        		"                    \"bedroom_ac\": { \"state\": \"on\", \"temperature\": 26 },\r\n" + 
        		"                    \"hallway_motion_sensor\": { \"state\": \"inactive\" }\r\n" + 
        		"                  }\r\n" + 
        		"                }";
    }
	

	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		return null;
	}
}
