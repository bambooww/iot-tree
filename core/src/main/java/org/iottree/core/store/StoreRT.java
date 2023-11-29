package org.iottree.core.store;

import java.util.LinkedHashMap;

import org.iottree.core.UAPrj;

/**
 * real time data store support
 * @author jason.zhu
 *
 */
public class StoreRT
{
	String id = null ;
	
	String name = null ;
	
	String title = null ;
	
	long scanIntervalMS = 10000 ;
	
	LinkedHashMap<String,StoreTB> name2tb = null ;
	
	UAPrj prj = null ;
	
	private transient Source usingSor = null ;
	
	public StoreRT(UAPrj prj,Source usingsor)
	{
		this.prj = prj ;
		this.usingSor = usingsor ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public long getScanIntervalMS()
	{
		return this.scanIntervalMS ;
	}
	
	public Source getUsingSource()
	{
		return usingSor ;
	}
	
	
}
