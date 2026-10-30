package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.ZSDevice;
import org.iottree.driver.zigbee.zspec.util.EndDeviceCap;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

/**
 * This callback indicates the ZDO End Device Announce
 * 
 * @author jason.zhu
 *
 */
public class EndDeviceAnnceIndSREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x45 ;
	public static final int CMD1 = 0xC1 ;
	
	
	public EndDeviceAnnceIndSREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_END_DEVICE_ANNCE_IND";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 0x0D;
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

	public int getSrcAddr()
	{
		return ((this.data[1] & 0xFF)<<8)+(this.data[0]&0xFF) ;
	}
	
	public int getNwkAddr()
	{
		return ((this.data[3] & 0xFF)<<8)+(this.data[2]&0xFF) ;
	}
	
	/**
	 * Specifies the 64 bit IEEE address of source device.
	 * @return
	 */
	public String getIEEEAddr()
	{
		return DataUtil.transIEEEAddr(this.data, 4);
	}
	
	/**
	 * Specifies the MAC capabilities of the device.
	 BIT:
 0 – Alternate PAN Coordinator
 1 – Device type: 1- ZigBee Router; 0 – End Device
 2 – Power Source: 1 Main powered
 3 – Receiver on when Idle
 4 – Reserved
 5 – Reserved
 6 – Security capability
 7 – Reserved
	 * @return
	 */
	public EndDeviceCap getDeviceCapabilites()
	{
		return new EndDeviceCap(this.data[12]) ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		JSONObject r = new JSONObject().put("src_addr",this.getSrcAddr()).put("nwk_addr", this.getNwkAddr())
				.putOpt("ieee_addr", this.getIEEEAddr());
		EndDeviceCap edc = getDeviceCapabilites() ;
		r.put("end_device_cap",edc.toJO());
		return r ;
	}
	

	public boolean MGR_checkChanged(ZSDevice d)
	{
		ShortAddr d_addr = d.getShortAddr() ;
		if(d_addr==null)
			return true;
		if(this.getNwkAddr()!=d_addr.getAddrVal())
			return true ;
		
		if(d.getDeviceCap()==null)
			return true ;
		if(this.getDeviceCapabilites().getCapVal()!=d.getDeviceCap().getCapVal())
			return true ;
		return false;
	}
	
	
}
