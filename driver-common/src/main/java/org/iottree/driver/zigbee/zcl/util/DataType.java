package org.iottree.driver.zigbee.zcl.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.iottree.driver.zigbee.DataUtil;
import kotlin.NotImplementedError;

/**
 * p2-41 
 * @author jason.zhu
 *
 */
public enum DataType
{
	nodata(0x00,0,"")  //Null
	,data8(0x08,1,"byte") //8 - bit data  //General Data Discrete
	,data16(0x09,2,"int16")
	,data24(0x0a,3,"int32")
	,data32(0x0b,4,"int32")
	,data40(0x0c,5,"int64")
	,data48(0x0d,6,"int64")
	,data56(0x0e,7,"int64")
	,data64(0x0f,8,"int64") //64-bit data
	
	,bool(0x10,1,"bool")
	
	,map8(0x18,1,  "int16") //8 - bit bitmap  used for bit flag
	,map16(0x19,2,"int16")
	,map24(0x1a,3,"int32")
	,map32(0x1b,4,"int32")
	,map40(0x1c,5,"int64")
	,map48(0x1d,6,"int64")
	,map56(0x1e,7,"int64")
	,map64(0x1f,8,"int64") //64-bit bitmap
	
	,uint8(0x20,1,"uint16")  //unsigned integer analog
	,uint16(0x21,2,"uint16")
	,uint24(0x22,3,"uint32")
	,uint32(0x23,4,"uint32")
	,uint40(0x24,5,"uint64")
	,uint48(0x25,6,"uint64")
	,uint56(0x26,7,"uint64")
	,uint64(0x27,8,"uint64")
	
	,int8(0x28,1,"byte")  //signed integer analog
	,int16(0x29,2,"int16")
	,int24(0x2a,3,"int32")
	,int32(0x2b,4,"int32")
	,int40(0x2c,5,"int64")
	,int48(0x2d,6,"int64")
	,int56(0x2e,7,"int64")
	,int64(0x2f,8,"int64")
	
	,enum8(0x30,1,"int16") //Enumeration Discrete
	,enum16(0x31,2,"int32")
	
	 //floating point analog
	,semi(0x38,2,"float") //semi-precision
	,single(0x39,4,"float") // single precision
	,float64(0x3a,8,"double") //double precision,doc short name is double
	
	//string discrete
	,oct_str(0x41,-1,1,"str") // len defined in first octet and 0xff is invalid, =byte[]
	,char_str(0x42,-1,1,"str") // character string len defined in first octet and 0xff is invalid = string 
	
	,oct_str16(0x43,-1,2,"str") // len defined in first two octets and 0xffff is invalid, short[]
	,char_str16(0x44,-1,2,"str") // character string len defined in first two octets and 0xffff is invalid = utf16
	
	,array(0x48,-1,2,"")  // 2 + sum of lengths of contents
	,struct(0x4c,-1,2,"")  // 2 + sum of lengths of contents
	
	,set(0x50,-1,-1,"") // sum of lengths of contents
	,bag(0x51,-1,-1,"") // sum of lengths of contents
	
	,ToD(0xe0,4,"int32") //time of day
	,date(0xe1,4,"int32")  //Date
	,UTC(0xe2,4,"uint32") //UTCTime
	
	,clusterId(0xe8,2,"int32") //Cluster ID
	,attribId(0xe9,2,"int32") //Attributre ID
	,bacOID(0xea,4,"int64") //BACnet OID
	
	,EUI64(0xf0,8,"str") // IEEE Address
	,key128(0xf1,16,"str") // 128-bit security key
	//opaque
	
	,unk(0xff,0,"") // unknown
	;

	private final int id;
	private final int len; //byte len
	
	private final int firstLenOctets;
	
	private final String ua_val_tp ;

	DataType(int id, int len,String ua_val_tp)
	{
		this.id = id;
		this.len = len;
		this.firstLenOctets =-1;
		this.ua_val_tp = ua_val_tp ;
	}
	
	DataType(int id, int len,int first_len_oct,String ua_val_tp)
	{
		this.id = id;
		this.len = len;
		this.firstLenOctets = first_len_oct ;
		this.ua_val_tp = ua_val_tp ;
	}

	public int getID()
	{
		return this.id;
	}

	public int getOctLen()
	{
		return len;
	}
	
	public String getUAValTP()
	{
		return this.ua_val_tp ;
	}
	
	public int getFirstOctLen()
	{
		return this.firstLenOctets ;
	}
	
	public boolean isGeneralData()
	{
		return (this.id>=0x08 && this.id<=0x0f) ;
	}
	
	public boolean isBitmap()
	{
		return (this.id>=0x18 && this.id<=0x1f);
	}
	
