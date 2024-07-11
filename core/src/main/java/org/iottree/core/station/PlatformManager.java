package org.iottree.core.station;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * a platform can has many station nodes,every station is run remote and controlled by platform
 * 
 *   1) station's running state can be controlled by platform
 *   2) station's project can be add or update by platform
 *   3) station's running data can be syn to platform
 *   4) station's iot-tree self update can be set by platform
 *   
 * 
 * @author jason.zhu
 */
public class PlatformManager
{
	private static PlatformManager instance = null ;
	
	public static PlatformManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		File f = Config.getConfFile("platform.json") ;
		if(!f.exists())
			throw new RuntimeException("no platfrom.json found") ;
		
		synchronized(PlatformManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new PlatformManager() ;
			return instance ;
		}
	}
	
	private static Boolean bInPlatform = null ;
	
	/**
	 * 
	 * @return
	 */
	public static boolean isInPlatform()
	{
		if(bInPlatform!=null)
			return bInPlatform;
		
		File f = Config.getConfFile("platform.json") ;
		bInPlatform = f.exists() ;
		return bInPlatform;
	}
	
	private LinkedHashMap<String,PStation> id2station = null;// new LinkedHashMap<>() ;
	
	private HashMap<String,Long> unknownStation2DT = new HashMap<>() ;
	
	private PlatformManager()
	{}
	
	public Map<String,PStation> getPStationMap()  //throws IOException
	{
		if(id2station!=null)
			return id2station;
		
		try
		{
			id2station = loadStations() ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace(); 
		}
		return id2station;
	}
	
	private LinkedHashMap<String,PStation> loadStations() throws IOException
	{
		LinkedHashMap<String,PStation> rets = new LinkedHashMap<>() ;
		File f = Config.getConfFile("platform.json") ;
		if(f.exists())
		{
			JSONObject jo = Convert.readFileJO(f) ;
			JSONArray jarr = jo.optJSONArray("stations") ;
			if(jarr!=null)
			{
				int n = jarr.length() ;
				for(int i = 0 ; i < n ; i ++)
				{
					JSONObject tmpjo = jarr.getJSONObject(i) ;
					PStation ps = PStation.fromJO(tmpjo) ;
					if(ps==null)
						continue ;
					rets.put(ps.id,ps) ;
				}
			}
		}
		return rets ;
	}
	
	public PStation getStationById(String stationid)
	{
		return getPStationMap().get(stationid) ;
	}
	
	public void fireUnknownStation(String stationid)
	{
		unknownStation2DT.put(stationid,System.currentTimeMillis()) ;
	}
	
	public Map<String,Long> getUnknownStations()
	{
		return unknownStation2DT ;
	}
}
