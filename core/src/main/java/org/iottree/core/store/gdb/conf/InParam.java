package org.iottree.core.store.gdb.conf;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;

import org.iottree.core.store.gdb.GdbException;
import org.w3c.dom.Element;

public class InParam
{
	protected static HashMap<ParamType, int[]> ParamType2JdbcType = new HashMap<ParamType, int[]>();

	static
	{
		ParamType2JdbcType.put(ParamType.Null, new int[] { Types.NULL });
		ParamType2JdbcType.put(ParamType.String, new int[] { Types.VARCHAR });
		ParamType2JdbcType.put(ParamType.Boolean, new int[] { Types.BIT });
		ParamType2JdbcType.put(ParamType.Byte, new int[] { Types.TINYINT });
		ParamType2JdbcType.put(ParamType.SByte, new int[] { Types.TINYINT });
		ParamType2JdbcType.put(ParamType.Int16, new int[] { Types.SMALLINT });
		ParamType2JdbcType.put(ParamType.Int32, new int[] { Types.INTEGER });
		ParamType2JdbcType.put(ParamType.Int64, new int[] { Types.BIGINT });
		ParamType2JdbcType.put(ParamType.UInt16, new int[] { Types.INTEGER });
		ParamType2JdbcType.put(ParamType.UInt32, new int[] { Types.BIGINT });
		ParamType2JdbcType.put(ParamType.UInt64, new int[] { Types.BIGINT });
		ParamType2JdbcType.put(ParamType.Single, new int[] { Types.FLOAT });
		ParamType2JdbcType.put(ParamType.Double, new int[] { Types.DOUBLE });
		ParamType2JdbcType.put(ParamType.Decimal, new int[] { Types.DECIMAL });
		ParamType2JdbcType.put(ParamType.DateTime, new int[] { Types.TIMESTAMP });
		ParamType2JdbcType.put(ParamType.ByteArray, new int[] { Types.BLOB });
		ParamType2JdbcType.put(ParamType.Guid, new int[] { Types.VARCHAR });
		ParamType2JdbcType.put(ParamType.Clob, new int[] { Types.CLOB });
	}
	
	public static int getJdbcType(ParamType pt)
	{
		int[] v = ParamType2JdbcType.get(pt);
		if(v==null)
			throw new RuntimeException("cannot get jdbc type with ParamType="+pt.toString());
		
		return v[0];
	}
	
	public static InParam createInParam(String name,String typestr)
	{
		InParam ip = new InParam();
		
		ip.name = name;
		if(ip.name==null||ip.name.equals(""))
			return null ;
		
		String str_type = typestr ;
		if(str_type!=null&&!str_type.equals(""))
			ip.paramType = ParamType.parseFromStr(str_type);//ParamType.valueOf(str_type);
		
		ip.jdbcType = getJdbcType(ip.paramType);
		return ip ;
	}
	
	public static InParam parseInParam(Element ele) throws GdbException
	{
		InParam ip = new InParam();
		
		ip.name = ele.getAttribute(Gdb.ATTR_NAME);
		if(ip.name==null||ip.name.equals(""))
			return null ;
		
		String str_type = ele.getAttribute(Gdb.ATTR_TYPE) ;
		if(str_type!=null&&!str_type.equals(""))
			ip.paramType = ParamType.parseFromStr(str_type);//ParamType.valueOf(str_type);
		
		ip.jdbcType = getJdbcType(ip.paramType);
		
		ip.valueGen = ele.getAttribute(Gdb.ATTR_VALUE_GEN);
		
		String str_nullable = ele.getAttribute(Gdb.ATTR_NULLABLE);
		ip.bNullable = "true".equalsIgnoreCase(str_nullable)||"1".equals(str_nullable);
		
		String str_def_val = ele.getAttribute(Gdb.ATTR_DEFAULT_VAL);
		if(str_def_val!=null&&str_def_val.endsWith("()"))
		{
			ip.bValGenDefVal = true ;
			ip.valGenName = str_def_val.substring(0,str_def_val.length()-2).toUpperCase() ;
			ip.bNullable = true ;
		}
		else
		{
			ip.defaultVal = calDefaultVal(ip.paramType, str_def_val);
			if(ip.defaultVal!=null)
				ip.bNullable = true ;
		}
		
		String str_auto_trun = ele.getAttribute(Gdb.ATTR_AUTO_TRUNCATE);
		ip.autoTruncate = "true".equalsIgnoreCase(str_auto_trun)||"1".equals(str_auto_trun);
		
		String str_max_size = ele.getAttribute(Gdb.ATTR_MAX_SIZE);
		if(str_max_size!=null&&!str_max_size.equals(""))
		{
			ip.maxSize = Integer.parseInt(str_max_size);
			if(ip.maxSize<0)
				ip.maxSize = -1 ;
		}
		
		return ip;
	}
	
