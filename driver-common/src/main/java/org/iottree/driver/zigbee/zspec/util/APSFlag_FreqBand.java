package org.iottree.driver.zigbee.zspec.util;

public class APSFlag_FreqBand
{
	int val ;
	
	public APSFlag_FreqBand(int v)
	{
		this.val = v ;
	}
	
	public int getAPSFlag()
	{
		return this.val & 0x1F;  //bit 0-4
	}
	
	/**
	 * Indicates if complex descriptor is available for the node
	 * @return
	 */
	public int getNodeFreqBand()
	{
		return this.val & 0xE0 ; // bit 5-7  1110-0000
	}
	
	/**
	 *  Indicates if user descriptor is available for the node
	 * @return
	 */
	public boolean isUserDescAvailable()
	{
		return (this.val & 0x10) > 0 ; // bit 4
	}
	
	public String toString()
	{
		return "aps_flag="+this.getAPSFlag()+" freq_band="+getNodeFreqBand();
	}
}
