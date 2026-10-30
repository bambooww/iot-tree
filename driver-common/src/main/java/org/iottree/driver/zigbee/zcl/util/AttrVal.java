package org.iottree.driver.zigbee.zcl.util;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zcl.ZCLAttr;
import org.json.JSONObject;

public class AttrVal
{
	int attrId ;
	DataVal dataVal = null ;
	
//	public AttrVal()
//	{}
	
//	public AttrVal(int attrid,DataVal dv)
//	{
//		this.attrId = attrid ;
//		this.dataVal = dv ;
//	}
	
	public AttrVal(ZCLAttr attr,Object obj)
	{
		this.attrId = attr.getID() ;
		this.dataVal = new DataVal(attr.getDataType(),obj) ;
	}
	
	public AttrVal(int attr_id,DataType dt,Object obj)
	{
		this.attrId = attr_id ;
		this.dataVal = new DataVal(dt,obj) ;
	}
	
	public int getAttrId()
	{
		return this.attrId ;
	}
	
	public DataVal getDataVal()
	{
		return this.dataVal ;
	}
	
	public Object getAttrObjVal()
	{
		if(this.dataVal==null)
			return null ;
		return this.dataVal.getObjVal() ;
	}
	
	public int getPackLen()
	{
		return 3+dataVal.getOctetLen() ;
	}
	
	public JSONObject toJO()
	{
		JSONObject r = this.dataVal.toJO() ;
		r.put("attr_id", this.attrId) ;
		return r ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.attrId = jo.optInt("attr_id",-1) ;
		if(this.attrId<0)
			return false;
		DataVal dv = new DataVal() ;
		if(!dv.fromJO(jo))
			return false;
		this.dataVal = dv;
		return true;
	}
	
	public byte[] getPackData()
	{
		byte[] r = new byte[this.getPackLen()] ;
		DataUtil.setUInt16(attrId, r, 0); //0-1
		r[2] = (byte)dataVal.getDataType().getID() ;
		int res = DataType.packObjValTo(dataVal, r, 3) ;
		if(res<0)
			throw new RuntimeException("pack obj val failed") ;
		return r ;
	}
	
	@Override
	public String toString()
	{
		String r = " attr_id="+DataUtil.transHex(this.attrId, 4);
		return r += " data_v="+this.dataVal ;
	}
	
	public static AttrVal parseFrom(byte[] bs,int offset)
	{
		if(bs.length-offset<3)
			return null ;
		
		int attr_id = DataUtil.getUInt16(bs,offset + 0) ; //0-1
		int tp_id = bs[offset+2] ;
		//r.dataVal = new DataVal() ;
		DataType dtp = DataType.valOfInt(tp_id) ;
		if(dtp==null)
			return null ;
		DataVal objv = dtp.parseObjVal(bs, offset+3) ;
		if(objv==null)
			return null;
		return new AttrVal(attr_id,dtp,objv.getObjVal()) ;
	}
}
