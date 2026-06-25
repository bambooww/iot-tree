package org.iottree.core.devtree;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class DTDevPartManager
{
	private static DTDevPartManager instance = null ;
	
	private static File DIR = new File(Config.getDataDirBase()+"/devtree/partlibs/") ;
	
	public static DTDevPartManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(DTDevPartManager.class)
		{
			if(instance!=null)
				return instance ;
			
			if(!DIR.exists())
				DIR.mkdirs() ;
			
			return instance = new DTDevPartManager() ;
		}
	}
	
	private LinkedHashMap<String,DTDevPartLib> id2lib = null ;
	
	private DTDevPartManager()
	{}
	
	public synchronized LinkedHashMap<String,DTDevPartLib> getId2Lib()
	{
		if(id2lib!=null)
			return id2lib;
		return id2lib = loadLibs();
	}
	
	private LinkedHashMap<String,DTDevPartLib> loadLibs()
	{
		File[] fs = DIR.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(!f.isDirectory())
					return false;
				String fn = f.getName() ;
				return fn.startsWith("lib_");
			}}) ;
		
		LinkedHashMap<String,DTDevPartLib> ret = new LinkedHashMap<>() ;
		for(File f:fs)
		{
			String libid = f.getName().substring(4) ;
			try
			{
				File libf = new File(f,"_lib.json") ;
				JSONObject jo = Convert.readFileJO(libf) ;
				DTDevPartLib lib = new DTDevPartLib() ;
				if(!lib.fromJO(libid, jo))
					continue ;
				ret.put(lib.libId,lib) ;
			}
			catch(Exception ee)
			{
				System.out.println(ee.getMessage()) ;
			}
		}
		return ret ;
	}
	
	private static File calLibDir(String libid)
	{
		return new File(DIR,"lib_"+libid+"/") ;
	}
	
	void saveLib(DTDevPartLib lib) throws IOException
	{
		File libf = new File(DIR,"_lib.json") ;
		Convert.writeFileJO(libf, lib.toJO());
	}
	
	public DTDevPartLib getLibById(String libid)
	{
		return getId2Lib().get(libid) ;
	}
	
	public DTDevPartLib addLib(String title,String desc) throws IOException
	{
		DTDevPartLib lib = new DTDevPartLib(title,desc) ;
		File dirf = calLibDir(lib.getLibId()) ;
		dirf.mkdirs() ;
		saveLib(lib) ;
		getId2Lib().put(lib.getLibId(),lib) ;
		return lib ;
	}
}
