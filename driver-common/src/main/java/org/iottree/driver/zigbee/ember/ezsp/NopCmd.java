package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * A command which does nothing.
 *  The Host can use this to set the sleep mode or to check the status of the NCP.
 *  
 * @author jason.zhu
 */
public class NopCmd extends EZSPCmd
{
	
	public NopCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x0005;
	}

	@Override
	public String getFrmName()
	{
		return "nop";
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
