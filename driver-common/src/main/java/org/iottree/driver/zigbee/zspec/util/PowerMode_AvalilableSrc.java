package org.iottree.driver.zigbee.zspec.util;

/**
 * 电源模式和供电来源
 * @author jason.zhu
 *
 */
public class PowerMode_AvalilableSrc
{
	int pv ;
	
	public PowerMode_AvalilableSrc(int v)
	{
		this.pv = v ;
	}
	
	/**
	 * CurrentPowerMode
	 * @return
	 */
	public PowerMode getCurPowerMode()
	{
		return PowerMode.valOfInt(this.pv & 0x0F) ; // bit 0-3
	}
	
	/**
	 * AvailablePowerSources
	 * @return
	 */
	public PowerSrcAvailable getAvailableSrc()
	{
		//return this.pv & 0xF0 ; // bit 4-7
		return new PowerSrcAvailable((this.pv >> 4) & 0x0F) ;
	}
	
	public String toString()
	{
		return "pow_mode="+this.getCurPowerMode()+" av_src="+this.getAvailableSrc() ;
	}
}
