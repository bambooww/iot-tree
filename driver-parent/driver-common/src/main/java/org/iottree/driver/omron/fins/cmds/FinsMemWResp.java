package org.iottree.driver.omron.fins.cmds;

import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.fins.FinsMsgReq;
import org.iottree.driver.omron.fins.FinsMsgResp;

public class FinsMemWResp extends FinsMsgResp
{
	int errCode = -1 ;
	
	
	FinsMemWReq memWReq  =null ;
	
	public FinsMemWResp(FinsMsgReq req)
	{
		super(req);
		
		if(!(req instanceof FinsMemWReq))
			throw new IllegalArgumentException("not FinsMemWReq") ;
		memWReq = (FinsMemWReq)req ;
	}

	@Override
	protected boolean parseParam(int mrc, int src, byte[] param_bs, StringBuilder failedr)
	{
		if(param_bs==null||param_bs.length!=2)
			return false;
		this.errCode = bytes2short(param_bs,0) ;
		
		return true;
	}

	public int getErrCode()
	{
		return this.errCode ;
	}
	
	
}
