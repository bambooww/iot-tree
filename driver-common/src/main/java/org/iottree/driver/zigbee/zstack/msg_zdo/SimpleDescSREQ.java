package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgData;

public class SimpleDescSREQ extends MsgData
{
	//byte[] data = null;
	
	public SimpleDescSREQ()
	{
		super(0x25,0x04) ;
	}
	/**
	 * 
	 * @param dst_addr Specifies NWK address of the device to route the inquiry.
	 * @param nwk_addr_of_int Specifies NWK address of the destination device being queried
	 * @param endpoint Specifies the application endpoint the data is from.
	 */
	public SimpleDescSREQ(int dst_addr,int nwk_addr_of_int,int endpoint)
	{
		super(0x25,0x04) ;
		
		this.data = new byte[5] ;
		data[0] = (byte)dst_addr ;
		data[1] = (byte)(dst_addr>>8) ;
		data[2] = (byte)nwk_addr_of_int ;
		data[3] = (byte)(nwk_addr_of_int>>8) ;
		data[4] = (byte)endpoint ;
	}
	@Override
	public String getTpName()
	{
		return "ZDO_SIMPLE_DESC_REQ";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 5;
	}
	
	public int getDstAddr()
	{
		return DataUtil.getUInt16(this.data, 0) ;
	}
	
	public int getNwkAddrOfI()
	{
		return DataUtil.getUInt16(this.data, 2) ; //2-3
	}
	
	public int getEndpoint()
	{
		return this.data[4] & 0xFF ;
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " dst_addr="+DataUtil.transHex(getDstAddr())+" nwk_addr="+DataUtil.transHex(this.getNwkAddrOfI())
			+" ep="+this.getEndpoint();
	}
}
