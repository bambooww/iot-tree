package org.iottree.core.ecp;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

public class ECPCompManager
{
	private static ILogger log = LoggerManager.getLogger(ECPCompManager.class) ;
	
	private static ECPCompManager instance = null ;
	
	public static ECPCompManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(ECPCompManager.class)
		{
			if(instance!=null)
				return instance ;
			return instance = new ECPCompManager() ;
		}
	}
	
	LinkedHashMap<String,ECPCompLib> id2lib = null ;
	
	private ECPCompManager()
	{}
	
	public LinkedHashMap<String,ECPCompLib> getLibMap()
	{
		if(id2lib!=null)
			return this.id2lib ;
		
		synchronized(this)
		{
			if(id2lib!=null)
				return this.id2lib ;
			
			LinkedHashMap<String,ECPCompLib> i2l = new LinkedHashMap<>() ;
			for(ECPCompLib c:this.loadLibs())
			{
				i2l.put(c.getLibId(),c) ;
			}
			this.id2lib = i2l ;
			if(this.id2lib.size()<=0)
			{
				File dir = calCompDir() ;
				try
				{
					ECPCompLibDir edir = ECPCompLibDir.addLib("default", "Default", dir) ;
					this.id2lib.put(edir.getLibId(),edir) ;
				}
				catch ( IOException e)
				{
					e.printStackTrace();
				}
			}
			return this.id2lib ;
		}
	}
	
	private List<ECPCompLib> loadLibs()
	{
		File dir =  calCompDir() ;
		final FileFilter ff = new FileFilter() {
			@Override
			public boolean accept(File f)
			{
				if(!f.isDirectory()) return false;
				
				String fn = f.getName() ;
				return fn.startsWith("lib_") ;
			}};
		ArrayList<ECPCompLib> rets = new ArrayList<>() ;
		
		if(dir.exists())
		{
			for(File mnf:dir.listFiles(ff))
			{
				String fn = mnf.getName() ;
				String id = fn.substring(4,fn.length()-5) ;
				ECPCompLib mnn = null;
				try
				{
					mnn = ECPCompLibDir.loadLib(id,mnf) ;//loadLib(mnf,id);
				}
				catch ( Exception e)
				{
					e.printStackTrace();
				}
				
				if(mnn==null)
				{
					log.warn("load ECPCompLib failed :"+mnf.getAbsolutePath());
					continue ;
				}
				rets.add(mnn) ;
			}
		}
		return rets ;
	}
	
	public ECPCompLibDir addLibDir(String title,String desc) throws Exception
	{
		File dir = calCompDir() ;
		ECPCompLibDir edir = ECPCompLibDir.addLib(title, desc, dir) ;
		getLibMap().put(edir.getLibId(), edir) ;
		return edir ;
	}
	
	
	private static File calCompDir()
	{
		return new File(Config.getDataDirBase(),"ecps/comps/");
	}
	
	public ECPCompLib getLibById(String lib_id)
	{
		return this.getLibMap().get(lib_id) ;
	}
	
	public boolean delLib(String libid)
	{
		ECPCompLibDir lib = (ECPCompLibDir)this.getLibById(libid) ;
		if(lib==null)
			return false;
		File libd = lib.getLibDir() ;
		if(Convert.deleteDir(libd))
		{
			this.getLibMap().remove(libid) ;
			return true;
		}
		return false;
	}
	
	public ECPComp getComp(String lib_id,String comp_id)
	{
		ECPCompLib lib = getLibById(lib_id);
		if(lib==null)
			return null ;
		return lib.getCompById(comp_id) ;
	}
	
	public ECPComp getCompByUID(String comp_uid)
	{//comp-00MWU69NPW-00MWU6ZCH100010
		List<String> ss = Convert.splitStrWith(comp_uid, "-") ;
		if(ss==null||ss.size()<3) return null ;
		return this.getComp(ss.get(1), ss.get(2)) ;
	}
	
	public ECPComp findCompById(String comp_id)
	{
		for(ECPCompLib lib:this.getLibMap().values())
		{
			ECPComp r = lib.getCompById(comp_id) ;
			if(r!=null)
				return r ;
		}
		return null ;
	}
}
