package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class MsgCBRegisterSRSP extends MsgRecv
{
	public static final int CMD0 = 0x65 ;
	public static final int CMD1 = 0x3E ;
	
	
	public MsgCBRegisterSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_MSG_CB_REGISTER";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 1;
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

	public boolean getStatus()
	{//Status is either Success (0) or Failure (1).
		return this.data[0]==0 ;
	}
	
	public int getStatusVal()
	{
		return this.data[0] & 0xFF;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("status",this.getStatusVal()).put("status_ok", this.getStatus());
	}
	
//	@Override
//	public String toString()
//	{
//		return super.toString()+" Status="+this.getStatus() ;
//	}
}
