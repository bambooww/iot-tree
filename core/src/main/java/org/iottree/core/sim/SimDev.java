package org.iottree.core.sim;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;

/**
 * according to Device Lib DevDef,it create a slave device item
 * it will be used as Modbus Slave
 * 
 * @author jason.zhu
 */
@data_class
public abstract class SimDev extends SimNode
{
	@data_val(param_name = "en")
	boolean bEnable = true ;

	
	public SimDev()
	{
		
	}
	
	
	public abstract String getDevTitle() ; 
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	transient private List<SimTag> simTags = null ; 
	
	protected abstract List<SimTag> listSimTagsInner() ;
	
	public List<SimTag> getSimTags()
	{
		List<SimTag> r = simTags;
		if(r!=null)
			return r ;
		
		synchronized(this)
		{
			List<SimTag> rets = listSimTagsInner() ;
			if(rets==null)
				rets = new ArrayList<>() ;
			
			for(SimTag st:rets)
				st.belongTo = this ;
			
			simTags = rets ;
			return rets ;
		}
	}
	
	protected void clearBuffer()
	{
		simTags = null ;
	}
	
	
	public SimTag getSimTagByName(String n)
	{
		List<SimTag> tags = getSimTags() ;
		if(tags==null)
			return null ;
		
		for(SimTag tag:tags)
		{
			if(n.equals(tag.getName()))
				return tag;
		}
		
		return null ;
	}
	
	
	public abstract void init();
	
	public abstract boolean RT_init(StringBuilder failedr);
	
	public Object JS_get(String key)
	{
		Object r = super.JS_get(key);
		if (r != null)
			return r;
		return this.getSimTagByName(key);
	}
	
//	@Override
//	public void JS_set(String key, Object v)
//	{
//		// TODO Auto-generated method stub
//		super.JS_set(key, v);
//	}
	
	@Override
	public Class<?> JS_type(String key)
	{
		SimTag st = this.getSimTagByName(key);
		if(st!=null)
		{
			return st.getClass();
		}
		
		return super.JS_type(key);
	}

	public List<String> JS_names()
	{
		List<String> rets = super.JS_names();

		List<SimTag> tags = getSimTags() ;
		if(tags!=null)
		{
			for(SimTag tag:tags)
			{
				String n = tag.getName() ;
				rets.add(n);
			}
		}
		
		return rets;
	}
}
