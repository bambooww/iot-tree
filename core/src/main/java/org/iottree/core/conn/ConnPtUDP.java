package org.iottree.core.conn;

import java.io.File;
import java.net.DatagramSocket;

import org.iottree.core.UATag;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

public class ConnPtUDP extends ConnPtMSG
{
	static ILogger log = LoggerManager.getLogger(ConnPtUDP.class) ;
	
	DatagramSocket dgSock = null ;
	
	@Override
	protected void RT_connInit() throws Exception
	{
		super.RT_connInit();
		
		
	}
	
	@Override
	public boolean sendMsg(String topic, byte[] bs) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void runOnWrite(UATag tag, Object val) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean readMsgToFile(File f) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getConnType()
	{
		// TODO Auto-generated method stub
		return null;
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
	
	@Override
	public boolean isPassiveRecv() 
	{
		return true;
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
