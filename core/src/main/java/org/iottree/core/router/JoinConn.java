package org.iottree.core.router;

import javax.script.ScriptException;

import org.iottree.core.cxt.UACodeItem;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class JoinConn
{
	RouterManager rmgr = null ;
	
	JoinOut fromJO = null ;
	
	JoinIn toJI = null ;
	
	String fromId = null; 
	
	
	String toId = null ;
	
	String transJS = null ;
	
	private UACodeItem codeItem = null ;
	
	JoinConn(RouterManager rm,JoinOut fromjo,JoinIn toji)
	{
		this.rmgr = rm ;
		this.fromJO = fromjo ;
		this.toJI = toji ;
	}
	
	public JoinConn(RouterManager rm,JoinOut fromjo,JoinIn toji,String fromid,String toid)
	{
		this.rmgr = rm ;
		this.fromJO = fromjo ;
		this.toJI = toji ;
		this.fromId = fromid ;
		this.toId = toid ;
	}
	
	public synchronized JoinConn asTransJS(String js)
	{
		if(js!=null)
			this.transJS = js.trim() ;
		else
			this.transJS = null ;
		this.codeItem = null ;
		return this ;
	}
	
	public JoinOut getFromJO()
	{
		return this.fromJO ;
	}
	
	public String getFromId()
	{
		return fromId ;
	}
	
	public JoinIn getToJI()
	{
		return this.toJI ;
	}
	
	public String getToId()
	{
		return toId ;
	}
	
	public String getKey()
	{
		return fromId+","+toId ;
	}
	
	public static String calKey(String fromid,String toid)
	{
		return fromid+","+toid;
	}
	
	public String getTransJS()
	{
		return transJS ;
	}
	
	private UAContext getCxt() throws ScriptException
	{
		RouterNode rn = this.fromJO.getBelongNode() ;
		if(rn instanceof RouterInnCollator)
		{
			return ((RouterInnCollator)rn).RT_getContext() ;
		}
		rn = this.toJI.getBelongNode() ;
		if(rn instanceof RouterInnCollator)
		{
			return ((RouterInnCollator)rn).RT_getContext() ;
		}
		return null ;
	}
	
	private synchronized UACodeItem getCodeItem() throws ScriptException
	{
		if(Convert.isNullOrTrimEmpty(transJS))
			return null ;
		
		this.codeItem = new UACodeItem("","{\r\n"+transJS+"\r\n}") ;
		this.codeItem.initItem(getCxt(), "$input") ;
		return this.codeItem ;
	}
	
	private Exception rt_transErr = null ;
	
	public String RT_doTrans(String input)// throws Exception 
	{
		try
		{
			UACodeItem ci = getCodeItem() ;
			if(ci==null)
				return input ;
			
			Object jo = ci.runCodeFunc(input) ;
			rt_transErr = null ;
			return jo.toString() ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			rt_transErr = ee ;
			return null ;
		}
	}
	
	public Exception RT_getTransErr()
	{
		return this.rt_transErr ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("fid",this.fromId);
		jo.put("tid",this.toId);
		jo.putOpt("js", transJS) ;
		return jo ;
	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("fid",this.fromId);
		jo.put("tid",this.toId);
		jo.put("has_js", Convert.isNotNullTrimEmpty(transJS)) ;
		return jo ;
	}
	
	public static JoinConn RIC_fromJO(RouterManager rm,JSONObject jo)
	{
		String fid = jo.getString("fid") ;
		String tid = jo.getString("tid") ;
		
		if(Convert.isNullOrEmpty(fid))
			return null;
		if(Convert.isNullOrEmpty(tid))
			return null;
		
		JoinOut jjo = rm.CONN_RIC_getJoinOutByFromId(fid) ;
		if(jjo==null)
			return null;
		
		JoinIn ji = rm.CONN_ROA_getJoinInByToId(tid) ;
		if(ji==null)
			return null;
		
		JoinConn ret = new JoinConn(rm,jjo,ji,fid,tid) ;
		ret.transJS = jo.optString("js") ;
		return ret ;
	}
	
	public static JoinConn ROA_fromJO(RouterManager rm,JSONObject jo)
	{
		String fid = jo.getString("fid") ;
		String tid = jo.getString("tid") ;
		
		if(Convert.isNullOrEmpty(fid))
			return null;
		if(Convert.isNullOrEmpty(tid))
			return null;
		
		JoinOut jjo = rm.CONN_ROA_getJoinOutByFromId(fid) ;
		if(jjo==null)
			return null;
		
		JoinIn ji = rm.CONN_RIC_getJoinInByToId(tid) ;
		if(ji==null)
			return null;
		
		JoinConn ret = new JoinConn(rm,jjo,ji,fid,tid) ;
		ret.transJS = jo.optString("js") ;
		return ret ;
	}
}
