package org.iottree.driver.zigbee.zspec.util;

public class DeviceType
{
	int tpVal ;
	
	public DeviceType(int dev_tp)
	{
		this.tpVal = dev_tp ;
	}
	
	public int getTypeVal()
	{
		return this.tpVal ;
	}
	
	public String getTypeTitle()
	{
		String ret = "" ;
		if(isCoordinator())
			ret +=" coordinator";
		if(isRouter())
			ret += " router" ;
		if(isEndDevice())
			ret += " end_device" ;
		return ret;
	}
	
	public boolean isCoordinator()
	{
		return (this.tpVal & 0x01) > 0 ;//bit 0
	}
	
	public boolean isRouter()
	{
		return (this.tpVal & 0x02)>0 ;
	}
	
	public boolean isEndDevice()
	{
		return (this.tpVal & 0x04)>0 ;
	}
	
	public String toString()
	{
		return this.getTypeTitle() ;
	}
}
