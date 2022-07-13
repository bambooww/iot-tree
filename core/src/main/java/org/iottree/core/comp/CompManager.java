package org.iottree.core.comp;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.iottree.core.Config;
import org.iottree.core.UANode;
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
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
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
public class CompManager //implements IResCxt //
{
	protected static ILogger log = LoggerManager.getLogger(CompManager.class) ;
	
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
	

	private ArrayList<CompLib> libs = null ;
	
	private CompManager()
	{
		fileDirBase = new File(Config.getDataDirBase()+"/comp_lib/") ;
		if(!fileDirBase.exists())
			fileDirBase.mkdirs() ;
	}
	
	public File getCompLibBase()
	{
		return fileDirBase;//new File(Config.getDataDirBase()+"/comp_lib/") ;
	}
	
	
	private File getCompLibDir(String libid)
	{
		return new File(getCompLibBase(),"lib_"+libid+"/") ;
	}
	
	private CompLib loadLib(String libid) throws Exception
	{
		File libdir = getCompLibDir(libid);
		if (!libdir.exists())
			return null;
		File catf = new File(libdir, "lib.xml");
		if (!catf.exists())
			return null;
		CompLib r = new CompLib();
		if(catf.length()>0)
		{
			XmlData tmpxd = XmlData.readFromFile(catf);
			DataTranserXml.injectXmDataToObj(r, tmpxd);
		}
		r.id = libid ;
		return r;
	}
	
	void saveLib(CompLib dc) throws Exception
	{
		File libdir = getCompLibDir(dc.getId());
		if (!libdir.exists())
			libdir.mkdirs();
		XmlData xd = DataTranserXml.extractXmlDataFromObj(dc);
		// XmlData xd = rep.toUAXmlData();
		XmlData.writeToFile(xd, new File(libdir, "lib.xml"));
	}
	
	CompLib reloadLib(String libid) throws Exception
	{
		CompLib c = this.loadLib(libid);
		if (c == null)
			return null;

		List<CompLib> libs = getCompLibs();

		for (int i = 0, n = libs.size(); i < n; i++)
		{
			CompLib cat = libs.get(i);
			if (cat.getId().equals(libid))
			{
				libs.set(i, c);
				return c;
			}
		}
		libs.add(c);
		return c;
	}
	
	private ArrayList<CompLib> loadLibs()
	{
		ArrayList<CompLib> rets = new ArrayList<>() ;
		File lbf = getCompLibBase() ;
		if(!lbf.exists())
			return rets ;
		
		File[] fs = lbf.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (!f.isDirectory())
					return false;
				String n = f.getName();
				return n.startsWith("lib_");
			}
		});
		
		for (File tmpf : fs)
		{
			String libid = tmpf.getName().substring(4);
			try
			{
				CompLib dc = loadLib(libid);
				if (dc == null)
				{
					log.warn("load CompLib failed [" + libid + "]");
					continue;
				}
				rets.add(dc);
			}
			catch ( Exception e)
			{
				log.warn("load CompLib error [" + libid + "]");
				e.printStackTrace();
			}
		}
		return rets;
	}
	
	public List<CompLib> getCompLibs()
	{
		if(libs!=null)
			return libs ;
		
		synchronized(this)
		{
			if(libs!=null)
				return libs ;
			
			libs = loadLibs() ;
			Collections.sort(libs);
		}
		return libs ;
	}
	
	public CompLib getCompLibById(String libid)
	{
		for(CompLib lib: getCompLibs())
		{
			if(lib.getId().equals(libid))
				return lib ;
		}
		return null ;
	}
//	
//	public DevDef getDevDefById(String libid,String defid)
//	{
//		CompLib lib = getCompLibById(libid);
//		if(lib==null)
//			return null ;
//		return lib.getDevDefById(defid);
//	}
//	
	
	public CompLib addCompLib(String title) throws Exception
	{
		StringBuilder failedr = new StringBuilder();
		CompLib lib = new CompLib(title);
		this.saveLib(lib);
		this.getCompLibs().add(lib);
		return lib;
	}

	public void delCompLib(String libid)
	{
		CompLib dc = this.getCompLibById(libid);
		if (dc == null)
			return;
		File dir = getCompLibDir(libid);
		if (!dir.exists())
			return;
		Convert.deleteDir(dir);
		this.getCompLibs().remove(dc);
	}
	
	public CompCat getCompCatById(String libid,String catid)
	{
		CompLib lib = this.getCompLibById(libid) ;
		if(lib==null)
			return null ;
		return lib.getCatById(catid);
	}
	
	public CompItem getCompItemById(String libid,String catid,String compid)
	{
		CompCat cc = getCompCatById(libid,catid);
		if(cc==null)
			return null ;
		return cc.getItemById(compid) ;
	}
	
	public CompItem getCompItemById(String libid,String compid)
	{
		int k = libid.indexOf('_') ;
		if(k>0)
			libid = libid.substring(k+1);
		CompLib lib = this.getCompLibById(libid) ;
		if(lib==null)
			return null ;
		
		return lib.getItemById(compid) ;
	}
	
