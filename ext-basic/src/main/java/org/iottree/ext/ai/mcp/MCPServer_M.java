package org.iottree.ext.ai.mcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.util.Convert;
import org.iottree.core.util.UrlUtil;
import org.iottree.core.util.logger.ILogable;
import org.iottree.ext.ai.LLMModel;
import org.iottree.ext.ai.LLMModelOllama;
import org.json.JSONArray;
import org.json.JSONObject;

public class MCPServer_M extends MNModule implements IMNRunner,ILogable
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
	
	private transient Thread RT_th = null ;
	
	private transient boolean RT_bRun = false;
	
	private Runnable mcpRunner = new Runnable()
	{
		public void run()
		{
			consumerRun() ;
		}
	};
	
	private void consumerRun()
	{
		try
		{
			while (RT_bRun)
			{
				//this.checkConn();
			}//end of while
		}
		finally
		{
			RT_bRun=false;
			RT_th = null ;
			
//			if(mqttEP!=null)
//			{
//				mqttEP.disconnect();
//				mqttEP = null;
//			}
		}
	}
	
	protected boolean RT_init(StringBuilder failedr)
	{
		//bRTInitOk = false;
		return true;
	}

	@Override
	public boolean RT_start(StringBuilder failedr)
	{
		if(RT_bRun)
			return true;
		
		if(!RT_init(failedr))
			return false;
		
		RT_bRun=true ;
		RT_th = new Thread(mcpRunner);
		RT_th.start();
		return true;
	}

	@Override
	public void RT_stop()
	{
		Thread th = RT_th ;
		if(th==null)
			return ;
		
		if(th!=null)
			th.interrupt();
		
		RT_bRun = false ;
		RT_th  =null ;
//		if(mqttEP!=null)
//		{
//			mqttEP.disconnect();
//			mqttEP = null;
//		}
	}

	@Override
	public boolean RT_isRunning()
	{
		return RT_th!=null;
	}

	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		return false;
	}

	@Override
	public boolean RT_runnerEnabled()
	{
		return true;
	}

	@Override
	public boolean RT_runnerStartInner()
	{
		return false;
	}
	

}
