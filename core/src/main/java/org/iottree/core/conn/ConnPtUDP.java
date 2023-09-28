package org.iottree.core.conn;

import java.io.File;

import org.iottree.core.UATag;

public class ConnPtUDP extends ConnPtMSG
{

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
	
	public void RT_checkConn()
	{}

	@Override
	public String getConnErrInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
