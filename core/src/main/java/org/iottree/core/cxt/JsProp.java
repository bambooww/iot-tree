package org.iottree.core.cxt;

import java.io.Writer;

import org.iottree.core.basic.JSObMap;
import org.json.JSONObject;

public class JsProp
{
	String name = null ;
	
	Object val = null ;
	//Class<?> valTp = null ;
	
	String title = null ;
	
	String desc = null ;
	
	public JsProp(String name,Object val,String title,String desc)
	{
		this.name = name ;
		//this.valTp = valtp;
		this.val = val;
		this.title = title ;
		this.desc = desc ;
	}
	
//	public JsProp(String name,Class<?> valtp,String title,String desc)
//	{
//		this.name = name ;
//		this.valTp = valtp;
//		this.title = title ;
//		this.desc = desc ;
//	}

	public String getName()
	{
		return name;
	}
	
	public Object getVal()
	{
		return val;
	}

	public Class<?> getValTp()
	{
		return val.getClass();//.valTp ;
	}

	public String getTitle()
	{
		return title;
	}


	public String getDesc()
	{
		return desc;
	}

	public void writeTree(String pid,Writer w) throws Exception
	{
		w.write("{\"id\":\"" + pid+this.name + "\"");
		
		w.write(",\"tp\": \"tag\"");
		w.write(",\"icon\": \"fa-solid fa-file fa-lg\"");
		w.write(",\"text\":\""+this.title+"\"");
		w.write(",\"state\": {\"opened\": false}");
		
		Class<?> c = getValTp() ;
		if(JSObMap.class.isAssignableFrom(c))
		{
			w.write(",\"children\":[");
			w.write("]");
		}
		
		
//		//
//		boolean bfirst = true;
//		for(CompCat cc:lib.getAllCats())
//		{
//			if (bfirst)
//				bfirst = false;
//			else
//				w.write(',');
//
//			w.write("{\"id\":\"" + lib.getId()+"-"+ cc.getId() + "\"");
//			
//			w.write(",\"tp\": \"tag\"");
//			w.write(",\"icon\": \"fa-regular fa-folder fa-lg\"");
//
//			w.write(",\"text\":\""+cc.getTitle()+"\"}");
//		}
		
		
		w.write("}");
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
