package org.iottree.core.sim;

import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.data_class;
import org.json.JSONObject;

@data_class
public abstract class SimConn
{
	
	public static SimConn createNewInstance(String tp)
	{
		switch(tp)
		{
		case "tcp_server":
			return new SimConnTcp() ;
		case "tcp_client":
			return null ;
		case "com":
			return new SimConnCOM() ;
		default:
			return null ;
		}
	}
	
	SimChannel relatedCh = null ;
	
	public SimConn()
	{
		
	}
	
	public boolean RT_init(StringBuilder failedr)
	{
		return true;
	}
	
	public SimConn asChannel(SimChannel sc)
	{
		this.relatedCh = sc ;
		return this ;
	}
	
	public abstract String getConnTitle();
	
	public abstract InputStream getConnInputStream() ;
	
	public abstract OutputStream getConnOutputStream() ;

	
	public abstract String toConfigStr() ;
	
	
	public abstract boolean fromConfig(JSONObject jo) ;
	
	public abstract void startConn() ;
	
	public abstract void stopConn() ;
	
	public abstract boolean isConn() ;
	
	
	public abstract void pulseConn()  throws Exception;
	
	
	
	public static SimConn fromConfig(String confstr)
	{
		JSONObject jo = new JSONObject(confstr) ;
		String tp = jo.optString("tp") ;
		if(Convert.isNullOrEmpty(tp))
			return null ;
		SimConn ret = null ;
		switch(tp)
		{
		case "tcp":
			ret = new SimConnTcp() ;
			if(!ret.fromConfig(jo))
				return null ;
			return ret ;
		case "com":
			ret = new SimConnCOM() ;
			if(!ret.fromConfig(jo))
				return null ;
			return ret ;
		default:
			return null ;
		}
	}
}
