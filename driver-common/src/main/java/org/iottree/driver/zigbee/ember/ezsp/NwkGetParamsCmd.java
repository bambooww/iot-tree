package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

public class NwkGetParamsCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0028 ;
	public static final String FRAME_NAME = "getNetworkParameters" ;
	
	public NwkGetParamsCmd(EmberManager emgr)
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
