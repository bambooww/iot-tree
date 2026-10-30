package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.PowerLevel;
import org.iottree.driver.zigbee.zspec.util.PowerMode;
import org.iottree.driver.zigbee.zspec.util.PowerMode_AvalilableSrc;
import org.iottree.driver.zigbee.zspec.util.PowerSrcAvailable;
import org.iottree.driver.zigbee.zspec.util.PowerSrcLevel;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class PowerDescRspAREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x45 ;
	public static final int CMD1 = 0x83 ;
	
	public PowerDescRspAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_POWER_DESC_RSP";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 7;
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
	
	/**
	 * CurrentPowerMode/AvailablePowerSources
	 * @return
	 */
	public PowerMode_AvalilableSrc getPowerMode_Src()
	{
		return new PowerMode_AvalilableSrc(this.data[5] & 0xFF) ;
	}
	
	public PowerSrcLevel getPowerSrcLevel()
	{
		return new PowerSrcLevel(this.data[6] & 0xFF) ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		PowerMode_AvalilableSrc pm_a = getPowerMode_Src() ;
		PowerMode pm = pm_a.getCurPowerMode() ;
		PowerSrcAvailable src_av =pm_a.getAvailableSrc() ;
		
		PowerSrcLevel psl = getPowerSrcLevel() ;
		PowerSrcAvailable psrc = psl.getCurPowerSrc() ;
		PowerLevel plvl = psl.getSrcLevel() ;
		
		return new JSONObject().put("status",this.getStatusVal()).put("status_ok", this.getStatus()).putOpt("src_addr", this.getSrcAddr())
				.putOpt("nwk_addr", this.getNwkAddr()).put("power_mode", pm.toTitleString()).put("power_src_av", src_av.toString())
				.put("power_src", psrc.toString()).put("power_lvl", plvl.toString());
	}
	
//	public String toString()
//	{
//		return super.toString() +" src_addr="+this.getSrcAddr()+" status="+this.getStatus()+" nwk_addr="+this.getNwkAddr()
//			+" pow_mode="+this.getPowerMode_Src()+" pow_cur_lvl="+this.getPowerSrcLevel();
//	}
}
