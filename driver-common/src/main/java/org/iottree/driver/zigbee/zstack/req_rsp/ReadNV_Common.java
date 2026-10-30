package org.iottree.driver.zigbee.zstack.req_rsp;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataBuffer;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.ZStackManager;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVLengthSREQ;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVLengthSRSP;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVReadExtSREQ;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVReadExtSRSP;
import org.iottree.driver.zigbee.zstack.msg_util.GetDeviceInfoSREQ;
import org.iottree.driver.zigbee.zstack.msg_util.GetDeviceInfoSRSP;
import org.iottree.driver.zigbee.zstack.util.NVItemIDS;
import org.json.JSONObject;

public class ReadNV_Common extends ReadNV
{
	public ReadNV_Common(int zcd_code)
	{
		super(zcd_code,0);
	}
	
	public ReadNV_Common(int nv_itemid,int offset)
	{
		super(nv_itemid,offset);
	}
	
	@Override
	protected JSONObject toRetJO()
	{
		JSONObject ret = new JSONObject() ;
		String nv_hex = "" ;
		int c = 0 ;
		if(this.respNVData!=null)
		{
			c = this.respNVData.length ;
			nv_hex = Convert.byteArray2HexStr(this.respNVData) ;
		}
		ret.put("nv_hex", nv_hex) ;
		ret.put("len", c) ;
		return ret;
	}
	
	
}
