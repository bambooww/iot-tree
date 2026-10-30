package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.NwkInitBitmask;
import org.json.JSONObject;

/**
 * Resume network operation after a reboot. The node retains its original type.
 *  This should be called on startup whether or not the node was previously part of a network.
 *   SL_STATUS_NOT_JOINED is returned if the node is not part of a network.
 *    This command accepts options to control the network initialization.
 * 
 * @author jason.zhu
 */
public class NwkInitCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0017 ;
	public static final String FRAME_NAME = "networkInit" ;
	/**
	 * sl_zigbee_network_init_bitmask_t	uint16_t	Bitmask options for sli_zigbee_stack_network_init()
	 * 
		sl_zigbee_network_init_struct_t  Network Initialization parameters.

									sl_zigbee_network_init_bitmask_t bitmask Configuration options for network init.
	 */
	//NwkInitBitmask bitmask ;
	int bitmask ;
	
	
	public NwkInitCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public NwkInitCmd(EmberManager emgr,int bitmask)
	{
		super(emgr) ;
		this.bitmask = bitmask ;
	}
	
	@Override
	public int getFrmID()
	{
		return FRAME_ID;
	}

	@Override
	public String getFrmName()
	{
		return FRAME_NAME ;
	}

	
	public int getBitmask()
	{
		return this.bitmask ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 2;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		DataUtil.setUInt16(this.bitmask, bs, offset);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		this.bitmask = DataUtil.getUInt16(bs, offset);
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("bitmask",this.bitmask) ;
	}

}
