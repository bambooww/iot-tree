package org.iottree.portal;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
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

	public static PortalManager getInstance()
	{
		String pro_n = Config.getProductName() ;
		if(Convert.isNotNullEmpty(pro_n))
		{
			return getProductIns(pro_n) ;
		}
		else
		{
			UAPrj mainprj = UAManager.getInstance().getPrjDefault() ;
			if(mainprj==null)
			{
				throw new RuntimeException("no main prj set") ;
			}
			return getInstance(mainprj);
		}
	}
	
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

	UAPrj uaPrj = null ;
	
	File portalDir = null ;
	
	private LinkedHashMap<String, PageCat> pageCats = null;

	private LinkedHashMap<String, TempletCat> templetCats = null;
	
	

	private PortalManager(UAPrj owner)
	{
		this.uaPrj = owner ;
		File dirf = uaPrj.getPrjSubDir() ;
		portalDir = new File(dirf+ "/portal/");
		if(!portalDir.exists())
			portalDir.mkdirs() ;
	}

//	private PortalManager(String product_n)
//	{
//		File dirf = uaPrj.getPrjSubDir() ;
//		portalDir = new File(dirf+ "/portal/");
//	}

	File getDir()
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

	// templet

	public LinkedHashMap<String, TempletCat> listTempletCats()
	{
		if (templetCats != null)
			return templetCats;

		synchronized (this)
		{
			if (templetCats != null)
				return templetCats;

			try
			{
				List<TempletCat> pcs = this.loadTempletCats();
				LinkedHashMap<String, TempletCat> ret = new LinkedHashMap<>();
				for (TempletCat pc : pcs)
				{
					ret.put(pc.getName(), pc);
				}
				return templetCats = ret;
			}
			catch (Exception ee)
			{
				ee.printStackTrace();
				return null;
			}
		}
	}
	
	private List<TempletCat> loadTempletCats() throws IOException
	{
		ArrayList<TempletCat> rets = new ArrayList<>();

		File dir = TempletCat.getTempletBaseDir() ;
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
			String catn = subd.getName();
			File tf = new File(subd, "_templets.json");
			JSONObject jo = Convert.readFileJO(tf);
			String catt = catn;
			if (jo == null)
			{
				TempletCat tc = new TempletCat(catn,catt) ;
				rets.add(tc) ;
				continue ;
			}
			
			catt = jo.optString("t", catn);
			TempletCat tc = new TempletCat(catn,catt) ;
			rets.add(tc) ;
			
			JSONArray jarr = jo.optJSONArray("templets");
			if (jarr != null)
			{
				int ii = jarr.length();
				for (int i = 0; i < ii; i++)
				{
					JSONObject tmpjo = jarr.getJSONObject(i);
					// {"n":"dp1","t":"大屏模板1","page":"dp1.html"}
					String n = tmpjo.optString("n");
					if (Convert.isNullOrEmpty(n))
						continue;
					String t = tmpjo.optString("t");
					String p = tmpjo.optString("page");
					Templet tmp = new Templet(tc,n,t,p) ;
					tc.name2tmp.put(n,tmp) ;
				}
			}
		}

		return rets;
	}
	

	public List<Templet> listTempletsAll()
	{
		ArrayList<Templet> rets = new ArrayList<>() ;
		for(TempletCat tc: listTempletCats().values())
		{
			rets.addAll(tc.listTempletAll().values()) ;
		}
		return rets;
	}

	
	public TempletCat getTempletCat(String name)
	{
		return listTempletCats().get(name) ;
	}
	
	public Templet getTemplet(String cat_name,String name)
	{
		TempletCat tc = listTempletCats().get(cat_name) ;
		if(tc==null)
			return null ;
		return tc.getTemplet(name) ;
	}
	
	public Templet getTempletByUID(String uid)
	{
		int k = uid.indexOf('.') ;
		if(k<=0)
			return null ;
		return this.getTemplet(uid.substring(0,k), uid.substring(k+1)) ;
	}

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

		pc = new PageCat(this,name,title);
		pc.savePageCat();
		
		pageCats.put(name, pc);
		return pc;
	}
	
	public synchronized PageCat editPageCat(String name, String title, StringBuilder failedr) throws IOException
	{
		PageCat pc = getPageCat(name);
		if (pc == null)
		{
			failedr.append("PageCat is already existed with name=" + name);
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
		List<String> ss = Convert.splitStrWith(path, "/\\") ;
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
					JSONArray jarr = tmpjo.optJSONArray("nfs") ;
					if(jarr!=null)
					{
						int n = jarr.length() ;
						for(int i = 0 ; i < n ; i ++)
						{
							JSONObject jo0 = jarr.getJSONObject(i) ;
							NavFrame nf = new NavFrame(this) ;
							if(nf.fromJO(jo0))
								nfs.put(nf.getId(),nf) ;
						}
					}
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
		return nfs ;
	}
	
	private void saveNavFrames() throws IOException
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("default_uid", this.navFrameIdDefault) ;
		
		LinkedHashMap<String,NavFrame> nfss = getNavFrameAll() ;
		if(nfss!=null && nfss.size()>0)
		{
			JSONArray jarr = new JSONArray() ;
			jo.put("nfs",jarr) ;
			for(NavFrame nf:nfss.values())
			{
				jarr.put(nf.toJO()) ;
			}
		}
		File nff = getNavFrameFile() ;
		Convert.writeFileJO(nff, jo);
	}
	
	public NavFrame setNavFrameBasic(String id,String title,String name,boolean b_def,StringBuilder failedr) throws IOException
	{
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
			this.navFrameIdDefault = nf.getId() ;
		this.saveNavFrames(); 
		return nf ;
	}
	
	public NavFrame setNavFrameDetail(String id,JSONObject jo,StringBuilder failedr) throws IOException
	{
		NavFrame nf =  this.getNavFrameById(id) ;
		nf.setDetailByJO(jo);
		this.saveNavFrames(); 
		return nf ;
	}
}
