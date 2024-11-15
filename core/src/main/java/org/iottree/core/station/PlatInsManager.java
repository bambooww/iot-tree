package org.iottree.core.station;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.Config;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * a platform can has many station nodes,every station is run remote and controlled by platform
 * 
 *   1) station's running state can be controlled by platform
 *   2) station's project can be add or update by platform
 *   3) station's running data can be syn to platform
 *   4) station's iot-tree self update can be set by platform
 *   
 * 
 * @author jason.zhu
 */
public class PlatInsManager
{
	private static PlatInsManager instance = null ;
	
	public static PlatInsManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
//		File f = Config.getConfFile("platform.json") ;
//		if(!f.exists())
//			throw new RuntimeException("no platfrom.json found") ;
		
		synchronized(PlatInsManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new PlatInsManager() ;
			return instance ;
		}
	}
	
//	private static Boolean bInPlatform = null ;
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public static boolean isInPlatform()
//	{
//		if(bInPlatform!=null)
//			return bInPlatform;
//		
//		File f = Config.getConfFile("platform.json") ;
//		bInPlatform = f.exists() ;
//		return bInPlatform;
//	}
	
	private LinkedHashMap<String,PStation> id2station = null;// new LinkedHashMap<>() ;
	
	private HashMap<String,Long> unknownStation2DT = new HashMap<>() ;
	
	private File prjUpDir = new File(Config.getDataDynDirBase()+"prj_up/") ;//prj_up_dir
	
	//private File rtDataDir = new File(Config.getDataDynDirBase()+"rt_data/") ;
	
	//private JSONObject rtDataDB = null ;
	
	private HashMap<String,PlatInsSaver> prj2saver = new HashMap<>() ;
	
	private PlatInsManager()
	{}
	
	public Map<String,PStation> getPStationMap()  //throws IOException
	{
		if(id2station!=null)
			return id2station;
		
		try
		{
			id2station = loadStations() ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace(); 
		}
		return id2station;
	}
	
	public List<PStation> listStations()
	{
		Map<String,PStation> id2sts = getPStationMap() ;
		ArrayList<PStation> rets = new ArrayList<>(id2sts.size()) ;
		rets.addAll(id2sts.values()) ;
		return rets ;
	}
	
	private void saveStations() throws IOException
	{
		Map<String,PStation> id2sts = getPStationMap() ;
		JSONArray jarr = new JSONArray() ;
		for(PStation ps:id2sts.values())
		{
			jarr.put(ps.toJO()) ;
		}
		File svf = new File(Config.getDataDirBase()+"/pstation/pstations.json") ;
		if(!svf.getParentFile().exists())
			svf.getParentFile().mkdirs() ;
		Convert.writeFileTxt(svf, jarr.toString());
	}
	
	private LinkedHashMap<String,PStation> loadStations() throws IOException
	{
		LinkedHashMap<String,PStation> rets = new LinkedHashMap<>() ;
		
		File svf = new File(Config.getDataDirBase()+"/pstation/pstations.json") ;
		if(!svf.exists())
			return rets ;
		
		String txt = Convert.readFileTxt(svf) ;
		if(Convert.isNullOrEmpty(txt))
			return rets ;
		
		JSONArray jarr = new JSONArray(txt) ;
		int n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			PStation ps = PStation.fromJO(jo) ;
			if(ps==null)
				continue ;
			rets.put(ps.getId(), ps) ;
		}
		return rets ;
		
//		for(UAPrj prj :UAManager.getInstance().listPrjs())
//		{
//			PStation ps = prj.getPrjPStationInsDef() ;
//			if(ps==null)
//				continue ;
//			rets.put(ps.id,ps);
//		}
		
