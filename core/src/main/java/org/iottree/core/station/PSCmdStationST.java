package org.iottree.core.station;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class PSCmdStationST extends PSCmd
{

	public final static String CMD ="station_st" ;
	
	
	private HashMap<String,JSONObject> name2prjst = null ;
	
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
		jo.put("can_write", sl.isCanPlatformWrite()) ;
		List<UAPrj> prjs = UAManager.getInstance().listPrjs() ;
		JSONArray prjst = new JSONArray() ;
		HashMap<String,JSONObject> n2prjst = new HashMap<>() ;
		for(UAPrj prj:prjs)
		{
			String prjn = prj.getName() ;
			boolean brun = prj.RT_isRunning() ;
			JSONObject tmpjo = new JSONObject() ;
			tmpjo.put("n", prjn) ;
			//tmpjo.put(", value)
			tmpjo.put("run", brun) ;
			tmpjo.put("auto_start", prj.isAutoStart()) ;
			
			StationLocal.PrjSynPm synpm = sl.getPrjSynPM(prjn) ;
			if(synpm!=null)
			{
				tmpjo.put("data_syn_en",synpm.dataSynEn);
				tmpjo.put("data_syn_intv",synpm.dataSynIntv);
				tmpjo.put("failed_keep", synpm.failedKeep) ;
				tmpjo.put("keep_max_len", synpm.keepMaxLen) ;
				if(synpm.failedKeep)
				{
					StationLocSaver pls = StationLocSaver.getSaver(prjn) ;
					if(pls!=null)
					{
						tmpjo.put("cur_keep_len", pls.getSavedNum()) ;
					}
				}
			}
			prjst.put(tmpjo) ;
			n2prjst.put(prjn,tmpjo) ;
		}
		name2prjst = n2prjst ;
		jo.put("prjs", prjst) ;
		this.asCmdDataJO(jo) ;
		return this ;
	}
	
	public boolean chkChg(StationLocal sl)
	{
		if(name2prjst==null)
			return true ;
		List<UAPrj> prjs = UAManager.getInstance().listPrjs() ;
		for(UAPrj prj:prjs)
		{
			String prjn = prj.getName() ;
			JSONObject c_prjst = this.name2prjst.get(prjn) ;
			if(c_prjst==null)
				return true ;
			
			boolean brun = prj.RT_isRunning() ;
			if(!c_prjst.has("run")) return true ;
			if(brun!=c_prjst.getBoolean("run"))
				return true ;
			
			if(!c_prjst.has("auto_start") ||  prj.isAutoStart()!=c_prjst.getBoolean("auto_start"))
				return true ;
			
			StationLocal.PrjSynPm synpm = sl.getPrjSynPM(prjn) ;
			if(synpm!=null)
			{
				if(!c_prjst.has("data_syn_en") || !c_prjst.has("data_syn_intv"))
					return true ;
				if(c_prjst.getBoolean("data_syn_en")!=synpm.dataSynEn)
					return true ;
				
				if(c_prjst.getLong("data_syn_intv")!=synpm.dataSynIntv)
					return true ;
			}
		}
		return false;
	}
	
	//
	@Override
	public void RT_onRecvedInPlatform(PlatInsWSServer.SessionItem si,PStation ps) throws Exception
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
				boolean datasyn_en = tmpjo.optBoolean("data_syn_en", false) ;
				long datasyn_intv = tmpjo.optLong("data_syn_intv", 10000) ;
				boolean failed_keep = tmpjo.optBoolean("failed_keep",false) ;
				long keep_max_len = tmpjo.optLong("keep_max_len",3153600) ;
				PStation.PrjST prjst = new PStation.PrjST(prjn,brun,bautostart,datasyn_en,datasyn_intv,failed_keep,keep_max_len) ;
				prjsts.add(prjst) ;
			}
		}
		ps.RT_updateLocalState(si,prjsts);
	}
}
