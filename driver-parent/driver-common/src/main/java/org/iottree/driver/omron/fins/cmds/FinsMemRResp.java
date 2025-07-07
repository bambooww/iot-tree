package org.iottree.driver.omron.fins.cmds;

import org.iottree.driver.omron.fins.FinsException;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.fins.FinsMsgReq;
import org.iottree.driver.omron.fins.FinsMsgResp;
import org.iottree.driver.omron.hostlink.HLException;

public class FinsMemRResp extends FinsMsgResp
{
	int errCode = -1 ;
	
	
	FinsMemRReq memRReq  =null ;
	
	byte[] retBS = null ;
	
	public FinsMemRResp(FinsMsgReq req)
	{
		super(req);
		
		if(!(req instanceof FinsMemRReq))
			throw new IllegalArgumentException("not FinsMemRReq") ;
		memRReq = (FinsMemRReq)req ;
	}

	@Override
	protected boolean parseParam(int mrc, int src, byte[] param_bs, StringBuilder failedr)
	{
		if(param_bs==null||param_bs.length<2)
			return false;
		FinsMode.AreaCode ac = memRReq.getAreaCode() ;
		this.errCode = bytes2short(param_bs,0) ;
		
		int data_len = param_bs.length-2 ;
		if(ac.isBit())
		{
			if(data_len!=memRReq.itemNum)
			{
				failedr.append("response data num is not match to request") ;
				return false;
			}
		}
		else
		{
			if(data_len!=memRReq.itemNum*2)
			{
				failedr.append("response data num is not match to request") ;
				return false;
			}
		}
		
		this.retBS = new byte[data_len] ;
		System.arraycopy(param_bs, 2, this.retBS, 0, data_len);
		return true;
	}

	public int getErrCode()
	{
		return this.errCode ;
	}
	
	public byte[] getReturnBytes()
	{
		return this.retBS ;
	}
}
