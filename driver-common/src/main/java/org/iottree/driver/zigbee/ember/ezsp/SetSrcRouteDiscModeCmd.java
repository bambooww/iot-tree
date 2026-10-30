package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.EmberSrcRouteDiscMode;
import org.json.JSONObject;

/**
 * Sets source route discovery(MTORR) mode to on, off, reschedule.
 * 
 * @author jason.zhu
 */
public class SetSrcRouteDiscModeCmd extends EZSPCmd
{
	/**
	 * uint8_t mode Source route discovery mode: off:0, on:1, reschedule:2
	 */
	EmberSrcRouteDiscMode mode ;
	
	public SetSrcRouteDiscModeCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public SetSrcRouteDiscModeCmd(EmberManager emgr,EmberSrcRouteDiscMode mode)
	{
		super(emgr) ;
		this.mode = mode ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x005A;
	}

	@Override
	public String getFrmName()
	{
		return "setSourceRouteDiscoveryMode";
	}

	
	public EmberSrcRouteDiscMode getMode()
	{
		return this.mode ;
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
		bs[offset] = (byte)this.mode.getIntVal() ;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.mode = EmberSrcRouteDiscMode.valOfInt(bs[offset] & 0xFF) ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("mode",this.mode) ;
	}

}
