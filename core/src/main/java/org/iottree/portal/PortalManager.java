package org.iottree.portal;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.iottree.core.Config;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.res.ResItem;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;

/**
 * 
 * @author zzj
 *
 */
public class PortalManager
{
	//private static PortalManager instance = null;

//	public static PortalManager getInstance()
//	{
//		String pro_n = Config.getProductName() ;
//		if(Convert.isNotNullEmpty(pro_n))
//		{
//			return getProductIns(pro_n) ;
//		}
//		else
//		{
////			UAPrj mainprj = UAManager.getInstance().getPrjDefault() ;
////			if(mainprj==null)
////			{
////				throw new RuntimeException("no main prj set") ;
////			}
////			return getInstance(mainprj);
//			return null ;
//		}
//	}
	
	private static HashMap<String,PortalManager> prjn2pm = new HashMap<>() ;
	
	public static PortalManager getInstance(UAPrj prj)
	{
		PortalManager ins = prjn2pm.get(prj.getName()) ;
		if (ins != null)
			return ins;

		synchronized (PortalManager.class)
		{
			ins = prjn2pm.get(prj.getName()) ;
			if (ins != null)
				return ins;

			ins  = new PortalManager(prj);
			prjn2pm.put(prj.getName(),ins) ;
			return ins ;
		}
	}
	
	public static PortalManager getInstanceByPrjId(String prjid)
	{
		UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
		if(prj==null)
			return null ;
		return getInstance(prj) ;
	}
	
	/**
	 * 根据产品名称获取对应的实例
	 * @param product_name
	 * @return
	 */
	public static PortalManager getProductIns(String product_name)
	{
		String prj_n = "pro_"+product_name ;
		UAPrj prj = UAManager.getInstance().getPrjByName(prj_n) ;
		if(prj==null)
		{
			return null ;
		}
		return getInstance(prj);
	}

//	public static void regDataSor(DataSor ds)
//	{
//
//	}

	private UAPrj uaPrj = null ;
	
	private File portalDir = null ;
	
	private LinkedHashMap<String, PageCat> pageCats = null;

	private PortalManager(UAPrj owner)
	{
		this.uaPrj = owner ;
		File dirf = uaPrj.getPrjSubDir() ;
		portalDir = new File(dirf+ "/portal/");
		if(!portalDir.exists())
			portalDir.mkdirs() ;
	}
	
	public UAPrj getOwner()
	{
		return this.uaPrj ;
	}

//	private PortalManager(String product_n)
//	{
//		File dirf = uaPrj.getPrjSubDir() ;
//		portalDir = new File(dirf+ "/portal/");
//	}

	public File getDir()
	{
		return portalDir;
	}

	
//	public void onWebAllLoaded()
//	{
//		for (AppInfo awc : CompManager.getInstance().getAllAppInfo())
//		{
//			loadInWeb(awc);
//		}
//	}
//
//	private void loadInWeb(AppInfo appi) // throws ClassNotFoundException
//	{
//		String appn = appi.getContextName();
//		AppWebConfig awc = AppWebConfig.getModuleWebConfig(appn);
//		if (awc == null)
//			return;
//
//		Element ele = awc.getConfElement("portal");
//		if (ele == null)
//			return;
//		for (Element dnp_ele : XmlHelper.getSubChildElementList(ele, "dn_plug"))
//		{
//			try
//			{
//				// String cn = dnp_ele.getAttribute("cn") ;
//				// Class<?> c = Class.forName(cn, true,
//				// appi.getRelatedClassLoader()) ;
//				// DNPlug p = (DNPlug)c.getConstructor().newInstance() ;
//				// String pm_url = dnp_ele.getAttribute("pm_url") ;
//				// if(Convert.isNotNullEmpty(pm_url))
//				// {
//				// if(pm_url.startsWith("."))
//				// {
//				// pm_url = "/"+appn+"/"+pm_url ;
//				// }
//				// }
//				// //p.pmUrl = pm_url;
//				// System.out.println(" register DNPlug "+cn +"
//				// @"+appi.getContextName()) ;
//				// //registerPlug(p) ;
//			}
//			catch (Exception ee)
//			{
//				ee.printStackTrace();
//			}
//		}
//	}

	
	// pages

	public LinkedHashMap<String, PageCat> listPageCats()
	{
		if (pageCats != null)
			return pageCats;

		synchronized (this)
		{
			if (pageCats != null)
				return pageCats;

			try
			{
				List<PageCat> pcs = this.loadPageCats();
				LinkedHashMap<String, PageCat> ret = new LinkedHashMap<>();
				for (PageCat pc : pcs)
				{
					ret.put(pc.getName(), pc);
				}
				return pageCats = ret;
			}
			catch (Exception ee)
			{
				ee.printStackTrace();
				return null;
			}
		}
	}

