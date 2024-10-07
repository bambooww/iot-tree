package org.iottree.core.conn;

import java.io.File;

import org.iottree.core.UATag;

public class ConnPtMSGMultiTcp extends ConnPtMSG
{

	@Override
	public boolean sendMsg(String topic, byte[] bs) throws Exception
	{
		return false;
	}

	@Override
	public void runOnWrite(UATag tag, Object val) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPassiveRecv()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean readMsgToFile(File f) throws Exception
	{
		return false;
	}

	@Override
	public String getConnType()
	{
		return null;
	}

	@Override
	public String getStaticTxt()
	{
		return null;
	}

	@Override
	public void RT_checkConn()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConnReady()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getConnErrInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
