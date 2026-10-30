package org.iottree.driver.zigbee.zspec.zdo;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOCluster;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;

import kotlin.NotImplementedError;

public class AddrRsp extends ZDOResp
{
	ZDOClusterId clusterId ;
	
	ZDOStatus status ;
	
	String eui64 ;
	
	int nwk_addr ;
	
	int start_idx ;
	
	List<Integer> assoc_dev_list ;
	
	public AddrRsp(ZDOClusterId cid)
	{
		if(cid!=ZDOClusterId.NETWORK_ADDRESS_RESPONSE && cid!=ZDOClusterId.IEEE_ADDRESS_RESPONSE)
			throw new IllegalArgumentException("not address response") ;
		clusterId = cid ;
	}
	
	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return this.clusterId ;
	}

	@Override
	public void packTo(ZDOBuffer buf)
	{
		throw new NotImplementedError() ;
	}

	@Override
	protected void unpackBuf(ZDOBuffer buf)
	{
		this.status = ZDOStatus.valOfInt(buf.readUInt8()) ;
		if(this.status!=ZDOStatus.SUCCESS)
			return ;
		this.eui64 = buf.readIEEEAddr() ;
		this.nwk_addr = buf.readUInt8() ;
		int assoc_cc = 0 ;
		this.start_idx = 0 ;
		this.assoc_dev_list = new ArrayList<>() ;
		if(buf.hasMore())
		{
			assoc_cc = buf.readUInt8() ;
			this.start_idx = buf.readUInt8() ;
			this.assoc_dev_list = buf.readListUInt16(assoc_cc) ;
		}
	}
	
	//public static final int PACK_MIN_LEN = 13 ; 

//	@Override
//	public int getClusterPackMinLen()
//	{
//		return PACK_MIN_LEN;
//	}
	
	public ZDOStatus getStatus()
	{//Status is either Success (0) or Failure (1).
		return status;//this.data[0]==0 ;
	}
	
	public String getIEEEAddr()
	{
		return this.eui64 ;
	}

	public int getNwkAddr() // node id 2byte
	{
		return this.nwk_addr ;
	}
	
	public int getStartIdx()
	{
		return this.start_idx ;
	}
	
	public List<Integer> getAssocDevList()
	{
		return this.assoc_dev_list ;
	}
	
	@Override
	public String toString()
	{
		String r = super.toString() ;
		return r += " status="+this.getStatus()+" ieee_addr="+this.getIEEEAddr()+" nwk_addr="+this.getNwkAddr()
			+" idx="+this.getStartIdx()+"["+Convert.combineWith(getAssocDevList(), ',')+"]" ;
	}

	

	
}
