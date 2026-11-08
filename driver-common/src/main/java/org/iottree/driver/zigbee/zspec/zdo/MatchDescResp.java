package org.iottree.driver.zigbee.zspec.zdo;

import java.util.List;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;

public class MatchDescResp extends ZDOResp
{
	public ZDOStatus status ;
	
	public int nwk_addr ;
	
	public int endpoint_c ;
	
	public List<Integer> endpoint_list ;
	

	public MatchDescResp()
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
		return ZDOClusterId.MATCH_DESCRIPTORS_RESPONSE;
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
        	this.nwk_addr = buf.readUInt16();
            // Length in bytes of the Simple Descriptor to follow. [0x00-0xff]
            this.endpoint_c = buf.readUInt8();
            this. endpoint_list = buf.readListUInt8(endpoint_c);
        }
	}

}
