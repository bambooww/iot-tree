package org.iottree.driver.zigbee.zspec.zdo;

import java.util.List;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;
import org.iottree.driver.zigbee.zspec.zdo_def.ServerMask;

public class SystemServerDiscResp extends ZDOResp
{
	public ZDOStatus status ;
	
	public ServerMask server_mask ;
	
	public SystemServerDiscResp()
	{
	}

	@Override
	public ZDOStatus getStatus()
	{
		return status;
	}

	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return ZDOClusterId.SYSTEM_SERVER_DISCOVERY_RESPONSE;
	}

	@Override
	public void packTo(ZDOBuffer buf)
	{
		throw new RuntimeException("no impl") ;
	}

	@Override
	protected void unpackBuf(ZDOBuffer buf)
	{
		ZDOStatus status = ZDOStatus.valOfInt(buf.readUInt8());
        if (status == ZDOStatus.SUCCESS) {
        	this.server_mask = ServerMask.getServerMask(buf.readUInt16());
        }
	}

}
