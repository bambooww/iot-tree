package org.iottree.driver.omron.hostlink.fins;

import org.iottree.core.util.IBSOutput;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.hostlink.HLMsgResp;

/**
 * 
 * @author jason.zhu
 *
 */
public class HLFinsReqTcpHandshake extends HLFinsReq
{

	public HLFinsReqTcpHandshake(FinsMode mode)
	{
		super(mode);
	}

	@Override
	protected short getMR()
	{
		return 0;
	}

	@Override
	protected short getSR()
	{
		return 0;
	}

	@Override
	protected void packOutCmdParam(IBSOutput bso)
	{
		
	}

	@Override
	protected HLMsgResp newRespInstance()
	{
		return null;
	}

}
