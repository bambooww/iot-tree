package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class OsalNVReadSRSP extends MsgRecv
{
	public static final int CMD0 = 0x61 ;
	public static final int CMD1 = 0x08 ;
	
	public OsalNVReadSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_OSAL_NV_READ";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 2;
	}
	
	public boolean getStatus()
	{
		return this.data[0]==0 ;
	}
	
	public int getStatusVal()
	{
		return this.data[0] & 0xFF;
	}
	
	public int getNVValueLen()
	{
		return this.data[1] & 0xFF ;
	}
	
	public byte[] getNVValue()
	{
		int len = this.getNVValueLen() ;
		if(len>248) return null ;
		byte[] r = new byte[len] ;
		System.arraycopy(this.data, 2, r, 0, len);
		return r ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("status",this.getStatusVal()).put("status_ok", this.getStatus())
				.put("nv_length", getNVValueLen()).putOpt("nv_data", Convert.byteArray2HexStr(this.getNVValue(), "-"));
	}

//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " status="+this.getStatus()+" nv_value_len="+this.getNVValueLen();
//	}

}
