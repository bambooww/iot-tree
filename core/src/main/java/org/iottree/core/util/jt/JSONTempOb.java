package org.iottree.core.util.jt;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.jt.JSONTemp.PropItem;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 依附于JSONTemp 定义内部可以重复出现的json对象格式
 * 
 * 此定义只允许一层Prop
 * @author zzj
 *
 */
public class JSONTempOb
{
	public static JSONTempOb loadFromJSON(JSONObject jo)
	{
		JSONTempOb t = new JSONTempOb();
		t.name = jo.optString("name") ;
		t.title = jo.optString("title") ;
		JSONArray jos = jo.optJSONArray("props") ;
		if(jos!=null)
		{
			int len = jos.length() ;
			for(int i = 0 ; i < len ; i ++)
			{
				JSONObject tmpjo = jos.getJSONObject(i) ;
				PropItem pi = new PropItem() ;
				pi.fromJSON(tmpjo) ;
				t.propItems.add(pi) ;
			}
		}
		
		return t;
	}
	
	String name = null ;
	
	String title = null ;
	
	ArrayList<PropItem> propItems = new ArrayList<>() ;
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public ArrayList<PropItem> getPropItems()
	{
		return this.propItems ;
	}
}
