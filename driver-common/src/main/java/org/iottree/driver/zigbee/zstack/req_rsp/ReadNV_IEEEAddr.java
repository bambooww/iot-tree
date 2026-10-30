package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.util.NVItemIDS;
import org.json.JSONObject;

public class ReadNV_IEEEAddr extends ReadNV
{
	public ReadNV_IEEEAddr()
	{
		super(NVItemIDS.ZCD_NV_EXTADDR,0);
	}

	public String getIEEEAddr()
	{
		if(this.respNVData==null)
			return null ;
		return DataUtil.transPanId(this.respNVData, 0);
	}
	
	@Override
	protected JSONObject toRetJO()
	{
		return new JSONObject().putOpt("ieee_addr", this.getIEEEAddr());
	}

}
