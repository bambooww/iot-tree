package org.iottree.core;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConnDev
{
	public static class Data
	{
		List<String> path = null ;
		
		String vt = null ;
		
		Object v = null ;
		
		public List<String> getPath()
		{
			return path ;
		}
		
		public String getValTp()
		{
			return this.vt ;
		}
		
		public Object getVal()
		{
			return v ;
		}
	}


	public static Data createData(String p,String vt,Object v,StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(p))
		{
			failedr.append("no path input") ;
			return null ;
		}
		List<String> ps = Convert.splitStrWith(p, ".") ;
		for(String p0:ps)
		{
			if(!Convert.checkVarName(p0, true, null))
			{
				failedr.append("path must be var name or var names") ;
				return null ;
			}
		}
			
		ValTP vtp = UAVal.getValTp(vt) ;
		if(vtp==null)
		{
			failedr.append("unknown vt ="+vt) ;
			return null ;
		}
		Data ret = new Data() ;
		ret.path = ps ;
		ret.vt = vt ;
		ret.v = v ;
		return ret ;
	}
	
	
	String name = null ;
	
	String title = null ;
	
	ArrayList<Data> datas = new ArrayList<>() ;
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public List<Data> getDatas()
	{
		return datas ;
	}
	
	public static ConnDev transFromJO(JSONObject jo,StringBuilder failedr)
	{
		String devname = jo.optString("dev_name") ;
		if(Convert.isNullOrEmpty(devname))
		{
			failedr.append("return device has no dev_name");
			return null;
		}
		if(!Convert.checkVarName(devname, true, failedr))
		{
			return null ;
		}
		String devtitle = jo.optString("dev_title") ;
		
		JSONArray ds = jo.optJSONArray("data") ;
		if(ds==null)
		{
			failedr.append("no data sub array") ;
			return null ;
		}
		
		ConnDev ret = new ConnDev() ;
		ret.name = devname ;
		ret.title = devtitle ;
		
		int dnum = ds.length();
		for(int j = 0 ; j < dnum ; j ++)
		{//{"n":"g1.v1","vt":"float","val":18.5},
			JSONObject tmpo = ds.getJSONObject(j) ;
			String p = tmpo.optString("n") ;
			String vt = tmpo.optString("vt") ;
			Object ov = tmpo.opt("v") ;
			Data d = createData(p,vt,ov,failedr) ;
			if(d==null)
				return null ;
			ret.datas.add(d) ;
		}
		return ret ;
	}
}
