package org.iottree.core.comp;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.dict.DataClass;
import org.iottree.core.dict.DataNode;
import org.iottree.core.dict.DictManager;

/**
 * scan /_iottree/di_div_comps to acquire div component all
 * 
 * then create DivCompItem list in server.
 * 
 * @author jason.zhu
 *
 */
public class DivCompManager
{
	static DivCompManager ins = null ;
	
	public static DivCompManager getInstance()
	{
		if(ins!=null)
			return ins ;
		
		synchronized(DivCompManager.class)
		{
			if(ins!=null)
				return ins ;
			
			ins = new DivCompManager() ;
			return ins ;
		}
	}
	
	LinkedHashMap<String, DivCompCat> name2cat = new LinkedHashMap<String, DivCompCat>() ;
	
	private DivCompManager()
	{
		loadComps();
	}
	
	private void loadComps()
	{
		File dirf = new File(Config.getWebappBase()+"/_iottree/di_div_comps/");
		if(!dirf.exists())
			return ;
		final FileFilter ff = new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(!f.isDirectory())
					return false;
				return true;
			}};
		File[] fss = dirf.listFiles(ff) ;
		Arrays.sort(fss);
		for(File itemdir:fss)
		{
			try
			{
				DivCompCat dcc = loadCat(itemdir) ;
				if(dcc==null)
					continue;
				name2cat.put(dcc.getName(), dcc) ;
			}
			catch(Exception e)
			{
				System.out.println("load div comp cat error:"+itemdir.getName()) ;
			}
		}
	}
	
	private DivCompCat loadCat(File dir) throws Exception
	{
		if(!dir.exists())
			return null ;
		File catf = new File(dir,"_cat.xml") ;
		if(!catf.exists())
			return null ;
		
		DivCompCat ret = null;
		DataClass dc = null;
		try(FileInputStream fis = new FileInputStream(catf);)
		{
			dc = DictManager.loadDataClass(fis) ;
			if(dc==null)
				return null ;
			String n = dc.getClassName() ;
			if(Convert.isNullOrEmpty(n))
				return null ;
			ret = new DivCompCat(n,dc) ;
		}
		
		for(DataNode dn:dc.getRootNodes())
		{
			String n = dn.getName() ;
			if(Convert.isNullOrEmpty(n))
				continue ;
			DivCompItem item = new DivCompItem(ret,n,dn) ;
			ret.items.add(item) ;
		}
		
		return ret;
	}
	
	public Collection<DivCompCat> listCats()
	{
		Collection<DivCompCat> rets = name2cat.values() ;
		
		return rets ;
	}
	
	public DivCompCat getCat(String n)
	{
		return name2cat.get(n) ;
	}
	
	public DivCompItem getItem(String catname,String itemname)
	{
		DivCompCat cat = name2cat.get(catname) ;
		if(cat==null)
			return null ;
		return cat.getItem(itemname) ;
	}
	
	
}
