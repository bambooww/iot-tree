package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

/**
 * This message is an indication to inform the host of a device leaving the network.
 * 
 * @author jason.zhu
 */
public class LeaveIndAREQ extends MsgRecv
{
	public static final int CMD0 = 0x45 ;
	public static final int CMD1 = 0xC9 ;
	
	
	public LeaveIndAREQ()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_LEAVE_IND";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 0x0D;
	}

//	@Override
//	public byte[] getData()
//	{
//		return this.data;
//	}
//
//	@Override
//	protected boolean parseData(byte[] data, StringBuilder failedr)
//	{
//		if(data==null||data.length<0x0D)
//			return false;
//		this.data = data ;
//		return true;
//	}
	
	@Override
	protected boolean checkCmd()
	{
		if(this.cmd0!=CMD0)
			return false;
		if(this.cmd1!=CMD1)
			return false;
		return true ;
	}

	public int getSrcAddr()
	{
		return ((this.data[1] & 0xFF)<<8)+(this.data[0]&0xFF) ;
	}
	
	/**
	 * Extended address (LSB-MSB) of the source of the leave indication.
	 * @return
	 */
	public String getExtAddr()
	{
		return DataUtil.transIEEEAddr(this.data, 2) ; //2-9
	}
	
	/**
	 * Boolean, TRUE = request, FALSE = indication
	 * @return
	 */
	public boolean getRequest()
	{
		return this.data[10]>0 ;
	}
	
	/**
	 * Boolean, TRUE = remove children
	 * @return
	 */
	public boolean getRemove()
	{
		return this.data[11]>0 ;
	}
	
	/**
	 * Boolean, TRUE = rejoin
	 * @return
	 */
	public boolean getRejoin()
	{
		return this.data[12]>0 ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("src_addr",this.getSrcAddr()).putOpt("ext_addr", this.getExtAddr())
				.put("request", this.getRequest()).put("remove", this.getRemove()).put("rejoin", this.getRejoin());
	}
}
