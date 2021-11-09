package org.iottree.core.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.ext.AbstractPlugin;
import org.iottree.core.util.Convert;

/**
 * 
 * @author jason.zhu
 *
 */
public class PlugManager
{
	private static PlugManager instance = null ;
	
	public static PlugManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(PlugManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new PlugManager() ;
			return instance ;
		}
	}
	
	LinkedHashMap<String,PlugDir> name2plug = new LinkedHashMap<>() ;
	
	private PlugManager()
	{
		findPlugs();
	}
	
	private void findPlugs()
	{
		File plugdir = new File(Config.getDataDirBase()+"/plugins/");
		if(!plugdir.exists())
			return ;//new ArrayList<>(0) ;
		
		File[] dirfs = plugdir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				return f.isDirectory() ;
			}}) ;
		
		for(File dirf:dirfs)
		{
			PlugDir pd = PlugDir.parseDir(dirf);
			if(pd==null)
				continue ;
			name2plug.put(pd.getName(), pd) ;
		}
		//return plugname2cl ;
	}
	
	public Collection<PlugDir> listPlugs()
	{
		return this.name2plug.values();
	}
	
	public PlugDir getPlug(String name)
	{
		return this.name2plug.get(name) ;
	}
	
	
	public HashMap<String,Object> getJsApiAll()
	{
		HashMap<String,Object> ret = new HashMap<>() ;
		for(PlugDir pd:this.name2plug.values())
		{
			try
			{
				HashMap<String,Object> n2o = pd.getOrLoadJsApiObjs() ;
				if(n2o!=null)
					continue ;
				ret.putAll(n2o);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ret ;
	}
}
