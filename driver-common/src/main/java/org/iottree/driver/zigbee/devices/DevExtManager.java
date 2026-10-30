package org.iottree.driver.zigbee.devices;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class DevExtManager
{
	private static DevExtManager instance = null ;
	
	public static DevExtManager getInstance()
	{
		if(instance!=null)
			return instance;
		
		synchronized(DevExtManager.class)
		{
			if(instance!=null)
				return instance;
			
			return instance = new DevExtManager() ;
		}
	}
	
	private HashMap<String,Object> name2vendor = new HashMap<>() ;
	
	private DevExtManager()
	{}
	
	private VendorDef loadVendorDef(String name) throws IOException
	{
		File f = new File(Config.getDataDirBase()+"/zigbee/devices/"+name.toLowerCase()+".json") ;
		JSONObject jo = Convert.readFileJO(f) ;
		if(jo==null)
			return null ;
		VendorDef vd = new VendorDef(name) ;
		if(!vd.fromJO(jo))
			return null ;
		return vd ;
	}
	
	public VendorDef getVendorDef(String name)
	{
		name = name.toLowerCase() ;
		Object obj = name2vendor.get(name) ;
		if(obj!=null)
		{
			if(obj instanceof VendorDef)
				return (VendorDef)obj ;
			return null ;
		}
		
		synchronized(this)
		{
			try
			{
				VendorDef vd = loadVendorDef(name) ;
				if(vd==null)
					this.name2vendor.put(name,"") ;
				else
					this.name2vendor.put(name,vd) ;
				return vd ;
			}
			catch ( IOException e)
			{
				e.printStackTrace();
				return null ;
			}
		}
	}
}
