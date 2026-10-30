package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.APSFlag_FreqBand;
import org.iottree.driver.zigbee.zspec.util.MacCapabilitiesFlags;
import org.iottree.driver.zigbee.zspec.util.NodeLogicTpCDUD;
import org.iottree.driver.zigbee.zspec.util.ServerMask;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class NodeDescRspAREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x45 ;
	public static final int CMD1 = 0x82 ;
	
	public NodeDescRspAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_NODE_DESC_RSP";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 18;
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
	 * The message’s source network address.
	 * @return
	 */
	public ShortAddr getSrcAddr()
	{
		return new ShortAddr(DataUtil.getUInt16(this.data, 0)) ; //0-1
	}
	
	public boolean getStatus()
	{//Status is either Success (0) or Failure (1).
		return this.data[2]==0 ;
	}
	
	public int getStatusVal()
	{
		return this.data[2] & 0xFF;
	}
	/**
	 * Device’s short address of this Node descriptor
	 * @return
	 */
	public ShortAddr getNwkAddr()
	{
		return new ShortAddr(DataUtil.getUInt16(this.data, 3)) ; //3-4
	}
	
	public NodeLogicTpCDUD getNodeLogic()
	{
		return new NodeLogicTpCDUD(this.data[5] & 0xFF) ;
	}
	
	public APSFlag_FreqBand getAPSFlags_FrequencyBand()
	{
		return new APSFlag_FreqBand(this.data[6] & 0xFF) ;
	}
	
	public MacCapabilitiesFlags getMacCapabilities()
	{
		return new MacCapabilitiesFlags(this.data[7] & 0xFF) ;
	}
	
	/**
	 * Specifies a manufacturer code that is allocated by the
	 * ZigBee Alliance, relating to the manufacturer to the
	 * device.
	 * @return
	 */
	public int getManufacturerCode()
	{
		return DataUtil.getUInt16(this.data,8) ; //8-9
	}
	
	/**
	 * Indicates size of maximum NPDU. This field is used as a high level indication for management.
	 * @return
	 */
	public int getMaxBufferSize()
	{
		return this.data[10] & 0xFF ; 
	}
	
	/**
	 * Indicates maximum size of Transfer up to 0x7fff (This field is reserved in version 1.0 and shall be set to zero).
	 */
	public int getMaxInTransferSize()
	{
		return DataUtil.getUInt16(this.data,11) ; //11-12
	}
	
	public ServerMask getServerMask()
	{
		return new ServerMask(DataUtil.getUInt16(this.data,13)); //13-14
	}
	
	/**
	 * Indicates maximum size of Transfer up to 0x7fff (This 
	 * field is reserved in version 1.0 and shall be set to zero).
	 * @return
	 */
	public int getMaxOutTransferSize()
	{
		return DataUtil.getUInt16(this.data,15) ; //15-16
	}
	
	/**
	 * Specifies the Descriptor capabilities
	 * @return
	 */
	public int getDescriptorCapabilities()
	{
		return this.data[17] ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		JSONObject ret = new JSONObject().put("status",this.getStatusVal()).put("status_ok", this.getStatus())
				.put("src_addr", this.getSrcAddr().toHexStr()).put("nwk_addr", this.getNwkAddr().toString())
				.put("node_logic", this.getNodeLogic().toJO());
		ret.putOpt("aps_freq", this.getAPSFlags_FrequencyBand().toString()).putOpt("mac_cap", this.getMacCapabilities())
			.putOpt("fac", this.getManufacturerCode()).put("max_buf",this.getMaxBufferSize())
			.putOpt("max_in_tran_size", this.getMaxInTransferSize()).putOpt("server_mask", this.getServerMask().getOptionsList())
			.put("max_out_tran_size", this.getMaxOutTransferSize());
		return ret ;
	}

//	public String toString()
//	{
//		return super.toString() +" src_addr="+this.getSrcAddr()+" status="+this.getStatus()+" nwk_addr="+this.getNwkAddr()
//			+" node_logic="+this.getNodeLogic()+" aps_freq="+this.getAPSFlags_FrequencyBand()+" mac_cap="+this.getMacCapabilities()
//			+"\r\n\t fac="+this.getManufacturerCode()+" max_buf="+this.getMaxBufferSize();
//	}

}
