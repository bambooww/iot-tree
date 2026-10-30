package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * This command is used by the tester to read a single memory item from the target non-volatile
memory. The command accepts an attribute Id value and data offset and returns the memory value
present in the target for the specified attribute Id.
 *
 *  p92 3.8.1.34 SYS_OSAL_NV_READ_EXT - doc error cmd1=0x08 is err,cmd1=0x1C
 * @author jason.zhu
 *
 */
public class OsalNVReadExtSREQ extends MsgData
{
	public OsalNVReadExtSREQ()
	{
		super(0x21,0x1C) ;
	}
	
	public OsalNVReadExtSREQ(int nv_item_id,int offset)
	{
		super(0x21,0x1C) ;
		this.data = new byte[4] ;
		DataUtil.setUInt16(nv_item_id, this.data, 0);
		DataUtil.setUInt16(offset, this.data, 2);
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_OSAL_NV_READ_EXT";
	}
	
	public int getNVItemId()
	{
		return DataUtil.getUInt16(this.data, 0) ;
	}
	
	public int getOffset()
	{
		return DataUtil.getUInt16(this.data, 2) ;
	}

	@Override
	public int getDataLenMin()
	{
		return 4;
	}

	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " nv_item_id="+DataUtil.transHexAddr(this.getNVItemId())+" offset="+this.getOffset();
	}
}
