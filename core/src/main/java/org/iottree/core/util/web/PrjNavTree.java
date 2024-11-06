package org.iottree.core.util.web;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
  {
    color:green,scolor:yellow,
    navs:[]
  }
 * @author jason.zhu
 *
 */
public class PrjNavTree
{
	public static class NavItem
	{
		String n ;
		
		String t ;
		
		String u ;
		
		ArrayList<NavItem> subs = null  ;
		
		NavItem parent = null ;
		
		public NavItem(NavItem p)
		{
			parent = p ;
		}
		
		public String getName()
		{
			return n ;
		}
		
		public String getTitle()
		{
			return t ;
		}
		
		public String getUrl()
		{
			return u ;
		}
				
		public List<NavItem> listSubNav()
		{
			return subs ;
		}
		
		public int getLvl()
		{
			if(parent==null)
				return 1 ;
			return parent.getLvl()+1 ;
		}
		
		public int getDeep()
		{
			if(subs==null||subs.size()<=0)
				return 1 ;
			int maxd = 0 ;
			for(NavItem sub:subs)
			{
				int dp = sub.getDeep() ;
				if(dp>maxd)
					maxd = dp ;
			}
			return maxd +1 ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("n", this.n) ;
			jo.putOpt("t", this.t) ;
			jo.putOpt("u", this.u) ;
			if(subs!=null&&subs.size()>0)
			{
				JSONArray jarr = new JSONArray() ;
				jo.put("navs",jarr) ;
				for(NavItem ni:this.subs)
				{
					JSONObject tmpjo = ni.toJO() ;
					jarr.put(tmpjo) ;
				}
			}
			return jo ;
		}
	}
	
	private static NavItem parseFromJO(NavItem p,JSONObject jo)
	{
		String n = jo.optString("n") ;
		if(Convert.isNullOrEmpty(n))
			return null ;
		String t = jo.optString("t",n) ;
		String u = jo.optString("u") ;
		
		NavItem ret = new NavItem(p) ;
		ret.n = n ;
		ret.t = t ;
		ret.u = u ;
		
		JSONArray jarr = jo.optJSONArray("navs") ;
		if(jarr==null||jarr.length()<=0)
			jarr = jo.optJSONArray("subs") ;
		
		int nn ;
		if(jarr!=null&&(nn=jarr.length())>0)
		{
			ret.subs = new ArrayList<>() ;
			for(int i = 0 ; i < nn ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				NavItem subni = parseFromJO(ret,tmpjo) ;
				if(subni!=null)
					ret.subs.add(subni) ;
			}
		}
		return ret ;
	}
	
	private String navBkColor = "#8a8c98";
	private String navTxtColor = "#1a1a1a" ;
	
	private String selBkColor = "#3498e8";
	private String selTxtColor = "#c8ceb0" ;
	
	private ArrayList<NavItem> lvl1Nav = new ArrayList<>() ;
	
	private int maxDeep = 1 ;
	
	public PrjNavTree()
	{}
	
	public String getNavBkColor()
	{
		return this.navBkColor ;
	}
	
	public String getNavTxtColor()
	{
		return this.navTxtColor ;
	}
	
	public String getSelBkColor()
	{
		return this.selBkColor ;
	}
	
	public String getSelTxtColor()
	{
		return this.selTxtColor ;
	}
	
	public List<NavItem> listLvl1Nav()
	{
		return lvl1Nav ;
	}
	
	public int getMaxDeep()
	{
		return this.maxDeep ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		navBkColor = jo.optString("nav_bk_color", "#8a8c98") ;
		navTxtColor = jo.optString("nav_txt_color", "#1a1a1a") ;
		selBkColor = jo.optString("sel_bk_color", "#3498e8") ;
		selTxtColor = jo.optString("sel_txt_color", "#c8ceb0") ;
		
		JSONArray jarr = jo.optJSONArray("navs") ;
		if(jarr==null||jarr.length()<=0)
			jarr = jo.optJSONArray("subs") ;
		
		if(jarr==null||jarr.length()<=0)
			return false;
		int n = jarr.length() ;
		for(int i = 0  ; i < n ; i ++)
		{
			JSONObject tmpjo = jarr.getJSONObject(i) ;
			NavItem ni = parseFromJO(null,tmpjo);
			if(ni==null)
				continue ;
			lvl1Nav.add(ni) ;
		}
		
		for(NavItem ni:lvl1Nav)
		{
			int dp = ni.getDeep() ;
			if(dp>maxDeep)
				maxDeep = dp ;
		}
		
		return true ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("nav_bk_color", this.navBkColor) ;
		jo.put("nav_txt_color", this.navTxtColor) ;
		jo.put("sel_bk_color", this.selBkColor) ;
		jo.put("sel_txt_color", this.selTxtColor) ;
		
		JSONArray jarr = new JSONArray() ;
		jo.put("navs", jarr) ;
		for(NavItem ni:this.lvl1Nav)
		{
			jarr.put(ni.toJO()) ;
		}
		return jo ;
	}
}
