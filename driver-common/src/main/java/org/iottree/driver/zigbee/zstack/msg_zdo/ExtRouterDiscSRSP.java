package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class ExtRouterDiscSRSP extends MsgRecv
{
	public static final int CMD0 = 0x65 ;
	public static final int CMD1 = 0x45 ;
	
	public ExtRouterDiscSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_EXT_ROUTE_DISC";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 1;
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
		return new JSONObject().put("status_v", this.getStatusVal()).put("status", this.getStatus());
	}

}
