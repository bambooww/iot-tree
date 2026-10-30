package org.iottree.driver.zigbee.zspec.util;

public class PowerSrcLevel
{
	int pv ;
	
	public PowerSrcLevel(int v)
	{
		this.pv = v ;
	}
	
	/**
	 * CurrentPowerMode
	 * @return
	 */
	public PowerSrcAvailable getCurPowerSrc()
	{
		return new PowerSrcAvailable(this.pv & 0x0F) ; // bit 0-3
	}
	
	/**
	 * AvailablePowerSources
	 * @return
	 */
	public PowerLevel getSrcLevel()
	{
		//return this.pv & 0xF0 ; // bit 4-7
		return new PowerLevel((this.pv>>4) & 0x0F) ;
	}
	
	public String toString()
	{
		return "pow_src="+this.getCurPowerSrc()+" lvl="+this.getSrcLevel() ;
	}
}
