package org.iottree.core;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import org.iottree.core.res.IResCxt;
import org.iottree.core.res.IResNode;
import org.iottree.core.res.ResDir;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
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
public class DevCat implements IResNode
{
	@data_val
	String id = "" ;
	
	@data_val
	String name = null ;
	
	@data_val
	String title = null ;
	
	@data_val
	String desc = null ;
	
	//ArrayList<DevModel> devModels = new ArrayList<>() ;
	
	transient DevDriver driver = null ;
	
	transient List<DevDef> devDefs = null ;
	
	public DevCat(DevDriver dd)
	{
		id = CompressUUID.createNewId();
		this.driver = dd ;
	}
	
	public DevCat(DevDriver dd,String name,String title)
	{
		id = CompressUUID.createNewId();
		this.driver = dd ;
		this.name = name ;
		this.title = title ;
	}
	
	public DevDriver getDriver()
	{
		return driver ;
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
		return driver.getDevCatDir(this.getId()) ;
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
		File f = dd.getDevDefFile();
		if(f.exists())
		{
			f.delete();
		}
		File df = dd.getDevDefDir() ;
		if(df.exists())
		{
			Convert.deleteDir(df) ;
		}
		this.getDevDefs().remove(dd);
	}
	
	private DevDef loadDevDef(String id) throws Exception
	{
		File catdir=  getDevCatDir();
		if(!catdir.exists())
			return null;
		File ddf = new File(catdir,"dd_"+id+".xml");
		if(!ddf.exists())
			return null ;
		XmlData tmpxd = XmlData.readFromFile(ddf);
		DevDef r = new DevDef(this) ;
		DataTranserXml.injectXmDataToObj(r, tmpxd);
		return r ;
	}

	private List<DevDef> loadDevDefs()
	{
		ArrayList<DevDef> rets = new ArrayList<>() ;
		
		File catdir=  driver.getDevCatDir(this.getId()) ;
		if(!catdir.exists())
			return rets;
		File[] fs = catdir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(f.isDirectory())
					return false;
				String n = f.getName() ;
				return n.startsWith("dd_")&&n.endsWith(".xml");
			}
		});
		//ArrayList<DevCat> rets = new ArrayList<>() ;
		for(File tmpf:fs)
		{
			String n = tmpf.getName() ;
			String id = n.substring(3,n.length()-4) ;
			try
			{
				DevDef dd =  loadDevDef(id) ;
				if(dd==null)
				{
					System.out.println("Warning,load DevDef failed ["+id+"]");
					continue ;
				}
				rets.add(dd);
			}
			catch(Exception e)
			{
				System.out.println("Warning,load DevCat error ["+id+"]");
				e.printStackTrace();
			}
		}
		return rets;
	}
	
	public DevDef addDevDef(String name,String title,String desc) throws Exception
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
		saveDevDef(r);
		this.getDevDefs().add(r) ;
		r.constructNodeTree();
		return r ;
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
		return dd ;
	}

	ResDir resCxt = null ;
	
	@Override
	public String getResNodeId()
	{
		return this.getId() ;
	}
	
	@Override
	public String getResNodeTitle()
	{
		return this.getTitle() ;
	}
	
	@Override
	public ResDir getResDir()
	{
		if(resCxt!=null)
			return resCxt ;
		File catdir = this.getDevCatDir();
		File dir = new File(catdir,"_res/") ;
		if(!dir.exists())
			dir.mkdirs();
		resCxt=new ResDir(this,this.getName(),this.getTitle(),dir);
		return resCxt;
	}

	@Override
	public IResNode getResNodeSub(String subid)
	{
		return this.getDevDefById(subid) ;
	}

	@Override
	public IResNode getResNodeParent()
	{
		
		return DevManager.getInstance();
	}

	
//	public List<DevModel> getDevModels()
//	{
//		return devModels ;
//	}
//	
//	public DevModel getDevModelByName(String n)
//	{
//		for(DevModel m:devModels)
//		{
//			if(n.contentEquals(m.getName()))
//				return m ;
//		}
//		return null ;
//	}
//	
//	public DevModel createDevModelByName(String n)
//	{
//		DevModel m = getDevModelByName(n);
//		if(m==null)
//			return null ;
//		return m.copyMe() ;
//	}
}
