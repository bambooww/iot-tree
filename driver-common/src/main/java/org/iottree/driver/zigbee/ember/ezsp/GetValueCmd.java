package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * Reads a value from the NCP.
 * @author jason.zhu
 *
 */
public class GetValueCmd  extends EZSPCmd
{
	/**
	 * sl_zigbee_ezsp_value_id_t	uint8_t	Identifies a value.
	 * 
		sl_zigbee_ezsp_value_id_t valueId Identifies which value to read.
	 */
	int valueId ;
	
	public GetValueCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public GetValueCmd(EmberManager emgr,int val_id)
	{
		super(emgr) ;
		this.valueId = val_id ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x00AA;
	}

	@Override
	public String getFrmName()
	{
		return "getValue";
	}

	
	public int getValueId()
	{
		return this.valueId ;
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
		bs[offset] = (byte)this.valueId ;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.valueId = bs[offset] & 0xFF ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("value_id",this.valueId) ;
	}
}
