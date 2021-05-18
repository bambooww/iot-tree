package org.iottree.core;

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
	
	String chId = null ;
	
	
	public ConnJoin()
	{}
	
	public ConnJoin(String connid,String chid)
	{
		this.connId = connid ;
		this.chId = chid ;
	}
	
	public String getConnId()
	{
		return connId ;
	}
	
	public String getChId()
	{
		return chId ;
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = new XmlData() ;
		xd.setParamValue("conn_id", connId);
		xd.setParamValue("ch_id", this.chId);
		return xd;
	}

	@Override
	public void fromXmlData(XmlData xd)
	{
		this.connId = xd.getParamValueStr("conn_id") ;
		this.chId = xd.getParamValueStr("ch_id") ;
	}
}
