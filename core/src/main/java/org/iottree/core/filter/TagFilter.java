package org.iottree.core.filter;

import java.util.HashMap;
import java.util.Map;

import org.iottree.core.UATag;
import org.json.JSONObject;

/**
 * all inner pm are and cond
 * @author jason.zhu
 *
 */
public class TagFilter extends Filter<UATag>
{
	boolean bIncSys = false;
	
	/**
	 * name=val val can be empty
	 */
	HashMap<String,String> extName2Val = null ;
	
	public TagFilter()
	{}
	
	
	
	@Override
	public String getTP()
	{
		return "tag";
	}
	
	@Override
	public boolean accept(UATag ob)
	{
		if(!bIncSys && ob.isSysTag())
			return false;
		
		if(this.extName2Val==null||this.extName2Val.size()<=0)
			return true ;
		
		for(Map.Entry<String, String> n2v:this.extName2Val.entrySet())
		{
			String n = n2v.getKey() ;
			String v = n2v.getValue() ;
			//ob.toPropNodeValJSON()
		}
		return false;
	}


	@Override
	public JSONObject toDefJO()
	{
		JSONObject jo = super.toDefJO();
		jo.put("inc_sys", this.bIncSys) ;
		jo.putOpt("ext_n2v", this.extName2Val) ;
		return jo ;
	}
	
	public boolean fromDefJO(JSONObject jo)
	{
		super.fromDefJO(jo) ;
		this.bIncSys = jo.optBoolean("inc_sys", false) ;
		JSONObject tmpjo = jo.optJSONObject("ext_n2v") ;
		if(tmpjo!=null)
		{
			HashMap<String,String> n2v = new HashMap<>() ;
			for(String n : tmpjo.keySet())
			{
				String v = tmpjo.optString(n, "") ;
				n2v.put(n, v) ;
			}
			this.extName2Val = n2v ;
		}
		
		return true ;
	}
}
