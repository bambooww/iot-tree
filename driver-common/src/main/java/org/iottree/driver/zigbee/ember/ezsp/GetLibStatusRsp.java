package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

public class GetLibStatusRsp extends EZSPRsp
{
	/**
	 * sl_zigbee_library_status_t 	uint8_t	The presence and status of the Ember library.
	 * 
	 * sl_zigbee_library_status_t status The status of the library being queried.
	 */
	int status ;
	
	public GetLibStatusRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
//	public GetLibStatusRsp(int status)
//	{
//		this.status = status ;
//	}
	
	@Override
	public int getFrmID()
	{
		return 0x01;
	}

	@Override
	public String getFrmName()
	{
		return "getLibraryStatus";
	}

	public int getStatus()
	{
		return this.status ;
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
		bs[offset] = (byte)this.status;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.status = bs[offset] & 0xFF ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("status",this.status) ;
	}
}
