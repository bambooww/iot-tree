package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.zdo.IEEEAddrReq;
import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * This command will request a device’s IEEE 64-bit address. You must subscribe to “ZDO IEEE
Address Response” to receive the data response to this message. The response message listed
below only indicates whether or not the message was received properly.

 * @author jason.zhu
 *
 */
public class IEEEAddrSREQ extends MsgData
{
	IEEEAddrReq zspecReq = null ;
	
	public IEEEAddrSREQ()
	{
		super(0x25,0x01) ;
	}
	/**
	 * This command is generated to inquire about the Node Descriptor information
	 *  of the destination device.
	 */
	public IEEEAddrSREQ(int shortaddr,boolean req_type,int start_idx)
	{
		super(0x25,0x01) ;
		zspecReq = new IEEEAddrReq(shortaddr,req_type, start_idx);
	}

	public IEEEAddrSREQ(int shortaddr)
	{
		super(0x25,0x01) ;
		zspecReq = new IEEEAddrReq(shortaddr,false, 0x00);
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_IEEE_ADDR_REQ";
	}
	
	
	@Override
	public int getDataLenMin()
	{
		return IEEEAddrReq.PACK_MIN_LEN;
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
		this.zspecReq = new IEEEAddrReq() ;
		this.zspecReq.unpackFrom(new ZDOBuffer(data,0,data.length)); //.fromClusterPack(data, failedr) ;
		return true;
	}

	@Override
	public String toString()
	{
		return super.toString()+this.zspecReq;
	}
}
