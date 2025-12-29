package org.iottree.ext.ai.mn;

import org.iottree.core.util.*;

import java.util.Arrays;
import java.util.List;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class OllamaChat  extends MNNodeMid
{
	
	ChatModel chatModel = null;
	
	String ollamaHost = "localhost" ;
	
	int ollamaPort = 11434;
	
	String modelName = null;//"qwen3:4b";

	@Override
	public String getTP()
	{
		return "ollama_chat";
	}

	@Override
	public String getTPTitle()
	{
		return "Ollama Chat";
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
	public int getOutNum()
	{
		return 1;
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
	
	public String getModelName()
	{
		if(this.modelName==null)
			return "" ;
		return this.modelName ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.ollamaHost) || this.ollamaPort<=0)
		{
			failedr.append("no valid ollama host:port set") ;
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
		jo.putOpt("ollama_host", this.ollamaHost) ;
		jo.put("ollama_port",this.ollamaPort) ;
		jo.putOpt("model_name",this.modelName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.ollamaHost = jo.optString("ollama_host") ;
		this.ollamaPort = jo.optInt("ollama_port", 11434) ;
		this.modelName = jo.optString("model_name") ;
	}
	
	@Override
	public String getPmTitle()
	{
		if(Convert.isNullOrEmpty(this.ollamaHost))
			return "" ;
		return this.ollamaHost+":"+this.ollamaPort;
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

	static String SYSTEM_PROMPT="你是一名智能家居管家，只能从下面工具列表里挑选，输出 JSON。\r\n" + 
			"工具列表：\r\n" + 
			"[\r\n" + 
			"  {\"name\":\"bedroomLight\",\"desc\":\"主卧灯\",\"args\":{\"action\":\"on|off\"}},\r\n" + 
			"  {\"name\":\"ac\",\"desc\":\"空调\",\"args\":{\"action\":\"on|off|set_temp\",\"temp\":16-30}}\r\n" + 
			"]\r\n" + 
			"当前状态：{\"bedroomLight\":\"off\",\"ac\":\"off\",\"temp\":26\"}\r\n" + 
			"用户说：{{用户原文}}\r\n" + 
			"请输出：\r\n" + 
			"{\"plan\":[{\"tool\":\"bedroomLight\",\"action\":\"on\"}]}" ;

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
	
	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		String pld = msg.getPayloadStr() ;
		if(Convert.isNullOrEmpty(pld))
			return null;
		
		//if(chatModel==null)
		{
			if(Convert.isNullOrEmpty(this.ollamaHost))
			{
				RT_DEBUG_ERR.fire("model", "model is not ready");
				return null;
			}
			String url = "http://"+this.ollamaHost+":"+this.ollamaPort ;
			chatModel = OllamaChatModel.builder().baseUrl(url).modelName(modelName)
					//.numPredict(128) //限制输出token，避免胡思乱想
					.build();
		}
		
		

		List<ChatMessage> messages = Arrays.asList(
                SystemMessage.from(SYSTEM_PROMPT), // 第一段：系统角色
                UserMessage.from("当前设备状态：\n" + getCurrentDeviceStatus()), // 第二段：状态
                UserMessage.from("用户指令：" + pld) // 第三段：本次指令
        );
		
		ChatResponse resp = chatModel.chat(messages);
    	//String res = chatModel.chat(pld);
		String res = resp.aiMessage().text();
 
    	MNMsg outm = new MNMsg().asPayload(res) ;
		return RTOut.createOutIdx().asIdxMsg(0, outm);
	}

}
