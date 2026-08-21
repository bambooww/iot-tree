package org.iottree.portal;

import java.util.ArrayList;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class NavNodeIns
{
	String id ;
	
	String title ;
	
	String icon ;
	
	String url ;
	
	String target ;
	
	ArrayList<NavNodeIns> subs = null;
	
	public NavNodeIns(String id,String title,String icon,String url,String target)
	{
		this.id = id ;
		this.title = title ;
		this.icon = icon ;
		this.url = url ;
		this.target = target ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public String getIcon()
	{
		return this.icon ;
	}
	
	public String getUrl()
	{
		return this.url ;
	}
	
	public String getTarget()
	{
		return this.target ;
	}
	
	private JSONObject toLocalJO()
	{
		return new JSONObject().put("id",id).put("title", this.title)
				.putOpt("icon", this.icon).putOpt("url", this.url).putOpt("target", this.target).put("color", "#495359");
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = toLocalJO();
		if(this.subs!=null&&this.subs.size()>0)
		{
			JSONArray jarr = new JSONArray() ;
			ret.put("sub", jarr) ;
			for(NavNodeIns nn:this.subs)
				jarr.put(nn.toJO()) ;
		}
		return ret ;
	}
	
	public JSONObject toNavJO()
	{
		JSONObject ret = toLocalJO();
		ret.put("node_id", this.id);
		if(this.subs!=null&&this.subs.size()>0)
		{
			JSONArray jarr = new JSONArray() ;
			ret.put("sub", jarr) ;
			for(NavNodeIns nn:this.subs)
				jarr.put(nn.toNavJO()) ;
		}
		return ret;
	}
	
	public static NavNodeIns fromJO(JSONObject jo)
	{
		String id = jo.optString("id") ;
		String tt = jo.optString("title") ;
		if(Convert.isNullOrEmpty(id)||Convert.isNullOrEmpty(tt))
			return null ;
		String icon = jo.optString("icon") ;
		String url = jo.optString("url") ;
		String target = jo.optString("target") ;
		NavNodeIns ret = new NavNodeIns(id,tt,icon,url,target);
		JSONArray jarr = jo.optJSONArray("sub") ;
		int n = 0 ;
		if(jarr!=null&& (n=jarr.length())>0)
		{
			ret.subs = new ArrayList<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject subjo = jarr.getJSONObject(i) ;
				NavNodeIns subn = fromJO(subjo) ;
				if(subn!=null)
				{
					ret.subs.add(subn) ;
				}
			}
		}
		return ret ;
	}
}
