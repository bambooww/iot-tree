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

public class DTTreeManager
{
	private static DTTreeManager instance = null ;
	
	private static File DIR = new File(Config.getDataDirBase()+"/devtree/trees/") ;
	
	public static DTTreeManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(DTTreeManager.class)
		{
			if(instance!=null)
				return instance ;
			
			if(!DIR.exists())
				DIR.mkdirs() ;
			
			return instance = new DTTreeManager() ;
		}
	}
	
	private static File calTreeFile(String treeid)
	{
		return new File(DIR,"tree_"+treeid+".json") ;
	}
	
	private LinkedHashMap<String,DTTree> id2tree = null;
	
	private DTTreeManager()
	{}
	
	public LinkedHashMap<String,DTTree> getId2Tree()
	{
		if(id2tree!=null)
			return id2tree ;
		synchronized(this)
		{
			if(id2tree!=null)
				return id2tree ;
			return id2tree = loadTrees();
		}
	}
	
	private LinkedHashMap<String,DTTree> loadTrees()
	{
		LinkedHashMap<String,DTTree> rets = new LinkedHashMap<>() ;
		if(!DIR.exists())
			return rets ;
		File[] fs = DIR.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(!f.isFile()) return false;
				String fn = f.getName() ;
				return fn.startsWith("tree_") && fn.endsWith(".json") ;
			}});
		
		for(File f:fs)
		{
			try
			{
				JSONObject tmpjo = Convert.readFileJO(f) ;
				DTTree dtt = new DTTree() ;
				if(dtt.fromJO(tmpjo))
				{
					rets.put(dtt.getTreeId(),dtt) ;
					dtt.updateDT = f.lastModified();
				}
			}
			catch(Exception ee)
			{
				System.out.println("load tree error :"+f.getName());
				ee.printStackTrace();
			}
		}
		return rets ;
	}
	
	synchronized void saveTree(DTTree dtt) throws IOException
	{
		File f = calTreeFile(dtt.getTreeId()) ;
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs() ;
		
		JSONObject jo = dtt.toJO(false) ;
		Convert.writeFileJO(f, jo);
		dtt.updateDT = f.lastModified();
	}
	
	public ArrayList<DTTree> listTrees()
	{
		ArrayList<DTTree> rets = new ArrayList<>() ;
		rets.addAll(getId2Tree().values()) ;
		Collections.sort(rets);
		return rets ;
	}
	
	public DTTree getTreeById(String treeid)
	{
		return getId2Tree().get(treeid) ;
	}
	
	public DTTree addTree(String title,String desc) throws IOException
	{
		DTTree dtt = new DTTree(title,desc) ;
		this.saveTree(dtt);
		getId2Tree().put(dtt.getTreeId(),dtt) ;
		return dtt ;
	}
	
	public DTTree delTreeById(String treeid)
	{
		DTTree dtt = getTreeById(treeid) ;
		if(dtt==null)
			return null ;
		File f = calTreeFile(treeid) ;
		f.delete() ;
		getId2Tree().remove(treeid) ;
		return dtt ;
	}
}
