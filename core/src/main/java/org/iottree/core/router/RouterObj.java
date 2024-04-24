package org.iottree.core.router;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * support txt or jsonobject or jsonarray 
 * 
 * @author jason.zhu
 *
 */
public class RouterObj
{
	boolean bTxtOnly = false ;
	String txt = null ;
	
	JSONObject jo = null ;
	JSONArray jarr = null ;
	
	public RouterObj(String txt,boolean b_txt_only)
	{
		this.txt = txt ;
		this.bTxtOnly = b_txt_only ;
	}
	
	public RouterObj(Object obj)
	{
		if(obj instanceof JSONObject)
		{
			jo = (JSONObject)obj ;
			return ;
		}
		if(obj instanceof JSONArray)
		{
			jarr = (JSONArray)obj ;
			return ;
		}
		if(obj instanceof String)
		{
			this.txt = (String)obj ;
			String ss = this.txt.trim() ;
			if(ss.startsWith("{"))
			{
				try
				{
					JSONObject tmpjo = new JSONObject(ss) ;
					this.jo = tmpjo ;
				}
				catch(Exception ee)
				{
					this.bTxtOnly = true ;
				}
			}
			else if(ss.startsWith("["))
			{
				try
				{
					JSONArray tmpjarr = new JSONArray(ss) ;
					this.jarr = tmpjarr ;
				}
				catch(Exception ee)
				{
					this.bTxtOnly = true ;
				}
			}
			return ;
		}
		
		throw new IllegalArgumentException("unknown obj tp="+obj.getClass().getCanonicalName()) ;
	}
	
	public boolean isTxtOnly()
	{
		return this.bTxtOnly ;
	}
	
	public String getTxt()
	{
		if(this.txt!=null || this.bTxtOnly)
			return this.txt ;
		
		if(jo!=null)
			return jo.toString() ;
		
		if(jarr!=null)
			return jarr.toString() ;
		
		return null ;
	}
	
	public JSONObject getJSONObject()
	{
		return jo ;
	}
	
	public JSONArray getJSONArray()
	{
		return jarr ;
	}
}
