package org.iottree.driver.omron.fins;

import java.io.IOException;
import java.io.OutputStream;

import org.iottree.core.util.IBSOutput;

public abstract class FinsCmdReq extends FinsCmd
{
//	short area ;
//	int startAddr ;
//	short startBit ;
//	int readLen ;
	
	public FinsCmdReq(FinsMode fins_mode)
	{
		super(fins_mode) ;
//		super(tar_clientid, sor_clientid);
//		this.area = area ;
//		this.startAddr = start_addr ;
//		this.startBit = start_bit ;
//		this.readLen = read_len ;
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

	protected final short getICF()
	{
		if(isNeedResp())
			return 0x80;
		else
			return 0x81 ;
	}
	
	protected boolean isNeedResp()
	{
		return true ;
	}
	
}
