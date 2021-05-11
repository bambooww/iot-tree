package org.iottree.core.util.xmldata;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.iottree.core.util.xmldata.XmlDataStruct.StoreType;
import org.iottree.core.util.xmldata.XmlVal.XmlValType;

public class XmlValDef implements IXmlDataDef
{
	String valType = XmlVal.VAL_TYPE_STR;

	boolean bArray = false;

	boolean bNullable = true;

	int maxLen = -1;
	
	StoreType storeType = StoreType.Normal ;
	
	//String title = null ;
	
	/**
	 * 缺省的Xml字符串值
	 */
	String defaultStrVal = null ;
	
	/**
	 * 用来支持输入界面的需要多行的定义
	 */
	boolean bMultiRows = false;
	
	/**
	 * 用来支持数据库主键建立
	 */
	boolean bPk = false;
	
	/**
	 * 用来支持数据库索引建立
	 */
	boolean bIdx = false;

	public XmlValDef()
	{
	}
	
	public XmlValDef(String vt)
	{
		this(vt,false,true,-1,StoreType.Normal);
	}
	
	public XmlValDef(String vt,int maxlen)
	{
		this(vt,false,true,maxlen,StoreType.Normal);
	}

	public XmlValDef(String vt, boolean ba, boolean bnullable,
			int maxlen)
	{
		this(vt,ba,bnullable,maxlen,StoreType.Normal);
	}
	
	public XmlValDef(String vt, boolean ba, boolean bnullable,
			int maxlen,StoreType st)
	{
		setStructInfo(vt, ba, bnullable, maxlen,st,false,null);
	}
	
	public XmlValDef(String vt, boolean ba, boolean bnullable,
			int maxlen,StoreType st,boolean bmultirows,String defstrval)
	{
		setStructInfo(vt, ba, bnullable, maxlen,st,bmultirows,defstrval);
	}
	
	public String getValueTypeStr()
	{
		return valType ;
	}
	
	public XmlValDef copyMe()
	{
		XmlValDef si = new XmlValDef();
		
		si.valType = valType;

		si.bArray = bArray;

		si.bNullable = bNullable;

		si.maxLen = maxLen;
		
		si.storeType = storeType;
		
		si.defaultStrVal = this.defaultStrVal ;
		
		return si ;
	}

	public Object randomCreateValue()
	{
		Random rand = new Random();
		if (XmlVal.VAL_TYPE_BOOL.equals(valType))
		{
			if (bArray)
			{
				int c = rand.nextInt(5);
				Object[] rets = new Object[c];
				for (int i = 0; i < c; i++)
				{
					rets[i] = rand.nextBoolean();
				}
				return rets;
			}
			else
			{
				return rand.nextBoolean();
			}
		}
		else if (XmlVal.VAL_TYPE_BYTE.equals(valType))
		{
			if (bArray)
			{
				int c = rand.nextInt(5);
				Object[] rets = new Object[c];
				for (int i = 0; i < c; i++)
				{
					rets[i] = (byte) rand.nextInt();
				}
				return rets;
			}
			else
			{
				return (byte) rand.nextInt();
			}
		}
		else if (XmlVal.VAL_TYPE_DATE.equals(valType))
		{
			if (bArray)
			{
				int c = rand.nextInt(5);
				Object[] rets = new Object[c];
				long curl = System.currentTimeMillis();
				for (int i = 0; i < c; i++)
				{
					rets[i] = new Date(curl + rand.nextLong() % 86400000);
				}
				return rets;
			}
			else
			{
				long curl = System.currentTimeMillis();
				return new Date(curl + rand.nextLong() % 86400000);
			}
		}
		else if (XmlVal.VAL_TYPE_DOUBLE.equals(valType))
		{
			if (bArray)
			{
				int c = rand.nextInt(5);
				Object[] rets = new Object[c];
				for (int i = 0; i < c; i++)
				{
					rets[i] = rand.nextDouble();
				}
				return rets;
			}
			else
			{
				return (double) rand.nextInt();
			}
		}
		else if (XmlVal.VAL_TYPE_FLOAT.equals(valType))
		{
			if (bArray)
			{
				int c = rand.nextInt(5);
				Object[] rets = new Object[c];
				for (int i = 0; i < c; i++)
				{
					rets[i] = rand.nextFloat();
				}
				return rets;
			}
			else
			{
				return rand.nextFloat();
			}
		}
		else if (XmlVal.VAL_TYPE_INT16.equals(valType))
		{
			if (bArray)
			{
				int c = rand.nextInt(5);
				Object[] rets = new Object[c];
				for (int i = 0; i < c; i++)
				{
					rets[i] = (short) rand.nextInt();
				}
				return rets;
			}
			else
			{
				return (short) rand.nextInt();
			}
		}
		else if (XmlVal.VAL_TYPE_INT32.equals(valType))
		{
			if (bArray)
			{
				int c = rand.nextInt(5);
				Object[] rets = new Object[c];
				for (int i = 0; i < c; i++)
				{
					rets[i] = (int) rand.nextInt();
				}
				return rets;
			}
			else
			{
				return rand.nextInt();
			}
		}
		else if (XmlVal.VAL_TYPE_INT64.equals(valType))
		{
			if (bArray)
			{
				int c = rand.nextInt(5);
				Object[] rets = new Object[c];
				for (int i = 0; i < c; i++)
				{
					rets[i] = rand.nextLong();
				}
				return rets;
			}
			else
			{
				return rand.nextLong();
			}
		}
		else if (XmlVal.VAL_TYPE_STR.equals(valType))
		{
			if (bArray)
			{
				int c = rand.nextInt(5);
				Object[] rets = new Object[c];
				for (int i = 0; i < c; i++)
				{
					String tmps = UUID.randomUUID().toString();
					if (tmps.length() > maxLen)
						tmps = tmps.substring(0, maxLen);
					rets[i] = tmps;
				}
				return rets;
			}
			else
			{
				String tmps = UUID.randomUUID().toString();
				if (tmps.length() > maxLen)
					tmps = tmps.substring(0, maxLen);
				return tmps;
			}
		}

		return null;
	}

