package org.iottree.driver.zigbee.zcl.util;

import java.io.UnsupportedEncodingException;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class DataVal
{
	private DataType dataTP = null ;
	
	private int octetLen = -1; // -1=invalid
	
	private Object objVal = null;
	
	public DataVal() {}
	
	DataVal(DataType tp,int len,Object objv)
	{
		this.dataTP = tp ;
		this.octetLen = len ;
		this.objVal = objv ;
	}
	
	public DataVal(DataType tp,Object objv)
	{
		this.dataTP = tp ;
		this.objVal = objv ;
		//this.octetLen = calOctetLen(tp,objv) ;
	}
	
	public static int calOctetLen(DataType dtp,Object objv)
	{
		int fixlen = dtp.getOctLen() ;
		if(fixlen>=0)
			return fixlen ;
		
		try
		{
			int f_len = dtp.getFirstOctLen();
			int len = 0 ;
			switch(dtp)
			{
			case char_str:
			case oct_str:
				len += f_len ;
				String pv = (String)objv ;
				byte[] bs = null;
				if(Convert.isNotNullEmpty(pv))
					bs = pv.getBytes("ISO-8859-1") ;
				if(bs!=null)
					len += bs.length ;
				break ;
			case oct_str16:
			case char_str16:
				len += f_len ;
				pv = (String)objv ;
				bs = null;
				if(Convert.isNotNullEmpty(pv))
					bs = pv.getBytes("UTF-16") ;
				if(bs!=null)
					len += bs.length ;
				break ;
			default:
				throw new RuntimeException("unsupported paramter data type="+dtp.name()) ;
			}
			return len ;
		}
		catch(UnsupportedEncodingException uee)
		{
			uee.printStackTrace();
			return -1 ;
		}
	}
	
	public boolean isInvalid()
	{
		return this.octetLen<0 ;
	}
	
	public DataType getDataType()
	{
		return this.dataTP ;
	}
	
	public int getOctetLen()
	{
		if(this.octetLen>=0)
			return this.octetLen ;
		if(objVal==null)
			return -1 ;
		return this.octetLen = calOctetLen(this.dataTP,this.objVal) ;
	}
	
	public Object getObjVal()
	{
		return this.objVal ;
	}
	
	void RT_setObjVal(Object objv)
	{
		this.objVal = objv ;
		this.octetLen = -1 ;
	}
	
	public JSONObject toJO()
	{
		JSONObject r = new JSONObject() ;
		if(dataTP!=null)
		{
			r.put("tp", this.dataTP.name()) ;
			r.put("tp_id", this.dataTP.getID()) ;
		}
		r.putOpt("val", this.objVal) ;
		r.put("oct_len", this.getOctetLen()) ;
		return r ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		int tp_id = jo.optInt("tp_id", -1) ;
		if(tp_id<0)
			return false;
		this.dataTP = DataType.valOfInt(tp_id) ;
		if(this.dataTP==null)
			return false;
		this.objVal = jo.opt("val") ;
		this.octetLen = jo.optInt("oct_len",-1) ;
		return true;
	}
	
	public String toString()
	{
		return dataTP.name()+"["+this.octetLen+"]="+toValString() ;
	}
	
	public String toValString()
	{
		if(objVal==null)
			return "";
		return objVal.toString();
	}
	/**
	 * check bit pos 1 or 0 when bitmap
	 * @param pos
	 * @return
	 */
	public boolean checkBitmap(int pos)
	{
		if(!this.dataTP.isBitmap())
			throw new RuntimeException("no bitmap type") ;
		long v = (Long)objVal ;
		int oct_len = this.dataTP.getOctLen() ;
		if(pos<0||pos>=oct_len*8)
			throw new IllegalArgumentException("pos must in [0,"+(oct_len*8-1)+"]") ;
		return (v & (1L<<pos))!=0 ;
	}
}
