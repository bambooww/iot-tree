package org.iottree.core.util.cer;

import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONObject;

public class CerItem
{
	String autoId ;
	
	String title ;
	
	String org ;
	
	String org_depart;
	
	CerItem()
	{}
	
	public CerItem(String tt,String org,String org_depart)
	{
		this.autoId = IdCreator.newSeqId() ;
		this.title = tt ;
		this.org = org ;
		this.org_depart = org_depart ;
	}
	
	public String getAutoId()
	{
		return this.autoId ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public String getOrg()
	{
		return this.org ;
	}
	
	public String getOrgDepart()
	{
		return this.org_depart ;
	}
	
	public JSONObject toIdxJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id",this.autoId) ;
		jo.put("t", this.title) ;
		jo.putOpt("org", this.org) ;
		jo.putOpt("org_d", this.org_depart) ;
		return jo ;
	}
	
	public boolean fromIdxJO(JSONObject jo)
	{
		this.autoId = jo.optString("id") ;
		if(Convert.isNullOrEmpty(this.autoId))
			return false;
		this.title = jo.optString("t") ;
		if(Convert.isNullOrEmpty(this.title))
			return false;
		this.org = jo.optString("org") ;
		this.org_depart = jo.optString("org_d") ;
		return true ;
	}
}