	void setStructInfo(String vt, boolean ba, boolean bnullable,
			int maxlen,StoreType st,boolean bmulti_rows,String def_strval)
	{
		valType = vt;
		if (valType == null || valType.equals(""))
			valType = XmlVal.VAL_TYPE_STR;

		XmlValType xvt = XmlVal.StrType2ValType(valType);

		bArray = ba;
		bNullable = bnullable;
		maxLen = maxlen;
		storeType = st ;
		this.defaultStrVal = def_strval ;
		this.bMultiRows = bmulti_rows ;
	}

	public boolean equals(Object o)
	{
		XmlValDef si = (XmlValDef) o;
		
		if (!valType.equals(si.valType))
			return false;
		if (bArray != si.bArray)
			return false;
		if (bNullable != si.bNullable)
			return false;
		if (maxLen != si.maxLen)
			return false;
		if(storeType!=si.storeType)
			return false;

		if(bPk!=si.bPk)
			return false;
		if(bIdx!=si.bIdx)
			return false;
		
		return true;
	}

	public String getValType()
	{
		return valType;
	}
	
	public void setValType(String vt)
	{
		valType = vt ;
	}
	
	public boolean isMultiRows()
	{
		return this.bMultiRows ;
	}

	public boolean isArray()
	{
		return bArray;
	}
	
	public void setArray(boolean b)
	{
		bArray = b ;
	}

	public boolean isNullable()
	{
		return bNullable;
	}
	
	public void setNullable(boolean b)
	{
		bNullable = b ;
	}

	public int getMaxLen()
	{
		return maxLen;
	}
	
	public void setMaxLen(int ml)
	{
		maxLen = ml ;
		if(maxLen<0)
			maxLen = -1 ;
	}

	public StoreType getStoreType()
	{
		return storeType ;
	}
	
	public void setStoreType(StoreType st)
	{
		storeType = st ;
	}
	
	public String getDefaultStrVal()
	{
		return this.defaultStrVal ;
	}
	
	public void setDefaultStrVal(String sdv)
	{
		this.defaultStrVal = sdv ;
	}
	
	public boolean isPk()
	{
		return bPk ;
	}
	
	public void setIsPk(boolean pk)
	{
		bPk = pk ;
	}
	
	public boolean hasIdx()
	{
		return bIdx ;
	}
	
	public void setHasIdx(boolean idx)
	{
		bIdx = idx ;
	}
	
	public XmlData toXmlData()
	{
		XmlData xd = new XmlData();
		if(valType!=null)
			xd.setParamValue("val_type", valType);
		xd.setParamValue("is_array", bArray);
		xd.setParamValue("nullable", bNullable);
		xd.setParamValue("max_len", maxLen);
		xd.setParamValue("store_type", storeType.getIntValue());
		if(this.defaultStrVal!=null)
			xd.setParamValue("def_str_val", defaultStrVal) ;
		xd.setParamValue("is_pk", bPk) ;
		xd.setParamValue("has_idx", bIdx) ;
		return xd;
	}

	public void fromXmlData(XmlData xd)
	{
		valType = xd.getParamValueStr("val_type");
		bArray = xd.getParamValueBool("is_array", false);
		bNullable = xd.getParamValueBool("nullable", true);
		maxLen = xd.getParamValueInt32("max_len", -1);
		storeType = StoreType.valueOf(xd.getParamValueInt32("store_type", 1));
		
		defaultStrVal = xd.getParamValueStr("def_str_val") ;
		
		bPk = xd.getParamValueBool("is_pk", false) ;
		bIdx = xd.getParamValueBool("has_idx", false) ;
	}
}