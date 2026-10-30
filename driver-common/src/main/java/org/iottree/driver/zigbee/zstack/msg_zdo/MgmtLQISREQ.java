package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * This command is generated to request the destination device to perform a LQI query of other
devices in the network.

 * @author jason.zhu
 *
 */
public class MgmtLQISREQ extends MsgData
{
	
	/**
	 * This command is generated to request the destination device to perform a LQI query of other
	 * devices in the network.
	 * 
	 * @param dst_addr Specifies the network address of the device to process the query.
	 * @param start_idx 
	 */
	public MgmtLQISREQ(int dst_addr,int start_idx)
	{
		super(0x25,0x31) ;
		
		data = new byte[3] ;
		data[0] = (byte)dst_addr ;
		data[1] = (byte)(dst_addr>>8) ;
		data[2] = (byte)start_idx ;
	}
	
	public MgmtLQISREQ()
	{
		this(0x0000,0) ; //req self
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_MGMT_LQI_REQ";
	}
	
//	@Override
//	public byte[] getData()
//	{
//		return this.data;
//	}
//	
//	@Override
//	protected boolean parseData(byte[] data, StringBuilder failedr)
//	{
//		return false;
//	}

	@Override
	public int getDataLenMin()
	{
		return 3;
	}

}
