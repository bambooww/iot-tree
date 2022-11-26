package org.iottree.core.store;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;

/**
 * 
 * @author jason.zhu
 *
 */
@data_class
public abstract class Store
{
	@data_val(param_name = "id")
	String id = null ;
	
	@data_val(param_name = "name")
	String name = null ;
	
	@data_val
	String title = null ;
	
	@data_val(param_name = "enable")
	boolean bEnable = true ;
	
	@data_val(param_name = "desc")
	String desc = "" ;
	
	public Store()
	{
		this.id = CompressUUID.createNewId();
	}
	
//	public Store(String n,String t)
//	{
//		this.id = CompressUUID.createNewId();
//		this.name = n ;
//		this.title = t ;
//	}
	
	public String getId()
	{
		return id ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}

	public String getDesc()
	{
		return this.desc ;
	}
	
	public abstract String getStoreTp() ;
	
	public abstract String getStoreTpTitle() ;
}
