package org.iottree.core.station;

import java.util.List;

/**
 * multi iottree server can be connected together. and then construct a bigger tree.
 * 
 * StationManager represent local station management
 * 1) it has one parent or null
 * 2) it can has 0 or more children(stations)
 * @author zzj
 *
 */
public class StationManager
{
	static StationManager instance = null ;
	
	public static StationManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(StationManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new StationManager() ;
			return instance ;
		}
		
	}
	
	/**
	 * local station's parent address
	 */
	String parentHost = null ;
	
	int parentPort = 9999 ;
	
	List<Station> subStations = null ;
	
	private StationManager()
	{
		
	}
	
	
	public String getParentHost()
	{
		return parentHost ;
	}
	
	public int getParentPort()
	{
		return parentPort ;
	}
	
	public List<Station> getSubStations()
	{
		return subStations ;
	}
	
	
}
