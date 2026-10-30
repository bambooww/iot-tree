package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * This command is generated to inquire about the Node Descriptor information of the destination device.
 * @author jason.zhu
 *
 */
public class NodeDescSREQ extends MsgData
{
	
	public NodeDescSREQ()
	{
		super(0x25,0x02) ;
	}
	/**
	 * This command is generated to inquire about the Node Descriptor information
	 *  of the destination device.
	 */
	public NodeDescSREQ(int dst_addr,int nwk_addr_of_i)
	{
		super(0x25,0x02) ;
		
		this.data = new byte[4] ;
		data[0] = (byte)dst_addr ;
		data[1] = (byte)(dst_addr>>8) ;
		data[2] = (byte)nwk_addr_of_i ;
		data[3] = (byte)(nwk_addr_of_i>>8) ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_NODE_DESC_REQ";
	}

	@Override
	public int getDataLenMin()
	{
		return 4;
	}
	
	public ShortAddr getDstAddr()
	{
		return new ShortAddr(DataUtil.getUInt16(this.data, 0)) ;
	}
	
	public ShortAddr getNwkAddr()
	{
		return new ShortAddr(DataUtil.getUInt16(this.data, 2)) ;
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " dst_addr="+this.getDstAddr()+" nwk_addr="+this.getNwkAddr();
	}
}
