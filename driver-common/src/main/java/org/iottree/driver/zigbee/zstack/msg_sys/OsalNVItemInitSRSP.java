package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class OsalNVItemInitSRSP extends MsgRecv
{
	public static final int CMD0 = 0x61 ;
	public static final int CMD1 = 0x07 ;
	
	public OsalNVItemInitSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_OSAL_NV_ITEM_INIT";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 1;
	}

	public int getStatusVal()
	{
		return data[0] & 0xFF ;
	}

	public boolean isAlreadyExists()
	{
		return getStatusVal() == 0x00 ; //Item already exists, no action taken
	}
	
	public boolean isInitSucc()
	{
		return getStatusVal() == 0x09 ; //  Success, item created and initialized
	}
	
	public boolean isInitFailed()
	{
		return getStatusVal() == 0x0A ; //  Initialization failed, item not created
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("status",this.getStatusVal());
	}

}
