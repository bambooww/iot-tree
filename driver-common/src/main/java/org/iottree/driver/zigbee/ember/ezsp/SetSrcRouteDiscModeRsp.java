package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

public class SetSrcRouteDiscModeRsp extends EZSPRsp
{
	/**
	 * uint32_t remainingTime
	 * Remaining time(ms) until next MTORR broadcast if the mode is on, 
	 * MAX_INT32U_VALUE if the mode is off.
	 */
	long remain_time ;
	
	public SetSrcRouteDiscModeRsp(EmberManager emgr)
	{
		super(emgr) ;
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

	public long getRemainingTime()
	{
		return this.remain_time ;
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
		DataUtil.setUInt32(this.remain_time, bs, offset);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
		{
			if(failedr!=null)
				failedr.append("unpack params len<0");
			return false;
		}
		this.remain_time = DataUtil.getUInt32(bs, offset) ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject r = super.toJO().put("remain_time",this.remain_time);
		return r ;
	}
}
