package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * Returns the EUI64 ID of the local node.
 * 
 * @author jason.zhu
 */
public class GetEui64Cmd extends EZSPCmd
{
	
	public GetEui64Cmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x0026;
	}

	@Override
	public String getFrmName()
	{
		return "getEui64";
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
