package org.iottree.ext.ai.mcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.msgnet.MNModule;
import org.iottree.core.util.Convert;
import org.iottree.core.util.UrlUtil;
import org.iottree.ext.ai.LLMModel;
import org.iottree.ext.ai.LLMModelOllama;
import org.json.JSONArray;
import org.json.JSONObject;

public class MCPServer_M extends MNModule
{
	int port = 8008;
	
	@Override
	public String getTP()
	{
		return "mcp";
	}

	@Override
	public String getTPTitle()
	{
		return "MCP Server";
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
		return "PK_mcp";
	}
	
	public int getPort()
	{
		return this.port ;
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(port<=0)
		{
			failedr.append("no port set") ;
			return false ;
		}
		
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("port",this.port) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.port = jo.optInt("port", 8008) ;
		//this.modelName = jo.optString("model_name") ;
	}
	
	@Override
	public String getPmTitle()
	{
		return ""+this.port;
	}
	

}
