package org.iottree.portal;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class PageCat
{
//	public static class Head
//	{
//		public String pageid ;
//		
//		public String name ;
//		
//		public String title ;
//		
//		public String templet_uid ;
//		
//		public Head(String id,String n,String t,String temp_uid)
//		{
//			this.pageid = id ;
//			this.name = n ;
//			this.title = t ;
//			if(Convert.isNullOrEmpty(t))
//				this.title = n ;
//			this.templet_uid = temp_uid ;
//		}
//		
//		public Templet getTemplet()
//		{
//			if(Convert.isNullOrEmpty(templet_uid))
//				return null ;
//			return PortalManager.getInstance().getTempletByUID(this.templet_uid) ;
//		}
//		
//		public JSONObject toJO()
//		{
//			JSONObject jo = new JSONObject() ;
//			jo.put("id", this.pageid) ;
//			jo.put("n", this.name) ;
//			jo.put("t", this.title) ;
//			jo.put("templet_uid", templet_uid) ;
//			return jo ;
//		}
//		
//		public static Head fromJO(JSONObject jo)
//		{
//			String id = jo.optString("id") ;
//			if(Convert.isNullOrEmpty(id))
//				return null ;
//			String n = jo.optString("n") ;
//			String t = jo.optString("t") ;
//			String tuid = jo.optString("templet_uid") ;
//			return new Head(id,n,t,tuid) ;
//		}
//	}
	
	String name  ;
	
	String title ;
	
	private LinkedHashMap<String,Page> id2page = null ;
	private HashMap<String,Page> name2page = null ;
	
	//private HashMap<String,Page> id2page = new HashMap<>() ;
	
	public PageCat(String name,String title) //,LinkedHashMap<String,Head> pageid2t)
	{
		if(Convert.isNullOrEmpty(name))
			throw new IllegalArgumentException("name cannot be null") ;
		
		this.name = name ;
		if(Convert.isNullOrEmpty(title))
			title = name ;
		this.title = title ;
//		if(pageid2t==null)
//			pageid2t = new LinkedHashMap<>() ;
//		this.pageid2head = pageid2t ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public LinkedHashMap<String,Page> getId2PageMap()
	{
		if(this.id2page!=null)
			return this.id2page ;
		
		synchronized(this)
		{
			if(this.id2page!=null)
				return this.id2page ;
			
			loadPages();
		}
		return this.id2page ;
	}
	
	public HashMap<String,Page> getName2PageMap()
	{
		if(this.name2page!=null)
			return this.name2page ;
		
		synchronized(this)
		{
			if(this.name2page!=null)
				return this.name2page ;
			
			loadPages();
		}
		return this.name2page ;
	}
	
	private File getCatDir()
	{
		return new File(PortalManager.getDir(),this.name+"/") ;
	}
	
	private synchronized void loadPages()
	{
		LinkedHashMap<String,Page> id2page = new LinkedHashMap<>() ;
		HashMap<String,Page> name2page = new HashMap<>() ;
		
		File catdir = getCatDir() ;
		File[] pfs = catdir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(!f.isFile())
					return false;
				String fn = f.getName() ;
				return fn.startsWith("p_") && fn.endsWith(".json");
			}}) ;
		
		ArrayList<Page> ld_pages = new ArrayList<>() ;
		for(File pf:pfs)
		{
			try
			{
				Page page = loadPage(pf) ;
				if(page==null)
					continue ;
				ld_pages.add(page) ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
		Collections.sort(ld_pages) ;
		for(Page p:ld_pages)
		{
			id2page.put(p.getId(),p) ;
			String n = p.getName() ;
			if(Convert.isNotNullEmpty(n))
				name2page.put(n,p) ;
		}
		
		this.id2page = id2page ;
		this.name2page = name2page ;
	}
	
	private synchronized void sortPages()
	{
		LinkedHashMap<String,Page> id2p = getId2PageMap();
		ArrayList<Page> ld_pages = new ArrayList<>() ;
		ld_pages.addAll(id2p.values()) ;
		Collections.sort(ld_pages) ;
		LinkedHashMap<String,Page> id2page = new LinkedHashMap<>() ;
		for(Page p:ld_pages)
		{
			id2page.put(p.getId(),p) ;
		}
		this.id2page = id2page ;
	}
	
	private Page loadPage(File pf) throws IOException
	{
		String fn = pf.getName() ;
		if(!fn.startsWith("p_") || !fn.endsWith(".json"))
			return null ;
		String id = fn.substring(2,fn.length()-5) ;
		JSONObject jo = Convert.readFileJO(pf) ;
		if(jo==null)
			return null ;
		
		return Page.fromJO(this, id,pf.lastModified(), jo) ;
	}
	
	void savePageCat() throws IOException
	{
		File dirf = getCatDir();
		dirf.mkdirs();

		JSONObject jo = this.toJO() ;
		Convert.writeFileJO(new File(dirf, "_cat.json"), jo);
	}
	
	static PageCat loadPageCat(String name) throws IOException
	{
		File dirf = new File(PortalManager.getDir(),name+"/") ;
		File catf = new File(dirf, "_cat.json");
		if (!catf.exists())
			return null ;
		
			JSONObject jo = Convert.readFileJO(catf);
			return PageCat.fromJO(name,jo) ;
	}
	
	void setAndSavePage(Page p) throws IOException
	{
		File pf = new File(getCatDir(),"p_"+p.getId()+".json") ;
		JSONObject jo = p.toJO() ;
		Convert.writeFileJO(pf, jo);
		p.chgDT = System.currentTimeMillis() ;
		
		this.getId2PageMap().put(p.getId(),p) ;
		String n = p.getName() ;
		if(Convert.isNotNullEmpty(n))
			this.getName2PageMap().put(n,p) ;
		sortPages();
	}
	

	public Page getPageById(String id)
	{
		return this.getId2PageMap().get(id) ;
	}
	
	public Page getPageByName(String n)
	{
		return this.getName2PageMap().get(n) ;
	}
	
	//only head
	public Page addPage(String name,String title,String templet_uid,StringBuilder failedr) throws IOException
	{
		Templet temp = PortalManager.getInstance().getTempletByUID(templet_uid) ;
		if(temp==null)
		{
			failedr.append("no templet found") ;
			return null ;
		}
		if(Convert.isNotNullEmpty(name))
		{
			Page oldp = this.getPageByName(name) ;
			if(oldp!=null)
			{
				failedr.append("name="+name+" is already existed") ;
				return null ;
			}
		}
		
		Page p = new Page(this,name,title,temp) ;
		this.setAndSavePage(p);
		return p ;
	}
	
	public boolean editPageBasic(String pageid,String name,String title,String templet_uid,StringBuilder failedr) throws IOException
	{
		Page p = this.getPageById(pageid) ;
		if(p==null)
		{
			failedr.append("no page found with id="+pageid) ;
			return false ;
		}
		
		Templet temp = PortalManager.getInstance().getTempletByUID(templet_uid) ;
		if(temp==null)
		{
			failedr.append("no templet found") ;
			return false ;
		}
		if(Convert.isNotNullEmpty(name))
		{
			Page oldp = this.getPageByName(name) ;
			if(oldp!=null && !oldp.getId().equals(pageid))
			{
				failedr.append("name="+name+" is already existed") ;
				return false ;
			}
		}
		p.setBasic(name,title, temp);
		this.setAndSavePage(p);
		return true ;
	}
	/**
	 * 外界修改页面详细信息时调用
	 * @param pageid
	 * @param jo
	 * @param failedr
	 * @return
	 * @throws IOException
	 */
	public boolean setPageDetail(String pageid,JSONObject jo,StringBuilder failedr) throws IOException
	{
		Page p = this.getPageById(pageid) ;
		if(p==null)
		{
			failedr.append("no page found with id="+pageid) ;
			return false ;
		}
		p.setDetailJO(jo);
		this.setAndSavePage(p);
		return true ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("n", this.name) ;
		jo.putOpt("t", this.title) ;
//		JSONArray jarr = new JSONArray() ;
//		jo.put("page_idtts",jarr) ;
//		for(Map.Entry<String, Head> id2t:this.pageid2head.entrySet())
//		{
//			JSONObject tmpjo = id2t.getValue().toJO() ;
//			jarr.put(tmpjo) ;
//		}
		return jo ;
	}

	public static PageCat fromJO(String name,JSONObject jo)
	{
		String t = jo.optString("t",name) ;
//		JSONArray jarr = jo.optJSONArray("page_idtts") ;
//		LinkedHashMap<String,Head> id2t = new LinkedHashMap<>() ;
//		if(jarr!=null)
//		{
//			int nn = jarr.length() ; 
//			for(int i = 0 ; i < nn ; i ++)
//			{
//				JSONObject tmpjo = jarr.getJSONObject(i) ;
//				Head h = Head.fromJO(tmpjo) ;
//				if(h==null)
//					continue ;
//				id2t.put(h.pageid,h) ;
//			}
//		}
		return new PageCat(name,t) ;
	}

}
