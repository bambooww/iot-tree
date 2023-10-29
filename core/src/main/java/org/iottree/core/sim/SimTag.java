package org.iottree.core.sim;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAVal;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsProp;
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

	public List<JsProp> JS_props()
	{
		List<JsProp> rets = super.JS_props() ;
		
		Class<?> vt = this.getValueTp();

		rets.add(new JsProp("_pv",null,vt,false,"SimTag Value","Tag Value,you can get or set by using '='"));
		//rets.add(new JsProp("_value",null,vt,false,"SimTag Value","same as _pv"));
		//rets.add(new JsProp("_v",null,vt,false,"SimTag Value","same as _pv"));
		
//		rets.add(new JsProp("_valid",Boolean.class,"Valid","Tag Value is valid or not in running"));
//		rets.add(new JsProp("_updt",Long.class,"Update Date","Tag Value last update date with millisseconds,value may not be changed"));
//		rets.add(new JsProp("_chgdt",Long.class,"Change Date","Tag Value last changed date with millisseconds"));
		
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
