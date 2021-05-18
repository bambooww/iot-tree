package org.iottree.core;

import java.util.List;

import org.iottree.core.UAVal.ValTP;

/**
 * simple fix address
 *  
 * @author zzj
 *
 */
public class DevAddrFix extends DevAddr
{
	
	public DevAddrFix()
	{}
	
	DevAddrFix(String addr,ValTP vtp)
	{
		super(addr,vtp) ;
	}
	
	
	@Override
	public DevAddr parseAddr(String addr, ValTP vtp, StringBuilder failedr)
	{
		return new DevAddrFix(addr,vtp);
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


//	@Override
//	public int getRegPos()
//	{
//		return -1;
//	}
//
//	@Override
//	public int getBitPos()
//	{
//		return -1;
//	}

	@Override
	public boolean canRead()
	{
		return true;
	}

	@Override
	public boolean canWrite()
	{
		return false;
	}

}
