package org.iottree.core.conn.masyn;

import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;

//@XRmi(reg_name = "msgcmd_clientinfo")
public class MCmdAsynClientInfo implements IXmlDataable
{
	private String clientIP = null ;
	private int port = -1 ;
	
	private String id = null ;
	
	/**
	 * �Ựid
	 */
	String sessionId = null ;
	//String devType = null ;
	
	public MCmdAsynClientInfo()
	{}
	
	public MCmdAsynClientInfo(String addrip,int p,String id)
	{
		clientIP = addrip ;
		port = p ;
		//devId = devid ;
		setIdSession(id) ;
	}
	
	
	
	public String getClientIPAddr()
	{
		return clientIP ;
	}
	
	public int getClientPort()
	{
		return port ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	/**
	 * ����id�Ự�����ӽ���id���á�
	 * @param id_sess
	 * @return ����Ψһid����������֧�ֶԾ����ӵĹرղ���
	 */
	public String setIdSession(String id_sess)
	{// xxxx$sessionid22
		if(id_sess==null)
			return null;
		int i = id_sess.indexOf('$') ;
		if(i>0)
		{
			id = id_sess.substring(0,i) ;
			sessionId = id_sess.substring(i+1) ;
		}
		else
		{
			id = id_sess ;
			sessionId = null ;
		}
		return id ;
	}
	
	public String getSessionId()
	{
		return sessionId ;
	}
//	public String getDevId()
//	{
//		if(devId==null)
//			return "" ;
//		
//		return devId;
//	}
//	
//	public String getDevType()
//	{
//		if(devType==null)
//			return "" ;
//		
//		return devType ;
//	}
	
	public String toString()
	{
		return id+"@["+clientIP+":"+port+"]";
	}

	public XmlData toXmlData()
	{
		XmlData xd = new XmlData();
		
		xd.setParamValue("client_ip", clientIP);
		xd.setParamValue("client_port",port);
//		if(devId!=null)
//			xd.setParamValue("dev_id", devId);
//		if(devType!=null)
//			xd.setParamValue("dev_type",devType) ;
		
		return xd;
	}

	public void fromXmlData(XmlData xd)
	{
		clientIP = xd.getParamValueStr("client_ip");
		port = xd.getParamValueInt32("client_port",-1);
//		devId = xd.getParamValueStr("dev_id");
//		devType = xd.getParamValueStr("dev_type") ;
	}
}
