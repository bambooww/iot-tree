package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.zstack.MsgNoData;

public class VersionSREQ extends MsgNoData
{
	public VersionSREQ()
	{
		super(0x21,0x02) ;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_VERSION";
	}
}
