package org.iottree.core.comp;

import java.util.*;

import org.iottree.core.dict.DataClass;

public class DivCompCat
{
	String name = null ;
	
	//String title = null ;
	
	DataClass dc = null ;
	
	ArrayList<DivCompItem> items = new ArrayList<>() ;
	
	public DivCompCat(String name,DataClass dc)
	{
		this.name = name ;
		this.dc = dc ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		return this.dc.getNameBySysLan(this.name) ;
	}
	
	public List<DivCompItem> getItems()
	{
		return items;
	}
	
	public DivCompItem getItem(String name)
	{
		for(DivCompItem item:items)
		{
			if(item.getName().equals(name))
				return item ;
		}
		return null ;
	}
}
