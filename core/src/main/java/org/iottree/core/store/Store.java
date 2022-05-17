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
	
	public Store()
	{}
	
	public Store(String n)
	{
		this.id = CompressUUID.createNewId();
		this.name = n ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	
	public abstract String getStoreTp() ;
}
