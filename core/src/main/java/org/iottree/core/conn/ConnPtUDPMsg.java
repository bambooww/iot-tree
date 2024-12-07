package org.iottree.core.conn;

import java.net.DatagramSocket;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

public class ConnPtUDPMsg extends ConnPtMsg
{
	static ILogger log = LoggerManager.getLogger(ConnPtUDPMsg.class) ;
	
	DatagramSocket dgSock = null ;
	
	@Override
	protected void RT_connInit() throws Exception
	{
		super.RT_connInit();
		
		
	}
	

	@Override
	public String getConnType()
	{
		return "udp_msg";
	}

	@Override
	public String getStaticTxt()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnReady()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	private long lastChk = -1 ;
	
	public void RT_checkConn()
	{
		if(System.currentTimeMillis()-lastChk<5000)
			return ;
		
		try
		{
			//connectToWS();
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
				log.debug("connect to websocket",e);
		}
		finally
		{
			lastChk = System.currentTimeMillis() ;
		}
	}

	@Override
	public String getConnErrInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
