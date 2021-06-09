package org.iottree.core.basic;

public class IdName
{
	String id = null ;
	
	String name = null ;
	
	String title = null ;
	
	public IdName(String id,String n)
	{
		this.id = id ;
		this.name = n ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public boolean chkId()
	{
		return true ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public IdName withTitle(String t)
	{
		this.title= t;
		return this ;
	}
}
