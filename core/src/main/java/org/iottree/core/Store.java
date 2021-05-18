package org.iottree.core;

import java.util.List;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;

@data_class
public class Store  // extends UANodeOC implements IOCUnit
{
	String id = null ;
	
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	
	@data_val(param_name = "tp")
	String storeTp = "db" ;// db  file mq redis ...
	
	public Store()
	{
		id = CompressUUID.createNewId() ;
	}
	
	public Store(String name,String title,String desc,String tp)
	{
		id = CompressUUID.createNewId() ;
		this.name = name ;
		this.title = title ;
		this.desc = desc ;
		storeTp = tp ;
	}
	
	
	public String getStoreTp()
	{
		return storeTp ;
	}

	public String getId()
	{
		return id ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	public String getDesc()
	{
		return desc ;
	}
}

