package org.iottree.core.comp;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlData;
import org.apache.commons.io.FileUtils;
import org.iottree.core.res.IResCxt;
import org.iottree.core.res.IResNode;
import org.iottree.core.res.ResDir;
import org.iottree.core.util.CompressUUID;
import org.w3c.dom.Element;


public class CompCat  //implements IResNode
{
	static ILogger log = LoggerManager.getLogger(CompCat.class) ;
	
	public static final String TAG = "cat" ;
	
	/**
	 * uuid
	 */
	String id = null ;
	
	String title = null ;
	
	transient boolean bReadonly = false;
	
	transient CompLib belongToLib = null ;
	
	private transient ArrayList<CompItem> items = null;//new ArrayList<>() ;
	
	public CompCat()
	{}
	
	/**
	 * add and create a new cat
	 * @param title
	 */
	public CompCat(String title)
	{
		this.id = CompressUUID.createNewId();//.randomUUID().toString() ;
		//this.dirName = this.id ;
		this.title = title ;
	}
	
	public CompLib getBelongTo()
	{
		return this.belongToLib ;
	}
	
	
	public void save() throws Exception
	{
		this.belongToLib.saveCatXd(this);
	}
//	public CompCat(Element gisele)
//	{
//		this.id = id ;
//		this.title = title ;
//		
//		
//	}
	
//	public static CompCat createByEle(Element catele)
//	{
//		if(catele==null)
//			return null ;
//		CompCat ret = new CompCat() ;
//		ret.id = catele.getAttribute("id") ;
//		if(Convert.isNullOrEmpty(ret.id))
//			return null ;
//		ret.title = catele.getAttribute("title") ;
//		if(Convert.isNullOrEmpty(ret.title))
//			ret.title = "noname" ;
//		for(Element ele:Convert.getSubChildElement(catele,CompItem.TAG))
//		{
//			CompItem ci = CompItem.createByEle(ele) ;
//			if(ci==null)
//				continue ;
//			ci.belongTo = ret ;
//			ret.items.add(ci) ;
//			
//		}
//		return ret ;
//	}
	
	public static CompCat createByXD(XmlData xd)
	{
		CompCat cc = new CompCat() ;
		cc.fromXmlData(xd);
		return cc ;
	}
	/**
	 * 
	 * @return
	 */
	public String getId()
	{
		return id ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	/**
	 * modify by outer in file system
	 * @return
	 */
	public boolean isReadOnly()
	{
		return bReadonly ;
	}
	
	/**
	 * get cat dir
	 * @return
	 */
	public File getCatDir()
	{
		File fb = belongToLib.getLibDir() ;
		return new File(fb,this.id+"/");
	}
	
	public List<CompItem> getItems()
	{
		if(items!=null)
			return items ;
		
		synchronized(this)
		{
			if(items!=null)
				return items ;
			
			items = loadItems() ;
			return items ;
		}
	}
	
	
	private ArrayList<CompItem> loadItems()
	{
		ArrayList<CompItem> rets =new ArrayList<>() ;
		File catdir = getCatDir() ;
		File[] itemdirs = catdir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				return f.isDirectory() ;
			}}) ;
		
		for(File itemdir:itemdirs)
		{
			try
			{
				String id = itemdir.getName() ;
				
				CompItem ci = CompItem.loadFromDir(belongToLib.getResLibId(),id,itemdir);
				if(ci==null)
					continue ;
				rets.add(ci) ;
			}
			catch(Exception e)
			{
				if(log.isWarnEnabled())
					log.warn("load CompItem failed:"+itemdir.getAbsolutePath());
				return null ;
			}
		}
		return rets ;
	}
	
