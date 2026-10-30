package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zstack.MsgData;

public class MgmtLeaveSREQ extends MsgData
{
	public MgmtLeaveSREQ()
	{
		super(0x25,0x34) ;
	}
	/**
	 * This command is generated to inquire about the Node Descriptor information
	 *  of the destination device.
	 */
	public MgmtLeaveSREQ(int shortaddr,String ieee_addr,int rm_kid_or_rejoin)
	{
		super(0x25,0x34) ;
		this.data = new byte[11] ;
		DataUtil.setUInt16(shortaddr, this.data, 0); //0-1
		DataUtil.transStr2IEEEAddr(ieee_addr, this.data, 2) ; //2-9
		this.data[10] = (byte)rm_kid_or_rejoin ;
	}

	
	@Override
	public String getTpName()
	{
		return "ZDO_MGMT_LEAVE_REQ";
	}
	
	
	@Override
	public int getDataLenMin()
	{
		return 11;
	}
	
	public ShortAddr getDstAddr()
	{
		return new ShortAddr(DataUtil.getUInt16(this.data, 0)) ;
	}
	
	public String getIEEEAddr()
	{
		return DataUtil.transIEEEAddr(this.data, 2) ;
	}
	
	public int getRemoveKid_Rejoin()
	{
		return this.data[10] & 0xFF ;
	}
	
	public boolean isRemoveRejoin()
	{
		int v = getRemoveKid_Rejoin() ;
		return (v & RM_REJOIN)>0 ; //bit 0 ;
	}
	
	public boolean isRemoveKid()
	{
		int v = getRemoveKid_Rejoin() ;
		return (v & RM_KID)>0 ; //bit 1 ;
	}
	
	public static final int RM_REJOIN = 0x01 ; //bit1
	
	public static final int RM_KID = 0x02 ; //bit2
	
	@Override
	public String toString()
	{
		return super.toString()+" dst_addr="+this.getDstAddr()+" ieee="+this.getIEEEAddr()
		+" rm_rejoin="+isRemoveRejoin() +" rm_kid="+this.isRemoveKid() ;
	}

}
