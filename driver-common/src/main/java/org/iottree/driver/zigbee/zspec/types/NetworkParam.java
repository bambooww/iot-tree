package org.iottree.driver.zigbee.zspec.types;

public class NetworkParam
{
	public int panID ;//: number;
	public String extPanID ; //extendedPanID: string; // `0x${string}` same as IEEE address
	public int channel ;//: number;
	public int nwkUpdateID ; //: number;
	
	public NetworkParam(int pan_id,String ext_pan_id,int ch,int nwk_update_id)
	{
		this.panID = pan_id ;
		this.extPanID = ext_pan_id ;
		this.channel = ch ;
		this.nwkUpdateID = nwk_update_id ;
	}
}
