package org.iottree.driver.zigbee.zspec.zdo;

import java.util.ArrayList;
import java.util.List;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;

public class ParentAnnounceResp extends ZDOResp
{
	public ZDOStatus status ;
	
	public int num_of_child ;
	
	public List<String> child_ieee_list = new ArrayList<>() ;
	
	public ParentAnnounceResp()
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
		return ZDOClusterId.PARENT_ANNOUNCE_RESPONSE;
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
            this.num_of_child = buf.readUInt8();
            for(int i = 0 ; i < num_of_child ; i ++)
            	this.child_ieee_list.add(buf.readIEEEAddr());
        }
	}

}
