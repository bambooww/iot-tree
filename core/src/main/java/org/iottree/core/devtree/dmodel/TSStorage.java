package org.iottree.core.devtree.dmodel;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.devtree.DTTree;

public abstract class TSStorage
{
	
	DTTree owner ;
	
	String name ;
	
	String title ;
	
	int saveIdCC=0 ;
	
	ArrayList<TSTag> tstags = new ArrayList<>() ;
	
	public TSStorage(DTTree owner)
	{
		this.owner =owner ;
	}
	
	public DTTree getOwner()
	{
		return this.owner ;
	}
	
	public String getName()
	{
		return name; 
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public List<TSTag> getTSTags()
	{
		return this.tstags ;
	}
}
