package org.iottree.driver.common.modbus;

import org.iottree.core.util.xmldata.*;

//@XRmi(reg_name = "msgcmd_clientinfo")
public class ModbusConnInfo implements IXmlDataable
{
	private String clientIP = null ;
	private int port = -1 ;
	
	String devId = null ;
	String devType = null ;
	
	public ModbusConnInfo()
	{}
	
	public ModbusConnInfo(String addrip,int p,String devid)
	{
		clientIP = addrip ;
		port = p ;
		devId = devid ;
	}
	
	public String getClientIPAddr()
	{
		return clientIP ;
	}
	
	public int getClientPort()
	{
		return port ;
	}
	
	public String getDevId()
	{
		if(devId==null)
			return "" ;
		
		return devId;
	}
	
	public String getDevType()
	{
		if(devType==null)
			return "" ;
		
		return devType ;
	}
	
	public String toString()
	{
		return "["+devId+"#"+devType+"] "+clientIP+":"+port;
	}

	public XmlData toXmlData()
	{
		XmlData xd = new XmlData();
		
		xd.setParamValue("client_ip", clientIP);
		xd.setParamValue("client_port",port);
		if(devId!=null)
			xd.setParamValue("dev_id", devId);
		if(devType!=null)
			xd.setParamValue("dev_type",devType) ;
		
		return xd;
	}

	public void fromXmlData(XmlData xd)
	{
		clientIP = xd.getParamValueStr("client_ip");
		port = xd.getParamValueInt32("client_port",-1);
		devId = xd.getParamValueStr("dev_id");
		devType = xd.getParamValueStr("dev_type") ;
	}
}
