package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.NwkStatus;
import org.json.JSONObject;

public class NwkStateRsp extends EZSPRsp
{
	/**
	 * sl_zigbee_network_status_t	uint8_t	The possible join states for a node.
		An sl_zigbee_network_status_t value indicating the current join status.
	 */
	NwkStatus status ;
	
	public NwkStateRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return NwkStateCmd.FRAME_ID ;
	}

	@Override
	public String getFrmName()
	{
		return NwkStateCmd.FRAME_NAME ;
	}
	

	public NwkStatus getStatus()
	{
		return this.status ;
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
		bs[offset] = (byte)this.status.getIntVal();
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.status = NwkStatus.valOfInt(bs[offset] & 0xFF) ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("status",this.status);
	}

}
