package org.iottree.driver.zigbee.zstack.msg_util;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class AssocGetWithAddrSRSP extends MsgRecv
{
	public static final int CMD0 = 0x67 ;
	public static final int CMD1 = 0x4A ;
	
	public AssocGetWithAddrSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "UTIL_ASSOC_GET_WITH_ADDRESS";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 18;
	}

	@Override
	protected boolean checkCmd()
	{
		if(this.cmd0!=CMD0)
			return false;
		if(this.cmd1!=CMD1)
			return false;
		return true ;
	}

	
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject() ;
	}

}
