package org.iottree.core;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;

/**
 * Join between ConnPt and channel
 * @author jason.zhu
 *
 */
public class ConnJoin implements IXmlDataable
{
	String connId = null ;
	
	/**
	 * chid
	 * or chid-devid
	 */
	String nodeId = null ;
	
	private transient String chId,devId ;

	public ConnJoin()
	{}
	
	public ConnJoin(String connid,String nodeid)
	{
		this.connId = connid ;
		this.nodeId = nodeid ;
		//this.devId = devid ; //
		onNodeIdSet();
	}
	
	private void onNodeIdSet()
	{
		if(Convert.isNullOrEmpty(this.nodeId))
			return ;
	
		int k = nodeId.indexOf('-') ;
		if(k>0)
		{
			this.chId = nodeId.substring(0,k) ;
			this.devId = nodeId.substring(k+1) ;
		}
		else
		{
			this.chId = nodeId ;
			this.devId = null;
		}
	}
	
	public String getConnId()
	{
		return connId ;
	}
	
	public String getNodeId()
	{
		return this.nodeId ;
	}
	
	
	public String getNodeIdFull()
	{
		if(Convert.isNotNullEmpty(this.devId))
			return "dev_"+this.nodeId ;
		else
			return "ch_"+this.nodeId ;
	}
	
	
	public String getRelatedChId()
	{
		return chId ;
	}
	
	public String getRelatedDevId()
	{
		return this.devId ;
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = new XmlData() ;
		xd.setParamValue("conn_id", connId);
		xd.setParamValue("node_id", this.nodeId);
		//if(this.devId!=null)
		//	xd.setParamValue("dev_id", this.devId);
		return xd;
	}

	@Override
	public void fromXmlData(XmlData xd)
	{
		this.connId = xd.getParamValueStr("conn_id") ;
		this.nodeId = xd.getParamValueStr("node_id") ;
		if(Convert.isNullOrEmpty(this.nodeId))
			this.nodeId = xd.getParamValueStr("ch_id") ;
		
		onNodeIdSet();
		//this.devId = xd.getParamValueStr("dev_id") ;
	}
}
