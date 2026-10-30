package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * Causes the stack to leave the current network. This generates a stackStatusHandler callback to 
 * indicate that the network is down. The radio will not be used until after sending a formNetwork 
 * or joinNetwork command.
 * 
 * @author jason.zhu
 *
 */
public class NwkLeaveCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0020 ;
	public static final String FRAME_NAME = "leaveNetwork" ;
	
	public NwkLeaveCmd(EmberManager emgr)
	{
		super(emgr) ;
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
		return 0;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO() ;
	}

}
