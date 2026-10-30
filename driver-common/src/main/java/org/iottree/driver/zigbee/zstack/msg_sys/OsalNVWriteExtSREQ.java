package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgData;

public class OsalNVWriteExtSREQ extends MsgData
{
	public OsalNVWriteExtSREQ()
	{
		super(0x21,0x1D) ; //p91 3.8.1.35 SYS_OSAL_NV_WRITE_EXT 0x09 err -> 0x1D
	}
	
	
	public OsalNVWriteExtSREQ(int nv_item_id,int nv_offset,byte[] val,int offset,int len)
	{
		super(0x21,0x1D) ;
		//int len = nv_val!=null?nv_val.length:0 ;
		if(len>246)
			throw new IllegalArgumentException("nv value len must in [0,246]");
		this.data = new byte[5+len] ;
		DataUtil.setUInt16(nv_item_id, this.data, 0); //0-1
		DataUtil.setUInt16(nv_offset, this.data, 2); //2-3
		this.data[4] = (byte)len ;
		if(len>0)
			System.arraycopy(val, offset, this.data, 5, len);
	}
	
	public OsalNVWriteExtSREQ(int nv_item_id,int nv_offset,byte[] val)
	{
		this( nv_item_id, nv_offset, val, 0 ,val.length) ;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_OSAL_NV_WRITE_EXT";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 5;
	}
	
	public int getNVItemId()
	{
		return DataUtil.getUInt16(this.data, 0) ;
	}
	
	public int getOffset()
	{
		return DataUtil.getUInt16(this.data, 2) ;
	}
	
	public int getNVValueLen()
	{
		return this.data[4] & 0xFF ;
	}
	
	public byte[] getNVValue()
	{
		int len = getNVValueLen() ;
		byte[] bs = new byte[len] ;
		if(len>0)
			System.arraycopy(this.data, 5, bs, 0, len);
		return bs ;
	}

	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " nv_item_id="+DataUtil.transHexAddr(this.getNVItemId())
			+" offset="+this.getOffset()+" nv_value_len="+this.getNVValueLen();
	}

}
