package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * 3.8.1.1 SYS_RESET_REQ
 * 
 * Length = 0x01 Cmd0 = 0x41 Cmd1 = 0x00 Type
 * @author jason.zhu
 *
 */
public class ResetAREQ extends MsgData
{
	
	public ResetAREQ()
	{
		super(0x41,0x00) ;
	}
	
	public ResetAREQ(byte reset_tp)
	{
		super(0x41,0x00) ;
		this.data = new byte[1] ;
		this.data[0] = reset_tp ;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_PING";
	}

	@Override
	public int getDataLenMin()
	{
		return 1;
	}

	
}
