package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgData;

public class OsalNVReadSREQ extends MsgData
{
	public OsalNVReadSREQ()
	{
		super(0x21,0x08) ;
	}
	
	public OsalNVReadSREQ(int nv_item_id,int offset)
	{
		super(0x21,0x08) ;
		this.data = new byte[3] ;
		DataUtil.setUInt16(nv_item_id, this.data, 0);
		this.data[2] = (byte)offset;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_OSAL_NV_READ";
	}
	
	public int getNVItemId()
	{
		return DataUtil.getUInt16(this.data, 0) ;
	}
	
	public int getOffset()
	{
		return this.data[2] & 0xFF ;
	}

	@Override
	public int getDataLenMin()
	{
		return 3;
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " nv_item_id="+this.getNVItemId()+" offset="+this.getOffset();
	}
}
