package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.util.NVItemIDS;
import org.json.JSONObject;

/**
 * 在NVItem 中读取网络密钥
 * @author jason.zhu
 *
 */
public class ReadNVNwkKey extends ReadNV
{
	public ReadNVNwkKey()
	{
		super(NVItemIDS.ZCD_NV_NWKKEY,0);
	}

	public int getKeySeqNum()
	{
		if(respNVReadExt==null)
			return -1 ;
		byte[] bs = respNVReadExt.getNVValue() ;
		if(bs.length<1)
			return -1 ;
		return bs[0] & 0xFF ;
	}

	public String getNetworkKey()
	{
		if(respNVReadExt==null)
			return null ;
		byte[] bs = respNVReadExt.getNVValue() ;
		if(bs.length<17)
			return null ;
		return Convert.byteArray2HexStr(bs,1,16) ;
	}
	
	public int getFrameCounter()
	{
		if(respNVReadExt==null)
			return -1 ;
		byte[] bs = respNVReadExt.getNVValue() ;
		if(bs.length<19)
			return -1 ;
		return DataUtil.getUInt16(bs, 17) ;
	}
	
	public JSONObject toRetJO()
	{
		JSONObject ret = new JSONObject() ;
		ret.putOpt("key", this.getNetworkKey())
				.put("seq_num",this.getKeySeqNum()).put("frame_counter", this.getFrameCounter()) ;
		return ret ;
	}
	
}
