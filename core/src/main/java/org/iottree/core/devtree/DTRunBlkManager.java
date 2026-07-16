package org.iottree.core.devtree;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class DTRunBlkManager
{
	private static DTRunBlkManager instance = null ;
	
	public static DTRunBlkManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(DTRunBlkManager.class)
		{
			if(instance!=null)
				return instance ;
			return instance = new DTRunBlkManager() ;
		}
	}
	
	private static File getRunBlksDir()
	{
		return new File(Config.getDataDirBase()+"/devtree/runblks/") ;
	}
	
	private LinkedHashMap<String,DTRunBlkCat> name2cat = null ;
	
	private DTRunBlkManager()
	{}
	
	private LinkedHashMap<String,DTRunBlkCat> loadRunBlks()
	{
		LinkedHashMap<String,DTRunBlkCat> rets = new LinkedHashMap<>() ;
		File[] catdirs = getRunBlksDir().listFiles(new FileFilter() {

			@Override
			public boolean accept(File d)
			{
				return d.isDirectory();
			}}) ;
		if(catdirs!=null)
		{
			for(File catdir:catdirs)
			{
				try
				{
					DTRunBlkCat cat = loadCat(catdir);
					if(cat==null)
						continue ;
					rets.put(cat.getName(),cat) ;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return rets;
	}
	
	private DTRunBlkCat loadCat(File catdir) throws IOException
	{
		if(!catdir.exists()) return null;
		File catf = new File(catdir,"__cat.json") ;
		JSONObject catjo = Convert.readFileJO(catf) ;
		if(catjo==null)
			return null ;
		String t_en = catjo.optString("t_en") ;
		if(Convert.isNullOrEmpty(t_en))
			t_en = catjo.optString("title") ;
		if(Convert.isNullOrEmpty(t_en))
			return null ;
		String t_cn = catjo.optString("t_cn") ;
		if(Convert.isNullOrEmpty(t_cn))
			t_cn = t_en ;
		DTRunBlkCat cat = new DTRunBlkCat(catdir.getName(),t_en,t_cn) ;
		
		File[] ffs = catdir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f)
			{
				if(!f.isFile())
					return false;
				String fn = f.getName() ;
				return fn.startsWith("blk_") && fn.endsWith(".json");
			}}) ;
		for(File ff:ffs)
		{
			try
			{
				DTRunBlk rb = loadRunBlk(cat ,ff) ;
				if(rb==null)
					continue ;
				cat.setRunBlk(rb);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return cat ;
	}
	
	private DTRunBlk loadRunBlk(DTRunBlkCat cat ,File f) throws Exception
	{
		JSONObject jo = Convert.readFileJO(f) ;
		if(jo==null)
			return null ;
		String n = f.getName() ;
		n = n.substring(4,n.length()-5) ;
		String cls = jo.optString("class") ;
		if(Convert.isNullOrEmpty(cls))
			return null ;
		Class<?> c = Class.forName(cls) ;
		DTRunBlk rb = (DTRunBlk)c.getConstructor(DTRunBlkCat.class,String.class).newInstance(cat,n) ;
		StringBuilder failedr = new StringBuilder() ;
		if(!rb.fromJO(jo, failedr))
			throw new Exception(failedr.toString()) ;
		return rb ;
	}
	
	public LinkedHashMap<String,DTRunBlkCat> getRunBlkCatMap()
	{
		if(this.name2cat!=null)
			return this.name2cat ;
		
		synchronized(this)
		{
			if(this.name2cat!=null)
				return this.name2cat ;
			
			return this.name2cat = loadRunBlks() ;
		}
	}
	
	public DTRunBlkCat getRunBlkCat(String catn)
	{
		return this.getRunBlkCatMap().get(catn) ;
	}
	
	public DTRunBlk getRunBlk(String catname,String blkname)
	{
		DTRunBlkCat cat = this.getRunBlkCatMap().get(catname) ;
		if(cat==null)
			return null ;
		return cat.getRunBlk(blkname) ;
	}
	
	public DTRunBlk getRunBlkByUID(String uid)
	{
		int k = uid.indexOf('.') ;
		if(k<=0)
			return null ;
		return this.getRunBlk(uid.substring(0,k), uid.substring(k+1)) ;
	}
}
