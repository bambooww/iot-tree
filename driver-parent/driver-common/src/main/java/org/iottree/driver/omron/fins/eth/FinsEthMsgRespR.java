package org.iottree.driver.omron.fins.eth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FinsEthMsgRespR extends FinsEthMsg
{
	int errorCode = -1 ;
	byte[] respVals = null ;
	
	public FinsEthMsgRespR(short tar_clientid, short sor_clientid)
	{
		super(tar_clientid, sor_clientid);
	}

	@Override
	protected short getMRC()
	{
		return 1;
	}

	@Override
	protected short getSRC()
	{
		return 1;
	}

	protected short getICF()
	{
		return 0xC0;
	}

	@Override
	protected int getParamBytesNum()
	{
		return 0;
	}

	@Override
	protected void writeParam(OutputStream outputs) throws IOException
	{
		
	}

	public static FinsEthMsgRespR readFromStream(InputStream inputs,long timeout) throws IOException
	{
		
		//FinsEthMsgRespR ret = new FinsEthMsgRespR() ;
		return null ; //TODO 
	}
}
