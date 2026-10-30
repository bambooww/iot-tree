package org.iottree.driver.zigbee.zspec.util;

import org.iottree.driver.zigbee.DataUtil;

public class MacCapabilitiesFlags
{
	public static final int CAPINFO_DEVICETYPE_RFD = 0x00;
	public static final int CAPINFO_ALTPANCOORD = 0x01;
	public static final int CAPINFO_DEVICETYPE_FFD = 0x02;
	public static final int CAPINFO_POWER_AC = 0x04;
	public static final int CAPINFO_RCVR_ON_IDLE = 0x08;
	public static final int CAPINFO_SECURITY_CAPABLE = 0x40;
	public static final int CAPINFO_ALLOC_ADDR = 0x80;
	
	private int cap ;
	
	public MacCapabilitiesFlags(int v)
	{
		this.cap = v ;
	}

	public int getCap()
	{
		return this.cap ;
	}
	
	public String toString()
	{
		return DataUtil.transHex(this.cap,2) ;
	}
}
