package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class ExtFindGroupSRSP extends MsgRecv
{
	public static final int CMD0 = 0x65 ;
	public static final int CMD1 = 0x4A ;
	
	public ExtFindGroupSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_EXT_FIND_GROUP";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 1;
	}
	
	public byte[] getGroup()
	{
		return this.data;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("group", Convert.byteArray2HexStr(this.getGroup(),"-"));
	}
	
//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " group="+this.data;
//	}

}
