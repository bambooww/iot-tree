package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.util.NVItemIDS;
import org.json.JSONObject;

public class ReadNV_EXT_PAN_ID extends ReadNV
{
	public ReadNV_EXT_PAN_ID()
	{
		super(NVItemIDS.ZCD_NV_EXTENDED_PAN_ID,0);
	}

	
	public String getExtPanID()
	{
		if(this.respNVData==null)
			return null ;
		return DataUtil.transPanId(this.respNVData, 0);
	}
	
	@Override
	protected JSONObject toRetJO()
	{
		return new JSONObject().putOpt("ext_pan_id", this.getExtPanID());
	}
	
}
