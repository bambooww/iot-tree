package org.iottree.core.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UATag;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlVal.*;
import org.json.JSONObject;

/**
 * single value transformer
 * 
 * @author jason.zhu
 *
 */
public abstract class ValTranser
{
	
	
	public static ValTranser parseValTranser(UATag tag,String str)
	{
		if(Convert.isNullOrEmpty(str))
			return null ;
		
		JSONObject jo = new JSONObject(str) ;
		String n = jo.optString("_n") ;
		if(Convert.isNullOrEmpty(n))
			return null ;
		
		ValTranser vt = null ;
		switch(n)
		{
		case ValTransScaling.NAME:
			vt = new ValTransScaling() ;
			break;
		case ValTransJS.NAME:
			vt = new ValTransJS() ;
			break;
		default:
			break;
			
		}
		
		if(vt!=null)
		{
			if(!vt.fromTransJO(jo))
				return null ;
			vt.belongTo = tag ;
		}
		return null ;
	}
	
	private static ArrayList<ValTranser> TRANSERS = null;// new ArrayList<>() ;
	
	public static List<ValTranser> listValTransers()
	{
		if(TRANSERS!=null)
			return TRANSERS;
		ArrayList<ValTranser> ss = new ArrayList<>() ;
		ss.add(new ValTransScaling()) ;
		ss.add(new ValTransJS()) ;
		TRANSERS = ss ;
		return ss ;
	}
	/**
	 * value type after transfered
	 */
	XmlValType transValTp = null ;
	
	String units = null;
	
	private UATag belongTo = null ;
	
	private Exception lastTransErr = null ;
	
	public UATag getBelongTo()
	{
		return belongTo ;
	}
	
	public abstract String getName();
	
	public abstract String getTitle() ;//may multi language
	
	public abstract Object transVal(Object v) throws Exception;
	
	public abstract ValTP getTransValTP() ;
	
	public Exception getTransErr()
	{
		return lastTransErr;
	}
	
	protected final void setTransErr(Exception e)
	{
		this.lastTransErr = e ;
	}
	
	public JSONObject toTransJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("_n", this.getName()) ;
		if(units!=null)
			jo.put("_u",this.units);
		return jo;
	}
	
	
	
	public boolean fromTransJO(JSONObject m)
	{
		String n = m.optString("_n") ;
		if(!this.getName().equals(n))
			return false;
		this.units = m.optString("_u");
		return true;
	}
	
	public String toString()
	{
		return toTransJO().toString();
	}
	
	public String toTitleString()
	{
		return this.getTitle()+" - " + this.getTransValTP().getStr();
	}
}