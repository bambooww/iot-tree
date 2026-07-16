package org.iottree.core.devtree;

import java.util.LinkedHashMap;

import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * node or tree static data like static properties,pictures,urls
 * @author jason.zhu
 *
 */
public class DTStaticData
{
	public static class Prop
	{
		public String name ;
		
		public String strval ;
		
		public UAVal.ValTP valTp = UAVal.ValTP.vt_str ;
		
		Prop()
		{}
		
		Prop(Prop oth)
		{
			this.name = oth.name ;
			this.strval = oth.strval ;
			this.valTp = oth.valTp ;
		}
		
		public JSONObject toJO()
		{
			return new JSONObject().put("n",this.name).putOpt("v", this.strval)
					.put("vt", this.valTp.getStr()) ;
		}
		
		public boolean fromJO(JSONObject jo)
		{
			this.name = jo.optString("n") ;
			if(Convert.isNullOrEmpty(this.name))
				return false;
			this.strval = jo.optString("v") ;
			this.valTp = UAVal.getValTp(jo.optString("vt","str")) ;
			return true ;
		}
	}
	
	public static class Link
	{
		String path ; // file or url
		
		String title ;
		
		boolean bPic ;
		
		boolean innerFile ;
		
		Link()
		{}
		
		Link(Link oth)
		{
			this.path = oth.path ;
			this.title = oth.path ;
			this.bPic = oth.bPic ;
			this.innerFile = oth.innerFile ;
		}

		public String getPath()
		{
			return this.path ;
		}
		
		public String getTitle()
		{
			return title ;
		}
		
		public boolean isPic()
		{
			return this.bPic ;
		}
		
		public boolean isInnerFile()
		{
			return this.innerFile ;
		}
		
		public JSONObject toJO()
		{
			return new JSONObject().put("p",this.path).putOpt("t", this.title)
					.put("pic", this.bPic).put("if", this.innerFile) ;
		}
		
		public boolean fromJO(JSONObject jo)
		{
			this.path = jo.optString("p") ;
			if(Convert.isNullOrEmpty(this.path))
				return false;
			this.title = jo.optString("t") ;
			this.bPic = jo.optBoolean("pic") ;
			this.innerFile = jo.optBoolean("if",true) ;
			return true ;
		}
	}
	
	DTNode owner ;
	
	LinkedHashMap<String,Prop> name2prop = new LinkedHashMap<>() ;
	
	LinkedHashMap<String,Link> path2link = new LinkedHashMap<>() ;
	
	public DTStaticData(DTNode nd)
	{
		this.owner = nd ;
	}
	
	DTStaticData(DTNode owner,DTStaticData oth)
	{
		this.owner = owner ;
		for(Prop p:oth.name2prop.values())
		{
			p = new Prop(p) ;
			this.name2prop.put(p.name,p) ;
		}
		for(Link p:oth.path2link.values())
		{
			p = new Link(p) ;
			this.path2link.put(p.path,p) ;
		}
	}
	
	public DTNode getOwner()
	{
		return this.owner ;
	}
	
	public boolean isEmpty()
	{
		return name2prop.size()<=0 && path2link.size()<=0 ;
	}

	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject() ;
		if(name2prop!=null&&name2prop.size()>0)
		{
			JSONArray jarr = new JSONArray() ;
			ret.put("props",jarr) ;
			for(Prop p:this.name2prop.values())
			{
				jarr.put(p.toJO()) ;
			}
		}
		
		if(path2link!=null&&path2link.size()>0)
		{
			JSONArray jarr = new JSONArray() ;
			ret.put("links",jarr) ;
			for(Link p:this.path2link.values())
			{
				jarr.put(p.toJO()) ;
			}
		}
		return ret ;
	}
	
	public static DTStaticData fromJO(DTNode dn,JSONObject jo)
	{
		DTStaticData ret = new DTStaticData(dn) ;
		JSONArray jarr = jo.optJSONArray("props") ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				Prop p = new Prop() ;
				if(p.fromJO(tmpjo))
					ret.name2prop.put(p.name,p) ;
			}
		}
		jarr = jo.optJSONArray("links") ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				Link p = new Link() ;
				if(p.fromJO(tmpjo))
					ret.path2link.put(p.path,p) ;
			}
		}
		if(ret.isEmpty())
			return null ;
		return ret;
	}
}
