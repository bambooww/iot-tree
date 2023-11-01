package org.iottree.core.cxt;

import java.io.Writer;
import java.lang.reflect.Field;

import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UATag;
import org.json.JSONObject;

import com.sun.xml.bind.v2.schemagen.xmlschema.List;

public class JsProp extends JsSub
{
	
	
	Object val = null ;
	
	Class<?> valTp = null ;
	
	
	boolean hSub = false;
	
	//UANodeOCTagsCxt cxtNode = null ; 
	boolean bList = false;
	
	public JsProp(String name,Object val,Class<?> valtp,boolean has_sub,String title,String desc)
	{
		super(name,title,desc) ;
		//this.cxtNode = cxtn;
		//this.name = name ;
		this.valTp = valtp;
		this.val = val;
		if(valtp==null && val!=null)
			this.valTp = val.getClass() ;
		//this.title = title ;
		//this.desc = desc ;
		hSub = has_sub ;
	}
	
	public JsProp asList(boolean b)
	{
		this.bList = b ;
		return this ;
	}
//	public JsProp(String name,Class<?> valtp,String title,String desc)
//	{
//		this.name = name ;
//		this.valTp = valtp;
//		this.title = title ;
//		this.desc = desc ;
//	}

	public Object getVal()
	{
		return val;
	}

	public Class<?> getValTp()
	{
		return valTp ;
	}
	
	public boolean hasSub()
	{
//		if(val instanceof Number)
//			return true ;
//		if(val instanceof Boolean)
//			return true ;
//		if(val instanceof String)
//			return true ;
//		
//		return false;
		return this.hSub ;
	}
	
	public boolean isTag()
	{
		return UATag.class.isAssignableFrom(this.valTp) ;
	}
	
	public boolean isSysTag()
	{
		if(!isTag()) return false;
		return this.name.startsWith("_") ;
	}
	
	@Override
	public String getSubTitle()
	{
		return this.name+":"+getClassJsTitle(valTp) ;
	}
	
	@Override
	public String getSubIcon()
	{
		JsDef jsd = valTp.getAnnotation(JsDef.class) ;
		if(jsd!=null)
			return jsd.icon() ;
		return "icon_prop" ;
	}
	
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("n", name) ;
		jo.put("tp", this.getValTp().getCanonicalName()) ;
		jo.putOpt("t", title) ;
		jo.putOpt("d", desc) ;
		return jo ;
	}
}