	private List<PageCat> loadPageCats() throws IOException
	{
		ArrayList<PageCat> rets = new ArrayList<>();

		File dir = getDir();
		if (!dir.exists())
			return rets;

		File[] subds = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				return f.isDirectory();
			}
		});

		for (File subd : subds)
		{
			String n = subd.getName();
			PageCat pc = PageCat.loadPageCat(this,n) ;
			if(pc!=null)
				rets.add(pc) ;
		}

		return rets;
	}

	public PageCat getPageCat(String name)
	{
		return this.listPageCats().get(name);
	}

	public synchronized PageCat addPageCat(String name, String title, StringBuilder failedr) throws IOException
	{
		PageCat pc = getPageCat(name);
		if (pc != null)
		{
			failedr.append("PageCat is already existed with name=" + name);
			return null;
		}

		if(!Convert.checkVarName(name, true, failedr))
			return null ;
		
		pc = new PageCat(this,name,title);
		pc.savePageCat();
		
		pageCats.put(name, pc);
		return pc;
	}
	
	public synchronized PageCat editPageCat(String name, String title, StringBuilder failedr) throws IOException
	{
		if(!Convert.checkVarName(name, true, failedr))
			return null ;
		
		PageCat pc = getPageCat(name);
		if (pc == null)
		{
			failedr.append("PageCat not existed with name=" + name);
			return null;
		}

		pc.title = title ;
		File dirf = new File(getDir(), "./" + name + "/");
		JSONObject jo = pc.toJO() ;
		Convert.writeFileJO(new File(dirf, "_cat.json"), jo);
		pageCats.put(name, pc);
		return pc;
	}
	
	public Page getPage(String cat_name,String page_id)
	{
		PageCat pc = this.getPageCat(cat_name) ;
		if(pc==null)
			return null ;
		return pc.getPageById(page_id) ;
	}
	
	public Page getPageByUID(String page_uid)
	{
		int k = page_uid.indexOf('.') ;
		if(k<=0)
			return null ;
		return getPage(page_uid.substring(0,k),page_uid.substring(k+1)) ;
	}
	
	public Page getPageByPath(String path)
	{
		List<String> ss = Convert.splitStrWith(path, "/\\.") ;
		if(ss==null||ss.size()!=2)
			return null ;
		PageCat pc = this.getPageCat(ss.get(0)) ;
		if(pc==null)
			return null ;
		return pc.getPageByName(ss.get(1)) ;
	}
	
	private LinkedHashMap<String,NavFrame> navFrameAll = null ;
	
	String navFrameIdDefault = null ;
	
	private File getNavFrameFile()
	{
		return new File(this.portalDir,"_nav_frames.json") ;
	}
	
	private File getNavFrameDir()
	{
		return this.portalDir ;//,"_nav_frames.json") ;
	}
	
	public LinkedHashMap<String,NavFrame> getNavFrameAll()
	{
		if(this.navFrameAll!=null)
			return this.navFrameAll ;
		
		LinkedHashMap<String,NavFrame> nfs =  loadNavFrames();
		
		return navFrameAll = nfs ;
	}
	
	public NavFrame getNavFrameById(String id)
	{
		return this.getNavFrameAll().get(id) ;
	}
	
	public NavFrame getNavFrameByName(String n)
	{
		for(NavFrame nf:this.getNavFrameAll().values())
			if(n.equals(nf.getName()))
				return nf;
		return null ;
	}
	
	public NavFrame getNavFrameDefault()
	{
		LinkedHashMap<String,NavFrame> id2nf = this.getNavFrameAll() ;
		if(id2nf==null||id2nf.size()<=0)
			return null ;
		if(id2nf.size()==1)
			for(NavFrame nf: id2nf.values())
				return nf ;
		if(Convert.isNullOrEmpty(this.navFrameIdDefault))
			return null ;
		return id2nf.get(this.navFrameIdDefault) ;
	}
	
	private LinkedHashMap<String,NavFrame> loadNavFrames()
	{
		LinkedHashMap<String,NavFrame> nfs = new LinkedHashMap<>() ;
		File nff = getNavFrameFile() ;
		if(nff.exists())
		{
			try
			{
				JSONObject tmpjo = Convert.readFileJO(nff) ;
				if(tmpjo!=null)
				{
					this.navFrameIdDefault = tmpjo.optString("default_uid") ;
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
		File pdir = this.getNavFrameDir() ;
		File[] nf_fs = pdir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(!f.isFile())
					return false;
				String fn = f.getName() ;
				return fn.startsWith("nf_")&&fn.endsWith(".json");
			}});
		
		for(File nf_f:nf_fs)
		{
			try
			{
				JSONObject jo0 = Convert.readFileJO(nf_f) ;
				if(jo0==null)
					continue ;
				NavFrame nf = new NavFrame(this) ;
				if(nf.fromJO(jo0))
					nfs.put(nf.getId(),nf) ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
		return nfs ;
	}
	
	private void saveNavFrameDefault() throws IOException
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("default_uid", this.navFrameIdDefault) ;
		
		
		File nff = getNavFrameFile() ;
		Convert.writeFileJO(nff, jo);
	}
	
	private File calcNavFrameFile(String nf_id)
	{
		return new File(this.portalDir,"nf_"+nf_id+".json") ;
	}
	
	private File calcNavFrameLogoFile(String nf_id,String inputfn)
	{
		inputfn = inputfn.toLowerCase() ;
		int k = inputfn.lastIndexOf('.') ;
		if(k<0)
			return null ;
		String ext = inputfn.substring(k+1) ;
		return new File(this.portalDir,"logo_"+nf_id+"."+ext) ;
	}
	
	private void saveNavFrame(NavFrame nf) throws IOException
	{
		JSONObject nfjo = nf.toJO() ;
		File f = calcNavFrameFile(nf.getId()) ;
		Convert.writeFileJO(f, nfjo);
	}
	
	public NavFrame setNavFrameBasic(String id,String title,String name,boolean b_def,StringBuilder failedr) throws IOException
	{
		if(Convert.isNotNullEmpty(name))
		{
			if(!Convert.checkVarName(name, true, failedr))
				return null;
		}
		
		NavFrame nf = null;
		if(Convert.isNullOrEmpty(id))
		{
			if(Convert.isNotNullEmpty(name))
			{
				NavFrame oldnf = this.getNavFrameByName(name) ;
				if(oldnf!=null)
				{
					failedr.append("["+name+"] is already existed") ;
					return null ;
				}
			}
			
			nf = new NavFrame(this,title, name) ;
			getNavFrameAll().put(nf.getId(),nf) ;
		}
		else
		{
			nf = this.getNavFrameById(id) ;
			if(nf==null)
			{
				failedr.append("no NavFrame found with id="+id) ;
				return null ;
			}
			if(Convert.isNotNullEmpty(name))
			{
				NavFrame oldnf = this.getNavFrameByName(name) ;
				if(oldnf!=null && oldnf!=nf)
				{
					failedr.append("["+name+"] is already existed") ;
					return null ;
				}
			}
			nf.setBasic(title, name);
		}
		if(b_def||this.navFrameAll.size()==1)
		{
			this.navFrameIdDefault = nf.getId() ;
			this.saveNavFrameDefault();
		}
		this.saveNavFrame(nf); 
		return nf ;
	}
	
	public NavFrame setNavFrameDetail(String id,JSONObject jo,StringBuilder failedr) throws IOException
	{
		NavFrame nf =  this.getNavFrameById(id) ;
		nf.setDetailByJO(jo);
		this.saveNavFrame(nf); 
		return nf ;
	}
	
	public File setNavFrameLogoFile(String id,FileItem logof,StringBuilder failedr) throws Exception
	{
		NavFrame nf =  this.getNavFrameById(id) ;
		if(nf==null)
		{
			failedr.append("no NavFrame found") ;
			return null ;
		}
		String inputfn = logof.getName() ;
		File tarf = calcNavFrameLogoFile(id,inputfn) ;
		if(tarf==null)
		{
			failedr.append("invalid input logo file") ;
			return null ;
		}
		if(tarf.exists())
			tarf.delete();
		
		logof.write(tarf);
//		if(!logof.renameTo(tarf))
//		{
//			failedr.append("invalid set target logo file") ;
//			return null ;
//		}
		nf.logo = tarf.getName() ;
		this.saveNavFrame(nf);
		return tarf ;
	}
	
	public NavFrame delNavFrame(String id,StringBuilder failedr) throws IOException
	{
		NavFrame nf =  this.getNavFrameById(id) ;
		if(nf==null)
		{
			failedr.append("no NavFrame found") ;
			return null ;
		}
		File f = calcNavFrameFile(nf.getId()) ;
		if(f.delete())
			return nf ;
		failedr.append("del nav frame failed") ;
		return null ;
	}
}
