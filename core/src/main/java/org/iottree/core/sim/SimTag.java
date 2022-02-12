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
	
	
}
