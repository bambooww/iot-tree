package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.zstack.MsgNoData;

/**
 * 3.8.1.2 SYS_PING
 * Length = 0x00 Cmd0 = 0x21 Cmd1 = 0x01

 * @author jason.zhu
 *
 */
public class PingSREQ extends MsgNoData
{
	public PingSREQ()
	{
		super(0x21,0x01) ;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_PING";
	}
}