//	public DevDef getDevDefById(String libid,String catid,String devid)
//	{
//		DevCat dc = getDevCatById(libid,catid);
//		if(dc==null)
//			return null;
//		return dc.getDevDefById(devid) ;
//	}
	
//	public UANode findNodeByPath(String path)
//	{
//		if(Convert.isNullOrTrimEmpty(path))
//			return null ;
//		LinkedList<String> ss = Convert.splitStrWithLinkedList(path, "/\\.") ;
//		String n = ss.removeFirst() ;
//		List<String> devps = Convert.splitStrWith(n, "-") ;
//		if(devps.size()!=3)
//			return null ;
//		CompLib dl = this.getCompLibById(devps.get(0));
//		// DevDriver drv = this.getDriver() ;
//		if(dl==null)
//			return null ;
//		CompCat cat = dl.getCatByName(devps.get(1)) ;
//		if(cat==null)
//			return null ;
//		CompItem dd = cat.getItemById(devps.get(2)) ;
//		if(dd==null)
//			return null ;
//		
//		return dd ;
//		//return dd.getDescendantNodeByPath(ss) ;
//	}
	
//	public UANode findNodeById(String id)
//	{
//		for(DevDriver drv:this.getDrivers())
//		{
//			DevDef dd = drv.getDevDefById(id) ;
//			if(dd==null)
//				continue ;
//			UANode n = dd.findNodeById(id) ;
//			if(n!=null)
//				return n ;
//		}
//		return null;
//	}
//	
//	public DevDef getDevDefById(String id)
//	{
//		for(DevDriver drv:this.getDrivers())
//		{
//			DevDef dd = drv.getDevDefById(id) ;
//			if(dd!=null)
//				return dd ;
//		}
//		return null;
//	}
//	
//	public DevCat getDevCatById(String catid)
//	{
//		for(DevDriver drv:this.getDrivers())
//		{
//			DevCat dc = drv.getDevCatById(catid) ;
//			if(dc!=null)
//				return dc ;
//		}
//		return null ;
//	}
	
	
	public File exportCompLibTo(String libid,File fout) throws IOException
	{
		CompLib lib = this.getCompLibById(libid) ;
		if(lib==null)
			return null ;
		
		
		File dir = lib.getLibDir();
		List<File> fs = Arrays.asList(dir) ;
		HashMap<String,String> metam = new HashMap<>() ;
		metam.put("tp", "complib") ;
		metam.put("libid", libid) ;
		metam.put("libtitle", lib.getTitle()) ;
		
		String metatxt=  Convert.transMapToPropStr(metam) ;
		
		ZipUtil.zipFileOut(metatxt,fs,fout) ;
		return fout;
	}
	
	public File exportCompLibToTmp(String libid) throws IOException
	{
		String fn ="lib_"+ libid+".zip";
		File fout = new File(Config.getDataDirBase()+"/tmp/"+fn) ;
		exportCompLibTo(libid,fout);
		return fout;
	}
	
	
	public File backupDevCatToZip(String libid) throws IOException
	{
		String fn = "lib_"+ libid+"_"+System.currentTimeMillis()+".zip";
		File fout = new File(Config.getDataBackupDir(),"comp_lib/"+fn) ;
		exportCompLibTo(libid,fout);
		return fout;
	}
	
	public HashMap<String,String> parseCompLibZipFileMeta(File zipf) throws Exception
	{
		//ArrayList<IdName> rets =new ArrayList<>() ;
		
		String metatxt = ZipUtil.readZipMeta(zipf);
		if(metatxt==null||metatxt.equals(""))
			return null ;
		HashMap<String,String> pms = Convert.transPropStrToMap(metatxt) ;
		if(!"complib".equals(pms.get("tp")))
			return null ;
		String libid = pms.get("libid") ;
		if(Convert.isNullOrEmpty(libid))
			return null ;
		return pms ;
	}
	

	public boolean importCompLibZipFile(File zipf,String libid,String title) throws Exception
	{
		HashMap<String,String> pms = parseCompLibZipFileMeta(zipf) ;
		if(pms==null)
			return false;
		
		String oldlibid = pms.get("libid") ;
		if(Convert.isNullOrEmpty(oldlibid))
			return false;
		
		if(Convert.isNullOrEmpty(libid))
			libid = CompressUUID.createNewId();

//		if( !catid.contentEquals(pms.get("catid")) 
//			||  !catname.contentEquals(pms.get("catname"))
//			||  !drvname.contentEquals(pms.get("drvname")))
//			return false;

		
		
		String libdirname = "lib_"+oldlibid+"/" ;
		String libdirname1 = "lib_"+oldlibid+"\\" ;
		int prefixlen = libdirname.length() ;
		List<String> ens = ZipUtil.readZipEntrys(zipf) ;
		HashMap<String,String> outens = new HashMap<>() ;
		for(String en:ens)
		{
			if(en.startsWith(libdirname)||en.startsWith(libdirname1))
			{
				String taren = "lib_"+libid+"/" + en.substring(prefixlen) ;
				outens.put(en,taren) ;
			}
		}
		
		File libbf = getCompLibBase();
		File oldcatdir = new File(libbf,libdirname) ;
		if(oldlibid.equals(libid) && oldcatdir.exists())
		{//back up
			backupDevCatToZip(libid) ;
			Convert.deleteDir(oldcatdir) ;
		}
		
		ZipUtil.readZipOut(zipf, outens, libbf);
		
		//
		CompLib lib = reloadLib(libid);
		if(lib==null)
			return false;
		
		if(Convert.isNotNullEmpty(title) && !title.equals(lib.getTitle()))
		{
			lib.asTitle(title);
			lib.save();
		}
		return true;
	}

	
	
	
	// runtime support
	
	public CompItem findCompItemById(String libid,String id)
	{
		int k = libid.indexOf('_') ;
		if(k>0)
			libid = libid.substring(k+1) ;
		CompLib lib = this.getCompLibById(libid) ;
		if(lib==null)
			return null ;
		
//		return lib.getItemById(id) ;
		for(CompCat cat:lib.getAllCats())
		{
			CompItem r = cat.getItemById(id) ;
			if(r!=null)
				return r ;
		}
		
		return null ;
	}
	
	public void renderLibAndCatsTree(Writer w) throws Exception
	{
		
		w.write("{\"id\":\"lib_and_cats\"");
		w.write(",\"nc\":0");
		w.write(",\"icon\": \"fa-solid fa-puzzle-piece fa-lg\"");
		w.write(",\"text\":\"HMI Lib and Category\"");
		w.write(",\"state\": {\"opened\": true}");

		
		w.write(",\"children\":[");
		//
		boolean bfirst = true;
		for(CompLib lib:this.getCompLibs())
		{
			if (bfirst)
				bfirst = false;
			else
				w.write(',');

			writeTreeCat(w, lib);
		}
		w.write("]");
		
		w.write("}");
	}
	
	public void writeTreeCat(Writer w, CompLib lib) throws Exception
	{
		w.write("{\"id\":\"" + lib.getId() + "\"");
		
		w.write(",\"tp\": \"tag\"");
		w.write(",\"icon\": \"fa-solid fa-folder fa-lg\"");
		w.write(",\"text\":\""+lib.getTitle()+"\"");
		w.write(",\"state\": {\"opened\": true}");
		w.write(",\"children\":[");
		//
		boolean bfirst = true;
		for(CompCat cc:lib.getAllCats())
		{
			if (bfirst)
				bfirst = false;
			else
				w.write(',');

			w.write("{\"id\":\"" + lib.getId()+"-"+ cc.getId() + "\"");
			
			w.write(",\"tp\": \"tag\"");
			w.write(",\"icon\": \"fa-regular fa-folder fa-lg\"");

			w.write(",\"text\":\""+cc.getTitle()+"\"}");
		}
		w.write("]");
		
		w.write("}");
	}
	
	//private static transient CompItem copiedCompItem = null ;

	private static final String COMP_ITEM =  "__comp_item";
	// copy paste support
	
	public static CompItem getCopiedCompItem(HttpSession hs)
	{
		return (CompItem)hs.getAttribute(COMP_ITEM) ;
	}
	
	public static CompItem copyComp(HttpSession hs,String libid,String compid)
	{
		CompLib lib = CompManager.getInstance().getCompLibById(libid) ;
		if(lib==null)
			return null;
		
		CompItem ci = lib.getItemById(compid);
		if(ci==null)
			return null;
		hs.setAttribute(COMP_ITEM, ci) ;
		return ci;
	}
	
	/**
	 * if compid is not existed in lib,then id is not changed
	 * @param sor_libid
	 * @param sor_compid
	 * @param tar_libid
	 * @param tar_catid
	 * @throws Exception 
	 */
	public static CompItem pasteComp(HttpSession hs,String tar_libid,String tar_catid) throws Exception
	{
		CompItem ci = (CompItem)hs.getAttribute(COMP_ITEM) ;
		if(ci==null)
			return null ;
		
		CompLib tarlib = CompManager.getInstance().getCompLibById(tar_libid) ;
		if(tarlib==null)
			return null;
		CompCat tarcat = tarlib.getCatById(tar_catid) ;
		if(tarcat==null)
			return null;
		
		return tarcat.pasteComp(ci);
	}
}
