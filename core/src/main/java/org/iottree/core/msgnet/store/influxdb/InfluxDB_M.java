package org.iottree.core.msgnet.store.influxdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.iottree.core.msgnet.IMNNodeRes;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.store.SourceInfluxDB;
import org.iottree.core.store.StoreManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IJoOut;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

public class InfluxDB_M extends MNModule implements IMNRunner, IMNNodeRes
{
	public static class ValItem implements IJoOut
	{
		public Object val ;
		public long dt ;
		
		public ValItem(Object v,long dt)
		{
			this.val = v ;
			this.dt = dt ;
		}

		@Override
		public JSONObject toJO()
		{
			return new JSONObject().put("dt", dt).putOpt("v", val);
		}
		
		public static final String JARR_SAMPLE = "[{dt:134344543534,v:100.0},{dt:134344543834,v:200.0}]";
	}
	
	
	private boolean usingSource = true;

	private String sourceName = null;

	private String url = "http://localhost:8086";

	private String token = null;

	private String org = null;

	private String bucket = null;

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
		if (this.usingSource)
		{
			if (Convert.isNullOrEmpty(this.sourceName))
			{
				failedr.append("no source name");
				return false;
			}
			SourceInfluxDB sor = StoreManager.getSourceInfluxDB(this.sourceName);
			if (sor == null)
			{
				failedr.append("no source found with name=" + this.sourceName);
				return false;
			}
			if (!sor.checkValid(failedr))
				return false;
		}
		else
		{
			if (Convert.isNullOrEmpty(this.url))
			{
				failedr.append("no url str");
				return false;
			}
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("using_sor", this.usingSource);
		jo.putOpt("sor_name", this.sourceName);
		jo.putOpt("url", this.url);
		jo.putOpt("token", token);
		jo.putOpt("org", this.org);
		jo.putOpt("bucket", this.bucket);
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.usingSource = jo.optBoolean("using_sor", true);
		this.sourceName = jo.optString("sor_name");
		this.url = jo.optString("url");
		this.token = jo.optString("token");
		this.org = jo.optString("org");
		this.bucket = jo.optString("bucket");

		RT_close();
	}

	public boolean isUsingSource()
	{
		return this.usingSource;
	}

	public String getSourceName()
	{
		return this.sourceName;
	}

	public String getInfluxUrl()
	{
		return url;
	}

	public String getInfluxToken()
	{
		return this.token;
	}

	public String getInfluxOrg()
	{
		return this.org;
	}

	public String getInfluxBucket()
	{
		return this.bucket;
	}

	// rt

	InfluxDBClient rtClient = null;

	// private boolean RT_init(StringBuilder failedr)
	// {
	// rtClient = InfluxDBClientFactory.create(this.url,
	// this.token.toCharArray(),org,bucket);
	//
	// return false;
	// }

	synchronized void RT_close()
	{
		if (rtClient != null)
		{
			rtClient.close();
			rtClient = null;
		}
	}

	public synchronized InfluxDBClient RT_getClient()
	{
		if (rtClient != null)
			return rtClient;

		if (this.usingSource)
		{
			if (Convert.isNullOrEmpty(this.sourceName))
				return null;
			SourceInfluxDB sor = StoreManager.getSourceInfluxDB(this.sourceName);
			if (sor == null)
				return null;
			StringBuilder failedr = new StringBuilder();
			if (!sor.checkValid(failedr))
				return null;
			rtClient = InfluxDBClientFactory.create(sor.getUrl(), sor.getToken().toCharArray(), sor.getOrg(),
					sor.getBucket());
		}
		else
		{
			rtClient = InfluxDBClientFactory.create(this.url, this.token.toCharArray(), org, bucket);
		}
		return rtClient;
	}
	
	public String getBucket()
	{
		if (this.usingSource)
		{
			if (Convert.isNullOrEmpty(this.sourceName))
				return null;
			SourceInfluxDB sor = StoreManager.getSourceInfluxDB(this.sourceName);
			if (sor == null)
				return null;
			return sor.getBucket() ;
		}
		else
			return this.bucket ;
	}

	public List<String> listBucketMeasurements()
	{
		InfluxDBClient client = RT_getClient();
		if(client==null)
			return null ;
		
		String bck = this.getBucket();
		
		String fluxQuery = "import \"influxdata/influxdb/schema\"\r\n" + "	            schema.measurements(bucket: \""
				+ bck + "\")";

		try
		{
			QueryApi queryApi = client.getQueryApi();
			List<FluxTable> tables = queryApi.query(fluxQuery);
	
			List<String> measurements = new ArrayList<>();
			for (FluxTable table : tables)
			{
				for (FluxRecord record : table.getRecords())
				{
					String measurement = record.getValueByKey("_value").toString();
					measurements.add(measurement);
				}
			}
			RT_DEBUG_WARN.clear("list_ms"); ;
			return measurements;
		}
		catch(Exception ee)
		{
			RT_DEBUG_WARN.fire("list_ms", "list measurements err", ee);
			return null ;
		}
	}

	private boolean bRun = false;

	private Thread procTh = null;

	private Runnable runner = new Runnable() {
		public void run()
		{
			try
			{
				while (bRun)
				{
					UTIL_sleep(10);

					if (!RT_monWriter())
						break;
				}
			}
			finally
			{
				synchronized (this)
				{
					procTh = null;
					bRun = false;
				}
			}
		}
	};