	public boolean isBool()
	{
		return this.id == 0x10 ;
	}
	
	public boolean isUnsignedInt()
	{
		return (this.id>=0x20 && this.id<=0x27) ;
	}
	
	public boolean isSignedInt()
	{
		return (this.id>=0x28 && this.id<=0x2f) ;
	}
	
	public boolean isFloatingPoint()
	{
		return (this.id>=0x38 && this.id<=0x3a) ;
	}
	
	public boolean isString()
	{
		return (this.id>=0x41 && this.id<=0x44) ;
	}
	
	/**
	 * 计算变长数据值
	 * 
	 * //string discrete
	,oct_str(0x41,-1,1,"str") // len defined in first octet and 0xff is invalid, =byte[]
	,char_str(0x42,-1,1,"str") // character string len defined in first octet and 0xff is invalid = string 
	
	,oct_str16(0x43,-1,2,"str") // len defined in first two octets and 0xffff is invalid, short[]
	,char_str16(0x44,-1,2,"str") // character string len defined in first two octets and 0xffff is invalid = utf16
	
	,array(0x48,-1,2,"")  // 2 + sum of lengths of contents
	,struct(0x4c,-1,2,"")  // 2 + sum of lengths of contents
	
	,set(0x50,-1,-1,"") // sum of lengths of contents
	,bag(0x51,-1,-1,"") // sum of lengths of contents
	
	 * @param objv
	 * @return
	 */
	public int calcVarOctLen(Object objv)
	{
		switch(this)
		{
		case oct_str:
		case char_str:
			byte[] strbs;
			String strv = (String)objv ;
			try
			{
				strbs = strv.getBytes("ISO-8859-1");
			}
			catch ( UnsupportedEncodingException e)
			{
				throw new RuntimeException(e) ;
			}
			if(strbs.length>=0xFF)
				throw new RuntimeException("str is too long 0xff") ;
			return 1+ strbs.length ;
		case oct_str16:
		case char_str16:
			strv = (String)objv ;
			try
			{
				strbs = strv.getBytes("UTF-16") ;
			}
			catch ( UnsupportedEncodingException e)
			{
				throw new RuntimeException(e) ;
			}
			if(strbs.length>=0xFFFF)
				throw new RuntimeException("str is too long 0xffff") ;
			return 2+ strbs.length ;
		case array:
		case struct:
			byte[] arrs = (byte[])objv ;
			return 2+arrs.length ;
		default:
			throw new RuntimeException("not support var value len with "+this.name()) ;
		}
	}
	/**
	 * 只把objval打包到数据包
	 * @param objval
	 * @param data
	 * @param offset
	 */
	public static int packObjValTo(DataType d_tp,Object objval,byte[] data,int offset)
	{
		DataVal dv = new DataVal(d_tp, objval) ;
		return packObjValTo(dv,data,offset) ;
	}
	
