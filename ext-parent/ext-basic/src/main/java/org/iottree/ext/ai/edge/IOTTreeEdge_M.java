package org.iottree.ext.ai.edge;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.modules.FSM_State;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;
import org.iottree.core.util.logger.*;

public class IOTTreeEdge_M extends MNModule implements IMNRunner
{
	static ILogger LOG = LoggerManager.getLogger(IOTTreeEdge_M.class) ;
	
	public static class CamLoc
	{
		public String id ;
		
		public String title ;
		
		public JSONObject toJO()
		{
			return new JSONObject().put("id", this.id).putOpt("t", this.title) ;
		}
		
		public static CamLoc fromJO(JSONObject jo)
		{
			String id = jo.optString("id") ;
			if(Convert.isNullOrEmpty(id))
				return null ;
			CamLoc ret = new CamLoc() ;
			ret.id = id ;
			ret.title = jo.optString("t") ;
			return ret ;
		}
	}
	
	public static class CamIP
	{
		public String id ;
		
		public String title ;
		
		public String url ;

		public JSONObject toJO()
		{
			return new JSONObject().put("id",id).putOpt("t", this.title).putOpt("u", url) ;
		}
		
		public static CamIP fromJO(JSONObject jo)
		{
			String id = jo.optString("id") ;
			if(Convert.isNullOrEmpty(id))
				return null ;
			CamIP ret = new CamIP() ;
			ret.id = id ;
			ret.title = jo.optString("t") ;
			ret.url = jo.optString("u");
			return ret ;
		}
	}
	
	String edgeHost = "localhost" ;
	
	int edgePort = 9091;
	
	long checkIntV = 1000 ;
	
	ArrayList<CamLoc> camLocs = null ;
	
	ArrayList<CamIP> camIPs = null ;
	
	@Override
	public String getTP()
	{
		return "iottree_edge";
	}

	@Override
	public String getTPTitle()
	{
		return "IOTTree Edge";
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
		return "\\uf0a0";
	}
	
	public String getEdgeHost()
	{
		if(this.edgeHost==null)
			return "" ;
		return this.edgeHost ;
	}
	
	public int getEdgePort()
	{
		return this.edgePort ;
	}
	
	public String getEdgeUrl()
	{
		if(Convert.isNullOrEmpty(this.edgeHost))
		{
			//RT_DEBUG_ERR.fire("model", "model is not ready");
			return null;
		}
		return "http://"+this.edgeHost+":"+this.edgePort ;
	}
	
	public List<CamLoc> listCamLoc()
	{
		return this.camLocs ;
	}
	
	public List<CamIP> listCamIP()
	{
		return this.camIPs ;
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.edgeHost) || this.edgePort<=0)
		{
			failedr.append("no valid ollama host:port set") ;
			return false ;
		}
		
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("edge_host", this.edgeHost) ;
		jo.put("edge_port",this.edgePort) ;
		jo.putOpt("check_intv",this.checkIntV) ;
		if(camLocs!=null)
		{
			JSONArray jarr = new JSONArray() ;
			for(CamLoc cip:this.camLocs)
				jarr.put(cip.toJO()) ;
			jo.put("cam_locs",jarr) ;
		}
		if(camIPs!=null)
		{
			JSONArray jarr = new JSONArray() ;
			for(CamIP cip:this.camIPs)
				jarr.put(cip.toJO()) ;
			jo.put("cam_ips",jarr) ;
		}
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.edgeHost = jo.optString("edge_host") ;
		this.edgePort = jo.optInt("edge_port", 9091) ;
		//this.modelName = jo.optString("model_name") ;
		this.checkIntV = jo.optLong("check_intv",1000) ;
		if(this.checkIntV<=0)
			this.checkIntV = 1000 ;
		
		JSONArray jarr = jo.optJSONArray("cam_locs") ;
		ArrayList<CamLoc> camlocs = null;
		if(jarr!=null)
		{
			camlocs = new ArrayList<>() ;
			for(int i = 0 ; i < jarr.length() ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				CamLoc cip = CamLoc.fromJO(tmpjo) ;
				if(cip==null)
					continue ;
				camlocs.add(cip) ;
			}
		}
		this.camLocs = camlocs ;
		
		jarr = jo.optJSONArray("cam_ips") ;
		ArrayList<CamIP> camips = null;
		if(jarr!=null)
		{
			camips = new ArrayList<>() ;
			for(int i = 0 ; i < jarr.length() ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				CamIP cip = CamIP.fromJO(tmpjo) ;
				if(cip==null)
					continue ;
				camips.add(cip) ;
			}
		}
		this.camIPs = camips ;
		
	}
	
	@Override
	public String getPmTitle()
	{
		if(Convert.isNullOrEmpty(this.edgeHost))
			return "" ;
		return this.edgeHost+":"+this.edgePort;
	}
	

	private boolean bRun = false;
	
	private Thread procTh = null ;

	private Runnable runner = new Runnable()
	{
		public void run()
		{
			try
			{
				while(bRun)
				{
					UTIL_sleep(checkIntV) ;
					
					//RT_runPID();
					RT_runInLoop();
				}
			}
			finally
			{
				synchronized(this)
				{
					procTh = null ;
					bRun = false;
				}
			}
		}
	};
	
	synchronized private void RT_runInLoop()
	{
		//check ai edge
		String url = "http://"+this.edgeHost+":"+this.edgePort+"/gesture/";
	}
	

	@Override
	public synchronized boolean RT_start(StringBuilder failedr)
	{
		if (bRun)
			return true;

		bRun = true;
		procTh = new Thread(runner);
		procTh.start();
		return true;
	}

	@Override
	public synchronized void RT_stop()
	{
		Thread th = procTh;
		if (th != null)
			th.interrupt();
		bRun = false;
		procTh = null;
	}

	@Override
	public boolean RT_isRunning()
	{
		return bRun;
	}

	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		return false;
	}
	
	/**
	 * false will not support runner
	 * @return
	 */
	public boolean RT_runnerEnabled()
	{
		return true ;
	}
	
	/**
	 * true will not support manual trigger to start
	 * @return
	 */
	public boolean RT_runnerStartInner()
	{
		return false;
	}
}
