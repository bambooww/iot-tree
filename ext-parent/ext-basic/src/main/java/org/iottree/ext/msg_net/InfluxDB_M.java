package org.iottree.ext.msg_net;

import java.util.Arrays;
import java.util.List;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

public class InfluxDB_M extends MNModule // implements IMNRunner
{
	private static InfluxDB_Writer SUP_W = new InfluxDB_Writer() ;
	private static InfluxDB_Tag2Point SUP_TAG2PT = new InfluxDB_Tag2Point() ;
	private static InfluxDB_JO2Point SUP_JO2PT = new InfluxDB_JO2Point() ;
	
	private static List<MNNode> SUPS = Arrays.asList(SUP_W,SUP_TAG2PT,SUP_JO2PT) ;
	
	String url = "http://localhost:8086" ;
	
	String token = null ;
	
	String org = null ;
	
	String bucket = null ;

	@Override
	protected List<MNNode> getSupportedNodes()
	{
		return SUPS;
	}

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
		if(Convert.isNullOrEmpty(this.url))
		{
			failedr.append("no url str") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("url", this.url) ;
		jo.putOpt("token", token) ;
		jo.putOpt("org", this.org) ;
		jo.putOpt("bucket", this.bucket) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.url = jo.optString("url") ;
		this.token = jo.optString("token") ;
		this.org = jo.optString("org") ;
		this.bucket = jo.optString("bucket") ;
		
		RT_close();
	}

	// rt
	
	InfluxDBClient rtClient = null ;
	
	private boolean RT_init(StringBuilder failedr)
	{
		rtClient =  InfluxDBClientFactory.create(this.url,
				this.token.toCharArray(),org,bucket);
		
		return false;
	}
	
	synchronized void RT_close()
	{
		if(rtClient!=null)
		{
			rtClient.close();
			rtClient=  null ;
		}
	}
	
	synchronized InfluxDBClient RT_getClient()
	{
		if(rtClient!=null)
			return rtClient ;
		
		rtClient =  InfluxDBClientFactory.create(this.url,
				this.token.toCharArray(),org,bucket);
		return rtClient ;
	}
}
