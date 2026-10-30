package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

/**
 * This message is a ZDO callback for TC Device Indication
 * 
 * Trust Center find new device
 * 
 * @author jason.zhu
 *
 */
public class TCDevIndAREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x45 ;
	public static final int CMD1 = 0xCA ;
	
	public TCDevIndAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_TC_DEV_IND";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 0x0C;
	}

	@Override
	protected boolean checkCmd()
	{
		if(this.cmd0!=CMD0)
			return false;
		if(this.cmd1!=CMD1)
			return false;
		return true ;
	}
	
	/**
	 * Source network Address. node id
	 * @return
	 */
	public int getSrcNwkAddr() //
	{
		return DataUtil.getUInt16(this.data,0) ;
	}
	
	/**
	 * Specifies the 64 bit IEEE address of source device.
	 * @return
	 */
	public String getSrcIEEEAddr()
	{
		return DataUtil.transIEEEAddr(this.data, 2);
	}

	public int getParentNwkAddr()
	{
		return DataUtil.getUInt16(this.data,10) ;
	}

	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("src_nwk_addr",this.getSrcNwkAddr())
				.putOpt("src_ieee", this.getSrcIEEEAddr())
				.put("parent_nwk_addr", this.getParentNwkAddr());
	}

}