	String name;

	ParamType paramType = ParamType.String;
	
	transient int jdbcType = -1 ;

	String valueGen;

	boolean bNullable = false;

	//String defaultValStr;

	int maxSize = -1;

	boolean autoTruncate = false;

	/**
	 * 是否是自动产生的缺省值
	 */
	private transient boolean bValGenDefVal = false ;
	private transient String valGenName = null ;
	
	private transient Object defaultVal = null;

	public String getName()
	{
		return name;
	}

	public ParamType getParamType()
	{
		return paramType;
	}
	
	public int getJdbcType()
	{
		return jdbcType ;
	}

	public String getValueGenerator()
	{
		return valueGen;
	}

	public boolean isNullable()
	{
		if(!bNullable&&defaultVal!=null)
			return true ;
		
		return bNullable;
	}

//	public String getDefaultValStr()
//	{
//		return defaultValStr;
//	}

	public Object getDefaultVal()
	{
		if(bValGenDefVal)
		{
			BuildInValGenerator bivg = BuildInValGenerator.getBuildInVG(valGenName);
			if(bivg==null)
				throw new RuntimeException("Cannot get Build-in value generator with name="+valGenName);
			return bivg.getVal(null);
		}
		return defaultVal;
	}
	
	/**
	 * 判断是否是自动产生值的缺省值
	 * @return
	 */
	public boolean isValGenDefVal()
	{
		return bValGenDefVal ;
	}
	
	/**
	 * 获得缺省值自动产生器的名称
	 * @return
	 */
	public String getDefValValGenName()
	{
		return valGenName ;
	}

	public int getMaxSize()
	{
		return maxSize;
	}

	public boolean isAutoTruncate()
	{
		return autoTruncate;
	}

	private static Object calDefaultVal(ParamType pt, String str_def_val)
			throws GdbException
	{
		if (str_def_val == null)
			return null;

		if (pt == ParamType.String || pt == ParamType.Guid)
		{
			return str_def_val;
		}
		else if (pt == ParamType.Boolean)
		{
			if ("true".equalsIgnoreCase(str_def_val) || "1".equals(str_def_val))
				return true;
			else
				return false;
		}
		else if (pt == ParamType.Byte || pt == ParamType.SByte)
		{
			if ("".equals(str_def_val))
				return 0;
			return Byte.parseByte(str_def_val);
		}
		else if (pt == ParamType.Int16 || pt == ParamType.UInt16)
		{
			if ("".equals(str_def_val))
				return (short) 0;
			return Short.parseShort(str_def_val);
		}
		else if (pt == ParamType.Int32 || pt == ParamType.UInt32)
		{
			if ("".equals(str_def_val))
				return 0;
			return Integer.parseInt(str_def_val);
		}
		else if (pt == ParamType.Int64 || pt == ParamType.UInt64)
		{
			if ("".equals(str_def_val))
				return (long) 0;
			return Long.parseLong(str_def_val);
		}
		else if (pt == ParamType.Single)
		{
			if ("".equals(str_def_val))
				return (float) 0;
			return Float.parseFloat(str_def_val);
		}
		else if (pt == ParamType.Double || pt == ParamType.Decimal)
		{
			if ("".equals(str_def_val))
				return (double) 0;
			return Double.parseDouble(str_def_val);
		}
		else if (pt == ParamType.DateTime)
		{
			if("".equals(str_def_val))
				return null ;
			return new Date(Long.parseLong(str_def_val));
		}
		else if (pt == ParamType.ByteArray)
		{
			if("".equals(str_def_val))
				return null ;
			
			throw new GdbException(
					"ByteArray当前不支持缺省值(ByteArray has no default value now!)");
		}
		else
		{
			return null;
		}
	}
}
