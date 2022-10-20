package org.iottree.core.util.filter;

import java.util.HashMap;

/**
 * Manager
 * @author jason zhu
 *
 */
public class FilterManager
{
	static FilterManager instance = null ;
	
	public static FilterManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(FilterManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new FilterManager() ;
			return instance ;
		}
	}
	
	
	static HashMap<String,AbstractFilter> N2F = new HashMap<String,AbstractFilter>();
	
	static void registerFilter(String classn)
	{
		try
		{
			Class c = Class.forName(classn) ;
			AbstractFilter f = (AbstractFilter)c.newInstance();
			N2F.put(f.getFilterName(), f);
		}
		catch(Exception e)
		{
			e.printStackTrace( );
		}
	}
	
	static
	{
		registerFilter("com.dw.scada2.filter.FilterLimit");
	}
	
	private FilterManager()
	{
		
	}
}

