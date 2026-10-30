package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;
import org.json.JSONObject;

public class NwkGetRadioChRsp extends EZSPRsp
{
	/**
	 * uint8_t channel Current radio channel.
	 */
	int channel ;
	
	public NwkGetRadioChRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return EzspFrameID.GET_RADIO_CHANNEL.getIntVal() ;
	}

	@Override
	public String getFrmName()
	{
		return "getRadioChannel" ;
	}
	

	public int getChannel()
	{
		return this.channel ;
	}
	
	
	@Override
	protected int getParamsLen()
	{
		return 1 ;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<1)
			return false;
		bs[offset] = (byte)this.channel;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.channel = bs[offset] & 0xFF ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("ch",this.channel);
	}
}
