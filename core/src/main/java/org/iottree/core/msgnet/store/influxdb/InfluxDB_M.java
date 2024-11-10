package org.iottree.core.msgnet.store.influxdb;

import java.util.List;

import org.iottree.core.msgnet.IMNNodeRes;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.store.SourceInfluxDB;
import org.iottree.core.store.StoreManager;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

public class InfluxDB_M extends MNModule  implements IMNRunner,IMNNodeRes
{
//	private static InfluxDB_Writer SUP_W = new InfluxDB_Writer() ;
//	private static InfluxDB_Tag2Point SUP_TAG2PT = new InfluxDB_Tag2Point() ;
//	private static InfluxDB_JO2Point SUP_JO2PT = new InfluxDB_JO2Point() ;
	
//	private static List<MNNode> SUPS = Arrays.asList(SUP_W,SUP_TAG2PT,SUP_JO2PT) ;
	
	boolean usingSource = true ;
	
	String sourceName = null ;
	
	String url = "http://localhost:8086" ;
	
	String token = null ;
	
	String org = null ;
	
	String bucket = null ;

//	@Override
//	protected List<MNNode> getSupportedNodes()
//	{
//		return SUPS;
//	}

	@Override
	public String getTP()
	{
		return "influxdb";
	}

	@Override
	public String getTPTitle()
	{
		return "InfluxDB V2";
	}

	@Override
	public String getColor()
	{
		return "#f3b484";
	}

	@Override
	public String getIcon()
	{
		return "PK_influxdb";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(this.usingSource)
		{
			if(Convert.isNullOrEmpty(this.sourceName))
			{
				failedr.append("no source name") ;
				return false;
			}
			SourceInfluxDB sor = StoreManager.getSourceInfluxDB(this.sourceName) ;
			if(sor==null)
			{
				failedr.append("no source found with name="+this.sourceName) ;
				return false;
			}
			if(!sor.checkValid(failedr))
				return false ;
		}
		else
		{
			if(Convert.isNullOrEmpty(this.url))
			{
				failedr.append("no url str") ;
				return false;
			}
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("using_sor", this.usingSource) ;
		jo.putOpt("sor_name", this.sourceName) ;
		jo.putOpt("url", this.url) ;
		jo.putOpt("token", token) ;
		jo.putOpt("org", this.org) ;
		jo.putOpt("bucket", this.bucket) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.usingSource = jo.optBoolean("using_sor",true) ;
		this.sourceName = jo.optString("sor_name") ;
		this.url = jo.optString("url") ;
		this.token = jo.optString("token") ;
		this.org = jo.optString("org") ;
		this.bucket = jo.optString("bucket") ;
		
		RT_close();
	}
	
	public boolean isUsingSource()
	{
		return this.usingSource ;
	}
	
	public String getSourceName()
	{
		return this.sourceName ;
	}
	

	public String getInfluxUrl()
	{
		return url ;
	}
	
	public String getInfluxToken()
	{
		return this.token ;
	}
	
	public String getInfluxOrg()
	{
		return this.org ;
	}
	
	public String getInfluxBucket()
	{
		return this.bucket ;
	}
	
	// rt
	
	InfluxDBClient rtClient = null ;
	
//	private boolean RT_init(StringBuilder failedr)
//	{
//		rtClient =  InfluxDBClientFactory.create(this.url,
//				this.token.toCharArray(),org,bucket);
//		
//		return false;
//	}
	
	synchronized void RT_close()
	{
		if(rtClient!=null)
		{
			rtClient.close();
			rtClient=  null ;
		}
	}
	
	public synchronized InfluxDBClient RT_getClient()
	{
		if(rtClient!=null)
			return rtClient ;
		
		if(this.usingSource)
		{
			if(Convert.isNullOrEmpty(this.sourceName))
				return null ;
			SourceInfluxDB sor = StoreManager.getSourceInfluxDB(this.sourceName) ;
			if(sor==null)
				return null ;
			StringBuilder failedr = new StringBuilder() ;
			if(!sor.checkValid(failedr))
				return null ;
			rtClient =  InfluxDBClientFactory.create(sor.getUrl(),
					sor.getToken().toCharArray(),sor.getOrg(),sor.getBucket());
		}
		else
		{
			rtClient =  InfluxDBClientFactory.create(this.url,
					this.token.toCharArray(),org,bucket);
		}
		return rtClient ;
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
					UTIL_sleep(10) ;
					
					if(!RT_monWriter())
						break ;
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
	
	private boolean RT_monWriter()
	{
		List<MNNode> ns = this.getRelatedNodes() ;
		if(ns==null)
			return false; //
		
		boolean b_has_w = false;
		for(MNNode n:ns)
		{
			if(n instanceof InfluxDB_Writer)
			{
				boolean bv = ((InfluxDB_Writer)n).onMonByModule() ;
				if(bv)
					b_has_w = true ;
			}
		}
		return b_has_w ;//还在写动作中
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
