package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

public class GetNodeIdRsp extends EZSPRsp
{
	/**
	 * uint16
	 */
	int node_id ;
	
	public GetNodeIdRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x0027;
	}

	@Override
	public String getFrmName()
	{
		return "getNodeId";
	}

	@Override
	protected int getParamsLen()
	{
		return 2;
	}

	
	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		DataUtil.setUInt16(this.node_id, bs, offset);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
		{
			if(failedr!=null)
				failedr.append("unpack params len<2");
			return false;
		}
		this.node_id = DataUtil.getUInt16(bs, offset) ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject r = super.toJO().put("node_id",this.node_id) ;
		return r ;
	}
}
