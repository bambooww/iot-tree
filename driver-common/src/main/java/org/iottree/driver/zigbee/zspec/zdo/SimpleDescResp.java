package org.iottree.driver.zigbee.zspec.zdo;

import java.util.List;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;

public class SimpleDescResp extends ZDOResp
{
	public ZDOStatus status ;
	
	public int nwk_addr ;
	
	public int len ;
	
	public int endpoint ;
	
	public  int profile_id ;
	
	public int device_id ;
	
	public int device_ver ;
	
	public int in_cluster_c ;
	
	public List<Integer> in_cluster_list ;
	
	public int out_cluster_c ;
	
	public List<Integer> out_cluster_list ;
	

	public SimpleDescResp()
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
		return ZDOClusterId.SIMPLE_DESCRIPTOR_RESPONSE;
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
            this.len = buf.readUInt8();
            this. endpoint = buf.readUInt8();
            this. profile_id = buf.readUInt16();
            this. device_id = buf.readUInt16();
            this. device_ver = buf.readUInt8();
            this. in_cluster_c = buf.readUInt8();
            this. in_cluster_list = buf.readListUInt16(in_cluster_c); // empty if inClusterCount==0
            this. out_cluster_c = buf.readUInt8();
            this. out_cluster_list = buf.readListUInt16(out_cluster_c); // empty if outClusterCount==0
        }
	}
}
