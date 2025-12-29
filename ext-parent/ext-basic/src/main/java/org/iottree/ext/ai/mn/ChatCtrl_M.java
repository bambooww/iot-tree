package org.iottree.ext.ai.mn;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

/**
 * using prompt and device status ,with chat
 * to return device json command  
 * 
 * it can be used to smart home,device voice controller
 * 
 * @author jason.zhu
 *
 */
public class ChatCtrl_M  extends MNModule
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
	
	String ollamaHost = "localhost" ;
	
	int ollamaPort = 11434;
	
	String modelName = null;//"qwen3:4b";

	@Override
	public String getTP()
	{
		return "chat_ctrl";
	}

	@Override
	public String getTPTitle()
	{
		return g("chat_ctrl");
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
}
