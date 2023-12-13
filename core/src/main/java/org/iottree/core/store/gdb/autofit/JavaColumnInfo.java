package org.iottree.core.store.gdb.autofit;

import java.io.*;
import java.util.*;

import org.iottree.core.util.xmldata.IXmlDataable;
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
			//return java.sql.Types.CLOB;
			return java.sql.Types.BLOB;
		}
		else
		{
			throw new IllegalArgumentException("unknow xml val type="+vt);
		}
	}
	
	/**
	 * ���ݶ������ͣ���ö�Ӧ��jdbc sql����
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
	
	private boolean bPk = false;

	private XmlVal.XmlValType valType = XmlVal.XmlValType.vt_string;
	
	transient private int sqlValType =  java.sql.Types.VARCHAR;

	private int maxLen = -1;

	private boolean bUnique = false;

	private boolean bHasIdx = true;
	
	/**
	 * �������ƣ��������������ͬ������Ҫ������������
	 */
	private String idxName = null ;

	private boolean bAutoVal = false;
	
	/**
	 * �Զ�ֵ����ʼֵ
	 */
	private long autoValStart = -1 ;
	
	private String defaultStrVal = null ;
	
	private boolean bReadOnDemand = false;
	
	private boolean bUpdateAsSingle = false;

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

	/**
	 * �õ�������
	 * 
	 * @return
	 */
	public String getColumnName()
	{
		return columnName;
	}
	
	public boolean isPk()
	{
		return bPk ;
	}

	public XmlVal.XmlValType getValType()
	{
		return valType;
	}
	
	/**
	 * �ж��Ƿ����ַ������͵�����ֵ����
	 * @return
	 */
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
		// bPrimaryKey = xd.getParamValueBool("is_pk", false);
	}
}
