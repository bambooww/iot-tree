package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgData;

public class OsalNVLengthSREQ extends MsgData
{
	public OsalNVLengthSREQ()
	{
		super(0x21,0x13) ;
	}
	
	public OsalNVLengthSREQ(int item_id)
	{
		super(0x21,0x13) ;
		this.data = new byte[2] ;
		DataUtil.setUInt16(item_id, this.data, 0);
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_OSAL_NV_LENGTH";
	}
	
	public int getNVItemId()
	{
		return DataUtil.getUInt16(this.data, 0) ;
	}

	@Override
	public int getDataLenMin()
	{
		return 2;
	}

	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " nv_itemid="+DataUtil.transHex(getNVItemId());
	}
}
