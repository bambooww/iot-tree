package org.iottree.core.util;

import java.util.List;

import org.json.JSONObject;

/**
 * combined JSONata JSONPath
 *  
 * @author jason.zhu
 *
 */
public class JsonUtil
{
	public static Object getValByPath(JSONObject jo,String json_path)
	{
		if(jo==null || Convert.isNullOrEmpty(json_path))
			return null ;
		
		List<String> ss = Convert.splitStrWith(json_path, ".") ;
		return getValByPath(jo,ss) ;
	}
	
	public static Object getValByPath(JSONObject jo,List<String> ps)
	{
		if(ps==null)
			return null ;
		
		Object curv = jo ;
		int n =  ps.size() ;
		for(int i = 0 ; i < n ; i ++)
		{
			String s = ps.get(i) ;
			curv = jo.opt(s) ;
			if(curv==null)
				return null ;
			if(i==n-1)
				return curv ;
			if(!(curv instanceof JSONObject))
				return null ;
			jo = (JSONObject)curv ;
		}
		return curv ;
	}
}
