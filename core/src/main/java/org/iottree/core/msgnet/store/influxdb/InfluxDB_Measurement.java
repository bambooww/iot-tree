package org.iottree.core.msgnet.store.influxdb;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;

public class InfluxDB_Measurement extends MNNodeRes
{
	String measurement = null ;

	int batchWriterBufLen = 100 ;
	
	@Override
	public String getTP()
	{
		return "influxdb_mt";
	}

	@Override
	public String getTPTitle()
	{
		return "Measourement";
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
	public String getPmTitle()
	{
		return measurement;
	}
	
	public String getMeasurement()
	{
		return this.measurement ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(measurement))
		{
			failedr.append("no measurement name") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("mt", this.measurement) ;
		jo.put("batch_w_buflen", this.batchWriterBufLen) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.measurement = jo.optString("mt") ;
		batchWriterBufLen = jo.optInt("batch_w_buflen",100) ;
		if(batchWriterBufLen<=0)
			batchWriterBufLen = 100 ;
	}

	// rt lines
	
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
	
	private transient long lastPtIn = -1 ;
	
	private transient ArrayList<Point> ptBuf = new ArrayList<>() ;
	
	private transient long lastWNum = -1 ;
	
	private transient long lastWCostMS = -1 ;
	
	public void RT_writePoints(List<Point> pts) throws Exception
	{
		if(pts==null)
			return;
		for(Point pt:pts)
			RT_writePoint(pt) ;
	}
	
	public void RT_writePoint(Point pt) throws Exception
	{
		if(pt==null)
			return;
		
		lastPtIn = System.currentTimeMillis() ;
		
		synchronized(this)
		{
			ptBuf.add(pt) ;
		}
		
		InfluxDB_M m = (InfluxDB_M)this.getOwnRelatedModule() ;
		m.RT_start(null) ;//in msg ,start module
		
		if(ptBuf.size()>=this.batchWriterBufLen)
			RT_doWriter() ;
		
		return;
	}
	
	
	private void RT_doWriter()
	{
		ArrayList<Point> pts = ptBuf ;
		
		synchronized(this)
		{
			ptBuf = new ArrayList<>() ;
		}
		
		if(pts==null||pts.size()<=0)
			return ;
		
		InfluxDB_M dbm = (InfluxDB_M)this.getOwnRelatedModule() ;
		InfluxDBClient client = dbm.RT_getClient() ;
		WriteApiBlocking wapi = client.getWriteApiBlocking() ;
		long st = System.currentTimeMillis() ;
		wapi.writePoints(pts);
		lastWCostMS = System.currentTimeMillis()-st ;
		this.lastWNum = pts.size() ;
	}
	
	/**
	 * called by module,no msg in after 1s -- will write left points in buffer
	 * @return
	 */
	boolean onMonByModule()
	{
		if(System.currentTimeMillis()-lastPtIn<1000)
			return true ;
		
		if(ptBuf.size()<=0)
			return false; //may couse module stop
		
		try
		{
			RT_doWriter() ;
			return true ;
		}
		catch(Exception ee)
		{
			RT_DEBUG_WARN.fire("influx_mt", "RT_doWriter error", ee);
			return false;
		}
	}
}
