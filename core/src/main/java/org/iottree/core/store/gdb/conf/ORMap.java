package org.iottree.core.store.gdb.conf;

import java.util.*;

import org.w3c.dom.Element;

public class ORMap
{
	static ORMap parseORMap(Gdb g,Element ele)
	{
		ORMap orm = new ORMap();
		orm.belongTo = g ;
		
		orm.id = ele.getAttribute(Gdb.ATTR_ID);
		orm.clazz = ele.getAttribute(Gdb.ATTR_CLASS);
		//orm.table = ele.getAttribute(Gdb.ATTR_TABLE)
		Element[] meles = XDataHelper.getCurChildElement(ele, Gdb.TAG_MAP);
		if(meles!=null)
		{
			for(Element tmpe:meles)
			{
				String col = tmpe.getAttribute(Gdb.ATTR_COLUMN);
				String prop = tmpe.getAttribute(Gdb.ATTR_PROPERTY);
				orm.property2column.put(prop,col);
			}
		}
		
		Element[] embed_eles = XDataHelper.getCurChildElement(ele, Gdb.TAG_EMBED);
		if(embed_eles!=null)
		{
			for(Element tmpe:embed_eles)
			{
				String c = tmpe.getAttribute(Gdb.ATTR_CLASS);
				String prop = tmpe.getAttribute(Gdb.ATTR_PROPERTY);
				orm.property2embedClass.put(prop,c);
			}
		}
		return orm ;
	}
	
	Gdb belongTo = null ;
	
	String id = null ;
	String clazz = null ;
	//String table = null ;
	
	HashMap<String,String> property2column = new HashMap<String,String>();
	
	HashMap<String,String> property2embedClass = new HashMap<String,String>() ;
	
	public String getId()
	{
		return id ;
	}
	
	public String getClazz()
	{
		return clazz;
	}
	
//	public String getTable()
//	{
//		return table ;
//	}
	
	public HashMap<String,String> getProperty2ColumnMap()
	{
		return property2column;
	}
	
	public String getColumnByProperty(String p)
	{
		return property2column.get(p);
	}
	
	public HashMap<String,String> getProperty2EmbedClassMap()
	{
		return property2embedClass;
	}
	
	public String getEmbedClassByProperty(String p)
	{
		return property2embedClass.get(p);
	}
}
