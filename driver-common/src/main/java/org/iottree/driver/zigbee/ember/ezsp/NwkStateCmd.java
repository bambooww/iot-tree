package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * Returns a value indicating whether the node is joining, joined to, or leaving a network.
 * 
 * @author jason.zhu
 *
 */
public class NwkStateCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0018 ;
	public static final String FRAME_NAME = "networkState" ;
	
	public NwkStateCmd(EmberManager emgr)
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
