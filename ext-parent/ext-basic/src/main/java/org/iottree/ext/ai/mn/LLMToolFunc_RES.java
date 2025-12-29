package org.iottree.ext.ai.mn;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.util.Convert;
import org.iottree.ext.ai.LLMTool;
import org.iottree.ext.ai.LLMToolFunc;
import org.json.JSONArray;
import org.json.JSONObject;

import dev.langchain4j.model.ollama.OllamaChatModel;

public class LLMToolFunc_RES extends MNNodeRes
{
	String name ; //func name
	
	String desc ;
	
	LinkedHashMap<String,LLMToolFunc.Param> params = new LinkedHashMap<>() ;
	
	@Override
	public String getTP()
	{
		return "llm_tool_func";
	}

	@Override
	public String getTPTitle()
	{
		return "Function";
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
		return "\\uf076";
	}
	
	public String getName()
	{
		if(this.name==null)
			return "" ;
		
		return name ;
	}
	
	public String getDesc()
	{
		if(this.desc==null)
			return "" ;
		return this.desc;
	}
	
	public LinkedHashMap<String,LLMToolFunc.Param> getParams()
	{
		return this.params ;
	}
	
	public LLMToolFunc toToolFunc()
	{
		StringBuilder failedr = new StringBuilder() ;
		if(!this.isParamReady(failedr))
			return null ;
		return new LLMToolFunc(this.name,this.desc,
				params.values().stream().collect(Collectors.toList())) ;
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(name))
		{
			failedr.append("no function name set") ;
			return false;
		}
		if(Convert.isNullOrEmpty(desc))
		{
			failedr.append("no function desc set") ;
			return false;
		}
		
		return true ;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("name", this.name) ;
		jo.put("desc",this.desc) ;
		if(params!=null)
		{
			JSONArray jarr = new JSONArray() ;
			jo.put("params", jarr) ;
			for(LLMToolFunc.Param pm:this.params.values())
			{
				jarr.put(pm.toJO()) ;
			}
		}
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.name = jo.optString("name") ;
		this.desc = jo.optString("desc") ;
		JSONArray jarr = jo.optJSONArray("params") ;
		LinkedHashMap<String,LLMToolFunc.Param> pms = null;
		int n ;
		if(jarr!=null && (n=jarr.length())>0)
		{
			pms = new LinkedHashMap<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				LLMToolFunc.Param pm = LLMToolFunc.Param.formJO(jarr.getJSONObject(i)) ;
				if(pm==null)
					continue ;
				pms.put(pm.name, pm) ;
			}
		}
		this.params = pms ;
	}
	
	@Override
	public String getPmTitle()
	{
		return this.name ;
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