	private boolean RT_monWriter()
	{
		List<MNNode> ns = this.getRelatedNodes();
		if (ns == null)
			return false; //

		boolean b_has_w = false;
		for (MNNode n : ns)
		{
			if (n instanceof InfluxDB_Writer)
			{
				boolean bv = ((InfluxDB_Writer) n).onMonByModule();
				if (bv)
					b_has_w = true;
			}

			if (n instanceof InfluxDB_Measurement)
			{
				boolean bv = ((InfluxDB_Measurement) n).onMonByModule();
				if (bv)
					b_has_w = true;
			}
		}
		return b_has_w;// 还在写动作中
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
	 * 
	 * @return
	 */
	public boolean RT_runnerEnabled()
	{
		return true;
	}

	/**
	 * true will not support manual trigger to start
	 * 
	 * @return
	 */
	public boolean RT_runnerStartInner()
	{
		return false;
	}
	
	// - query
	
	public static enum InterpolateWay
	{
		linear, //using linear 
		before_val //using before value
	}
	

	private ValItem[] queBeforeAfterValAt(String measurement,String tagpath,long at_dt)
	{
		if(Convert.isNullOrEmpty(measurement))
			return null ;
		InfluxDBClient dbc = this.RT_getClient();
		if (dbc == null)
			return null;

		String s_at_dt = Convert.toUTCFormat(new Date(at_dt));
		
		String flux_vars = "bkt = \"" + this.bucket + "\"\r\n" + "m = \"" + measurement + "\" \r\n" + "at_dt = " + s_at_dt
				+ "\r\nf = \"" + tagpath + "\"\r\n";
		String flux = flux_vars + BEFORE_AFTER_AT ;
		QueryApi qapi = dbc.getQueryApi();
		List<FluxTable> fts = qapi.query(flux);
		ValItem before = null ;
		ValItem after = null ;
		
		if (fts.size() <= 0)
			return null;
		for(FluxTable tb:fts)
		{
			List<FluxRecord> frs = tb.getRecords();
			if (frs.size() <= 0)
				continue;
			for(FluxRecord fr:frs)
			{
				Object sor = fr.getValueByKey("position") ;
				long dt = fr.getTime().toEpochMilli() ;
				Object val = fr.getValue();
				if("before".equals(sor))
					before =new ValItem(val,dt) ;
				else if("after".equals(sor))
					after = new ValItem(val,dt) ;
			}
		}
		
		if(before==null&&after==null)
			return null ;
		
		return new ValItem[] {before,after} ;
	}
	
	public Object queValLast(String measurement,String tagpath)
	{
		ValItem[] ba = queBeforeAfterValAt(measurement,tagpath,System.currentTimeMillis()) ;
		if(ba==null || ba[0]==null)
			return null ;
		return ba[0].val ;
	}
	
	public Object queValAt(String measurement,String tagpath,long at_dt,InterpolateWay iway)
	{
		ValItem[] ns = queBeforeAfterValAt(measurement,tagpath,at_dt);
		if(ns==null)
			return null ;
		if(ns[0]==null && ns[1]==null)
			return null;//ns[1].val ;
		if(ns[0].dt==at_dt)
			return ns[0].val;
		else if(ns[1].dt==at_dt)
			return ns[1].val ;
		if(ns[1].dt==ns[0].dt)
			return ns[0].val ;
		
		if(iway==InterpolateWay.linear)
		{
			double av = ((Number)ns[1].val).doubleValue() ;
			long at = ns[1].dt ;
			double bv = ((Number)ns[0].val).doubleValue() ;
			long bt = ns[0].dt ;
			double a = (av-bv)/(at-bt) ;
			double b = av-a*at ; 
			return a*at_dt+b ;
		}
		else if(iway==InterpolateWay.before_val)
		{
			return ns[0].val ;
		}
		
		return null ;
	}
	
	public List<ValItem> queValAtMulti(String measurement,String tagpath,List<Long> at_dts,InterpolateWay iway)
	{
		ArrayList<ValItem> ret = new ArrayList<>() ;
		for(Long dt:at_dts)
		{
			Object v = queValAt(measurement,tagpath,dt,iway);
			if(v==null)
				continue ;
			ret.add(new ValItem(v,dt)) ;
		}
		return ret ;
	}
	
	
	private static final String BEFORE_AFTER_AT = 
			"// 1. locate last before target_time\r\n" + 
			"before = from(bucket: bkt)\r\n" + 
			"  |> range(start: 0, stop: at_dt) \r\n" + 
			"  |> filter(fn: (r) => r._measurement == m and r._field == f)\r\n" + 
			"  |> last()\r\n" +
			"  |> map(fn: (r) => ({ r with position: \"before\" }))\r\n" + 
			"\r\n" + 
			"// 2. locate first after target_time\r\n" + 
			"after = from(bucket: bkt)\r\n" + 
			"  |> range(start: at_dt, stop: 2099-01-01T00:00:00Z) \r\n" + 
			"  |> filter(fn: (r) => r._measurement == m and r._field == f)\r\n" + 
			"  |> first()\r\n" + 
			"  |> map(fn: (r) => ({ r with position: \"after\" }))\r\n"+
			"\r\n" + 
			"union(tables: [before, after])" ;
}
