package org.iottree.core.node;

import java.util.HashMap;

import org.iottree.core.conn.mqtt.MqttEndPoint;
import org.iottree.core.node.NodeMsg.MsgTp;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

public abstract class PrjNode implements IXmlDataable
{
	
	String prjId = null ;
	
	boolean bEnable = true;
	
	XmlData paramXD = null ;
	
	PrjNodeAdapter nodeAdp = null ;
	
	public PrjNode()
	{
		
	}
	
	public PrjNode withPrjId(String prjid,boolean ben)
	{
		this.prjId = prjid ;
		this.bEnable = ben ;
		return this;
	}
	
	public PrjNode withParam(XmlData pms)
	{
		paramXD = pms ;
		return this;
	}
	
	public PrjNode withAdapter(PrjNodeAdapter adp)
	{
		this.nodeAdp = adp ;
		return this;
	}
	
	public boolean isEnable()
	{
		return bEnable;
	}
	
	public XmlData getParamXD()
	{
		return paramXD ;
	}
	
	public abstract boolean isValid();
	
	/**
	 * before run,init must be called
	 */
	public abstract void init() ;
	
	public final void sendMsg(String tarprjid,MsgTp mt,byte[] msg) throws Exception
	{
		NodeMsg nm = new NodeMsg() ;
		nm.sorId = this.prjId ;
		nm.tarId = tarprjid;
		nm.content = msg ;
		nm.msgTp = mt ;
		sendMsg(nm);
	}
	
	protected abstract void sendMsg(NodeMsg nm) throws Exception;
	
	
	protected abstract NodeMsg parseNodeMsg(String topic,byte[] msg) ;
	
	
	
	public String getPrjId()
	{
		return prjId;
	}
	
	
	public XmlData toXmlData()
	{
		XmlData xd = new XmlData() ;
		xd.setParamValue("prjid", prjId);
		xd.setParamValue("enable", bEnable);
		if(paramXD!=null)
			xd.setSubDataSingle("params", paramXD);
		return xd ;
	}
	
	public void fromXmlData(XmlData xd)
	{
		this.prjId = xd.getParamValueStr("prjid") ;
		this.bEnable = xd.getParamValueBool("enable", true) ;
		this.paramXD = xd.getSubDataSingle("params") ;
	}
	
	public void fromJSON(JSONObject jo) throws Exception
	{
		this.prjId = jo.optString("prjid") ;
		this.bEnable = jo.optBoolean("enable", true) ;
		XmlData xd = transParamsJSON2Xml(jo);
		if(xd!=null)
			this.paramXD = xd ;
	}
	
	public abstract XmlData transParamsJSON2Xml(JSONObject jo) throws Exception;
	
	
	protected final void onRecvedMsg(NodeMsg msg) throws Exception
	{
		//if(!msg.tarId.equals(this.prjId))
		//	return ;
		//System.out.println("node onRecvedMsg "+msg);
		try
		{
			switch(msg.msgTp)
			{
			case req:
				SW_sharerOnReq(msg.sorId,msg.content);
				break;
			case resp:
				SW_callerOnResp(msg.sorId,msg.content) ;
				break;
			case push:
				SW_callerOnPush(msg.content) ;
				
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void SW_callerSendReq(String shareprjid,byte[] cont) throws Exception
	{
		sendMsg(shareprjid,MsgTp.req,cont) ;
	}
	
	protected void SW_callerOnResp(String shareprjid,byte[] cont) throws Exception
	{
		if(nodeAdp!=null)
			nodeAdp.SW_callerOnResp(shareprjid, cont);
	}
	
	protected void SW_sharerOnReq(String callerprjid,byte[] cont) throws Exception
	{
		if(nodeAdp!=null)
			nodeAdp.SW_sharerOnReq(callerprjid, cont);
	}
	
	protected void SW_callerOnPush(byte[] cont) throws Exception
	{
		if(nodeAdp!=null)
			nodeAdp.SW_callerOnPush(cont);
	}
}
