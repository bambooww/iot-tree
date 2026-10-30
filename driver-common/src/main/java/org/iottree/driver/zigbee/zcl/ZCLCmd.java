package org.iottree.driver.zigbee.zcl;

import java.util.*;

import org.iottree.core.util.Convert;
import org.iottree.core.util.IJoIn;
import org.iottree.driver.zigbee.zcl.util.TagDef;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ZCL Command
 * 
 * @author jason.zhu
 */
public class ZCLCmd implements IJoIn
{
	int id ;
	
	String name ;
	
	ArrayList<ZCLParam> parameters = null ;
	
	Integer response = null ; //?:number
	
	boolean required = false;
	
	TagDef tagDef = null ;
	
	public int getID()
	{
		return this.id ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public List<ZCLParam> getParemeters()
	{
		return this.parameters ;
	}
	
	public ZCLParam getParamByIdx(int idx)
	{
		if(this.parameters==null)
			return null;
		if(idx<0)
			return null ;
		if(this.parameters.size()<=idx)
			return null ;
		return this.parameters.get(idx) ;
	}
	
	public Integer getResponse()
	{
		return this.response ;
	}
	
	public boolean isRequired()
	{
		return this.required ;
	}
	
	public TagDef getTagDef()
	{
		return this.tagDef ;
	}
	
	public JSONObject toJO()
	{
		JSONObject r = new JSONObject().put("id", this.id).put("name", this.name);
		if(this.parameters!=null)
		{
			JSONArray jarr = new JSONArray() ;
			r.put("parameters", jarr) ;
			for(ZCLParam a:this.parameters)
			{
				jarr.put(a.toJO()) ;
			}
		}
		
		if(this.tagDef!=null)
			r.put("__tag", this.tagDef.toJO()) ;
		
		return r ;
	}
	
	@Override
	public boolean fromJO(JSONObject jo, StringBuilder failedr)
	{
		this.id = jo.optInt("ID") ;
		this.name = jo.optString("name") ;
		if(this.id<0 || Convert.isNullOrEmpty(this.name))
			return false;
		
		JSONArray params = jo.optJSONArray("parameters") ;
		if(params!=null)
		{
			int n = params.length() ;
			ArrayList<ZCLParam> pms = new ArrayList<>(n) ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject pmjo = params.getJSONObject(i) ;
				ZCLParam pm = new ZCLParam(i) ;
				StringBuilder failedr0 = new StringBuilder() ;
				if(!pm.fromJO(pmjo, failedr0))
					return false;
				pms.add(pm) ;
			}
			this.parameters = pms ;
		}
		
		this.required = jo.optBoolean("required",false) ;
		
		this.tagDef = TagDef.fromObj(null,jo.opt("__tag")) ;
		
		return true;
	}

}
