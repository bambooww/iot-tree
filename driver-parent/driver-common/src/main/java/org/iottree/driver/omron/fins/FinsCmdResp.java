package org.iottree.driver.omron.fins;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.util.IBSOutput;

public class FinsCmdResp extends FinsCmd
{
	int errorCode = -1 ;
	byte[] respVals = null ;
	
	public FinsCmdResp(FinsMode fins_mode)
	{
		super(fins_mode) ;
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
	protected void writeParam(IBSOutput outputs) // throws IOException
	{
		
	}

	public static FinsCmdResp readFromStream(InputStream inputs,long timeout) throws IOException
	{
		
		//FinsEthMsgRespR ret = new FinsEthMsgRespR() ;
		return null ; //TODO 
	}
}
