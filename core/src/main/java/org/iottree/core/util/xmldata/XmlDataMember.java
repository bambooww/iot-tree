/**
 * 
 */
package org.iottree.core.util.xmldata;

import java.util.*;

import org.iottree.core.util.xmldata.XmlDataStruct.StoreType;

public class XmlDataMember implements IXmlDataable,Comparable<XmlDataMember>
{
	String pname = null;
	
	String title = null ;

	XmlValDef xmlValDef = new XmlValDef();
	
	/**
	 * 额外的属性存储，满足一些定义数据项的特殊需要
	 */
	HashMap<String,String> extProps = new HashMap<String,String>() ;
	/**
	 * 顺序，在很多场合，数据成员需要安装一定的顺序展示
	 */
	transient int orderNum = 100 ;
	
	

	transient XmlDataStruct belongTo = null;

	public XmlDataMember()
	{
	}
	
	public XmlDataMember(String n, String vt, boolean ba, boolean bnullable,
			int maxlen,StoreType st)
	{
		setStructInfo(n,null,vt, ba, bnullable, maxlen,st,false,null);
	}
	
	public XmlDataMember(String n,String title, String vt, boolean ba, boolean bnullable,
			int maxlen,StoreType st)
	{
		setStructInfo(n,title, vt, ba, bnullable, maxlen,st,false,null);
	}

	public XmlDataMember(String n,String title, String vt, boolean ba, boolean bnullable,
			int maxlen,StoreType st,boolean bmultirows,String defstrv)
	{
		setStructInfo(n,title, vt, ba, bnullable, maxlen,st,bmultirows,defstrv);
	}
	
	public XmlDataMember(String n,XmlValDef xvd)
	{
		if(xvd==null)
			throw new IllegalArgumentException("XmlValDef cannot be null");
		
		StringBuffer fr = new StringBuffer();
		if(!XmlDataStruct.checkName(n,fr))
			throw new IllegalArgumentException("invalid StructItem name for:"+fr.toString());
		
		pname = n;
		xmlValDef = xvd ;
	}
	
	public XmlDataMember copyMe()
	{
		XmlDataMember si = new XmlDataMember();
		si.pname = pname;
		si.title = title ;

		si.xmlValDef = xmlValDef.copyMe();
		
		return si ;
	}

	public XmlValDef getXmlValDef()
	{
		return xmlValDef ;
	}
	
	public Object randomCreateValue()
	{
		return xmlValDef.randomCreateValue();
	}

	void setStructInfo(String n,String t, String vt, boolean ba, boolean bnullable,
			int maxlen,StoreType st,boolean bmultirows,String defstrval)
	{
		StringBuffer fr = new StringBuffer();
		if(!XmlDataStruct.checkName(n,fr))
			throw new IllegalArgumentException("invalid StructItem name for:"+fr.toString());
		
		pname = n;
		title = t ;
		xmlValDef.setStructInfo(vt, ba, bnullable, maxlen, st,bmultirows,defstrval);
	}

	public XmlDataStruct getBelongTo()
	{
		return belongTo;
	}

	public String getPath()
	{
		if(belongTo==null)
			return pname ;
		
		return belongTo.getPath() + pname;
	}

	public boolean equals(Object o)
	{
		XmlDataMember si = (XmlDataMember) o;
		if (!pname.equals(si.pname))
			return false;
		
		return xmlValDef.equals(si.xmlValDef);
	}
	
	public boolean equalsByBelongTo(Object o)
	{
		XmlDataMember si = (XmlDataMember) o;
		if (!pname.equals(si.pname))
			return false;
		
		if(!xmlValDef.equals(si.xmlValDef))
			return false;
		
		if (isArrayWithBelongTo() != si.isArrayWithBelongTo())
			return false;
		
		if(getStoreTypeWithBelongTo()!=si.getStoreTypeWithBelongTo())
			return false;

		return true;
	}

	public String getName()
	{
		return pname;
	}

	public String getValType()
	{
		return xmlValDef.getValType();
	}
	
	public String getDefaultStrVal()
	{
		return xmlValDef.getDefaultStrVal() ;
	}
	
