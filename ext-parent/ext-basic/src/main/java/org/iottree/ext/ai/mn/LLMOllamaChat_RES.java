package org.iottree.ext.ai.mn;

import java.util.List;

import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.util.Convert;
import org.json.JSONObject;


public class LLMOllamaChat_RES extends MNNodeRes
{
	String modelName = null;//"qwen3:4b";
	
//	private OllamaChatModel chatModel = null ;
	
	@Override
	public String getTP()
	{
		return "llm_ollama_chat_res";
	}

	@Override
	public String getTPTitle()
	{
		return "Chat Res";
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
//		jo.putOpt("ollama_host", this.ollamaHost) ;
//		jo.put("ollama_port",this.ollamaPort) ;
		jo.putOpt("model_name",this.modelName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
//		this.ollamaHost = jo.optString("ollama_host") ;
//		this.ollamaPort = jo.optInt("ollama_port", 11434) ;
		this.modelName = jo.optString("model_name") ;
		
//		chatModel = null ;
	}
	
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
//		if(outTP==OutTP.msg_per_ln)
//		{
//			StringBuilder divsb = new StringBuilder() ;
//			divsb.append("<div class=\"rt_blk\">Read Line CC= "+LINE_CC) ;
//			divsb.append("</div>") ;
//			divblks.add(new DivBlk("file_r_line_cc",divsb.toString())) ;
//		}
		
		super.RT_renderDiv(divblks);
	}
	

	protected void RT_onBeforeNetRun()
	{
		StringBuilder failedr = new StringBuilder() ;
		//createNoExistedTable(failedr) ;
	}
}
