package org.iottree.driver.common;

import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.UAVal.ValTP;

public class HTTPClientJSONAddr extends DevAddr
{
	@Override
	public DevAddr parseAddr(String str, ValTP vtp, StringBuilder failedr)
	{
		return null;
	}

	@Override
	public boolean isSupportGuessAddr()
	{
		return false;
	}

	@Override
	public DevAddr guessAddr(String str)
	{
		return null;
	}

	@Override
	public List<String> listAddrHelpers()
	{
		return null;
	}

	@Override
	public ValTP[] getSupportValTPs()
	{
		return null;
	}

	@Override
	public boolean canRead()
	{
		return true ;
	}

	@Override
	public boolean canWrite()
	{
		return false;
	}

}

