package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.zdo.NwkAddrReq;
import org.iottree.driver.zigbee.zstack.MsgData;

public class NwkAddrSREQ extends MsgData
{
	NwkAddrReq zspecReq = null ;
	
	public NwkAddrSREQ()
	{
		super(0x25,0x00) ;
	}

	/**
	 * This command is generated to inquire about the Node Descriptor information
	 *  of the destination device.
	 * @param ieee_addr
	 * @param req_type 0=Single Device response  ; 1-extended,Extended, include associated devices 
	 * @param start_idx
	 */
	public NwkAddrSREQ(String ieee_addr,boolean req_type,int start_idx)
	{
		super(0x25,0x00) ;
		
		zspecReq = new NwkAddrReq(ieee_addr,req_type, start_idx);
	}
	
	public NwkAddrSREQ(String ieee_addr)
	{
		this(ieee_addr,false,0) ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_NWK_ADDR_REQ";
	}
	
	
	@Override
	public int getDataLenMin()
	{
		return NwkAddrReq.PACK_MIN_LEN;
	}
	
	public boolean isCluster()
	{
		return true ;
	}
	
	@Override
	public byte[] getClusterData()
	{
		return this.zspecReq.getClusterPack();
	}

	@Override
	protected boolean parseClusterData(byte[] data, StringBuilder failedr)
	{
		this.zspecReq = new NwkAddrReq() ;
		//this.zspecReq.fromClusterPack(data, failedr) ;
		this.zspecReq.unpackFrom(new ZDOBuffer(data,0,data.length)) ;
		return true; 
	}

}
