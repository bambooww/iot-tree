package org.iottree.core.store.gdb.conf;

import java.util.ArrayList;

import org.iottree.core.util.Convert;
import org.w3c.dom.*;


public class Module
{
	static Module parseModule(Gdb g,Element mele) throws Exception
	{
		Module m = new Module();
		m.belongTo = g ;
		
		m.usingDBName = mele.getAttribute(Gdb.ATTR_DBNAME) ;
		if(Convert.isNullOrEmpty(m.usingDBName))
			m.usingDBName = mele.getAttribute(Gdb.ATTR_DB_NAME) ;
		
		m.name = mele.getAttribute(Gdb.ATTR_NAME);
		if(m.name==null||m.name.equals(""))
			return null ;
		
		m.timeOut = g.getTimeOut() ;
		String module_time_str = mele.getAttribute(Gdb.ATTR_TIMEOUT);
		if(module_time_str!=null&&!module_time_str.equals(""))
			m.timeOut = Integer.parseInt(module_time_str);
		
		m.desc = mele.getAttribute(Gdb.ATTR_DESC);
		
		Element[] func_eles = XDataHelper.getCurChildElement(mele, Gdb.TAG_FUNC);
		if(func_eles!=null)
		{
			for(Element ftmpe:func_eles)
			{
				Func tmpf = Func.parseFunc(m,ftmpe);
				if(tmpf!=null)
					m.funcs.add(tmpf);
			}
		}
		
		return m ;
	}
	
	transient Gdb belongTo = null ;
	
	String usingDBName = null ;
	
	String name = null ;
	
	String desc = null ;
	
	int timeOut = -1 ;
	
	ArrayList<Func> funcs = new ArrayList<Func>() ;
	
	private Module()
	{
		
	}
	
	public Gdb getBelongTo()
	{
		return belongTo ;
	}
	
	
	public String getName()
	{
		return name ;
	}
	
	public String getUsingDBName()
	{
		if(Convert.isNotNullEmpty(usingDBName))
			return usingDBName ;
		
		return belongTo.usingDBName ;
	}
	
	public String getDesc()
	{
		return desc ;
	}
	
	public int getTimeOut()
	{
		return timeOut;
	}
	
	public ArrayList<Func> getFuncs()
	{
		return funcs ;
	}
}
