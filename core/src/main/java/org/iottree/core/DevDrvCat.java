package org.iottree.core;

import java.util.ArrayList;
import java.util.List;

public class DevDrvCat
{
	String name = null ;
	
	String title = null ;
	
	ArrayList<DevDriver> drivers = new ArrayList<>() ;
	
	public DevDrvCat()
	{}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public List<DevDriver> getDrivers()
	{
		return this.drivers ;
	}
	
//	public List<DevDriver> filterDrivers(UACh ch)
//	{
//		ArrayList<DevDriver> rets = new ArrayList<>() ;
//		ch.getSupportedDrivers();
//		return rets ;
//	}
}
