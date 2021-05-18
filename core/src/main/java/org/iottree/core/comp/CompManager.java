package org.iottree.core.comp;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.iottree.core.Config;
import org.iottree.core.util.xmldata.XmlData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * a categary library
 * 1) tree org items
 * 2) item is componsed by basic drawItem
 * 3) it may has js controller
 * 4) can share and deploy
 * 
 * @author zzj
 */
public class CompManager
{
	static Object locker = new Object() ;
	static CompManager ins = null ;
	
	public static CompManager getInstance()
	{
		if(ins!=null)
			return ins ;
		
		synchronized(locker)
		{
			if(ins!=null)
				return ins ;
			
			ins = new CompManager() ;
			return ins ;
		}
	}
	
	transient File fileDirBase = null ; 
	
	ArrayList<CompCat> giCats = null;//new ArrayList<GICat>() ;
	HashMap<String,CompItem> id2item = null ;
	
	private CompManager()
	{
		fileDirBase = new File(Config.getDataDirBase()+"/ua/lib/") ;
		if(!fileDirBase.exists())
			fileDirBase.mkdirs() ;
	}
	
	synchronized public List<CompCat> getAllCats()
	{
		if(giCats!=null)
			return giCats ;
		synchronized(this)
		{
			if(giCats!=null)
				return giCats ;
			
			load();
			return giCats ;
		}
	}
	
	synchronized private HashMap<String,CompItem> getId2ItemMap()
	{
		if(id2item!=null)
			return id2item ;
		synchronized(this)
		{
			if(id2item!=null)
				return id2item ;
			
			load();
			return id2item ;
		}
	}

	private ArrayList<CompCat> load()
	{
		ArrayList<CompCat> gics = new ArrayList<CompCat>() ;
		HashMap<String,CompItem> id2i = new HashMap<>() ;
		
		File[] fs = fileDirBase.listFiles(new FileFilter(){

			public boolean accept(File f)
			{
				return f.isDirectory() ;
			}}) ;
		
		for(File df:fs)
		{
			boolean breadonly = false;
			
			File grf = new File(df,"cr.xd.xml") ;
			if(!grf.exists())
			{
				grf = new File(df,"cr.xml") ;
				breadonly = true ;
			}
			
			if(!grf.exists())
				continue ;
			
			try
			{
				CompCat gic = null;
				if(breadonly)
					gic = loadCatXml(grf) ;
				else
					gic = loadCatXd(grf) ;
				if(gic==null)
					continue ;
				gic.dirName = df.getName() ;
				gics.add(gic) ;
				for(CompItem ci:gic.getItems())
				{
					id2i.put(ci.getId(), ci) ;
					
					ci.onLoaded();
				}
				gic.bReadonly = breadonly;
			}
			catch(Exception ee)
			{
				ee.printStackTrace() ;
			}
		}
		giCats = gics ;
		id2item = id2i ;
		return gics;
	}
	
	
	
	private CompCat loadCatXml(File crf) throws Exception
	{
		Element rootele = null ;
		try(FileInputStream fis = new FileInputStream(crf);)
		{
			DocumentBuilderFactory docBuilderFactory= DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setValidating(false);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		
			InputSource is = new InputSource(fis);
			//is.setEncoding("gb2312");
			Document doc = docBuilder.parse(is);
			rootele = doc.getDocumentElement();
		}
		
		
		if(rootele==null)
			return null ;
		
		return CompCat.createByEle(rootele) ;
	}
	
	/**
	 * load from xmldata file
	 * @param cf
	 * @return
	 * @throws Exception
	 */
	private CompCat loadCatXd(File cf) throws Exception
	{
		XmlData xd = XmlData.readFromFile(cf) ;
		if(xd==null)
			return null ;
		CompCat cc = new CompCat() ;
		cc.fromXmlData(xd);
		return cc ;
	}
	

	public void saveCatXd(CompCat cc) throws Exception
	{
		File xdf = new File(this.fileDirBase,cc.dirName+"/cr.xd.xml") ;
		if(!xdf.getParentFile().exists())
			xdf.getParentFile().mkdirs() ;
		XmlData.writeToFile(cc.toXmlData(), xdf);
	}
	/**
	 * 
	 *
	 */
	public void clearCache()
	{
		id2item = null ;
		giCats = null ;
	}
	
	/**
	 * @param jscn
	 * @return
	 */
	public CompItem getItemById(String id)
	{
		HashMap<String,CompItem> n2g = getId2ItemMap();
		return n2g.get(id) ;
	}
	
	public CompCat getCatById(String id)
	{
		for(CompCat gic:getAllCats())
		{
			if(id.equals(gic.getId()))
				return gic ;
		}
		return null ;
	}
	
	public List<CompCat> getCatAll()
	{
		return getAllCats() ;
	}
	
	public CompCat addCat(String title) throws Exception
	{
		CompCat cc = new CompCat(title) ;
		cc.dirName = cc.id ;
		saveCatXd(cc);
		giCats.add(cc);
		return cc ;
	}
	
	
	public CompItem addComp(CompCat cc,String title) throws Exception
	{
		if(cc.isReadOnly())
			throw new Exception("cat is readonly") ;
		
		CompItem ci = new CompItem(cc,title) ;
		cc.items.add(ci) ;
		saveCatXd(cc) ;
		this.id2item.put(ci.getId(), ci) ;
		return ci ;
	}
}
