package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.zstack.MsgNoData;

public class ExtNwkInfoSREQ extends MsgNoData
{
	public ExtNwkInfoSREQ()
	{
		super(0x25,0x50) ;
	}
	
	
	@Override
	public String getTpName()
	{
		return "ZDO_EXT_NWK_INFO";
	}
	
}
