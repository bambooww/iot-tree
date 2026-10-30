package org.iottree.driver.zigbee.devices;

import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 厂家特殊定义
 *  1 基于fac_name 和model定义非基本cluster，供电来源，数据转换等内容
 * 
 * 
 * @author jason.zhu
 *
 */
public class VendorDef
{
	String facName ;
	
	LinkedHashMap<String,ModelDef> name2vendor = new LinkedHashMap<>() ;
	
	public VendorDef(String fac_n)
	{
		this.facName = fac_n ;
	}
	
	public String getFacName()
	{
		return this.facName ;
	}
	
	
	public boolean fromJO(JSONObject jo)
	{
		JSONArray md_jarr = jo.optJSONArray("models") ;
		if(md_jarr!=null)
		{
			int n = md_jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject mjo = md_jarr.getJSONObject(i) ;
				ModelDef md = ModelDef.fromJO(mjo) ;
			}
		}
		return true ;
	}
}
