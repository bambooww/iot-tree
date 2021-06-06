package org.iottree.core;

import org.iottree.core.util.Convert;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.JSObMap;

import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;

/**
 * val types
 * 
 * @author zzj
 *
 */
@SuppressWarnings("serial")
public class UAVal //extends JSObMap
{
	public static String[] ValTPTitles = new String[] {"none","bool","byte","char","int16","int32","int64","float","double","str","date",
			"uint16","uint32","uint64"} ;
	public static Object[] ValTPVals = new Object[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13} ;
	
	public static enum ValTP
	{
		vt_none(0,-1,null),
		vt_bool(1,1,Boolean.class),
		vt_byte(2,1,Byte.class),
		vt_char(3,1,Character.class),//u8
		vt_int16(4,2,Short.class),
		vt_int32(5,4,Integer.class),
		vt_int64(6,8,Long.class),
		vt_float(7,4,Float.class),
		vt_double(8,8,Double.class),
		vt_str(9,-1,String.class),
		vt_date(10,8,java.util.Date.class),
		vt_uint16(11,2,Integer.class),
		vt_uint32(12,4,UnsignedInteger.class),
		vt_uint64(13,8,UnsignedLong.class);
		
		private final int val ;
		private final int byteLen;
		private final Class<?> valC;
		
		ValTP(int v,int blen,Class<?> c)
		{
			val = v ;
			byteLen = blen ;
			valC = c ;
		}
		
		public int getInt()
		{
			return val ;
		}
		
		public Class<?> getValClass()
		{
			return valC ;
		}
		
		public String getStr()
		{
			return this.toString().substring(3);
		}
		
		public int getValByteLen()
		{
			return byteLen;
		}
		
		public boolean isNumberVT()
		{
			switch(val)
			{
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 11:
			case 12:
			case 13:
				return true;
			default:
				return false;
			}
		}
		
		public boolean isNumberFloat()
		{
			switch(val)
			{
			case 7:
			case 8:
				return true;
			default:
				return false;
			}
		}
	}
	
	public static ValTP getValTp(int iv)
	{
		switch(iv)
		{
		case 0: return  ValTP.vt_none;
		case 1: return ValTP.vt_bool;
		case 2: return ValTP.vt_byte;
		case 3: return ValTP.vt_char;
		case 4: return ValTP.vt_int16;
		case 5: return ValTP.vt_int32;
		case 6: return ValTP.vt_int64;
		case 7: return ValTP.vt_float;
		case 8: return ValTP.vt_double;
		case 9: return ValTP.vt_str;
		case 10:return ValTP.vt_date;
		case 11:return ValTP.vt_uint16;
		case 12:return ValTP.vt_uint32;
		case 13:return ValTP.vt_uint64;
		default:
			throw new RuntimeException("unknow valtp="+iv) ;
		}
	}
	
	public static Object transStr2ObjVal(ValTP tp,String strv)
	{
		if(tp==ValTP.vt_str)
			return strv ;
		if(strv==null||"".contentEquals(strv))
			return null ;
	
		switch(tp)
		{
		case vt_bool:
			return "true".equalsIgnoreCase(strv) || Convert.parseToDouble(strv, -1)>0;
		case vt_byte:
			return Byte.parseByte(strv) ;
		case vt_char:
			return strv.charAt(0) ;
		case vt_int16:
			return Short.parseShort(strv) ;
		case vt_int32:
			return Integer.parseInt(strv) ;
		case vt_int64:
			return Long.parseLong(strv) ;
		case vt_float:
			return Float.parseFloat(strv) ;
		case vt_double:
			return Double.parseDouble(strv) ;
		case  vt_date:
			return Convert.toCalendar(strv).getTime() ;
		case  vt_uint16:
			int u16 = Integer.parseInt(strv) ;
			return u16&0xFFFF ;
		case  vt_uint32:
			UnsignedInteger uint= UnsignedInteger.valueOf(strv) ;
			return uint ;
		case  vt_uint64:
			UnsignedLong ulong = UnsignedLong.valueOf(strv) ;
			return ulong ;
		default:
			throw new RuntimeException("unknow valtp="+tp) ;
		}
	}
	
	
	private Object objVal = null ;
	
	private boolean bValid = false;
	
	private long valDT = System.currentTimeMillis() ;
	
	private long valChgDT = System.currentTimeMillis() ;
	/**
	 * 
	 */
	private String valErr = null;
	
	private Exception valErrOb = null ;
	
	public UAVal()
	{
		
	}
	
	public UAVal(boolean bvalid,Object val,long valdt)
	{
		bValid = bvalid ;
		objVal = val ;
		if(valdt>0)
			valDT = valdt ;
	}
	
	public UAVal copyMe()
	{
		UAVal r = new UAVal() ;
		r.objVal = this.objVal ;
		r.bValid = this.bValid ;
		r.valDT = this.valDT ;
		r.valChgDT = this.valChgDT ;
		r.valErr = this.valErr ;
		r.valErrOb = this.valErrOb ;
		return r ;
	}
	
	public boolean equals(Object o)
	{
		if(o==null)
			return false ;
		UAVal ov = (UAVal)o ;
		
		if(this.bValid!=ov.bValid)
			return false;
		if(this.valDT!=ov.valDT)
			return false;
		if(this.valChgDT!=ov.valChgDT)
			return false ;
		if(this.objVal==null)
		{
			if(ov.objVal!=null)
				return false;
			else
				return true ;
		}
		return this.objVal.equals(ov.objVal) ;
	}
	
	synchronized public void setVal(boolean bvalid,Object val,long valdt)
	{
		if(!bvalid)
		{
			if(!bValid)
				return ;
			
			bValid=false;
			this.valChgDT = valdt;
			return ;
		}
		
		if(!bValid)
		{
			bValid = true ;
			objVal = val ;
			this.valChgDT = valDT = valdt;
			return ;
		}
		
		if(val.equals(objVal))
		{
			valDT = valdt;
			return ;
		}
		
		objVal = val ;
		this.valChgDT = valDT = valdt;
	}
	
	synchronized public void setValErr(String err)
	{
		this.valErr = err ;
		if(!bValid)
			return ;
		
		bValid = false ;
		objVal = null ;
		this.valChgDT = valDT = System.currentTimeMillis();
	}
	
	synchronized public void setValException(String err,Exception e)
	{
		this.valErr = err ;
		this.valErrOb = e ;
		setValErr(err);
		
//		bValid = false ;
//		objVal = null ;
//		if(Convert.isNullOrEmpty(err))
//			err = e.getMessage() ;
		
	}
	
	public Object getObjVal()
	{
		return objVal ;
	}
	
	public boolean isValid()
	{
		return bValid ;
	}
	
	public long getValDT()
	{
		return valDT ;
	}
	
	public long getValChgDT()
	{
		return valChgDT ;
	}
	
	public String getErr()
	{
		return valErr ;
	}
	
//	protected Object JS_get(String  key)
//	{
//		switch(key.toLowerCase())
//		{
//		case "pv":
//			return objVal ;
//		case "up_dt":
//			
//		}
//		return null ;
//	}
}
