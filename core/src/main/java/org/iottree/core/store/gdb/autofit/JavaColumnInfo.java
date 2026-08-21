package org.iottree.core.store.gdb.autofit;

import java.io.*;
import java.util.*;

import org.iottree.core.util.xmldata.IXmlDataable;
import org.json.JSONObject;
import org.iottree.core.util.xmldata.*;


public class JavaColumnInfo implements IXmlDataable
{
	
	public static int XmlValType2SqlType(XmlVal.XmlValType vt)
	{
		if (vt == XmlVal.XmlValType.vt_xml_schema)
		{
			return java.sql.Types.VARCHAR;
		}
		else if (vt == XmlVal.XmlValType.vt_byte_array)
		{
			return java.sql.Types.BLOB;
		}
		else if (vt == XmlVal.XmlValType.vt_date)
		{
			//return java.sql.Types.DATE;
			return java.sql.Types.TIMESTAMP;
		}
		else if (vt == XmlVal.XmlValType.vt_double)
		{
			return java.sql.Types.DOUBLE;
		}
		else if (vt == XmlVal.XmlValType.vt_float)
		{
			return java.sql.Types.FLOAT;
		}
		else if (vt == XmlVal.XmlValType.vt_int64)
		{
			return java.sql.Types.BIGINT;
		}
		else if (vt == XmlVal.XmlValType.vt_int32)
		{
			return java.sql.Types.INTEGER;
		}
		else if (vt == XmlVal.XmlValType.vt_int16)
		{
			return java.sql.Types.SMALLINT;
		}
		else if (vt == XmlVal.XmlValType.vt_byte)
		{
			return java.sql.Types.TINYINT;
		}
		else if (vt == XmlVal.XmlValType.vt_string)
		{
			return java.sql.Types.VARCHAR;
		}
		else if (vt == XmlVal.XmlValType.vt_bool)
		{
			return java.sql.Types.BIT;
		}
		else if(vt== XmlVal.XmlValType.vt_bigdecimal)
		{
			return java.sql.Types.DECIMAL;
		}
		else if (vt == XmlVal.XmlValType.vt_xml_data)
		{
			return java.sql.Types.BLOB;
		}
		else
		{
			throw new IllegalArgumentException("unknow xml val type="+vt);
		}
	}
	
	
	public static XmlVal.XmlValType SqlType2XmlValType(int sql_type)
	{
		switch(sql_type)
		{
		case java.sql.Types.BLOB:
			return XmlVal.XmlValType.vt_byte_array;
		case java.sql.Types.TIMESTAMP:
			return XmlVal.XmlValType.vt_date;
		case java.sql.Types.DOUBLE:
			return XmlVal.XmlValType.vt_double;
		case java.sql.Types.FLOAT:
		case java.sql.Types.REAL:
			return XmlVal.XmlValType.vt_float;
		case java.sql.Types.BIGINT:
			return XmlVal.XmlValType.vt_int64;
		case java.sql.Types.INTEGER:
			return XmlVal.XmlValType.vt_int32;
		case java.sql.Types.SMALLINT:
			return XmlVal.XmlValType.vt_int16;
		case java.sql.Types.TINYINT:
			return XmlVal.XmlValType.vt_byte;
		case java.sql.Types.VARCHAR:
			return XmlVal.XmlValType.vt_string;
		case java.sql.Types.BIT:
			return XmlVal.XmlValType.vt_bool;
		case java.sql.Types.DECIMAL:
			return XmlVal.XmlValType.vt_bigdecimal;
		default:
			throw new IllegalArgumentException("unknow java sql type="+sql_type);
		}
	}
	/**
	 * 
	 * @param c
	 * @return
	 */
	public static int Class2SqlType(Class c)
	{
		XmlVal.XmlValType xvt = XmlVal.class2VT(c);
		if(xvt==null)
		{
			xvt = XmlVal.XmlValType.vt_string ;
		}
		return XmlValType2SqlType(xvt) ;
	}

	private String columnName = null;
	
	private String colTitle = null ;
	
	private boolean bPk = false;

	private XmlVal.XmlValType valType = XmlVal.XmlValType.vt_string;
	
	transient private int sqlValType =  java.sql.Types.VARCHAR;

	private int maxLen = -1;

	private boolean bUnique = false;

	private boolean bHasIdx = true;
	
	/**
	 * 
	 */
	private String idxName = null ;

	private boolean bAutoVal = false;

	private long autoValStart = -1 ;
	
	private String defaultStrVal = null ;
	
	private boolean bReadOnDemand = false;
	
	private boolean bUpdateAsSingle = false;

	private boolean bNullable = false;
	// private boolean bPrimaryKey = false;

	public JavaColumnInfo()
	{
	}

	public JavaColumnInfo(String coln,boolean b_pk, XmlVal.XmlValType vt, int maxlen,
			boolean hasidx, boolean unique,String idxname,
			boolean autoval,long autoval_st)
	{
		this(coln,b_pk, vt, maxlen,hasidx, unique,idxname, autoval,autoval_st,null,false,false);
	}
	