//		File f = Config.getConfFile("platform.json") ;
//		if(f.exists())
//		{
//			JSONObject jo = Convert.readFileJO(f) ;
//			JSONArray jarr = jo.optJSONArray("stations") ;
//			if(jarr!=null)
//			{
//				int n = jarr.length() ;
//				for(int i = 0 ; i < n ; i ++)
//				{
//					JSONObject tmpjo = jarr.getJSONObject(i) ;
//					PStation ps = PStation.fromJO(tmpjo) ;
//					if(ps==null)
//						continue ;
//					rets.put(ps.id,ps) ;
//				}
//			}
			
//			String prjupdir=jo.optString("prj_up_dir") ;
//			if(Convert.isNotNullEmpty(prjupdir))
//			{
//				prjUpDir = Config.parseDir(prjupdir) ;
//				if(!prjUpDir.exists())
//					prjUpDir.mkdirs() ;
//			}
			
//			String rtdatadir=jo.optString("rt_data_dir") ;
//			if(Convert.isNotNullEmpty(rtdatadir))
//			{
//				rtDataDir = Config.parseDir(rtdatadir) ;
//				if(!rtDataDir.exists())
//					rtDataDir.mkdirs() ;
//			}
			
//			rtDataDB = jo.optJSONObject("rt_data_db") ;
		
	}
	
	public void setStation(String id,String title,String key) throws IOException
	{
		Map<String,PStation> id2ps = this.getPStationMap() ;
		PStation ps = id2ps.get(id) ;
		if(ps==null)
		{
			ps = new PStation(id,title,key) ;
			id2ps.put(id, ps) ;
		}
		else
		{
			ps.title = title ;
			ps.key = key ;
		}
		this.saveStations();
	}
	
	public PStation delStation(String id) throws IOException
	{
		Map<String,PStation> id2ps = this.getPStationMap() ;
		PStation ps = id2ps.get(id) ;
		if(ps==null)
			return null ;
		id2ps.remove(id) ;
		this.saveStations();
		return ps ;
	}
	
	public File getPrjUpDir()
	{
		return this.prjUpDir ;
	}
	
//	public File getRTDataDir()
//	{
//		return this.rtDataDir ;
//	}
	
