package org.iottree.driver.zigbee.zstack.msg_util;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.util.ICopyable;
import org.json.JSONObject;

public class CommonStatusSRSP extends MsgRecv implements ICopyable
{
	String tp_name ;
	
	public CommonStatusSRSP(int cmd0,int cmd1,String tp_name)
	{
		this.cmd0 = cmd0 ;
		this.cmd1 = cmd1 ;
		this.tp_name = tp_name ;
	}
	

	@Override
	public CommonStatusSRSP copyMe()
	{
		return new CommonStatusSRSP(this.cmd0,this.cmd1,this.tp_name) ;
	}

	
	@Override
	public String getTpName()
	{
		return this.tp_name;
	}
	
	@Override
	public int getDataLenMin()
	{
		return 1;
	}

	@Override
	protected boolean checkCmd()
	{
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

}
