package org.iottree.driver.zigbee.zspec.zdo;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;

/**
 * 
 * @author jason.zhu
 *
 */
public class CommonStatusRsp extends ZDOResp
{
	ZDOClusterId clusterId ;
	
	ZDOStatus status = null ;
	
	public CommonStatusRsp(ZDOClusterId cid)
	{
		this.clusterId = cid ;
	}
	
	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return clusterId;
	}
	
	
//	@Override
//	public int getClusterPackMinLen()
//	{
//		return 1;
//	}
	

	@Override
	public void packTo(ZDOBuffer buf)
	{
		
	}

	@Override
	protected void unpackBuf(ZDOBuffer buf)
	{
		this.status = ZDOStatus.valOfInt(buf.readUInt8()) ;
	}

	public ZDOStatus getStatus()
	{//Status is either Success (0) or Failure (1).
		return status;//this.data[0]==0 ;
	}
	
	@Override
	public String toString()
	{
		String r = super.toString() ;
		return r += " status="+this.status;
	}

}
