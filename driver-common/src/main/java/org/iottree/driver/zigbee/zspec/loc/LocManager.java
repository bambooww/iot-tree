package org.iottree.driver.zigbee.zspec.loc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class LocManager
{
	private static LocManager instance = null ;
	
	public static LocManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(LocManager.class)
		{
			if(instance!=null)
				return instance ;
			
			return instance = new LocManager() ;
		}
	}
	
	private static File getDefEDFacFile()
	{
		return new File(Config.getDataDirBase()+"/zigbee/def_ed_fac.json") ;
	}
	
	private HashMap<String,LocEDFacModel> key2fac_model = null ;
	
	private LocManager()
	{}
	
	private HashMap<String,LocEDFacModel> loadFacModels() throws IOException
	{
		HashMap<String,LocEDFacModel> ret = new HashMap<>() ;
		File f = getDefEDFacFile() ;
		JSONObject jo = Convert.readFileJO(f) ;
		if(jo==null)
			return ret ;
		for(String fac:jo.keySet())
		{
			JSONObject mdsjo = jo.getJSONObject(fac) ;
			for(String model:mdsjo.keySet())
			{
				JSONObject mdjo = mdsjo.getJSONObject(model) ;
				LocEDFacModel fm = new LocEDFacModel(fac,model) ;
				fm.fromJO(mdjo) ;
				ret.put(fm.getFacModelKey(),fm) ;
			}
		}
		return ret ;
	}
	
	public HashMap<String,LocEDFacModel> getFacModelMap()
	{
		if(key2fac_model!=null)
			return key2fac_model;
		
		synchronized(this)
		{
			if(key2fac_model!=null)
				return key2fac_model;
			
			try
			{
				return key2fac_model = loadFacModels() ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return null ;
			}
		}
	}
	
	public LocEDFacModel getFacModel(String fac_name,String model)
	{
		String k = LocEDFacModel.calFacModelKey(fac_name, model) ;
		return getFacModelMap().get(k) ;
	}
	
	
}
