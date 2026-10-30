package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * The application may call this function when contact with the network has been lost.
 *  The most common usage case is when an end device can no longer communicate with its parent 
 *  and wishes to find a new one. Another case is when a device has missed a Network Key update 
 *  and no longer has the current Network Key. The stack will call sl_zigbee_ezsp_stack_status_handler 
 *  to indicate that the network is down, then try to re-establish contact with the network 
 *  by performing an active scan, choosing a network with matching extended pan id, 
 *  and sending a ZigBee network rejoin request. A second call to the sl_zigbee_ezsp_stack_status_handler callback 
 *  indicates either the success or the failure of the attempt. The process takes approximately 
 *  150 milliseconds per channel to complete.
 * 
 * @author jason.zhu
 *
 */
public class NwkFindAndRejoinCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0021 ;
	public static final String FRAME_NAME = "findAndRejoinNetwork" ;
	
	/**
	 * This parameter tells the stack whether to try to use the current network key. If it has the current network key
	 *  it will perform a secure rejoin (encrypted). If this fails the device should try an unsecure rejoin.
	 *   If the Trust Center allows the rejoin then the current Network Key will be sent encrypted using the device's Link Key.
	 */
	boolean have_cur_nwk_key ;
	
	/**
	 * A mask indicating the channels to be scanned. See sli_zigbee_stack_start_scan for format details. 
	 * A value of 0 is reinterpreted as the mask for the current channel.
	 */
	long channel_mask ; //uint32
	
	/**
	 * A sl_zigbee_rejoin_reason_t variable which could be passed in if there is actually a reason for rejoin, 
	 * or could be left at 0xFF
	 */
	int reson ; //uint8
	
	/**
	 * The rejoin could be triggered with a different nodeType. 
	 * This value could be set to 0 or SL_ZIGBEE_DEVICE_TYPE_UNCHANGED if not needed.
	 */
	int node_type ;//uint8
	
	public NwkFindAndRejoinCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	/**
	 * 
	 * @param emgr
	 * @param have_cur_nwk_key
	 * @param ch_mask
	 * @param reson
	 * @param node_tp 0 or EmberNodeTy
	 */
	public NwkFindAndRejoinCmd(EmberManager emgr,boolean have_cur_nwk_key,long ch_mask)
	{
		super(emgr) ;
		this.have_cur_nwk_key = have_cur_nwk_key ;
		this.channel_mask = ch_mask ;
		this.reson = 0xFF ;
		this.node_type = 0 ; //unknown or unchanged
	}
	
	@Override
	public int getFrmID()
	{
		return FRAME_ID;
	}

	@Override
	public String getFrmName()
	{
		return FRAME_NAME;
	}

	@Override
	protected int getParamsLen()
	{
		return 7;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		bs[offset] = this.have_cur_nwk_key?(byte)0x01:(byte)0x00 ;
		DataUtil.setUInt32(this.channel_mask, bs, offset+1); //1-4
		bs[offset+5] = (byte)this.reson ;
		bs[offset+6] = (byte)this.node_type ;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		this.have_cur_nwk_key = bs[offset] !=0 ;
		this.channel_mask = DataUtil.getUInt32(bs, offset+1) ; //1-4
		this.reson = bs[offset+5] & 0xFF ;
		this.node_type = bs[offset+6] & 0xFF ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO() ;
	}

}
