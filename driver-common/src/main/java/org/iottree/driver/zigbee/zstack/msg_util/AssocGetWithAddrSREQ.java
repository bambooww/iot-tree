package org.iottree.driver.zigbee.zstack.msg_util;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgData;

public class AssocGetWithAddrSREQ extends MsgData
{
	
	public AssocGetWithAddrSREQ()
	{
		super(0x27,0x4A) ;
	}
	/**
	 * This command is generated to inquire about the Node Descriptor information
	 *  of the destination device.
	 */
	public AssocGetWithAddrSREQ(String ieee_addr,int nwk_addr)
	{
		super(0x27,0x4A) ;
		
		this.data = new byte[10] ;
		DataUtil.transStr2IEEEAddr(ieee_addr, this.data, 0) ; //0-7
		DataUtil.setUInt16(nwk_addr, this.data, 8);
	}
	
	@Override
	public String getTpName()
	{
		return "UTIL_ASSOC_GET_WITH_ADDRESS";
	}

	@Override
	public int getDataLenMin()
	{
		return 10;
	}
	
	public String getIEEEAddr()
	{
		return DataUtil.transIEEEAddr(this.data, 0) ;
	}
	
	public int getNwkAddr()
	{
		return DataUtil.getUInt16(this.data, 8) ;
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " ieee="+this.getIEEEAddr()+" nwk_addr="+this.getNwkAddr();
	}

}
