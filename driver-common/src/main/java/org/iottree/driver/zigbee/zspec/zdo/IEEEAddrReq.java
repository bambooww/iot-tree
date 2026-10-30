package org.iottree.driver.zigbee.zspec.zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOCluster;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;

public class IEEEAddrReq extends ZDOCluster
{
	public static final int PACK_MIN_LEN = 4 ; 
	
	int nodeId ; //nwk_addr
	boolean reportKids ;
	int start_idx ;
	
	public IEEEAddrReq()
	{
		
	}
	
	/**
	 * Request:    [transaction sequence number: 1]
     *             [node ID:2] [request type:1] [start index:1]
     *             [request type] = 0x00 single address response, ignore the start index
     *                            = 0x01 extended response, sends kid's IDs as well
	 * @param ieee
	 * @param req_type  0x00-single addr resp  1-kid's IDs as well
	 * @param start_idx
	 */
	public IEEEAddrReq(int nodeid,boolean report_kids,int start_idx)
	{
		this.nodeId = nodeid ;
		this.reportKids = report_kids ;
		this.start_idx = start_idx ;
	}
	

	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return ZDOClusterId.IEEE_ADDRESS_REQUEST;
	}

//	@Override
//	public int getClusterPackMinLen()
//	{
//		return PACK_MIN_LEN;
//	}
	

	@Override
	public void packTo(ZDOBuffer buf)
	{
		buf.writeUInt16(this.nodeId);
		buf.writeUInt8(reportKids?1:0);
		buf.writeUInt8(start_idx);
	}

	@Override
	protected void unpackBuf(ZDOBuffer buf)
	{
//		this.data = new byte[4] ;
//		DataUtil.setUInt16(nodeid, this.data, 0);
//		this.data[2] = (byte)req_type ;
//		this.data[3] = (byte)start_idx ;
		this.nodeId = buf.readUInt16() ;
		this.reportKids = buf.readUInt8()>0 ;
		this.start_idx = buf.readUInt8() ;
	}
	
	public ShortAddr getNodeId()
	{
		return new ShortAddr(this.nodeId) ;
	}
	
	public boolean isReportKids()
	{
		return this.reportKids ;
	}
	
	public int getStartIdx()
	{
		return this.start_idx ;
	}
	
	@Override
	public String toString()
	{
		String r = super.toString() ;
		return r += " nodeid="+this.getNodeId()+" report_kids="+this.reportKids+" idx="+this.getStartIdx();
	}

}
