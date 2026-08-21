package org.iottree.portal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.web.AppWebConfig;
import org.iottree.core.util.xmldata.XmlHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;

/**
 * navigator and page framework
 * 
 * @author jason.zhu
 */
public class NavFrame
{
	String id ;
	
	String name = null ;
	
	String title = null ;//list title
	
	String sysTitle = "" ; //show system title in main page
	
	String logo = null ;
	
	/**
	 * navigator frame layout name
	 * null==default
	 */
	String layout ;
	
	//public Element eleRoot=null;
	//public NavNode navRoot = null;
	private String homeUrl = null ;
	
	//private ArrayList<String> navNodeUIDs = null ;
	
	//private transient ArrayList<NavNode> navNodes = null ;
	
	private ArrayList<NavNodeIns> navNodeInss = new ArrayList<>() ;
	
	PortalManager pmgr ;
	
	UAPrj prj ;
	
	public NavFrame(PortalManager pmgr)
	{
		this.pmgr = pmgr;
		prj = pmgr.getOwner() ;
	}
	
	//create new
	NavFrame(PortalManager pmgr,String title,String name)
	{
		this.pmgr = pmgr;
		prj = pmgr.getOwner() ;
		this.id = CompressUUID.createNewId() ;
		this.title = title ;
		this.name = name;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public String getName()
	{
		if(this.name==null)
			return "" ;
		return this.name ;
	}
	
	public boolean isDefault()
	{
		return this.id.equals(this.pmgr.navFrameIdDefault) ;
	}
	
	public String getHomeUrl()
	{
		return this.homeUrl ;
	}
	
	void setBasic(String title,String name)
	{
		this.title = title ;
		this.name = name;
	}
	
	public String getSysTitle()
	{
		if(this.sysTitle==null)
			return "" ;
		return this.sysTitle ;
	}
	
	public String getLogo()
	{
		return this.logo ;
	}
	
	public NavFrame asLogo(String fn)
	{
		this.logo = fn ;
		return this ;
	}
	
	public File getLogoFile()
	{
		if(Convert.isNullOrEmpty(this.logo))
			return null ;
		return new File(this.pmgr.getDir(),this.logo) ;
	}
	
	public String getLayout()
	{
		if(Convert.isNullOrEmpty(this.layout))
			return "default";
		return this.layout ;
	}
	
	public String getUrlPath(boolean ignore_default)
	{
		if(!ignore_default && this.isDefault())
			return "/"+this.prj.getName()+"/";
		return "/"+this.prj.getName()+"/_portal_"+this.getName() ;
	}
	
	public String getUrlPath()
	{
		return getUrlPath(false) ;
	}
	
	public synchronized List<NavNodeIns> getNavNodeInss()
	{
//		if(this.navNodes!=null)
//			return this.navNodes ;
//		ArrayList<NavNode> nns = new ArrayList<>() ;
//		if(this.navNodeUIDs!=null)
//		{
//			for(String nuid :this.navNodeUIDs)
//			{
//				NavNode nn = NavApp.getNavNodeByUID(nuid) ;
//				if(nn==null)
//					continue ;
//				nns.add(nn) ;
//			}
//		}
//		return this.navNodes = nns ;
		return navNodeInss;
	}
	
	private JSONArray navNodeInsJArr = null ;
	
	public synchronized JSONArray getNavNodeInssJArr()
	{
		if(navNodeInsJArr!=null)
			return navNodeInsJArr ;
		JSONArray jarr  =new JSONArray() ;
		//jarr.put(new JSONObject().put("node_id", "__home")) ;
		List<NavNodeIns> nns = getNavNodeInss() ;
		for(NavNodeIns nn:nns)
		{
			jarr.put(nn.toNavJO()) ;
		}
		return navNodeInsJArr = jarr ;
	}
	
	private synchronized void clearCache()
	{
		//this.navNodes = null ;
		this.navNodeInsJArr = null ;
	}
	
	public void setDetailByJO(JSONObject jo)
	{
		this.sysTitle = jo.optString("sys_t") ;
		//this.logo = jo.optString("logo") ;
		this.layout = jo.optString("layout") ;
		this.homeUrl = jo.optString("home_url") ;
		
		JSONArray jarr = jo.optJSONArray("node_inss") ;
		ArrayList<NavNodeIns> nnis = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				NavNodeIns nni = NavNodeIns.fromJO(tmpjo) ;
				if(nni!=null)
					nnis.add(nni) ;
			}
		}
		
