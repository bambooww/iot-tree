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
public abstract class SimDev
{
	
	@data_val
	String id = null ;
	
	@data_val
	String name = null ;
	
	@data_val
	String title = null ;
	
	
	
	@data_val(param_name = "en")
	boolean bEnable = true ;

	@data_obj(obj_c = UATag.class)
	List<UATag> tags = new ArrayList<>();
	
	
	public SimDev()
	{
		this.id = CompressUUID.createNewId() ;
		//this.devId = devid ;
		//devDef = dd ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public SimDev withId(String id)
	{
		this.id = id ;
		return this ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public abstract String getDevTitle() ; 
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public List<UATag> getTags()
	{
		return tags ;
	}
	
	public SimDev asDevTags(List<UATag> tags)
	{
		//this.devId = devid ;
		if(tags==null)
			this.tags = new ArrayList<>() ;
		else
			this.tags = tags ;
		return this ;
	}
	
	
	public boolean RT_init(StringBuilder failedr)
	{
		return false;
	}
	
}
