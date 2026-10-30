package org.iottree.driver.zigbee.zcl.util;

import java.util.Map;

import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class TagDef
{
	String name ;
	
	String title ;
	
	String vt = null ;
	
	boolean read = false;
	
	boolean write = false;
	
	String defaultStr = null ;
	
	String unit = null ;
	
	//display in panel
	boolean dis = false;
	
	private transient ValTP valTP = null ;
	
	public TagDef() {}
	
	public TagDef(String n,String t,String vt,boolean w,String defv,String unit)
	{
		this.name = n ;
		this.title = t ;
		this.vt = vt ;
		this.read = true ;
		this.write = w ;
		this.defaultStr = defv ;
		this.unit = unit ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public String getVT()
	{
		return this.vt ;
	}
	
	public ValTP getValTP()
	{
		if(this.valTP!=null)
			return this.valTP ;
		if(Convert.isNullOrEmpty(this.vt))
			return null ;
		return this.valTP = UAVal.getValTp(this.vt) ;
	}
	
	public boolean canRead()
	{
		return this.read;
	}
	
	public boolean canWrite()
	{
		return this.write ;
	}
	
	public String getDefaultStr()
	{
		return this.defaultStr ;
	}
	
	public String getUnit()
	{
		return this.unit ;
	}
	
	public boolean getDis()
	{
		return this.dis ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().put("n", this.name).putOpt("t",this.title).putOpt("vt", this.vt)
				.putOpt("r", this.read).putOpt("w", this.write).putOpt("default", this.defaultStr).putOpt("unit", unit) ;
	}
	
	public JSONObject toTagJO()
	{
		return toTagJO("") ;
	}
	
	public JSONObject toTagJO(String n_pre)
	{
		return new JSONObject().put("name", n_pre+this.name).putOpt("title",this.title).putOpt("val_tp", this.vt)
				.putOpt("read", this.read).putOpt("write", this.write).putOpt("default", this.defaultStr).putOpt("unit", unit) ;
	}
	
	public static TagDef fromObj(DataType dtp,Object obj)
	{
		if(obj==null)
			return null ;
		
		if(obj instanceof JSONObject)
		{
			JSONObject jo = (JSONObject)obj;
			
			String n = jo.optString("n") ;
			if(Convert.isNullOrEmpty(n))
				return null ;
			TagDef r = new TagDef() ;
			r.name = n ;
			r.title = jo.optString("t") ;
			r.vt = jo.optString("vt") ;
			if(Convert.isNullOrEmpty(r.vt) && dtp!=null)
				r.vt = dtp.getUAValTP() ;
			r.read = jo.optBoolean("r", false) ;
			r.write = jo.optBoolean("w", false) ;
			r.defaultStr = jo.optString("default") ;
			r.unit = jo.optString("unit") ;
			r.dis = jo.optBoolean("dis",false) ;
			return r ;
		}
		
		if(obj instanceof String)
		{
			String ss = (String)obj ;
			Map<String,String> n2v = Convert.parseCssParamStr(ss) ;
			String n = n2v.get("n") ;
			if(Convert.isNullOrEmpty(n))
				return null ;
			TagDef r = new TagDef() ;
			r.name = n ;
			r.title =n2v.get("t") ;
			r.vt = n2v.get("vt") ;
			if(Convert.isNullOrEmpty(r.vt) && dtp!=null)
				r.vt = dtp.getUAValTP() ;
			r.read = "true".equals(n2v.get("r")) ;
			r.write = "true".equals(n2v.get("w")) ;
			r.defaultStr = n2v.get("default") ;
			r.unit = n2v.get("unit") ;
			r.dis = "true".equals(n2v.get("dis")) ;
			return r ;
		}
		return null;
	}
}
