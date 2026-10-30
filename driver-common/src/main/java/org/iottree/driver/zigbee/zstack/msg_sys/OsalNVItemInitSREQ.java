package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgData;

public class OsalNVItemInitSREQ extends MsgData
{
	public OsalNVItemInitSREQ()
	{
		super(0x21,0x07) ;
	}
	
	public OsalNVItemInitSREQ(int item_id,int item_len,byte[] init_data,int offset,int len)
	{
		super(0x21,0x07) ;
		
		if(len>240)
			throw new IllegalArgumentException("init data len cannot bigger than 240") ;
		this.data = new byte[5+len] ;
		DataUtil.setUInt16(item_id, this.data, 0); //0-1
		DataUtil.setUInt16(item_len, this.data, 2); //2-3
		this.data[4] = (byte)len ;
		System.arraycopy(init_data, offset, this.data, 5, len);
	}
	
	public OsalNVItemInitSREQ(int item_id,int item_len,byte[] init_data)
	{
		this(item_id,item_len, init_data,0,init_data.length) ;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_OSAL_NV_ITEM_INIT";
	}
	
	public int getNVItemId()
	{
		return DataUtil.getUInt16(this.data, 0) ;
	}

	@Override
	public int getDataLenMin()
	{
		return 5;
	}

	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " nv_itemid="+DataUtil.transHex(getNVItemId());
	}

}
