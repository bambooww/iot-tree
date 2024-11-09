package org.iottree.core.msgnet.store;

import org.iottree.core.msgnet.store.influxdb.InfluxDB_M;
import org.w3c.dom.Element;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

public class StoreSorInfluxDB extends StoreSor
{
	public static final String TP="influxdb" ;
	
	String url ;
	
	String token ;
	
	String org ;
	
	String bucket ;
	
	InfluxDB_M dbM = null ;
			
	
	public StoreSorInfluxDB(String name,InfluxDB_M dbm)
	{
		super(name) ;
		this.dbM = dbm ;
	}

	@Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPTitle()
	{
		return "InfluxDB";
	}

	@Override
	protected boolean fromEle(Element ele)
	{
		if(!super.fromEle(ele))
			return false;

		url=ele.getAttribute("url");
		token=ele.getAttribute("token");
		org=ele.getAttribute("org");
		bucket = ele.getAttribute("bucket") ;
		
		return true ;
	}
	
	// RT
	
	InfluxDBClient rtClient = null ;
	
	public synchronized InfluxDBClient RT_getClient()
	{
//		if(rtClient!=null)
//			return rtClient ;
//		
//		rtClient =  InfluxDBClientFactory.create(this.url,
//				this.token.toCharArray(),org,bucket);
//		return rtClient ;
		
		return dbM.RT_getClient() ;
	}
	
//	synchronized void RT_close()
//	{
//		if(rtClient!=null)
//		{
//			rtClient.close();
//			rtClient=  null ;
//		}
//	}
}
