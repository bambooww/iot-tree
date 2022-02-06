package org.iottree.core.sim;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.DevDef;
import org.iottree.core.UAManager;
import org.iottree.core.task.Task;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;

/**
 * 
 * @author jason.zhu
 *
 */
public class SimManager
{
	static SimManager simMgr = null ;
	
	public static SimManager getInstance()
	{
		if(simMgr!=null)
			return simMgr ;
		
		synchronized(SimManager.class)
		{
			if(simMgr!=null)
				return simMgr ;
			
			simMgr = new SimManager() ;
			return simMgr ;
		}
	}
	
	ArrayList<SimInstance> instances = null;//new ArrayList<>() ;
	
	private SimManager()
	{
		//slaveChs = load() ;
	}
	
	
	public List<SimInstance> getInstances()
	{
		if(instances!=null)
			return instances ;
		
		synchronized(this)
		{
			if(instances!=null)
				return instances ;
			
			instances = load() ;
			return instances ;
		}
	}
	
	public SimInstance getInstance(String id)
	{
		for(SimInstance sc:getInstances())
		{
			if(sc.getId().equals(id))
				return sc ;
		}
		return null ;
	}
	
	public SimInstance getInstanceByName(String name)
	{
		for(SimInstance sc:getInstances())
		{
			if(sc.getName().equals(name))
				return sc ;
		}
		return null ;
	}
	
//	public SlaveChannel setSlaveChannelBasic(SlaveInstance sch) throws Exception
//	{
//		SlaveChannel oldsch = getSlaveChannel(sch.getId()) ;
//		if(oldsch!=null)
//		{
//			oldsch.withBasic(sch) ;
//			sch = oldsch ;
//		}
//		else
//		{
//			List<SlaveChannel> schs = getSlaveChannels() ;
//			schs.add(sch) ;
//		}
//		saveInstance(sch);
//		return sch ;
//	}
	
	private File getSimDir()
	{
		return new File(Config.getDataDirBase()+"simulator/") ;
	}
	
	private File[] getInsDirs()
	{
		File  mdir = getSimDir() ;
		if(!mdir.exists())
			return null ;
		
		final FileFilter ff = new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(!f.isDirectory())
					return false;
				String name = f.getName() ;
				
				return name.startsWith("sim_");
			}} ;
		
		return mdir.listFiles(ff) ;	
	}
	
	File getInsDir(String id)
	{
		File  mdir = getSimDir() ;
		//if(!mdir.exists())
		//	return null ;
		return new File(mdir,"sim_"+id+"/") ;
	}
	
	private String getInsIdByDir(File f)
	{
		if(!f.isDirectory())
			return null ;
		
		String fn = f.getName() ;
		//int len = fn.length() ;
		return fn.substring(4) ;
	}
	
	private static final String INS_FN = "_ins.xml";

	private ArrayList<SimInstance> load()
	{
		ArrayList<SimInstance> ret = new ArrayList<>() ;
		File[] insdirs = getInsDirs() ;
		if(insdirs==null)
			return ret;
		
		for(File insdir:insdirs)
		{
			File insf = new File(insdir,INS_FN);
			if(!insf.exists())
				continue ;
			
			String insid = getInsIdByDir(insdir) ;
			try
			{
				SimInstance sc = loadInstance(insf) ;
				if(sc==null)
					continue ;
				sc.withId(insid) ;
				ret.add(sc) ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	void saveInstance(SimInstance ins) throws Exception
	{
		File insdir = getInsDir(ins.getId()) ;
		if(!insdir.exists())
			insdir.mkdirs() ;
		XmlData xd = DataTranserXml.extractXmlDataFromObj(ins) ;
		//XmlData xd = rep.toUAXmlData();
		XmlData.writeToFile(xd, new File(insdir,INS_FN));
	}
	
	public boolean delInstance(String id)
	{
		SimInstance sc = this.getInstance(id);
		if(sc==null)
			return false;
		
		File f = getInsDir(id);
		if(f.exists())
		{
			Convert.deleteDir(f) ;
		}
		
		this.instances.remove(sc);
		return true;
	}
	
	private SimInstance loadInstance(File f) throws Exception
	{
		if(!f.exists())
			return null ;
		XmlData tmpxd = XmlData.readFromFile(f);
		SimInstance sc = new SimInstance() ;
		DataTranserXml.injectXmDataToObj(sc, tmpxd);
		return sc ;
	}
	
	public SimInstance setInstanceBasic(SimInstance ins) throws Exception
	{
		String n = ins.getName() ;
		if(Convert.isNullOrEmpty(n))
			throw new Exception("name cannot be null or empty") ;
		StringBuilder sb=  new StringBuilder() ;
		if(!Convert.checkVarName(n, true, sb))
			throw new Exception(sb.toString()) ;
		
		String insid = ins.getId() ;
		SimInstance oldins = this.getInstance(insid) ;
		if(oldins==null)
		{
			SimInstance tmpsi = this.getInstanceByName(n) ;
			if(tmpsi!=null)
				throw new Exception("instance name ["+n+"] is already existed!") ;
			
			this.instances.add(ins) ;
			saveInstance(ins) ;
			return ins ;
		}
		else
		{
			SimInstance tmpsi = this.getInstanceByName(n) ;
			if(tmpsi!=null && tmpsi!=oldins)
				throw new Exception("instance name ["+n+"] is already existed!") ;
			oldins.name = ins.name ;
			oldins.title = ins.title ;
			oldins.bEnable = ins.bEnable ;
			saveInstance(oldins) ;
			return oldins ;
		}
	}
}
