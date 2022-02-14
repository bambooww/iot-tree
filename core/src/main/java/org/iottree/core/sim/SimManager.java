package org.iottree.core.sim;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.iottree.core.Config;
import org.iottree.core.DevDef;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.basic.IdName;
import org.iottree.core.task.Task;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ZipUtil;
import org.iottree.core.util.web.Mime;
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
	
	public static File getSimDir()
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
			try
			{
				SimInstance sc = loadInsByDir(insdir);
				ret.add(sc) ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	private SimInstance loadInsByDir(File insdir) throws Exception
	{
		File insf = new File(insdir,INS_FN);
		if(!insf.exists())
			return null ;
		
		String insid = getInsIdByDir(insdir) ;
			SimInstance sc = loadInstance(insf) ;
			if(sc==null)
				return null ; ;
			sc.withId(insid) ;
			return sc;

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
		
		int s = this.instances.size() ;
		for(int i = 0 ; i < s ; i ++)
		{
			if(this.instances.get(i).getId().equals(id))
			{
				this.instances.remove(i) ;
				return true ;
			}
		}
		return false;
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
	
	
	public boolean exportIns(HttpServletResponse resp, String insid) throws IOException
	{
		SimInstance ins = this.getInstance(insid) ;
		if(ins==null)
			return false;
		
		File insdir = this.getInsDir(insid) ;
		if(!insdir.exists())
			return false;
		
		String filename = "sim_"+ins.getName()+".zip" ;
		List<File> fs = Arrays.asList(insdir) ;
		HashMap<String,String> metam = new HashMap<>() ;
		metam.put("tp", "sim_ins") ;
		metam.put("insid", insid) ;
		String metatxt=  Convert.transMapToPropStr(metam) ;
		
		resp.addHeader("Content-Disposition", "attachment; filename="
				+ new String(filename.getBytes(), "iso8859-1"));

		resp.setContentType(Mime.getContentType(filename));
		
		ServletOutputStream os = resp.getOutputStream();
		ZipUtil.zipOut(metatxt,fs,os) ;
		os.flush();
	
		return true;
	}
	
	public SimInstance importIns(File zipf) throws Exception
	{
		if(!zipf.exists())
			return null;
		
		String zmeta = ZipUtil.readZipMeta(zipf) ;
		if(Convert.isNullOrEmpty(zmeta))
			return null;
		HashMap<String,String> metam = Convert.transPropStrToMap(zmeta) ;
		if(!"sim_ins".equals(metam.get("tp")))
			return null;
		String id = metam.get("insid") ;
		List<String> ens = ZipUtil.readZipEntrys(zipf) ;
		HashMap<String,String> outens = new HashMap<>() ;
		
		String prefix = "sim_"+id+"/" ;
		String newid = CompressUUID.createNewId();
		for(String en:ens)
		{
			String fit_en = en.replaceAll("\\\\", "/") ;
			if(fit_en.startsWith(prefix))
			{
				String taren = "sim_"+newid+"/"+en.substring(prefix.length()) ;
				outens.put(en, taren) ;
			}
			//find prj
		}
		
		ZipUtil.readZipOut(zipf, outens, getSimDir());
		
		SimInstance sc = loadInsByDir(new File(getSimDir(),"sim_"+newid+"/")) ;
		if(sc==null)
			return null ;
		sc.withId(newid) ;
		instances.add(0, sc);
		return sc ;
	}
}
