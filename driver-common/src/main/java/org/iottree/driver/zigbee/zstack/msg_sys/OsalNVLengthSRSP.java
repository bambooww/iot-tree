package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class OsalNVLengthSRSP extends MsgRecv
{
	public static final int CMD0 = 0x61 ;
	public static final int CMD1 = 0x13 ;
	
	public OsalNVLengthSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_OSAL_NV_LENGTH";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 2;
	}

	public int getNVLength()
	{
		int r = data[1] & 0xFF ;
		r <<= 8 ;
		r += data[0] & 0xFF ;
		return r ;
	}

	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("nv_length",this.getNVLength());
	}

//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " = [NV_LENGTH="+this.getNVLength()+"]";
//	}

	
}
