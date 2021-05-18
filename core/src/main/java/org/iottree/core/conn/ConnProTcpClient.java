package org.iottree.core.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.ConnProvider;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.IXmlDataValidator;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;


public class ConnProTcpClient extends ConnProvider
{
	public ConnProTcpClient()
	{
	
	}

	@Override
	public String getProviderType()
	{
		return "tcp_client" ;
	}
	
	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtTcpClient.class ;
	}
	
	public String getName()
	{
		String n = super.getName() ;
		if(Convert.isNotNullEmpty(n))
			return n;
		return "" ;
	}
	
	public String getTitle()
	{
		String n = super.getTitle() ;
		if(Convert.isNotNullEmpty(n))
			return n;
		return "Tcp Client" ;
	}
	
	public boolean fromXmlData(XmlData xd,StringBuilder errsb)
	{
		if(!super.fromXmlData(xd,errsb))
			return false;
		
		return true ;
	}

	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData() ;
		
		return xd ;
	}
	

//	protected void injectByJson(JSONObject jo) throws Exception
//	{
//		super.injectByJson(jo);
//		
//		JSONArray jos = jo.getJSONArray("clients") ;
//		ArrayList<ClientItem> cis = new ArrayList<>() ;
//		if(jos!=null)
//		{
//			int s = jos.size() ;
//			for(int i = 0 ; i < s ; i ++)
//			{
//				JSONObject clientjo = jos.getJSONObject(i);
//				ClientItem ci = new ClientItem(clientjo) ;
//				cis.add(ci) ;
//			}
//		}
//		this.clientItems = cis ;
//	}


	
	public void disconnAll() //throws IOException
	{
		for(ConnPt ci:this.listConns())
		{
			try
			{
				ConnPtTcpClient conn = (ConnPtTcpClient)ci ;
				conn.disconnect();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	public void stop()
	{
		super.stop() ;
		
		disconnAll();
	}
	
	@Override
	protected long connpRunInterval()
	{
		return 500;
	}
	
	
	@Override
	protected void connpRunInLoop() throws Exception
	{
		for(ConnPt ci:this.listConns())
		{
			ConnPtTcpClient citc = (ConnPtTcpClient)ci ;
			citc.checkConn() ;
		}
	}

}
