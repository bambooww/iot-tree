package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.util.NVItemIDS;
import org.json.JSONObject;

public class ReadNV_PANID extends ReadNV
{
	public ReadNV_PANID()
	{
		super(NVItemIDS.ZCD_NV_PANID,0);
	}

	
	public int getPanID()
	{
		if(this.respNVData==null)
			return -1 ;
		return DataUtil.getUInt16(this.respNVData, 0);
	}
	
	@Override
	protected JSONObject toRetJO()
	{
		return new JSONObject().putOpt("pan_id", this.getPanID());
	}
}