//	public JSONObject getRTDataDB()
//	{
//		return this.rtDataDB ;
//	}
	
	public PStation getStationById(String stationid)
	{
		return getPStationMap().get(stationid) ;
	}
	
	public void fireUnknownStation(String stationid)
	{
		unknownStation2DT.put(stationid,System.currentTimeMillis()) ;
	}
	
	public Map<String,Long> getUnknownStations()
	{
		return unknownStation2DT ;
	}
	
	/**
	 * 从Station中，接收到的项目打包内容。需要专门临时保存，并且通过特定的管理界面设定是否要加入到
	 * 平台中，或者是替换平台中已经存在的项目——此时，会引起一系列的反应：
	 * 
	 * 1）如果顶层的很多应用和规则关联了之前的项目树，并且此项目树结构有所调整，那么就需要把相关应用和规则跟着调整
	 * 2）如果涉及到数据储存，则新旧数据之间的关联如何处理等等
	 *  
	 * @param prjname
	 * @param zipped_prj
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	void onRecvedStationPrj(PStation pstation,String prjname,byte[] zipped_prj) throws FileNotFoundException, IOException
	{
		String fn = pstation.getId()+"."+prjname+"_"+System.currentTimeMillis()+".zip" ;
		File outf = new File(prjUpDir,fn) ;
		try(FileOutputStream fos = new FileOutputStream(outf);)
		{
			fos.write(zipped_prj);
		}
	}
	
	/**
	 * 从Station接收到RTData，此函数会被调用
	 * @param prjname
	 * @param rt_jo
	 * @throws Exception 
	 */
	void onRecvedRTData(PStation pstation,UAPrj prj,String key,byte[] zipdata,JSONObject rt_jo,boolean b_his) throws Exception
	{
		if(!b_his)
		{
			if (rt_jo != null)
				RT_updateCxtDyn(prj, rt_jo);
			pstation	.RT_onRecvedRTData(prj,key,rt_jo,b_his) ;
		}
		
		PlatInsSaver ps = getSaver(pstation,prj,true) ;
		if(ps!=null)
		{
			ps.BLOB_storeJson(zipdata, key);
		}
	}
	
	void onRecvedHisRTDatas(PStation pstation,UAPrj prj,HashMap<String,byte[]> key2rt_jo) throws Exception
	{
		PlatInsSaver ps = getSaver(pstation,prj,true) ;
		HashMap<String,byte[]> k2txt = new HashMap<>() ;
		for(Map.Entry<String, byte[]> k2j:key2rt_jo.entrySet())
		{
			k2txt.put(k2j.getKey(),k2j.getValue()) ;
		}
		ps.BLOB_storeJsonMulti(k2txt);
	}
	
	private PlatInsSaver getSaver(PStation pstation,UAPrj prj,boolean blob) throws Exception
	{
		//String n = pstation.getId()+"_"+prjname ;
		String n = prj.getName();
		if(blob)
			n += "_b" ;
		PlatInsSaver ps = prj2saver.get(n) ;
		if(ps!=null)
			return ps;
		
		synchronized(this)
		{
			ps = prj2saver.get(n) ;
			if(ps!=null)
				return ps;
			
			ps = PlatInsSaver.createFromPrjTable(prj, n) ;
			if(ps==null)
				return null ;
			prj2saver.put(n,ps) ;
			return ps ;
		}
	}
	
	/**
	 * 直接更新内存中标签数据值
	 * 
	 * @param p
	 * @param curcxt
	 */
	private void RT_updateCxtDyn(UANodeOCTagsCxt p, JSONObject curcxt)
	{
		JSONArray jos = curcxt.optJSONArray("tags");
		if (jos != null)
		{
			for (int i = 0, n = jos.length(); i < n; i++)
			{
				JSONObject tg = jos.getJSONObject(i);
				String name = tg.getString("n");
				UATag tag = p.getTagByName(name);
				if (tag == null) // || tag.isSysTag())
					continue;
				// var tagp =p+n ;
				boolean bvalid = tg.optBoolean("valid", false);
				long dt = tg.optLong("dt", -1);
				long chgdt = tg.optLong("chgdt", -1);

				Object ov = tg.opt("v");
				String strv = "";
				if (ov != null && ov != JSONObject.NULL)
					strv = "" + ov;
				// set to cxt
				ov = UAVal.transStr2ObjVal(tag.getValTp(), strv);
				UAVal uav = new UAVal(bvalid, ov, dt, chgdt);
				// tag.RT_setValStr(strv, true);
				tag.RT_setUAValOnlyAlert(uav);

				//setToBuf(tag, uav);
			}
		}

		JSONArray subs = curcxt.optJSONArray("subs");
		if (subs != null)
		{
			for (int i = 0, n = subs.length(); i < n; i++)
			{
				JSONObject sub = subs.getJSONObject(i);

				String subn = sub.getString("n");

				UANode uan = p.getSubNodeByName(subn);
				if (uan == null)
					continue;
				if (!(uan instanceof UANodeOCTagsCxt))
					continue;

				RT_updateCxtDyn((UANodeOCTagsCxt) uan, sub);
			}
		}
	}
	
//	public JSONObject transPrjTreeJO(String prjname)
//	{
//		UAPrj prj = 
//		JSONObject jo = new JSONObject() ;
//		
//		return jo ;
//	}
	
	public JSONObject RT_toStatusJO()
	{
		JSONObject jo = new JSONObject() ;
		
		JSONArray jarr = new JSONArray() ;
		for(PStation st:getPStationMap().values())
		{
			JSONObject tmpjo = st.RT_toStatusJO() ;
			jarr.put(tmpjo) ;
		}
		jo.put("stations", jarr) ;
		
		return jo ;
	}
}
