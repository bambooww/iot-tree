package org.iottree.core.node;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.node.NodeMsg.MsgTp;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

/**
 * project share as a node,it will use 
 * @author jason.zhu
 *
 */
public abstract class PrjSharer extends PrjNode
{
	static ILogger log = LoggerManager.getLogger(PrjSharer.class) ;
	
	private transient long lastR = -1 ;
	
	private long pushInterval = 10000 ;
//	public abstract void start() ;
//	
//	public abstract void stop() ;
//	
//	public abstract boolean isRunning() ;
	
	public void runInLoop()
	{
		if(System.currentTimeMillis()-lastR<pushInterval)
			return ;
		
		try
		{
			pushRtData();
		}
		catch(Exception e)
		{
			log.info(e.getMessage());
			
			if(log.isDebugEnabled())
				log.error(e);
		}
		finally
		{
			lastR = System.currentTimeMillis();
		}
		
	}
	
	private void pushRtData() throws Exception
	{
		UAPrj p = UAManager.getInstance().getPrjById(this.getPrjId()) ;
		if(p==null)
			return ;
		StringWriter sw = new StringWriter() ;
		
		p.CXT_renderJson(sw);
		byte[] bs = sw.toString().getBytes("UTF-8") ;
		
		this.sendMsg(null, NodeMsg.MsgTp.push, bs);
		//this.sendMsg(nm);
	}
	
	protected void SW_sharerOnReq(String callerprjid,byte[] cont) throws Exception
	{
		//System.out.println("SW_sharerOnReq ") ;
		
		UAPrj p = UAManager.getInstance().getPrjById(this.getPrjId()) ;
		if(p==null)
			return ;
		
		XmlData xd = DataTranserXml.extractXmlDataFromObj(p) ;
		this.sendMsg(callerprjid, MsgTp.resp, xd.toBytesWithUTF8());
	}
	
	public abstract void runStop();
	
	
	public abstract boolean isRunning() ;
	
	
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData() ;
		xd.setParamValue("push_int", this.pushInterval);
		return xd ;
	}
	
	public void fromXmlData(XmlData xd)
	{
		super.fromXmlData(xd);
		this.pushInterval = xd.getParamValueInt64("push_int", 10000) ;
	}
	
	public void fromJSON(JSONObject jo) throws Exception
	{
		super.fromJSON(jo);
		this.pushInterval = jo.optLong("push_int",10000);
	}
	
	public XmlData transParamsJSON2Xml(JSONObject jo) throws Exception
	{
		return null;
	}
}