	public static int packObjValTo(DataVal dval,byte[] data,int offset)
	{
		DataType d_tp = dval.getDataType() ;
		if(d_tp.len==0)
			return 0;
		Object objval = dval.getObjVal() ;
		if(d_tp.id>=0x08 && d_tp.id<=0x0f)
		{
			System.arraycopy((byte[])objval, 0, data, offset, d_tp.len);
			return d_tp.len ;
		}
		
		if(d_tp.id==0x10)
		{
			boolean b = (Boolean)objval ;
			if(b)
				data[offset] = 0x01 ;
			else
				data[offset] = 0x00 ;
			return 1 ;
		}
		
		if(d_tp.id>=0x18 && d_tp.id<=0x1f)
		{//bitmap
			long val = (Long)objval ;
			for(int i = 0 ; i < d_tp.len ; i ++)
			{
				data[offset+i] = (byte)((val>>(i*8)) &0xFF) ;
			}
			return d_tp.len ;
		}
		
		if(d_tp.id>=0x20 && d_tp.id<=0x27)
		{//unsigned int   uint64-is not support
			long val = ((Number)objval).longValue() ;
			for(int i = 0 ; i < d_tp.len ; i ++)
			{
				data[offset+i] = (byte)((val>>(i*8)) &0xFF) ;
			}
			return d_tp.len ;
		}
		
		if(d_tp.id>=0x28 && d_tp.id<=0x2f)
		{//signed int   uint64-is not support
			long val = (Long)objval ;
			for(int i = 0 ; i < d_tp.len ; i ++)
			{
				data[offset+i] = (byte)((val>>(i*8)) &0xFF) ;
			}
			return d_tp.len ;
		}
		
		switch(d_tp.id)
		{ // enumeration
		case 0x30:
			int tmpv = (Integer)objval ;
			data[offset] =  (byte)(tmpv & 0xFF) ;
			return 1 ;
		case 0x31:
			tmpv = (Integer)objval ;
			DataUtil.setUInt16(tmpv, data, offset);
			return 2 ;
		}
		
		switch(d_tp.id)
		{ // floating
		case 0x38:
			float tmpf = ((Number)objval).floatValue() ;
			int tmpv = DataUtil.floatToHalf(tmpf) & 0xFFFF;
			DataUtil.setUInt16(tmpv, data, offset);
			return 2 ;
		case 0x39:
			tmpf = ((Number)objval).floatValue() ;
			DataUtil.floatToBytes(tmpf,data, offset) ;
			return 4 ;
		case 0x3a:
			double tmpd = ((Number)objval).doubleValue();
			DataUtil.doubleToBytes(tmpd,data,offset);
			return 8 ;
		}
		
		if(d_tp.id>=0x41 && d_tp.id<=0x44)
		{//signed int   uint64-is not support
			return packStringTo(dval,data,offset);
		}
		
		switch(d_tp.id)
		{// Array Structure
		case 0x48:
		case 0x4c:
			int len = dval.getOctetLen() ;
			DataUtil.setUInt16(len-2, data, offset);
			byte[] bs_arr = (byte[])objval ;
			System.arraycopy(bs_arr,0 ,data, offset+2, len-2);
			return len ;
		}
		
		switch(d_tp.id)
		{
		case 0x50:
		case 0x51:
			throw new NotImplementedError("Set Bag is not support") ;
		}
		
		switch(d_tp.id)
		{// time analog
		case 0xe0:
		case 0xe1:
		case 0xe2:
			long v = ((Number)objval).longValue();
			DataUtil.setUInt32(v, data, offset);
			return 4;
		}
		
		switch(d_tp.id)
		{// identifier
		case 0xe8: //cluster id
		case 0xe9: // attribute id
			int v = ((Number)objval).intValue();
			DataUtil.setUInt16(v,data, offset) ;
			return 2;
		case 0xea: // BACnet OID
			long lv =  ((Number)objval).longValue();
			DataUtil.setUInt32(lv,data, offset) ;
			return 4 ;
		}
		
		switch(d_tp.id)
		{// Miscellaneous
		case 0xf0:
			byte[] bs8 = (byte[])objval ;
			System.arraycopy(bs8,0,data, offset, 8);
			return 8 ;
		case 0xf1:
			byte[] bs16 =  (byte[])objval ;
			System.arraycopy(bs16,0,data, offset, 16);
			return 16 ;
		}
		
		if(d_tp.id==0xff)
			return 0 ;
		
		return -1 ;
	}
	/**
	 * 
	 * @param data objval对应的数据包
	 * @param offset
	 * @return null error or invalid
	 */
	public DataVal parseObjVal(byte[] data,int offset)
	{
		if(this.len==0)
			return new DataVal(this,0,null) ;
		
		if(this.id>=0x08 && this.id<=0x0f)
		{
			byte[] r = new byte[this.len] ;
			System.arraycopy(data, offset, r, 0, len);
			return new DataVal(this,len,r) ;
		}
		
		if(this.id==0x10)
		{
			switch((data[offset] & 0xFF))
			{
			case 0x00:return new DataVal(this,1,false) ;
			case 0x01:return new DataVal(this,1,true) ;
			default: return null ;//invalid
			}
		}
		
		if(this.id>=0x18 && this.id<=0x1f)
		{//bitmap
			long val = 0 ;
			for(int i = 0 ; i < len ; i ++)
			{
				long tmp = data[offset+i] &0xFF ;
				val |= (tmp<<(i*8)) ;
			}
			return new DataVal(this,len,val) ;
		}
		
		if(this.id>=0x20 && this.id<=0x27)
		{//unsigned int   uint64-is not support
			long val = 0 ;
			for(int i = 0 ; i < len ; i ++)
			{
				long tmp = data[offset+i] & 0xFF;
				val |= (tmp<<(i*8)) ;
			}
			return new DataVal(this,len,val) ;
		}
		
		if(this.id>=0x28 && this.id<=0x2f)
		{//signed int   uint64-is not support
			return parseSignedInt(data,offset) ;
		}
		
		switch(this.id)
		{ // enumeration
		case 0x30:
			return new DataVal(this,len,data[offset] & 0xFF) ;
		case 0x31:
			return new DataVal(this,len,DataUtil.getUInt16(data, offset)) ;
		}
		
		switch(this.id)
		{ // floating
		case 0x38:
			return new DataVal(this,len,
					DataUtil.halfToFloat((short)DataUtil.getUInt16(data, offset))) ;
		case 0x39:
			return new DataVal(this,len,DataUtil.bytesToFloat(data, offset)) ;
		case 0x3a:
			return new DataVal(this,len,DataUtil.bytesToDouble(data, offset)) ;
		}
		
		if(this.id>=0x41 && this.id<=0x44)
		{//signed int   uint64-is not support
			return parseString(data,offset) ;
		}
		
		switch(this.id)
		{
		case 0x48:
		case 0x4c:
			int len = DataUtil.getUInt16(data, offset) ;
			byte[] bs_arr = new byte[len] ;
			System.arraycopy(data, offset+2, bs_arr, 0, len);
			return new DataVal(this,len+2,bs_arr) ;
		}
		
		switch(this.id)
		{
		case 0x50:
		case 0x51:
			throw new NotImplementedError("Set Bag is not support") ;
		}
		
		switch(this.id)
		{// time analog
		case 0xe0:
		case 0xe1:
		case 0xe2:
			long v = DataUtil.getUInt32(data, offset) ;
			return new DataVal(this,4,v) ;
		}
		
		switch(this.id)
		{// identifier
		case 0xe8:
		case 0xe9:
			int v = DataUtil.getUInt16(data, offset) ;
			return new DataVal(this,2,v) ;
		case 0xea:
			long lv = DataUtil.getUInt32(data, offset) ;
			return new DataVal(this,4,lv) ;
		}
		
		switch(this.id)
		{// Miscellaneous
		case 0xf0:
			byte[] bs8 = new byte[8] ;
			System.arraycopy(data, offset, bs8, 0, 8);
			return new DataVal(this,8,bs8) ;
		case 0xf1:
			byte[] bs16 = new byte[16] ;
			System.arraycopy(data, offset, bs16, 0, 16);
			return new DataVal(this,16,bs16) ;
		}
		
		if(this.id==0xff)
			return new DataVal(this,0,null) ;
		
		return null ;
	}
	
