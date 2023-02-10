package org.iottree.core.util.jt;

import java.io.File;
import java.util.LinkedHashMap;

import org.iottree.core.Config;

public class JSONTempManager
{
	static JSONTempDir workTempDir = null ;
	
	static LinkedHashMap<String,JSONTempDir> app2TempDir = new LinkedHashMap<>() ;
	
	static JSONTempDir getOrloadGlobalWorkTempDir()
	{
		if(workTempDir!=null)
			return workTempDir;
		try
		{
//			loadJSONTempObs();
//			loadJSONTemps();
			//System.out.println("load work temp dir") ;
			File dir = new File(Config.getDataDirBase() + "/jt/json_temps/");
			workTempDir = new JSONTempDir(null,dir) ;
			return workTempDir;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null ;
		}
	}
	
	public static JSONTemp getJSONTemp(String name)
	{
		int k = name.indexOf('.') ;
		if(k<=0)
		{
			return workTempDir.getJSONTemp(name) ;
		}
		String appn = name.substring(0,k) ;
		String nn = name.substring(k+1) ;
		JSONTempDir jtd = app2TempDir.get(appn) ;
		if(jtd==null)
		{
			return null ;
		}
		return jtd.getJSONTemp(nn);
	}
}