	public boolean isMultiRows()
	{
		return xmlValDef.isMultiRows() ;
	}

	public boolean isArray()
	{
		return xmlValDef.isArray();
	}

	/**
	 * 判断本身，并且继承了父结构的是否是多值——相对于一个XmlData数据元素
	 * 
	 * @return
	 */
	public boolean isArrayWithBelongTo()
	{
		if (isArray())
			return true;

		if (belongTo == null)
			return false;

		boolean parray = belongTo.isArrayWithParent();
		if (parray)
			return true;

		return false;
	}

	public boolean isNullable()
	{
		return xmlValDef.isNullable();
	}
	
	void setNullable(boolean b)
	{
		xmlValDef.bNullable = b ;
	}

	public int getMaxLen()
	{
		return xmlValDef.getMaxLen();
	}

	public StoreType getStoreType()
	{
		return xmlValDef.getStoreType() ;
	}
	
	/**
	 * 判断本成员是否可以真正的做数据分离存储
	 * 目前只支持单值数据成员
	 * 
	 * 该方法才是用来真正判断是否需要建立分离存储的支持
	 * @return
	 */
	public StoreType getStoreTypeWithBelongTo()
	{
		if(getStoreType()==StoreType.Normal)
			return StoreType.Normal ;
		
		if(isArrayWithBelongTo()) //数组不直接支持分离存储
			return StoreType.Normal;
		
		int maxLen = getMaxLen();
		String valType = getValType();
		if(XmlVal.VAL_TYPE_STR.equals(getValType()))
		{
			
			if(maxLen<=0||maxLen>=1000)
				return StoreType.Normal;
			
			if(maxLen>500&&maxLen<1000)//长度大于500的字符串，不能建立索引
				return StoreType.Separate ;
		}
		else if(XmlVal.VAL_TYPE_BYTEARRAY.equals(valType))
		{
			if(maxLen<=0||maxLen>=1000)
				return StoreType.Normal;
			
			return StoreType.Separate ;
		}
		
		return getStoreType() ;
	}
	
	public String toFullPath()
	{
		return XmlDataPath.createPath(this).toFullString();
	}
	
	public String getTitle()
	{
		if(title!=null&&!title.equals(""))
			return title ;
		return pname ;
	}
	
	public void setTitle(String t)
	{
		title = t ;
	}
	
	public int getOrderNum()
	{
		return orderNum ;
	}
	
	public void setOrderNum(int ordn)
	{
		orderNum = ordn ;
	}
	
	public HashMap<String,String> getExtProp()
	{
		return extProps;
	}
	
	/**
	 * 
	 * @param n
	 * @param v
	 * @return 返回自己，方便外界连续调用
	 */
	public XmlDataMember setExtProp(String n,String v)
	{
		if(v==null)
		{
			extProps.remove(n) ;
			return this;
		}
		
		extProps.put(n, v) ;
		
		return this ;
	}
	//public String getDefaultXml
	
	/**
	 * 
	 */
	public XmlData toXmlData()
	{
		XmlData xd = xmlValDef.toXmlData();
		if(pname!=null)
			xd.setParamValue("name", pname);
		
		if(title!=null)
			xd.setParamValue("title", title);
		
		xd.setParamValue("order_num", orderNum);
		
		if(extProps!=null && extProps.size()>0)
		{
			XmlData extxd = xd.getOrCreateSubDataSingle("ext_prop") ;
			for(Map.Entry<String, String> n2v:extProps.entrySet())
			{
				extxd.setParamValue(n2v.getKey(), n2v.getValue());
			}
		}
		return xd;
	}

	public void fromXmlData(XmlData xd)
	{
		xmlValDef.fromXmlData(xd);
		pname = xd.getParamValueStr("name");
		title = xd.getParamValueStr("title");
		orderNum = xd.getParamValueInt32("order_num", 100) ;
		XmlData extxd = xd.getSubDataSingle("ext_prop") ;
		if(extxd!=null)
		{
			for(String n:extxd.getParamNames())
			{
				extProps.put(n, extxd.getParamValueStr(n)) ;
			}
		}
	}

	public int compareTo(XmlDataMember o)
	{
		return orderNum - o.orderNum;
	}
}