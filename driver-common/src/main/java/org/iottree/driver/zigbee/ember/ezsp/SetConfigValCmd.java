package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.EzspConfigId;
import org.json.JSONObject;

public class SetConfigValCmd extends EZSPCmd
{
	/**
	 * sl_zigbee_ezsp_config_id_t uint8_t Identifies a configuration value.
	 * 
	 * sl_zigbee_ezsp_config_id_t configId Identifies which configuration value to change.
	 */
	int configId ;
	
	/**
	 * uint16_t value The new configuration value.
	 */
	int val ;
	
	public SetConfigValCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public SetConfigValCmd(EmberManager emgr,int conf_id,int v)
	{
		super(emgr) ;
		this.configId = conf_id ;
		this.val = v;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x53;
	}

	@Override
	public String getFrmName()
	{
		return "setConfigurationValue";
	}
	
	public int getConfigId()
	{
		return this.configId ;
	}
	
	public EzspConfigId getEzspConfig()
	{
		return EzspConfigId.valOfInt(this.configId) ;
	}
	
	public int getValue()
	{
		return this.val ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 3;
	}
	
	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		bs[offset] = (byte)this.configId;
		DataUtil.setUInt16(this.val, bs, offset+1);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.configId = bs[offset] & 0xFF ;
		this.val = DataUtil.getUInt16(bs, offset+1) ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("config_id",this.configId).put("val", this.val).putOpt("config_n", this.getEzspConfig()) ;
	}
}
