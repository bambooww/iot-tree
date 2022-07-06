package org.iottree.core.comp;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.iottree.core.DevLib;
import org.iottree.core.res.IResCxt;
import org.iottree.core.res.IResNode;
import org.iottree.core.res.ResDir;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ZipUtil;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_val;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class CompLib implements Comparable<CompLib> ,IResCxt
{
	protected static ILogger log = LoggerManager.getLogger(CompLib.class) ;
	
	@data_val
	String id = null ;
	
//	String name = null ;
	@data_val
	String title = null ;
	
	@data_val(param_name = "create_dt")
	long createDT =System.currentTimeMillis() ;
	

	
	ArrayList<CompCat> giCats = null;//new ArrayList<GICat>() ;
	
	HashMap<String,CompItem> id2item = null ;
	
//	private transient ArrayList<CompCat> devCats = null;//new ArrayList<>();
	
	public CompLib()
	{
		this.id = CompressUUID.createNewId();
	}
	
	public CompLib(String id,String title)
	{
		this.id = id ;
		this.title = title ;
	}
	
	CompLib(String title)
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
		return new File(CompManager.getInstance().getCompLibBase(),"lib_"+this.getId()+"/") ;
	}
	
	public String getTitle()
	{
		if(Convert.isNullOrEmpty(this.title))
			return this.id ;
		return title;
	}
	
	public CompLib asTitle(String t)
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
		CompManager.getInstance().saveLib(this);
	}

//	File getDevCatDir(String cat)
//	{
//		return new File(getLibDir(), "cat_" + catname + "/");
//	}

	

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
		
		File[] fs = getLibDir().listFiles(new FileFilter(){

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
//				if(breadonly)
//					gic = loadCatXml(grf) ;
//				else
					gic = loadCatXd(grf) ;
				if(gic==null)
					continue ;
				gic.belongToLib = this;// df.getName() ;
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
	
	
	
//	private CompCat loadCatXml(File crf) throws Exception
//	{
//		Element rootele = null ;
//		try(FileInputStream fis = new FileInputStream(crf);)
//		{
//			DocumentBuilderFactory docBuilderFactory= DocumentBuilderFactory.newInstance();
//			docBuilderFactory.setNamespaceAware(false);
//			docBuilderFactory.setValidating(false);
//			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
//		
//			InputSource is = new InputSource(fis);
//			//is.setEncoding("gb2312");
//			Document doc = docBuilder.parse(is);
//			rootele = doc.getDocumentElement();
//		}
//		
//		
//		if(rootele==null)
//			return null ;
//		
//		return CompCat.createByEle(rootele) ;
//	}
	
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
		cc.belongToLib = this ;
		cc.fromXmlData(xd);
		return cc ;
	}
	

	public void saveCatXd(CompCat cc) throws Exception
	{
		File xdf = new File(getLibDir(),cc.id+"/cr.xd.xml") ;
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
	
//	/**
//	 * @param jscn
//	 * @return
//	 */
//	public CompItem getItemById(String id)
//	{
//		HashMap<String,CompItem> n2g = getId2ItemMap();
//		return n2g.get(id) ;
//	}
	
	public CompItem getItemById(String id)
	{
		for(CompCat cc:this.getAllCats())
		{
			CompItem ci = cc.getItemById(id);
			if(ci!=null)
				return ci ;
		}
		return null ;
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
	
//	public List<CompCat> getCatAll()
//	{
//		return getAllCats() ;
//	}
	
	public CompCat addCat(String title) throws Exception
	{
		CompCat cc = new CompCat(title) ;
		//cc.id = cc.id ;
		saveCatXd(cc);
		cc.belongToLib = this ;
		giCats.add(cc);
		return cc ;
	}
	
	public CompCat updateCat(String catid,String title) throws Exception
	{
		CompCat cc = getCatById(catid);
		if(cc==null)
			throw new Exception("no comp cat found") ;
		cc.title = title ;
		saveCatXd(cc) ;
		return cc;
	}
	
	public boolean delCat(String catid) throws Exception
	{
		CompCat cc = getCatById(catid) ;
		if(cc==null)
			return false;
		this.giCats.remove(cc) ;
		File xdf = new File(getLibDir(),cc.id+"/") ;
		if(xdf.exists())
		{
			Convert.deleteDir(xdf) ;
		}
		return true;
	}
	
	
	public boolean exportCompCat(String catid,File fout) throws IOException
	{
		CompCat cc = this.getCatById(catid) ;
		if(cc==null)
			return false;
		File catdir = cc.getCatDir() ;
		if(!catdir.exists())
			return false;
		HashMap<String,String> metam = new HashMap<>() ;
		metam.put("tp", "comp") ;
		metam.put("cat", catid) ;
		String metatxt=  Convert.transMapToPropStr(metam) ;
		ZipUtil.zipFileOut(metatxt,Arrays.asList(catdir), fout);
		return true;
	}
	
	public boolean importCompCat(File zipf) throws IOException
	{
		String txt = ZipUtil.readZipMeta(zipf) ;
		if(txt==null)
			return false;//
		HashMap<String,String> mmap = Convert.transPropStrToMap(txt) ;
		if(!"comp".contentEquals(mmap.get("tp")))
			return false;
		
		return true;
	}

//	@Override
//	public ResDir getResDir()
//	{
//		return null;
//	}
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
//	public IResNode getResNodeSub(String subid)
//	{
//		return this.getCatById(subid) ;
//	}
//	
//	public IResNode getResNodeParent()
//	{
//		return CompManager.getInstance() ;
//	}


	@Override
	public int compareTo(CompLib o)
	{
		if(this.createDT>o.createDT)
			return 1 ;
		else if(this.createDT<o.createDT)
			return -1;
		else
			return 0 ;
	}
	
	@Override
	public String getResCxtId()
	{
		return this.getId() ;
	}
	
//	@Override
//	public String getResCxtName()
//	{
//		return "hmi_comp";
//	}

	@Override
	public File getResRootDir()
	{
		return this.getLibDir();
	}
	
	public File getRefRootDir()
	{
		return null ;
	}

	@Override
	public List<String> getResRefferNames()
	{
		return null;
	}

	@Override
	public String getResPrefix()
	{
		return IResCxt.PRE_COMP;
	}

	@Override
	public IResNode getResNodeById(String res_id)
	{
		return getItemById(res_id);
	}

	@Override
	public String getResNodeId()
	{
		return this.getId();
	}

	@Override
	public String getResNodeTitle()
	{
		return this.getTitle();
	}

	@Override
	public File getResNodeDir()
	{
		return null;
	}
}