//	private CompItem loadItem(File itemdir)
//	{
//		File cif = new File(itemdir,"ci.xml") ;
//		if(!cif.exists())
//			return null ;
//		String id = itemdir.getName() ;
//		try
//		{
//			XmlData xd = XmlData.readFromFile(cif) ;
//			CompItem ci = new CompItem() ;
//			ci.fromXmlData(xd);
//			ci.id = id ;
//			ci.belongTo = this ;
//			//rets.add(ci) ;
//			return ci ;
//		}
//		catch(Exception e)
//		{
//			if(log.isWarnEnabled())
//				log.warn("load CompCat failed:"+cif.getAbsolutePath());
//			return null ;
//		}
//	}
	
	File getCompItemDir(String id)
	{
		File catdir = getCatDir() ;
		return new File(catdir,id+"/");
	}
	
	
	private void saveItem(CompItem ci) throws Exception
	{
		File cidir = getCompItemDir(ci.getId()) ;
		if(!cidir.exists())
			cidir.mkdirs() ;
		XmlData xd= ci.toXmlData();
		XmlData.writeToFile(xd, new File(cidir,"ci.xml"));
	}
	
	public CompItem getItemById(String id)
	{
		for(CompItem ci:getItems())
		{
			if(id.contentEquals(ci.getId()))
				return ci ;
		}
		return null ;
	}
	

	public CompItem addComp(String title) throws Exception
	{
		if(isReadOnly())
			throw new Exception("cat is readonly") ;
		
		File catdir = this.getCatDir();
		CompItem ci = new CompItem(catdir,title) ;
		saveItem(ci);
		getItems().add(ci) ;
		return ci ;
	}
	
	CompItem pasteComp(CompItem ci) throws Exception
	{
		File sordir = ci.getResNodeDir() ;
		if(!sordir.exists())
			return null ;

		String sorid= ci.getId();
		CompItem oldci = this.getBelongTo().getItemById(sorid) ;
		
		String tarid = sorid ;
		if(oldci!=null)
		{//create new id
			tarid = CompressUUID.createNewId();
		}
		
		File tardir= new File(getCatDir(),tarid+"/") ;
		if(!tardir.exists())
			tardir.mkdirs() ;
		FileUtils.copyDirectory(sordir, tardir);
		
		CompItem newci = CompItem.loadFromDir(this.getBelongTo().getResLibId(),tarid,tardir) ;
		this.getItems().add(newci) ;
		return newci ;
	}
	
	public CompItem updateComp(String compid,String title) throws Exception
	{
		if(isReadOnly())
			throw new Exception("cat is readonly") ;
		
		CompItem ci = getItemById(compid) ;
		if(ci==null)
			return null ;
		if(ci.title.equals(title))
			return ci ;
		ci.title = title ;
		saveItem(ci);
		return ci ;
	}
	
	public CompItem delComp(String compid) throws Exception
	{
		if(isReadOnly())
			throw new Exception("cat is readonly") ;
		
		CompItem ci = getItemById(compid) ;
		if(ci==null)
			return null ;
		getItems().remove(ci) ;
		File cdir = getCompItemDir(compid);
		Convert.deleteDir(cdir) ;
		return ci ;
	}
	
	
	public XmlData toXmlData()
	{
		XmlData xd = new XmlData() ;
		xd.setParamValue("id", id);
		xd.setParamValue("title", title);
//		List<XmlData> xds = xd.getOrCreateSubDataArray("items") ;
//		if(items!=null)
//		{
//			for(CompItem ci:items)
//			{
//				xds.add(ci.toXmlData()) ;
//			}
//		}
		return xd ;
	}
	
	public void fromXmlData(XmlData xd)
	{
		this.id = xd.getParamValueStr("id") ;
		this.title = xd.getParamValueStr("title") ;
	}

//	@Override
//	public IResNode getResNodeSub(String subid)
//	{
//		return this.getItemById(subid) ;
//	}
//
//	public IResNode getResNodeParent()
//	{
//		return this.belongToLib;
//	}
//
//	@Override
//	public String getResNodeId()
//	{
//		return this.getId();
//	}
//	
//	@Override
//	public String getResNodeTitle()
//	{
//		return this.getTitle() ;
//	}
}