	public JavaColumnInfo(String coln,boolean b_pk, XmlVal.XmlValType vt, int maxlen,
			boolean hasidx, boolean unique,String idxname, 
			boolean autoval,long autoval_st,String default_strv,
			boolean b_read_ondemand,boolean b_update_as_single)
	{
		columnName = coln;
		bPk = b_pk ;
		valType = vt;
		
		sqlValType = XmlValType2SqlType(vt) ;
		
		maxLen = maxlen;
		bHasIdx = hasidx;
		bUnique = unique;
		idxName = idxname ;
		bAutoVal = autoval;
		this.autoValStart = autoval_st ;
		defaultStrVal = default_strv ;
		// bPrimaryKey = pk ;
		bReadOnDemand = b_read_ondemand ;
		bUpdateAsSingle = b_update_as_single ;
	}

	public String getColumnName()
	{
		return columnName;
	}
	
	public String getColumnTitle()
	{
		if(this.colTitle==null)
			return "" ;
		return this.colTitle ;
	}
	
	public JavaColumnInfo asColumnTitle(String tt)
	{
		this.colTitle = tt ;
		return this;
	}
	
	public boolean isPk()
	{
		return bPk ;
	}

	public XmlVal.XmlValType getValType()
	{
		return valType;
	}
	
	public boolean isNullable()
	{
		return this.bNullable ;
	}
	
	public JavaColumnInfo asNullable(boolean b)
	{
		this.bNullable = b ;
		return this ;
	}
	
	public boolean isAutoStringValuePk()
	{
		if(!bPk)
			return false;
		
		if(valType!=XmlVal.XmlValType.vt_string)
			return false;
		
		return bAutoVal;
	}
	
	public boolean isStringValPk()
	{
		if(!bPk)
			return false;
		
		if(valType!=XmlVal.XmlValType.vt_string)
			return false;
		return true ;
	}
	
	public String getDefaultValStr()
	{
		return defaultStrVal;
	}
	/**
	 * java.util.Types��ָ����Sql����
	 * 
	 * @return
	 */
	public int getSqlValType()
	{
		return sqlValType ;
	}

	public int getMaxLen()
	{
		return maxLen;
	}
	
	public boolean isNeedMaxLen()
	{
		return XmlVal.XmlValType.vt_string== valType ;
	}

	public boolean hasIdx()
	{
		return bHasIdx;
	}

	public String getIdxName()
	{
		return idxName ;
	}
	// public boolean isPrimaryKey()
	// {
	// return bPrimaryKey ;
	// }

	public boolean isUnique()
	{
		return bUnique;
	}

	public boolean isAutoVal()
	{
		return bAutoVal;
	}
	
	public long getAutoValStart()
	{
		return this.autoValStart;
	}
	/**
	 * �������ݿ���,��һ������¶�ȡ��ʱ��,������Ҫ
	 * ����,�б�ʱ�п��ܲ���ȡ����
	 * @return
	 */
	public boolean isReadOnDemand()
	{
		return bReadOnDemand ;
	}
	
	/**
	 * �����Ƿ�ֻ�ܶ�������,��blob��Ӧ����Ӧ��ʹ��true
	 * @return
	 */
	public boolean isUpdateAsSingle()
	{
		return bUpdateAsSingle ;
	}
	
	public String toLnStr()
	{
		return "["+columnName+"] jdbc_tp="+sqlValType+" maxlen="+maxLen ;
	}

	public XmlData toXmlData()
	{
		XmlData xd = new XmlData();
		xd.setParamValue("col_name", columnName);
		xd.setParamValue("val_type", XmlVal.ValType2StrType(valType));
		xd.setParamValue("max_len", maxLen);
		xd.setParamValue("is_unique", bUnique);
		xd.setParamValue("has_idx", bHasIdx);
		xd.setParamValue("is_autoval", bAutoVal);

		// xd.setParamValue("is_pk", bPrimaryKey);
		return xd;
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject() ;
		ret.putOpt("coln", columnName);
		ret.putOpt("colt", colTitle);
		ret.putOpt("val_tp", valType.getTypeStr());
		ret.putOpt("max_len", maxLen);
		ret.putOpt("unique", bUnique);
		ret.putOpt("idx", bHasIdx);
		ret.putOpt("autoval", bAutoVal);
		ret.putOpt("pk",bPk) ;
		ret.putOpt("nullable", this.bNullable) ;
		return ret ;
	}

	public void fromXmlData(XmlData xd)
	{
		columnName = xd.getParamValueStr("col_name");
		String tmps = xd.getParamValueStr("val_type");
		if (tmps != null)
		{
			valType = XmlVal.StrType2ValType(tmps);
			sqlValType = XmlValType2SqlType(valType) ;
		}

		maxLen = xd.getParamValueInt32("max_len", -1);
		bUnique = xd.getParamValueBool("is_unique", false);
		bHasIdx = xd.getParamValueBool("has_idx", true);
		bAutoVal = xd.getParamValueBool("is_autoval", false);
		bNullable = xd.getParamValueBool("nullable", false);
	}
	
	public void fromJO(JSONObject jo)
	{
		columnName = jo.optString("coln");
		this.colTitle = jo.optString("colt") ;
		String tmps = jo.optString("val_tp");
		if (tmps != null)
		{
			valType = XmlVal.StrType2ValType(tmps);
			sqlValType = XmlValType2SqlType(valType) ;
		}

		maxLen =   jo.optInt("max_len", -1);
		bUnique =  jo.optBoolean("unique", false);
		bHasIdx =  jo.optBoolean("idx", true);
		bAutoVal = jo.optBoolean("autoval", false);
		bPk = jo.optBoolean("pk", false) ;
		bNullable = jo.optBoolean("nullable", false);
	}
}
