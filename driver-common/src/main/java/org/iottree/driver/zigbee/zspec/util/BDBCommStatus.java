package org.iottree.driver.zigbee.zspec.util;

/**
 * BDB COMMISSIONING Status
 * @author jason.zhu
 *
 */
public class BDBCommStatus
{
	private static String[] STATUSS=new String[] {
			"SUCCESS",
			"IN_PROGRESS",
			"NO_NETWORK",
			"TL_TARGET_FAILURE",
			"TL_NOT_AA_CAPABLE",
			"TL_NO_SCAN_RESPONSE",
			"TL_NOT_PERMITTED",
			"TCLK_EX_FAILURE",
			"FORMATION_FAILURE",
			"FB_TARGET_IN_PROGRESS",
			"FB_INITIATOR_IN_PROGRESS",
			"FB_NO_IDENTIFY_QUERY_RESPONSE",
			"FB_BINDING_TABLE_FULL",
			"NETWORK_RESTORED",
			"FAILURE"
	};
	
	private int status ;
	
	public BDBCommStatus(int v)
	{
		if(v<0||v>0x0E)
			throw new IllegalArgumentException("state value in [0x00,0x0E]") ;
		this.status = v ;
	}

	public int getStatus()
	{
		return this.status ;
	}
	
	public boolean isStatusOk()
	{
		return this.status == 0 ;
	}
	
	public String getTitle()
	{
		return STATUSS[this.status] ;
	}
	
	public String toString()
	{
		return "0x"+String.format("%02X", this.getStatus())+"-"+this.getTitle() ;
	}
}
