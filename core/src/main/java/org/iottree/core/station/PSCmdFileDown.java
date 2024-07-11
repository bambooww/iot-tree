package org.iottree.core.station;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class PSCmdFileDown extends PSCmd
{
	public final static String CMD = "files_down" ;


	@Override
	public String getCmd()
	{
		return CMD;
	}
	
	public PSCmdFileDown asSelectedFiles(Map<String,List<String>> module2fs)
	{
		JSONObject jo = new JSONObject() ;
		for(Map.Entry<String,List<String>> m2fs:module2fs.entrySet())
		{
			String m = m2fs.getKey() ;
			List<String> fs = m2fs.getValue() ;
			if(fs==null||fs.size()<=0)
				continue ;
			
			JSONArray jarr = new JSONArray(fs) ;
			jo.put(m, jarr) ;
		}
		
		this.asCmdDataJO(jo) ;
		return this ;
	}
	
	@Override
	public void RT_onRecvedInStationLocal(StationLocal sl) throws Exception
	{
		JSONObject jo = this.getCmdDataJO() ;
		if(jo==null)
			return ;
		
		//String 
	}
	
	private void RT_downloadFiles(List<String> fs)
	{
		
	}
}
