package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * This command registers for a ZDO callback (see reference [3], “6. ZDO Message Requests” for
	example usage).
 * @author jason.zhu
 *
 */
public class MsgCBRegisterSREQ extends MsgData
{
	
	public MsgCBRegisterSREQ()
	{
		super(0x25,0x3E) ;
	}
	/**
	 * This command is generated to inquire about the Node Descriptor information
	 *  of the destination device.
	 */
	public MsgCBRegisterSREQ(int cluster_id)
	{
		super(0x25,0x3E) ;
		
		this.data = new byte[4] ;
		data[0] = (byte)cluster_id ;
		data[1] = (byte)(cluster_id>>8) ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_MSG_CB_REGISTER";
	}

	@Override
	public int getDataLenMin()
	{
		return 2;
	}
}
