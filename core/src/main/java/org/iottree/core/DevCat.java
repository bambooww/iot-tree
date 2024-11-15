package org.iottree.core;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.iottree.core.res.ResDir;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;

/**
 * DevCat in driver
 * 
 * @author jason.zhu
 */
@data_class
public class DevCat //implements IResNode
{
	protected static ILogger log = LoggerManager.getLogger(DevCat.class) ;
	
	@data_val
	String id = "" ;
	
	@data_val
	String name = null ;
	
	@data_val
	String title = null ;
	
	@data_val
	String desc = null ;
	
	//ArrayList<DevModel> devModels = new ArrayList<>() ;
	
	transient DevLib devLib = null ;
	
	transient List<DevDef> devDefs = null ;
	
	public DevCat(DevLib dd)
	{
		id = CompressUUID.createNewId();
		this.devLib = dd ;
	}
	
	public DevCat(DevLib dd,String name,String title)
	{
		id = CompressUUID.createNewId();
		this.devLib = dd ;
		this.name = name ;
		this.title = title ;
	}
	
	public DevLib getDevLib()
	{
		return devLib ;
	}
	
	public String getId()
	{
		if(Convert.isNullOrEmpty(this.id))
			return this.name ;
		return id ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	public List<DevDef> getDevDefs()
	{
		if(devDefs!=null)
			return devDefs;
		synchronized(this)
		{
			if(devDefs!=null)
				return devDefs;
			
			devDefs = loadDevDefs();
			
			for(DevDef dd:devDefs)
			{
				dd.RT_init(true, true);
				dd.constructNodeTree();
			}
			
			return devDefs ;
		}
	}
	
	

	public DevDef getDevDefByName(String name)
	{
		List<DevDef> ddfs = getDevDefs();
		for(DevDef dd:ddfs)
		{
			if(name.equals(dd.getName()))
				return dd ;
		}
		return null ;
	}
	
	public DevDef getDevDefById(String id)
	{
		List<DevDef> ddfs = getDevDefs();
		for(DevDef dd:ddfs)
		{
			if(id.equals(dd.getId()))
				return dd ;
		}
		return null ;
	}
	
	
	File getDevCatDir()
	{
		return devLib.getDevCatDir(this.getId()) ;
	}
	
	void saveDevDef(DevDef dd) throws Exception
	{
		File ff = dd.getDevDefFile();
		if(!ff.getParentFile().exists())
			ff.getParentFile().mkdirs() ;
		XmlData xd = DataTranserXml.extractXmlDataFromObj(dd) ;
		//XmlData xd = rep.toUAXmlData();
		XmlData.writeToFile(xd, ff);
	}
	
	public void delDevDef(DevDef dd)
	{
//		File f = dd.getDevDefFile();
//		if(f.exists())
//		{
//			f.delete();
//		}
		File df = dd.getDevDefDir() ;
		if(df.exists())
		{
			Convert.deleteDir(df) ;
		}
		this.getDevDefs().remove(dd);
	}
	
	public boolean delDevDef(String devid)
	{
		DevDef dd = this.getDevDefById(devid) ;
		if(dd==null)
			return false;
		delDevDef(dd);
		return true ;
	}
	
	private DevDef loadDevDef(String id) throws Exception
	{
		File catdir=  getDevCatDir();
		if(!catdir.exists())
			return null;
		File ddd =calDevDefDir(id);
		
		return DevDef.loadFromDir(ddd,this) ;
	}

	private List<DevDef> loadDevDefs()
	{
		ArrayList<DevDef> rets = new ArrayList<>() ;
		
		File catdir=  devLib.getDevCatDir(this.getId()) ;
		if(!catdir.exists())
			return rets;
		File[] fs = catdir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				return f.isDirectory() ;
			}
		});
		//ArrayList<DevCat> rets = new ArrayList<>() ;
		for(File tmpf:fs)
		{
			if(!new File(tmpf,"_dd.xml").exists())
				continue ;
			String id =tmpf.getName() ;;
			try
			{
				DevDef dd =  loadDevDef(id) ;
				if(dd==null)
				{
					log.warn("load DevDef failed ["+id+"]");
					continue ;
				}
				rets.add(dd);
			}
			catch(Exception e)
			{
				log.warn("load DevCat error ["+id+"]");
				e.printStackTrace();
			}
		}
		return rets;
	}
	
	File calDevDefDir(String id)
	{
		return new File(getDevCatDir(),id+"/") ;
	}
	
	public DevDef addDevDef(String name,String title,String desc,String drv) throws Exception
	{
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(name,true,sb))
			throw new IllegalArgumentException(sb.toString()) ;
		
		DevDef r = this.getDevDefByName(name) ;
		if(r!=null)
		{
			throw new Exception("name="+name+" is existed!") ;
		}
		
		r = new DevDef(this,name,title,desc) ;
		r.asDriver(drv);
		saveDevDef(r);
		this.getDevDefs().add(r) ;
		r.constructNodeTree();
		return r ;
	}
	
	
	public DevDef updateDevDef(String devid,String name,String title,String desc,String drv) throws Exception
	{
		DevDef dev = this.getDevDefById(devid) ;
		if(dev==null)
			throw new Exception("no devdef found") ;
		
		DevDef dev0 = this.getDevDefByName(name) ;
		if(dev0!=null&&dev0!=dev)
			throw new Exception("devdef with name="+name+" is already existed") ;
		
		dev.setDefNameTitle(name, title, desc,false);
		dev.asDriver(drv) ;
		saveDevDef(dev);
		return dev ;
	}
	
	public DevDef setDevDefFromPrj(UADev dev,String name,String title) throws Exception
	{
		DevDef dd = this.getDevDefByName(name) ;
		boolean bnew = false;
		if(dd==null)
		{
			dd = new DevDef(this,name,title,"") ;
			bnew=true;
		}
		
		//DevDef dd = new DevDef(this,name,title,"") ;
		//copy dev to def
		
		String id = dd.getId() ;
		HashMap<IRelatedFile,IRelatedFile> rf2new = new HashMap<>();
		dev.copyTreeWithNewSelf(dd,dd,"",false,true,rf2new); //recreate tree
		dd.id = id ;
		dd.setNameTitle(name, title, "");
		
		saveDevDef(dd);
		
		if(bnew)
			this.getDevDefs().add(dd) ;
		dd.constructNodeTree();
		
		Convert.copyRelatedFile(rf2new);
		// copy res files
		ResDir sor_resdir = dev.getResDir();
		
		if(sor_resdir!=null && sor_resdir.hasResItems())
		{
			File sorf = sor_resdir.getResDir() ;
			ResDir tar_resdir = dd.getResDir() ;
			File tarf = tar_resdir.getResDir();
			if(!tarf.exists())
				tarf.mkdirs() ;
			FileUtils.copyDirectory(sorf, tarf);
		}
		
		return dd ;
	}

//	ResDir resCxt = null ;
//	
//	@Override
//	public String getResNodeId()
//	{
//		return this.getId() ;
//	}
//	
//	@Override
//	public String getResNodeTitle()
//	{
//		return this.getTitle() ;
//	}
//	
//	@Override
//	public ResDir getResDir()
//	{
//		if(resCxt!=null)
//			return resCxt ;
//		File catdir = this.getDevCatDir();
//		File dir = new File(catdir,"_res/") ;
//		if(!dir.exists())
//			dir.mkdirs();
//		resCxt=new ResDir(this,this.getName(),this.getTitle(),dir);
//		return resCxt;
//	}
//
//	@Override
//	public IResNode getResNodeSub(String subid)
//	{
//		return this.getDevDefById(subid) ;
//	}
//
//	@Override
//	public IResNode getResNodeParent()
//	{
//		return this.devLib;//DevManager.getInstance();
//	}
}
