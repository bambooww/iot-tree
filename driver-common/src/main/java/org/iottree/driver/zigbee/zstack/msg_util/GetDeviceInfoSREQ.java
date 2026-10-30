package org.iottree.driver.zigbee.zstack.msg_util;

import org.iottree.driver.zigbee.zstack.MsgNoData;

public class GetDeviceInfoSREQ extends MsgNoData
{	
	public GetDeviceInfoSREQ()
	{
		super(0x27,0x00) ;
	}
	
	
	@Override
	public String getTpName()
	{
		return "UTIL_GET_DEVICE_INFO";
	}

}
