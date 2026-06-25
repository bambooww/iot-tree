package org.iottree.core.devtree;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Device definition for anomaly detection
 * 
 * @author jason.zhu
 *
 */
public class DTCls
{
	public static class Prop
	{
		public String name ;
		
		public String title ;

		public boolean bStatic = false;//global in class or not
		
		public JSONObject toJO()
		{
			JSONObject ret = new JSONObject().put("n", this.name).putOpt("t", this.title);
			if(bStatic)
				ret.put("g", true) ;
			return ret ;
		}
		
		public boolean fromJO(JSONObject jo)
		{
			this.name = jo.optString("n") ;
			if(Convert.isNullOrEmpty(this.name))
				return false;
			this.title = jo.optString("t") ;
			this.bStatic = jo.optBoolean("g",false) ;
			return true ;
		}
	}
	
	public static class PropPrim extends Prop
	{
		public UAVal.ValTP valTp = UAVal.ValTP.vt_str ;
		
		public JSONObject toJO()
		{
			return super.toJO().put("vtp", this.valTp.getStr());
		}
		
		public boolean fromJO(JSONObject jo)
		{
			if(!super.fromJO(jo))
				return false;
			this.valTp = UAVal.getValTp(jo.optString("vtp","str")) ;
			return true ;
		}
	}
	
	public static class PropCls extends Prop
	{
		String clsPath ;
		
		//private DTCls cls = null ;
		
		public String getClsPath()
		{
			return this.clsPath ;
		}
		
		public DTCls getCls()
		{
//			if(cls!=null)
//				return cls ;
//			return cls = DTClsManager.getInstance().getOrLoadCls(this.clsPath) ;
			return DTClsManager.getOrLoadCls(this.clsPath) ;
		}
		
		public JSONObject toJO()
		{
			return super.toJO().put("cls", this.clsPath);
		}
		
		public boolean fromJO(JSONObject jo)
		{
			if(!super.fromJO(jo))
				return false;
			this.clsPath = jo.optString("cls") ;
			return Convert.isNotNullEmpty(this.clsPath) ;
		}
	}
	
	String clsPath ;
	
	List<String> catPath ;

	String name ;
	
	String title ;
	
	String desc ;
	
	/**
	 * include props
	 */
	ArrayList<PropPrim> propPrims = new ArrayList<>() ;
	
	/**
	 * include classes
	 */
	ArrayList<PropCls> propClss = new ArrayList<>() ;
	
	public DTCls(String cls_path)
	{
		clsPath = cls_path ;
		List<String> ss = Convert.splitStrWith(cls_path, "./") ;
		this.name = ss.remove(ss.size()-1) ;
		this.catPath = ss ;
	}
	
	public String getClsPath()
	{
		return this.clsPath ;
	}
	
	public List<String> getCatPath()
	{
		return this.catPath ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	public List<PropPrim> listPropPrims()
	{
		return this.propPrims ;
	}
	
	public List<PropCls> listPropClss()
	{
		return this.propClss ;
	}
	
	public JSONObject toClsJO()
	{//.put("n", this.name)
		JSONObject ret = new JSONObject().putOpt("t", this.title).putOpt("d", this.desc) ;
		JSONArray jarr = new JSONArray() ;
		ret.put("prims", jarr) ;
		for(Prop p:this.propPrims)
		{
			jarr.put(p.toJO()) ;
		}
		jarr = new JSONArray() ;
		ret.put("clss", jarr) ;
		for(Prop p:this.propClss)
		{
			jarr.put(p.toJO()) ;
		}
		return ret ;
	}
	
	public static DTCls fromClsJO(String cls_path,JSONObject jo)
	{
//		String n = jo.optString("n") ;
//		if(Convert.isNullOrEmpty(n))
//			return null ;
		DTCls ret = new DTCls(cls_path) ;
		ret.title = jo.optString("t") ;
		ret.desc = jo.optString("d") ;
		JSONArray jarr = jo.optJSONArray("prims") ;
		if(jarr!=null)
		{
			int k = jarr.length() ;
			for(int i = 0 ; i < k ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				PropPrim p = new PropPrim() ;
				if(p.fromJO(tmpjo))
					ret.propPrims.add(p) ;
			}
		}
		jarr = jo.optJSONArray("clss") ;
		if(jarr!=null)
		{
			int k = jarr.length() ;
			for(int i = 0 ; i < k ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				PropCls p = new PropCls() ;
				if(p.fromJO(tmpjo))
					ret.propClss.add(p) ;
			}
		}
		return ret ;
	}
}
