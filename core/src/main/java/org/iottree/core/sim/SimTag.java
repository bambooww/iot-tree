package org.iottree.core.sim;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAVal;
import org.iottree.core.basic.JSObMap;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;

@data_class
public abstract class SimTag  extends JSObMap
{
	@data_val
	String name = null ;
	
	protected SimDev belongTo = null ;
	
	
	public SimTag()
	{}
	
	public SimTag(String name)
	{
		this.name = name ;
	}
	
	
	public SimDev getBelongTo()
	{
		return belongTo ;
	}
	
	
	public String getName()
	{
		return name ;
	}
	
	public SimTag asName(String n)
	{
		this.name = n ;
		return this ;
	}
	
	//public 
	public abstract Class<?> getValueTp();
	
	public abstract Object getValue() ;
	
	public abstract void setValue(Object val) ;
	

	public Object JS_get(String key)
	{
		switch(key)
		{
		case "_value":
		case "_v":
		case "_pv":
			return this.getValue() ;
		default:
			return null ;
		}
	}

	public List<String> JS_names()
	{
		List<String> rets = super.JS_names() ;
		rets.add("_value");
		rets.add("_v");
		rets.add("_pv");
		return rets ;
	}
	
	public Class<?> JS_type(String key)
	{
		switch(key.toLowerCase())
		{
		case "_value":
		case "_v":
		case "_pv":
			return this.getValueTp();
		default:
			break ;//do nothing
		}
		return null ;
	}
	
	public void JS_set(String key,Object v)
	{
		switch(key.toLowerCase())
		{
		case "_value":
		case "_v":
		case "_pv":
			this.setValue(v);
			return;
		default:
			break ;//do nothing
		}
		
		super.JS_set(key, v);
	}
}
