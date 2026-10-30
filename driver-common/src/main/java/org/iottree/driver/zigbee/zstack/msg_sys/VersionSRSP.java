package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class VersionSRSP extends MsgRecv
{
	public static final int CMD0 = 0x61 ;
	public static final int CMD1 = 0x02 ;
	
	public VersionSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_VERSION";
	}

	@Override
	public int getDataLenMin()
	{
		return 5;
	}
	
//	@Override
//	public byte[] getData()
//	{
//		return this.data;
//	}
//
//	@Override
//	protected boolean parseData(byte[] data, StringBuilder failedr)
//	{
//		if(data==null||data.length<5)
//			return false;
//		this.data = data ;
//		return true;
//	}
	
	public int getTransportRev()
	{
		return data[0] & 0xFF ;
	}
	
	public int getProductId()
	{
		return data[1] & 0xFF ;
	}
	
	public int getMajorRel()
	{
		return data[2] & 0xFF ;
	}
	
	public int getMinorRel()
	{
		return data[3] & 0xFF ;
	}
	
	public int getMaintRel()
	{
		return data[4] & 0xFF ;
	}
	
//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " = [transportrev:"+getTransportRev()+" product="+this.getProductId()+ " majorrel="+this.getMajorRel()+" minorrel="+this.getMinorRel()+"]";
//	}

	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("transport_rev",this.getTransportRev())
				.putOpt("product_id", this.getProductId())
				.put("majorrel", this.getMajorRel())
				.put("minorrel", this.getMinorRel())
				.put("maintrel", this.getMaintRel())
				.put("data_hex", Convert.byteArray2HexStr(this.data, 0, 5));
	}
	
	public boolean fromRecvJO(JSONObject jo)
	{
		String data_hex = jo.optString("data_hex") ;
		if(Convert.isNullOrEmpty(data_hex))
			return false;
		this.data = Convert.hexStr2ByteArray(data_hex) ;
		return this.data.length>=5;
	}

}
