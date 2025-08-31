package org.iottree.core.store.gdb.conf;

import java.util.*;

public enum ParamType
{
	/**
	 * 空对象
	 */
	Null,
	
    /**
     * 字符串
     */
    String,
    
    /**
     * 布尔值
     */
    Boolean,
    
    /**
     * 8为数字
     */
    Byte,
    
    /**
     * 无符号8为数字
     */
    SByte,
    
    /**
     * 16为数字,对应java的short类型
     */
    Int16,
    
    /**
     * 32为数字,对应java的int类型
     */
    Int32,
    
    /**
     * 64为数字,对应java的long类型
     */
    Int64,
    
    /**
     * 16为数字,对应java的short类型
     */
    UInt16,
    
    /**
     * 32为数字,对应java的int类型
     */
    UInt32,
    
    /**
     * 64为数字,对应java的long类型
     */
    UInt64,
    
    /**
     * 对应java的float类型
     */
    Single,
    
    /**
     * 对应java的double类型
     */
    Double,
    
    /// <remarks/>
    Decimal,
    
    /**
     * 对应java的Date时间类型
     */
    DateTime,
    
    /**
     * 对应java的byte[]类型
     */
    ByteArray,
    
    /// <remarks/>
    Guid,
    
    /// <remarks/>
    Blob,
    
    /// <remarks/>
    Clob,
    
    /// <remarks/>
    File;
    
    static HashMap<String,ParamType> extStr2Type = new HashMap<String,ParamType>() ;
    static
    {
    	extStr2Type.put("float", ParamType.Single);
    	extStr2Type.put("date", ParamType.DateTime);
    	extStr2Type.put("bool", ParamType.Boolean);
    	extStr2Type.put("byte[]", ParamType.ByteArray);
    	extStr2Type.put("short", ParamType.Int16);
    	extStr2Type.put("int", ParamType.Int32);
    	extStr2Type.put("long", ParamType.Int64);
    	
    	extStr2Type.put("null", ParamType.Null);
    	extStr2Type.put("string", ParamType.String);
    	extStr2Type.put("boolean", ParamType.Boolean);
    	extStr2Type.put("byte", ParamType.Byte);
    	extStr2Type.put("sbyte", ParamType.SByte);
    	extStr2Type.put("int16", ParamType.Int16);
    	extStr2Type.put("int32", ParamType.Int32);
    	extStr2Type.put("int64", ParamType.Int64);
    	extStr2Type.put("uint16", ParamType.UInt16);
    	extStr2Type.put("uint32", ParamType.UInt32);
    	extStr2Type.put("uint64", ParamType.UInt64);
    	extStr2Type.put("single", ParamType.Single);
    	extStr2Type.put("double", ParamType.Double);
    	extStr2Type.put("decimal", ParamType.Decimal);
    	extStr2Type.put("datetime", ParamType.DateTime);
    	extStr2Type.put("date_time", ParamType.DateTime);
    	extStr2Type.put("time", ParamType.DateTime);
    	extStr2Type.put("bytearray", ParamType.ByteArray);
    	extStr2Type.put("guid", ParamType.Guid);
    	extStr2Type.put("blob", ParamType.Blob);
    	extStr2Type.put("clob", ParamType.Clob);
    	extStr2Type.put("file", ParamType.File);
    }
    
    public static ParamType parseFromStr(String s)
	{
    	try
    	{
    		return ParamType.valueOf(s);
    	}
    	catch(Exception ee)
    	{
    		String tmps = s.toLowerCase() ;
    		
    		ParamType pt = extStr2Type.get(tmps);
    		if(pt!=null)
    			return pt ;
    		
    		throw new IllegalArgumentException("unknown Sql ParamType="+s);
    	}
	}
}
