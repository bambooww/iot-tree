package org.iottree.driver.zigbee.ember.ezsp;

import java.util.ArrayList;
import java.util.List;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.EmberCounterType;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;
import org.json.JSONObject;

public class ReadClearCountersRsp extends EZSPRsp
{
	/**
	 * sl_zigbee_network_status_t	uint8_t	The possible join states for a node.
		An sl_zigbee_network_status_t value indicating the current join status.
	 */
	ArrayList<Integer> counts ;
	
	public ReadClearCountersRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return EzspFrameID.READ_AND_CLEAR_COUNTERS.getIntVal() ;
	}

	@Override
	public String getFrmName()
	{
		return "readAndClearCounters" ;
	}
	

	public List<Integer> getCounters()
	{
		return this.counts ;
	}
	
	
	@Override
	protected int getParamsLen()
	{
		return 1 ;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		int c = EmberCounterType.COUNT.getIntVal();
		counts = new ArrayList<>(c) ;
		for(int i = 0 ; i < c ; i ++)
		{
			counts.add(DataUtil.getUInt16(bs, offset+i*2)) ;
		}
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().putOpt("counts",this.counts);
	}
}
