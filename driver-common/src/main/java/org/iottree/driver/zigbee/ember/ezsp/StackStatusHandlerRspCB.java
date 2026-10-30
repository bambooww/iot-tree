package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

/**
 * A callback invoked when the status of the stack changes. 
 * If the status parameter equals SL_STATUS_NETWORK_UP, 
 * then the getNetworkParameters command can be called to obtain the new network parameters. 
 * If any of the parameters are being stored in nonvolatile memory by the Host, 
 * the stored values should be updated.
 */
public class StackStatusHandlerRspCB extends EZSPRsp
{
	SLStatus sl_status ;
	
	public StackStatusHandlerRspCB(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x0019 ;
	}

	@Override
	public String getFrmName()
	{
		return "stackStatusHandler";
	}
	

	public SLStatus getSLStatus()
	{
		return this.sl_status ;
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
		if(this.sl_status==null)
			return false;
		UTIL_writeStatus(this.owner.getEZSPVersion(), this.sl_status.getIntVal(), bs, offset) ;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<1)
			return false;
		SLStatus[] rets= new SLStatus[1] ;
		UTIL_readStatus(this.owner.getEZSPVersion(), bs, offset, rets) ;
		this.sl_status = rets[0] ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("sl_status",this.sl_status);
	}
}