	private DataVal parseString(byte[] data,int offset)
	{
		try
		{
			int len ;
			switch(this.id)
			{ // floating
			case 0x41:
			case 0x42:
				len = data[offset] & 0xFF ;
				return new DataVal(this,len+1,new String(data,offset+1,len,"ISO-8859-1")) ;
			case 0x43:
			case 0x44:
				len = DataUtil.getUInt16(data, offset) ;
				return new DataVal(this,len+2,new String(data,offset+2,len*2,"UTF-16")) ;
			default:
				return null ;
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return null ;
		}
	}
	
	private static int packStringTo(DataVal dval,byte[] data,int offset)
	{
		DataType d_tp = dval.getDataType() ;
		String strv = (String)dval.getObjVal() ;
		try
		{
			int len = dval.getOctetLen() ;
			switch(d_tp.id)
			{ // floating
			case 0x41:
			case 0x42:
				data[offset] = (byte)((len-1) & 0xFF) ;
				byte[] strbs = strv.getBytes("ISO-8859-1") ;
				int strlen = strbs.length ;
				if(strlen>len-1)
					strlen = len - 1 ;
				System.arraycopy(strbs, 0, data, offset+1, strlen);
				return len ;
			case 0x43:
			case 0x44:
				DataUtil.setUInt16(len-2, data, offset);
				strbs = strv.getBytes("UTF-16") ;
				strlen = strbs.length ;
				if(strlen>len-2)
					strlen = len - 2 ;
				System.arraycopy(strbs, 0, data, offset+2, strlen);
				return len ;
			default:
				return -1 ;
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return -1 ;
		}
	}
	
	private DataVal parseSignedInt(byte[] data,int offset)
	{
		switch(len)
		{
		case 1:
			return new DataVal(this,len,data[offset]) ;
		case 2:
			int v2 = (short)((data[offset+1] & 0xFF)<<8) ;
			v2 |= data[offset] & 0xFF ; 
			return new DataVal(this,len,(short)v2) ;
		case 3:
		case 4:
			int v4 = 0 ;
			for(int k = len-1 ; k >= 0 ; k --)
			{
				int tmpd = data[offset+k] & 0xFF ;
				v4 |= (tmpd<<8*k) ;
			}
			return new DataVal(this,len,v4) ;
		case 5:
		case 6:
		case 7:
		case 8:
			long v8 = 0 ;
			for(int k = len-1 ; k >= 0 ; k --)
			{
				long tmpd = data[offset+k] & 0xFF;
				v8 |= (tmpd<<8*k) ;
			}
			return new DataVal(this,len,v8) ;
		default:
			return null ;
		}
	}
	
	private static HashMap<Integer,DataType> ID2DT = new HashMap<>() ; 
	static
	{
		for(DataType dt:DataType.values())
			ID2DT.put(dt.getID(),dt) ;
	}
	
	public static DataType valOfInt(int v)
	{
		return ID2DT.get(v) ;
	}
}
