package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.types.NetworkParam;
import org.iottree.driver.zigbee.zspec.util.DeviceState;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

/**
 * This command handles the ZDO extension network message.
 * 
 * @author jason.zhu
 */
public class ExtNwkInfoSRSP extends MsgRecv
{
	public static final int CMD0 = 0x65 ;
	public static final int CMD1 = 0x50 ;
	
	public ExtNwkInfoSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_EXT_NWK_INFO";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 0x18;
	}
	
	public int getShortAddr()
	{
		return DataUtil.getUInt16(this.data, 0) ;
	}
	
	public DeviceState getDeviceState()
	{
		return new DeviceState(this.data[2]) ;
	}
	
	public int getPanId()
	{
		return DataUtil.getUInt16(this.data, 3) ;
	}
	
	public int getParentAddr()
	{
		return DataUtil.getUInt16(this.data, 5) ;
	}
	
	public String getExtendedPanId()
	{
		return DataUtil.transPanId(this.data, 7) ; //7-14
	}
	
	public String getExtendedParentAddr()
	{
		return DataUtil.transPanId(this.data, 15) ; //15-22
	}
	
	public int getChannel()
	{
		return this.data[23] & 0xFF ;
	}
	
	public NetworkParam toNwkParam()
	{
		return new NetworkParam(
				getPanId(),
				this.getExtendedPanId(),
				this.getChannel(),
				0) ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("short_addr",this.getShortAddr()).put("dev_state", this.getDeviceState())
				.put("pan_id", this.getPanId()).put("parent_addr",this.getParentAddr()).putOpt("ext_pan_id", this.getExtendedPanId())
				.putOpt("ext_parent_addr",this.getExtendedParentAddr()).put("channel", this.getChannel());
	}
	
//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " shortaddr="+DataUtil.transHexAddr(this.getShortAddr())+" dev_state="+this.getDeviceState()+
//				"\r\n    panid="+this.getPanId()+" parent_addr="+this.getParentAddr()+" ext_panid="+this.getExtendedPanId()
//				+" ext_parentaddr="+this.getExtendedParentAddr()+" ch="+this.getChannel();
//	}

}
