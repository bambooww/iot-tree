package org.iottree.driver.zigbee.zcl.util;

import org.iottree.driver.zigbee.DataUtil;
import org.json.JSONObject;

public class AttrReadStatusVal
{
	int attrId ;
	int status ;
	DataVal dataVal = null ;
	
	public boolean isStatusOk()
	{
		return this.status==0 ;
	}
	
	public int getAttrId()
	{
		return this.attrId ;
	}
	
	public DataVal getDataVal()
	{
		return this.dataVal ;
	}
	
	public int getPackLen()
	{
		if(!isStatusOk())
			return 3 ;
		
		return 4+dataVal.getOctetLen() ;
	}
	
	public int packTo(byte[] data,int offset)
	{
		int plen = getPackLen() ;
		if(data.length-offset<plen)
			return -1 ;
		DataUtil.setUInt16(this.attrId, data, offset); //0-1
		data[offset+2] = (byte)this.status ;
		if(!isStatusOk())
			return 3 ;
		DataType dtp = this.dataVal.getDataType() ;
		data[offset+3] = (byte)dtp.getID() ;
		DataType.packObjValTo(dataVal, data, offset+4) ;
		return plen ;
	}
	
	public JSONObject toJO()
	{
		if(this.dataVal==null)
		{
			return new JSONObject().put("attr_id", this.attrId).put("status", this.status) ;
		}
		JSONObject r = this.dataVal.toJO() ;
		r.put("attr_id", this.attrId) ;
		r.put("status", this.status) ;
		return r ;
	}
	
	@Override
	public String toString()
	{
		String r = " attr_id="+DataUtil.transHex(this.attrId, 4)+" status="+this.status ;
		if(this.status!=0)
			return r ;
		return r += " data_v="+this.dataVal ;
	}
	
	public static AttrReadStatusVal parseFrom(byte[] bs,int offset)
	{
		if(bs.length-offset<3)
			return null ;
		AttrReadStatusVal r = new AttrReadStatusVal() ;
		r.attrId = DataUtil.getUInt16(bs,offset + 0) ; //0-1
		r.status = bs[offset+2] & 0xFF ;
		if(r.status!=0)
			return r ;
		if(bs.length-offset<4)
			return null ;
		int tp_id = bs[offset+3] ;
		r.dataVal = new DataVal() ;
		DataType dtp = DataType.valOfInt(tp_id) ;
		if(dtp==null)
			return null ;
		r.dataVal = dtp.parseObjVal(bs, offset+4) ;
		return r ;
	}
}
