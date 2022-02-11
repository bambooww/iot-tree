package org.iottree.core.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.DevDef;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;
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

	@data_obj(obj_c = SimTag.class)
	List<SimTag> tags = new ArrayList<>();
	
	public SimDev()
	{
		
	}
	
	
	public abstract String getDevTitle() ; 
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public List<SimTag> getTags()
	{
		return tags ;
	}
	
	public SimTag getTagByName(String name)
	{
		for(SimTag t:tags)
		{
			if(name.equals(t.getName()))
				return t ;
		}
		return null ;
	}
	
	public SimDev asDevTags(List<UATag> tags)
	{
		//this.devId = devid ;
		if(tags==null)
			this.tags = new ArrayList<>() ;
		
		for(UATag uat:tags)
		{
			//this.tags = tags ;
		}
		
		return this ;
	}
	
	
	public abstract void init();
	
	public abstract boolean RT_init(StringBuilder failedr);
	
	public Object JS_get(String key)
	{
		Object r = super.JS_get(key);
		if (r != null)
			return r;
		return this.getTagByName(key);
	}

	public List<Object> JS_names()
	{
		List<Object> rets = super.JS_names();

		for (SimTag t : this.getTags())
		{
			rets.add(t.getName());
		}
		return rets;
	}
}
