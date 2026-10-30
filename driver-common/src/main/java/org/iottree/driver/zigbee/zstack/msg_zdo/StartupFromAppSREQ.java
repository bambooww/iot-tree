package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgData;

public class StartupFromAppSREQ extends MsgData
{
	
	public StartupFromAppSREQ()
	{
		super(0x25,0x40) ;
		
		this.data = new byte[2] ;
		DataUtil.setUInt16(0, this.data, 0);
	}
	/**
	 * This command starts the device in the network.
	 * @param start_delay
	 */
	public StartupFromAppSREQ(int start_delay)
	{
		super(0x25,0x40) ;
		
		this.data = new byte[2] ;
		DataUtil.setUInt16(start_delay, this.data, 0);
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_STARTUP_FROM_APP";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 2;
	}
}
