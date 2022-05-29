package org.iottree.core;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_val;

public class DevLib implements Comparable<DevLib>
{
	protected static ILogger log = LoggerManager.getLogger(DevLib.class) ;
	
	@data_val
	String id = null ;
	
//	String name = null ;
	@data_val
	String title = null ;
	
	@data_val(param_name = "create_dt")
	long createDT =System.currentTimeMillis() ;
	

	private transient ArrayList<DevCat> devCats = null;//new ArrayList<>();
	
	public DevLib()
	{
		this.id = CompressUUID.createNewId();
	}
	
	public DevLib(String id,String title)
	{
		this.id = id ;
		this.title = title ;
	}
	
	DevLib(String title)
	{
		this.id = CompressUUID.createNewId();
		this.title = title ;
		
	}
	
	public String getId()
	{
		return id ;
	}
	
//	public String getName()
//	{
//		return name ;
//	}
	
	public File getLibDir()
	{
		return new File(DevManager.getDevLibBase(),"lib_"+this.getId()+"/") ;
	}
	
	public String getTitle()
	{
		if(Convert.isNullOrEmpty(this.title))
			return this.id ;
		return title;
	}
	
	public DevLib asTitle(String t)
	{
		this.title = t ;
		return this ;
	}
	
	public long getCreateDT()
	{
		return this.createDT ;
	}
	
	public void save() throws Exception
	{
		DevManager.getInstance().saveLib(this);
	}

	File getDevCatDir(String catname)
	{
		return new File(getLibDir(), "cat_" + catname + "/");
	}

	

	public DevCat getDevCatByName(String n)
	{
		for (DevCat dc : this.getDevCats())
		{
			if (n.contentEquals(dc.getName()))
				return dc;
		}
		return null;
	}


	public DevDef getDevDefById(String id)
	{
		for (DevCat dc : getDevCats())
		{
			DevDef dd = dc.getDevDefById(id);
			if (dd != null)
				return dd;
		}
		return null;
	}

	public DevCat getDevCatById(String id)
	{
		for (DevCat dc : getDevCats())
		{
			if (id.equals(dc.getId()))
				return dc;
		}
		return null;
	}

//	File getDrvDir()
//	{
//		File fb = DevManager.getDevDrvBase();
//		return new File(fb, "devdef/drv_" + getName() + "/");
//	}

	
	private void saveCat(DevCat dc) throws Exception
	{
		File catdir = getDevCatDir(dc.getId());
		if (!catdir.exists())
			catdir.mkdirs();
		XmlData xd = DataTranserXml.extractXmlDataFromObj(dc);
		// XmlData xd = rep.toUAXmlData();
		XmlData.writeToFile(xd, new File(catdir, "cat.xml"));
	}

	private DevCat loadCat(String catid) throws Exception
	{
		File catdir = getDevCatDir(catid);
		if (!catdir.exists())
			return null;
		File catf = new File(catdir, "cat.xml");
		if (!catf.exists())
			return null;
		XmlData tmpxd = XmlData.readFromFile(catf);
		DevCat r = new DevCat(this);
		DataTranserXml.injectXmDataToObj(r, tmpxd);
		return r;
	}

	private ArrayList<DevCat> loadCats()
	{
		ArrayList<DevCat> rets = new ArrayList<>();

		File drvdir = getLibDir();
		if (!drvdir.exists())
			return rets;
		File[] fs = drvdir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (!f.isDirectory())
					return false;
				String n = f.getName();
				return n.startsWith("cat_");
			}
		});
		// ArrayList<DevCat> rets = new ArrayList<>() ;
		for (File tmpf : fs)
		{
			String catid = tmpf.getName().substring(4);
			try
			{
				DevCat dc = loadCat(catid);
				if (dc == null)
				{
					log.warn("load DevLib failed [" + catid + "]");
					continue;
				}
				rets.add(dc);
			}
			catch ( Exception e)
			{
				log.warn("load DevCat error [" + catid + "]");
				e.printStackTrace();
			}
		}
		return rets;
	}
	

	DevCat reloadCat(String catid) throws Exception
	{
		DevCat c = this.loadCat(catid);
		if (c == null)
			return null;

		List<DevCat> cats = getDevCats();

		for (int i = 0, n = cats.size(); i < n; i++)
		{
			DevCat cat = cats.get(i);
			if (cat.getId().equals(catid))
			{
				cats.set(i, c);
				return c;
			}
		}
		cats.add(c);
		return c;
	}

	/**
	 * get or load dev cats
	 * 
	 * @return
	 */
	public List<DevCat> getDevCats()
	{
		if(devCats!=null)
			return devCats ;
		
		synchronized (this)
		{
			if(devCats!=null)
				return devCats ;

			devCats = loadCats();
		}
		return devCats;
	}

	public DevCat addDevCat(String name, String title) throws Exception
	{
		StringBuilder failedr = new StringBuilder();
		if (!Convert.checkVarName(name, true, failedr))
			throw new Exception(failedr.toString());

		DevCat dc = this.getDevCatByName(name);
		if (dc != null)
			throw new Exception("name_existed");
		dc = new DevCat(this, name, title);
		this.saveCat(dc);
		this.getDevCats().add(dc);
		return dc;
	}
	
	public DevCat updateDevCat(String catid,String name,String title) throws Exception
	{
		StringBuilder failedr = new StringBuilder();
		if (!Convert.checkVarName(name, true, failedr))
			throw new Exception(failedr.toString());
		
		DevCat dc = this.getDevCatById(catid) ;
		if(dc==null)
			throw new Exception("no cat found") ;
		
		DevCat dc0 = this.getDevCatByName(name) ;
		if(dc0!=null&&dc0!=dc)
			throw new Exception("cat name="+name+" is existed!") ;
		
		dc.name = name ;
		dc.title = title ;
		this.saveCat(dc);
		return dc ;
	}

	public void delDevCat(String catid)
	{
		DevCat dc = this.getDevCatById(catid);
		if (dc == null)
			return;
		File catdir = dc.getDevCatDir();
		if (!catdir.exists())
			return;
		Convert.deleteDir(catdir);
		this.getDevCats().remove(dc);
	}

	@Override
	public int compareTo(DevLib o)
	{
		if(this.createDT>o.createDT)
			return 1 ;
		else if(this.createDT<o.createDT)
			return -1;
		else
			return 0 ;
	}
}