		navNodeInss = nnis ;
		
		clearCache();
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject() ;
		ret.put("id",this.id).putOpt("n", this.name).put("t", this.title).putOpt("sys_t", this.sysTitle)
			.putOpt("logo",this.logo).putOpt("layout", this.layout).putOpt("home_url", this.homeUrl) ;
		JSONArray jarr  =new JSONArray() ;
		//jarr.put(new JSONObject().put("node_id", "__home")) ;
		List<NavNodeIns> nns = getNavNodeInss() ;
		for(NavNodeIns nn:nns)
		{
			jarr.put(nn.toJO()) ;
		}
		ret.put("node_inss", jarr) ;
		
		//ret.putOpt("nav_node_uids", this.navNodeUIDs) ;
		return ret;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.id = jo.optString("id") ;
		if(Convert.isNullOrEmpty(this.id))
			return false;
		this.name = jo.optString("n") ;
		this.title = jo.optString("t") ;
		setDetailByJO(jo) ;
		return true;
	}
	

//	private void loadFromEle(Element web_conf)
//	{
//		String app_title=web_conf.getAttribute("title");
//		String app_name=web_conf.getAttribute("name");
//		String app_icon=web_conf.getAttribute("icon");
//		String app_img=web_conf.getAttribute("img");
//		NavNode ret=new NavNode(app_name,app_title,app_icon,"",app_img,0);
//		eleRoot=web_conf;
//		homeUrl = web_conf.getAttribute("home_page") ;
//		constructNavNodes(navRoot, eleRoot,1);
//	}
	
	private static void constructNavNodes(NavNode cur_tn, Element rele,int nav_idx)
	{
		Element[] nav1_eles = XmlHelper.getSubChildElement(rele,"sub_nav");
		if (nav1_eles == null)
		{
			return;
		}
		for (Element tmpe : nav1_eles)
		{
			NavNode tmpwtn = transEleToNavNode(tmpe);
			if (tmpwtn == null)
			{
				continue;
			}
			cur_tn.appendChild(tmpwtn);
			tmpwtn.navIdx = nav_idx ;
			constructNavNodes(tmpwtn, tmpe,nav_idx+1);
		}
	}
	
	private static NavNode transEleToNavNode(Element ele)
	{
		String n = ele.getAttribute("name");
		if (Convert.isNullOrEmpty(n)||"null".equals(n))
			return null;

		String title = ele.getAttribute("title");
		String title_en = ele.getAttribute("title_en");
		String title_cn = ele.getAttribute("title_cn");
		if(Convert.isNotNullEmpty(title))
			title_en = title_cn = title ;
		String url = ele.getAttribute("url");
		String icon = ele.getAttribute("icon");
		String tar = ele.getAttribute("target");
		String roles = ele.getAttribute("roles");
		String notroles=ele.getAttribute("notroles");
		
		int order=Convert.parseToInt32(ele.getAttribute("order"),-1);
		
		NavNode tmpNode=new NavNode(n, title_en,title_cn,icon,url,tar,roles,order);
		if(Convert.isNotNullEmpty(notroles)&&!"null".equals(notroles))
		{
			tmpNode.setNotRoles(notroles);
		}
		
		tmpNode.extAttr = Convert.getElementAttrMap(ele) ;
		return tmpNode;
	}
	
	static NavApp loadNavApp(AppWebConfig awc,Element nav_ele)
	{
		NavApp na = new NavApp(awc) ;
		constructNavNodes(na, nav_ele,1);
		return na ;
	}
	
	public static NavFrame getNavFrame(String prjid,String nf_id)
	{
		UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
		if(prj==null)
			return null ;
		PortalManager pm = PortalManager.getInstance(prj) ;
		if(pm==null)
			return null ;
		return pm.getNavFrameById(nf_id) ;
	}
}
