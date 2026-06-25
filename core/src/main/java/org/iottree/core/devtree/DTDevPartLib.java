package org.iottree.core.devtree;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * DevPart library
 * 
 * @author jason.zhu
 *
 */
public class DTDevPartLib
{
	String libId = null ;
	
	String title ;
	
	String desc ;
	
	DTDevPartLib()
	{
		
	}
	
	DTDevPartLib(String t,String d)
	{
		this.libId = CompressUUID.createNewId() ;
		this.title = t ;
		this.desc = d ;
	}
	
	public String getLibId()
	{
		return this.libId ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public String getDesc()
	{
		return this.desc ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().putOpt("t",this.title).putOpt("d", this.desc) ;
	}
	
	public boolean fromJO(String libid,JSONObject jo)
	{
		if(Convert.isNullOrEmpty(libid))
			return false;
		DTDevPartLib ret = new DTDevPartLib() ;
		ret.libId = libid ;
		ret.title = jo.optString("t") ;
		ret.desc = jo.optString("d") ;
		return true;
	}
}
