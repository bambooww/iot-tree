package org.iottree.core.conn;

import java.net.DatagramSocket;

/**
 * 原生bytes数据包支持的UDP接入——一般用来支持UDP协议的复杂驱动
 * 
 * @author jason.zhu
 *
 */
public class ConnPtUDPBytesPk extends ConnPtBytesPk
{
	int port = 5001 ;
	
	DatagramSocket dgSock = null ;
	
	@Override
	public String getConnType()
	{
		return "udp_bpk";
	}

	@Override
	public String getStaticTxt()
	{
		return null;
	}

	@Override
	public void RT_checkConn()
	{
		if(dgSock!=null)
			return ;
		
		try
		{
			dgSock = new DatagramSocket(port) ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	public DatagramSocket getUDPSock()
	{
		return this.dgSock ;
	}

	@Override
	public boolean isConnReady()
	{
		return dgSock!=null;
	}

	@Override
	public String getConnErrInfo()
	{
		return null;
	}

}
