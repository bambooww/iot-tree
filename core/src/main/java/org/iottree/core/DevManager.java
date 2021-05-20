package org.iottree.core;

import java.io.*;
import java.util.*;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;

/**
 * manager Driver,Device Cat and DevDef
 * 
 * @author jason.zhu
 */
public class DevManager
{
	private static DevManager instance = null ;
	
	public static DevManager getInstance()
	{
		if(instance!=null)
			return instance ;
		synchronized(DevManager.class)
		{
			if(instance!=null)
				return instance ;
			instance = new DevManager() ;
			return instance ;
		}
	}
	
	private ArrayList<DevDriver> drivers = new ArrayList<>();
	
	
	private DevManager()
	{
		try
		{
			loadDrivers();
			
			//loadCats();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	

	static File getDevFileBase()
	{
		String fp = Config.getDataDirBase() + "/dev_drv/";
		return new File(fp) ;
	}
	
	private void loadDrivers() throws Exception
	{
		File f = new File(getDevFileBase(),"drivers.txt");
		if(!f.exists())
			return ;
		List<String> lns = Convert.readFileTxtLines(f,"utf-8");
		for(String ln:lns)
		{
			try
			{
				Class<?> c = Class.forName(ln);
				DevDriver dd = (DevDriver)c.getConstructor().newInstance() ;
				if(dd==null)
					continue ;
				drivers.add(dd) ;
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				System.out.println("warn: load driver ["+ln+"] failed!") ;
			}
		}
	}
	
	public UANode findNodeByPath(String path)
	{
		if(Convert.isNullOrTrimEmpty(path))
			return null ;
		LinkedList<String> ss = Convert.splitStrWithLinkedList(path, "/\\.") ;
		String n = ss.removeFirst() ;
		List<String> devps = Convert.splitStrWith(n, "-") ;
		if(devps.size()!=3)
			return null ;
		DevDriver drv = this.getDriver(devps.get(0)) ;
		if(drv==null)
			return null ;
		DevCat cat = drv.getDevCatByName(devps.get(1)) ;
		if(cat==null)
			return null ;
		DevDef dd = cat.getDevDefByName(devps.get(2)) ;
		if(dd==null)
			return null ;
		
		return dd.getDescendantNodeByPath(ss) ;
	}
	
	public UANode findNodeById(String id)
	{
		for(DevDriver drv:this.getDrivers())
		{
			DevDef dd = drv.getDevDefById(id) ;
			if(dd==null)
				continue ;
			UANode n = dd.findNodeById(id) ;
			if(n!=null)
				return n ;
		}
		return null;
	}
	
	public DevDef getDevDefById(String id)
	{
		for(DevDriver drv:this.getDrivers())
		{
			DevDef dd = drv.getDevDefById(id) ;
			if(dd!=null)
				return dd ;
		}
		return null;
	}
//	private DevCat loadCatModels(File catdir) throws Exception
//	{
//		File catf = new File(catdir,"cat.json") ;
//		if(!catf.exists())
//			return null ;
//		String cattxt = Convert.readFileTxt(catf, "utf-8") ;
//		JSONObject catjo = new JSONObject(cattxt) ;
//		String n = catjo.optString("name") ;
//		if(Convert.isNullOrEmpty(n))
//			return null ;
//		String t = catjo.optString("title") ;
//		if(Convert.isNullOrEmpty(t))
//			t = n ;
//		DevCat dc = new DevCat(n,t) ;
//		File[] fs = catdir.listFiles(new FileFilter() {
//
//			@Override
//			public boolean accept(File f)
//			{
//				if(!f.isFile())
//					return false;
//				String n = f.getName() ;
//				return n.startsWith("m_")&&n.endsWith(".json");
//			}
//		});
//		for(File mf:fs)
//		{
//			try
//			{
//				DevModel dm = loadModel(mf) ;
//				if(dm==null)
//				{
//					System.out.println("Warning,load DevModel failed ["+dc.getName()+"]"+mf.getName()) ;
//					continue ;
//				}
//				dc.devModels.add(dm) ;
//			}
//			catch(Exception e)
//			{
//				System.out.println("Warning,load DevModel error ["+dc.getName()+"]"+mf.getName()) ;
//				e.printStackTrace();
//			}
//		}
//		return dc ;
//	}
	
//	private DevModel loadModel(File mf) throws Exception
//	{
//		String txt = Convert.readFileTxt(mf, "utf-8") ;
//		JSONObject mjo = new JSONObject(txt) ;
//		String cn = mjo.optString("cn");
//		if(Convert.isNullOrEmpty(cn))
//			return null ;
//		
//		JSONArray depends = mjo.optJSONArray("depend_drvs") ;
//		if(depends==null||depends.length()<=0)//Convert.isNullOrEmpty(dependdrvn))
//			return null ;
//		Class c = Class.forName(cn) ;
//		DevModel dm = (DevModel)c.getConstructor(new Class[] {}).newInstance(new Object[] {});
//		int dlen = depends.length() ;
//		for(int i = 0 ; i < dlen ; i ++)
//		{
//			String str = depends.getString(i);
//			if(Convert.isNullOrEmpty(str))
//				continue ;
//			dm.dependDrvNames.add(str);
//		}
//		return dm ;
//	}
	
	public List<DevDriver> getDrivers()
	{
		return drivers;
	}
	
	public DevDriver getDriver(String name)
	{
		for(DevDriver dd:drivers)
		{
			if(name.contentEquals(dd.getName()))
				return dd ;
		}
		return null ;
	}
	
	DevDriver createDriverIns(String name)
	{
		DevDriver dd = getDriver(name);
		if(dd==null)
			return null ;
		dd = dd.copyMe() ;
		return dd ;
	}
	
	
	public List<DevDriver> listDriversNotNeedConn()
	{
		List<DevDriver> drvs = getDrivers() ;
		ArrayList<DevDriver> rets = new ArrayList<>() ;
		for(DevDriver drv:drvs)
			rets.add(drv) ;
		return rets;
	}
}
