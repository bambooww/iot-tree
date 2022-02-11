package org.iottree.core.sim;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.data_class;
import org.json.JSONObject;

@data_class
public abstract class SimCP
{
	
	public static SimCP createNewInstance(String tp)
	{
		switch(tp)
		{
		case "tcp_server":
			return new SimCPTcp() ;
		case "tcp_client":
			return null ;
		case "com":
			return new SimCPCom() ;
		default:
			return null ;
		}
	}
	
	SimChannel relatedCh = null ;
	
	public SimCP()
	{
		
	}
	
	public abstract boolean RT_init(StringBuilder failedr);
	
	public SimCP asChannel(SimChannel sc)
	{
		this.relatedCh = sc ;
		return this ;
	}
	
	public SimChannel getRelatedCh()
	{
		return this.relatedCh ;
	}
	
	public abstract String getConnTitle();
	
	public abstract String toConfigStr() ;
	
	
	public abstract boolean fromConfig(JSONObject jo) ;
	
	
	
	public abstract void RT_runInLoop() throws Exception ;
	
	
	public abstract void RT_stop();
	
	public abstract List<SimConn> getConns() ;
	

	public abstract int getConnsNum();
	
	public static SimCP fromConfig(String confstr)
	{
		JSONObject jo = new JSONObject(confstr) ;
		String tp = jo.optString("tp") ;
		if(Convert.isNullOrEmpty(tp))
			return null ;
		SimCP ret = null ;
		switch(tp)
		{
		case "tcp":
			ret = new SimCPTcp() ;
			if(!ret.fromConfig(jo))
				return null ;
			return ret ;
		case "com":
			ret = new SimCPCom() ;
			if(!ret.fromConfig(jo))
				return null ;
			return ret ;
		default:
			return null ;
		}
	}
}
