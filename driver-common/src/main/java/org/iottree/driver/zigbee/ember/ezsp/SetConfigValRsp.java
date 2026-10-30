package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

public class SetConfigValRsp extends EZSPRsp
{
	/**
	 * sl_status_t	uint32_t	See sl_status.h for an enumerated list.
	 sl_status_t status
		SL_STATUS_OK if the configuration value was changed, SL_STATUS_ZIGBEE_EZSP_ERROR 
		if there was an error. Retrievable EZSP errors can be SL_ZIGBEE_EZSP_ERROR_OUT_OF_MEMORY 
		if the new value exceeded the available memory, SL_ZIGBEE_EZSP_ERROR_INVALID_VALUE if the new value was out of bounds, 
		SL_ZIGBEE_EZSP_ERROR_INVALID_ID if the NCP does not recognize configId, 
		SL_ZIGBEE_EZSP_ERROR_INVALID_CALL if configuration values can no longer be modified.
	 */
	SLStatus status ;
	
	
	public SetConfigValRsp(EmberManager emgr)
	{
		super(emgr) ;
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


	public SLStatus getStatus()
	{
		return this.status ;
	}
	

	@Override
	protected int getParamsLen()
	{
		return 4;
	}
	
	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		UTIL_writeStatus(this.owner.getEZSPVersion(),this.status.getIntVal(), bs, offset);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		SLStatus[] rets = new SLStatus[1] ;
		UTIL_readStatus(this.owner.getEZSPVersion(), bs, offset,rets) ;
		this.status = rets[0];
		return true ;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("status",this.status) ;
	}
}
