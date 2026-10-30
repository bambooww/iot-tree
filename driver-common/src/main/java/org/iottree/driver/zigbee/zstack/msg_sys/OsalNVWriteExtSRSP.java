package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class OsalNVWriteExtSRSP extends MsgRecv
{
	public static final int CMD0 = 0x61 ;
	public static final int CMD1 = 0x1D ;
	
	public OsalNVWriteExtSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_OSAL_NV_WRITE_EXT";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 1;
	}
	
	public boolean getStatus()
	{
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
//		String ret = super.toString() ;
//		return ret + " status="+this.getStatus();
//	}

}
