package org.iottree.core.station;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class PSCmdStationST extends PSCmd
{

	public final static String CMD ="station_st" ;
	
	@Override
	public String getCmd()
	{
		return CMD;
	}

	public PSCmdStationST asStationLocal(StationLocal sl)
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", sl.id) ;
		jo.put("local_ts", System.currentTimeMillis()) ;
		List<UAPrj> prjs = UAManager.getInstance().listPrjs() ;
		JSONArray prjst = new JSONArray() ;
		for(UAPrj prj:prjs)
		{
			String prjn = prj.getName() ;
			boolean brun = prj.RT_isRunning() ;
			JSONObject tmpjo = new JSONObject() ;
			tmpjo.put("n", prjn) ;
			//tmpjo.put(", value)
			tmpjo.put("run", brun) ;
			tmpjo.put("auto_start", prj.isAutoStart()) ;
			prjst.put(tmpjo) ;
		}
		jo.put("prjs", prjst) ;
		this.asCmdDataJO(jo) ;
		return this ;
	}
	
	//
	@Override
	public void RT_onRecvedInPlatform(PlatformWSServer.SessionItem si,PStation ps) throws Exception
	{
		JSONObject jo = this.getCmdDataJO() ;
		//System.out.println(" recv station "+ps.getId()+" "+jo) ;
		JSONArray jarr = jo.optJSONArray("prjs") ;
		ArrayList<PStation.PrjST> prjsts  = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				String prjn = tmpjo.optString("n") ;
				boolean brun = tmpjo.optBoolean("run",false) ;
				boolean bautostart = tmpjo.optBoolean("auto_start",false) ;
				if(Convert.isNullOrEmpty(prjn))
					continue ;
				PStation.PrjST prjst = new PStation.PrjST(prjn,brun,bautostart) ;
				prjsts.add(prjst) ;
			}
		}
		ps.RT_updateLocalState(si,prjsts);
	}
}
