package org.iottree.core.station;

import org.json.JSONObject;

public class PSCmdPlatformST extends PSCmd
{
	public final static String CMD = "platform_st" ;
	
	
	@Override
	public String getCmd()
	{
		return CMD;
	}

	public PSCmdPlatformST asPlatform(PlatformManager pm)
	{
		JSONObject jo = new JSONObject() ;
		jo.put("ts", System.currentTimeMillis()) ;
		this.asCmdDataJO(jo) ;
		
		return this ;
	}
	
	@Override
	public void RT_onRecvedInStationLocal(StationLocal sl) throws Exception
	{
		JSONObject jo = this.getCmdDataJO() ;
		if(jo==null)
			return ;
		long platform_ts = jo.optLong("ts",-1) ;
		if(platform_ts>0)
		{
			if(Math.abs(platform_ts-System.currentTimeMillis())>10000)
			{//change local ts
				
			}
		}
	}
}
