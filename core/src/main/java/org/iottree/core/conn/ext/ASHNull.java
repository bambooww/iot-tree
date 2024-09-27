package org.iottree.core.conn.ext;

import java.net.Socket;
import java.util.HashMap;

import org.iottree.core.basic.NameTitleVal;
import org.iottree.core.conn.ConnProTcpServer;
import org.iottree.core.util.xmldata.XmlData;

public class ASHNull implements ConnProTcpServer.AcceptedSockHandler
{

	@Override
	public String getName()
	{
		return "null";
	}
	
	public String getTitle()
	{
		return "Null" ;
	}

	@Override
	public String checkSockConnId(Socket sock) throws Exception
	{
		return null ;
	}

	@Override
	public int getRecvTimeout()
	{
		return 0;
	}

	@Override
	public int getRecvEndTimeout()
	{
		return 0;
	}

	@Override
	public NameTitleVal[] getParamDefs()
	{
		return null;//need no param
	}
	

	@Override
	public XmlData chkAndCreateParams(HashMap<String, String> pn2strv, StringBuilder failedr)
	{
		return new XmlData(); //return empty
	}

	@Override
	public void setParams(XmlData xd)
	{
		//do nothing
	}

}
