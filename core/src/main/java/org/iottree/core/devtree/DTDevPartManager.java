package org.iottree.core.devtree;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

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
		id2lib = loadLibs();
		if(id2lib.size()<=0)
		{
			try
			{
				addLib("Default","") ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return id2lib;
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
	
	static File calLibDir(String libid)
	{
		return new File(DIR,"lib_"+libid+"/") ;
	}
	
	void saveLib(DTDevPartLib lib) throws IOException
	{
		File libd = calLibDir(lib.getLibId()) ;
		File libf = new File(libd,"_lib.json") ;
		Convert.writeFileJO(libf, lib.toJO());
	}
	
	public List<DTDevPartLib> listLibs()
	{
		ArrayList<DTDevPartLib> rets = new ArrayList<>() ;
		rets.addAll(this.getId2Lib().values()) ;
		Collections.sort(rets) ;
		return rets;
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
	
	public boolean delLib(String libid)
	{
		DTDevPartLib lib = this.getLibById(libid) ;
		if(lib==null)
			return false;
		File libd = calLibDir(libid) ;
		if(Convert.deleteDir(libd))
		{
			this.getId2Lib().remove(libid) ;
			return true;
		}
		return false;
	}
	
	public DTDevPartTP getPartTP(String libid,String parttpid)
	{
		DTDevPartLib lib = getLibById(libid);
		if(lib==null)
			return null ;
		return lib.getPartTP(parttpid) ;
	}
	
	public DTDevPartTP getPartTPByUID(String uid)
	{
		if(Convert.isNullOrEmpty(uid))
			return null ;
		int k = uid.indexOf(".") ;
		if(k<=0) return null;
		String libid = uid.substring(0,k) ;
		String parttpid = uid.substring(k+1) ;
		return getPartTP(libid,parttpid);
	}
}
