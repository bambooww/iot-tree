package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgData;

public class MgmtPermitJoinSREQ extends MsgData
{
	public static final int ADDR_MODE_BROADCAST = 0x0F ;
	public static final int ADDR_MODE_BIT16 = 0x02 ; //short
	/**
	 * 
	 * @param addr_mode Destination address type: 0x02 – Address 16 bit, 0x0F – Broadcast
	 * @param dst_addr Specifies the network address of the destination device whose Permit Join
				information is to be modified.
	 * @param duration Specifies the duration to permit joining.
	 * 				0 = join disabled. 0xff = join enabled.
	 * 				 0x01-0xfe = number of seconds to permit joining.
	 * @param tc_significance Trust Center Significance Zigbee 3.0 要求 TCSignificance=0x01，否则某些设备会拒绝加入
	 */
	public MgmtPermitJoinSREQ(int addr_mode,int dst_addr,int duration,int tc_significance)
	{
		super(0x25,0x36) ;
		
		data = new byte[5] ;
		data[0] = (byte)addr_mode ; //
		data[1] = (byte)dst_addr ;
		data[2] = (byte)(dst_addr>>8) ;
		data[3] = (byte)duration ;
		data[4] = (byte)tc_significance ;
	}
	
	public MgmtPermitJoinSREQ(boolean permit_or_not)
	{
		//Broadcast,0xFFFC,dur=0xFE
		this(0x0F,0xFFFC,permit_or_not?0xfe:0x00,0x01) ;
	}
	
	public MgmtPermitJoinSREQ()
	{
		super(0x25,0x36) ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_MGMT_PERMIT_JOIN_REQ";
	}

	@Override
	public int getDataLenMin()
	{
		return 5;
	}
	
	/**
	 * Destination address type: 0x02 – Address 16 bit, 0x0F – Broadcast
	 * @return
	 */
	public int getAddrMode()
	{
		return this.data[0] & 0xFF ;
	}
	
	public String getAddrModeT()
	{
		switch(getAddrMode())
		{
		case 0x02: return "Address 16 bit";
		case 0x0F: return "Broadcast";
		default: return "" ;
		}
	}
	
	public int getDstAddr()
	{
		return DataUtil.getUInt16(this.data, 1) ; //1-2
	}
	
	/**
	 * duration Specifies the duration to permit joining.
	 * 				0 = join disabled. 0xff = join enabled.
	 * 				 0x01-0xfe = number of seconds to permit joining.
	 * @return
	 */
	public int getDuration()
	{
		return this.data[3] & 0xFF ; 
	}
	
	/**
	 * Trust Center Significance
	 * @return
	 */
	public int getTCSignificance()
	{
		return this.data[4] & 0xFF ; 
	}

	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " addr_mode="+this.getAddrModeT()+" dst_addr="+DataUtil.transHex(this.getDstAddr())
			+" duration="+DataUtil.transHex(this.getDuration(),2)+" tc_sign="+this.getTCSignificance();
	}
}
