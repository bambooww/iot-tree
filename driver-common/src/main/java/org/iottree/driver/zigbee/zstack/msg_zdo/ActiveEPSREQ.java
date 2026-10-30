package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.Msg;
import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * This command is generated to request a list of active endpoint from the destination device.
 * 
 * @author jason.zhu
 */
public class ActiveEPSREQ extends MsgData
{	
	public ActiveEPSREQ()
	{
		super(0x25,0x05) ;
	}
	/**
	 * This command is generated to inquire about the Node Descriptor information
	 *  of the destination device.
	 */
	public ActiveEPSREQ(int dst_addr,int nwk_addr_of_i)
	{
		super(0x25,0x05) ;
		
		data = new byte[4] ;
		data[0] = (byte)dst_addr ;
		data[1] = (byte)(dst_addr>>8) ;
		data[2] = (byte)nwk_addr_of_i ;
		data[3] = (byte)(nwk_addr_of_i>>8) ;
	}
	
	public ActiveEPSREQ(int dst_addr)
	{
		this(dst_addr,dst_addr);
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_ACTIVE_EP_REQ";
	}
	@Override
	public int getDataLenMin()
	{
		return 4;
	}
	
	public int getDstAddr()
	{
		return DataUtil.getUInt16(this.data, 0) ;
	}
	
	public int getNwkAddr()
	{
		return DataUtil.getUInt16(this.data, 2) ;
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " dst_addr="+DataUtil.transHex(getDstAddr())+" nwk_addr="+DataUtil.transHex(this.getNwkAddr());
	}

}
