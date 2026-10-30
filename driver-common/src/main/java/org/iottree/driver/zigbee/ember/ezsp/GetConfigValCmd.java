package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

public class GetConfigValCmd extends EZSPCmd
{
	/**
	 * sl_zigbee_ezsp_config_id_t uint8_t Identifies a configuration value.
	 */
	int configId ;
	
	public GetConfigValCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public GetConfigValCmd(EmberManager emgr,int conf_id)
	{
		super(emgr) ;
		this.configId = conf_id ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x52;
	}

	@Override
	public String getFrmName()
	{
		return "getConfigurationValue";
	}

	public int getConfigId()
	{
		return this.configId ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 1;
	}
	
	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		bs[offset] = (byte)this.configId;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.configId = bs[offset] & 0xFF ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("config_id",this.configId) ;
	}
}
