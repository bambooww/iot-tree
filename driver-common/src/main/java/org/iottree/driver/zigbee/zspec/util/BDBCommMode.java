package org.iottree.driver.zigbee.zspec.util;

public class BDBCommMode
{
	/**
	 "BDB_COMMISSIONING_INITIALIZATION",
			"BDB_COMMISSIONING_NWK_STEERING",
			"BDB_COMMISSIONING_FORMATION",
			"BDB_COMMISSIONING_FINDING_BINDING",
			"BDB_COMMISSIONING_TOUCHLINK",
			"BDB_COMMISSIONING_PARENT_LOST"
	 */
	private static String[] MODES=new String[] {
			"INITIALIZATION",
			"NWK_STEERING",
			"FORMATION",
			"FINDING_BINDING",
			"TOUCHLINK",
			"PARENT_LOST"
	};
	
	private int mode ;
	
	public BDBCommMode(int v)
	{
		if(v<0||v>0x05)
			throw new IllegalArgumentException("state value in [0x00,0x05]") ;
		this.mode = v ;
	}

	public int getMode()
	{
		return this.mode ;
	}
	
	public String getTitle()
	{
		return MODES[this.mode] ;
	}
	
	public String toString()
	{
		return "0x"+String.format("%02X", this.getMode())+"-"+this.getTitle() ;
	}
}
