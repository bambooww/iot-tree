package org.iottree.core.store.gdb;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;

public class DataColumn implements IXmlDataable
{
	private String name = null ;
	private String title = null ;
	private Class dataType = null ;
	
	
	/**
	 * jdbc����
	 */
	int jdbcType = -1 ;
	
	int preciesion = -1 ;
	int scale = -1 ;
	
	DataColumn()
	{}
	
	public DataColumn(String name,Class datatype)
	{
		//this.srcName = name ;
		this.name = name.toUpperCase() ;
		this.dataType = datatype ;
	}
	
	public DataColumn(String name,Class datatype,int jdbctype,
			int precision,int scale)
	{
		//this.srcName = name ;
		this.name = name.toUpperCase() ;
		this.dataType = datatype ;
		this.jdbcType = jdbctype ;
		this.preciesion = precision;
		this.scale = scale ;
	}
	
	public DataColumn(String name,String title,Class datatype)
	{
		//this.srcName = name ;
		this.name = name.toUpperCase() ;
		this.title = title ;
		this.dataType = datatype ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		if(Convert.isNotNullEmpty(title))
			return title ;
		return name ;
	}
	
	public Class getDataType()
	{
		return dataType ;
	}
	
	public int getJdbcType()
	{
		return jdbcType ;
	}
	
	public int getPreciesion()
	{
		return preciesion;
	}
	
	public int getScale()
	{
		return scale ;
	}
	
	public String toLnStr()
	{
		return "["+name+"]jdbc_tp="+jdbcType+" preciesion="+preciesion+" scale="+scale ;
	}

	public XmlData toXmlData()
	{
		XmlData xd = new XmlData() ;
		xd.setParamValue("name", name) ;
		if(title!=null)
			xd.setParamValue("title", title) ;
		if(dataType!=null)
			xd.setParamValue("type", dataType.getCanonicalName()) ;
		return xd;
	}

	public void fromXmlData(XmlData xd)
	{
		name = xd.getParamValueStr("name") ;
		title = xd.getParamValueStr("title");
		String strdt = xd.getParamValueStr("type") ;
		if(Convert.isNotNullEmpty(strdt))
		{
			try
			{
				dataType = Class.forName(strdt) ;
			}
			catch(Exception e)
			{}
		}
	}
}
