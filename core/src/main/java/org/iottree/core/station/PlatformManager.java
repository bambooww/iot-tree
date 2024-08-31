package org.iottree.core.station;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.iottree.core.Config;
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
public class PlatformManager
{
	private static PlatformManager instance = null ;
	
	public static PlatformManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		File f = Config.getConfFile("platform.json") ;
		if(!f.exists())
			throw new RuntimeException("no platfrom.json found") ;
		
		synchronized(PlatformManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new PlatformManager() ;
			return instance ;
		}
	}
	
	private static Boolean bInPlatform = null ;
	
	/**
	 * 
	 * @return
	 */
	public static boolean isInPlatform()
	{
		if(bInPlatform!=null)
			return bInPlatform;
		
		File f = Config.getConfFile("platform.json") ;
		bInPlatform = f.exists() ;
		return bInPlatform;
	}
	
	private LinkedHashMap<String,PStation> id2station = null;// new LinkedHashMap<>() ;
	
	private HashMap<String,Long> unknownStation2DT = new HashMap<>() ;
	
	private File prjUpDir = new File(Config.getDataDynDirBase()+"prj_up/") ;//prj_up_dir
	
	private File rtDataDir = new File(Config.getDataDynDirBase()+"rt_data/") ;
	
	private JSONObject rtDataDB = null ;
	
	private HashMap<String,PlatformSaver> prj2saver = new HashMap<>() ;
	
	private PlatformManager()
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
	
	private LinkedHashMap<String,PStation> loadStations() throws IOException
	{
		LinkedHashMap<String,PStation> rets = new LinkedHashMap<>() ;
		File f = Config.getConfFile("platform.json") ;
		if(f.exists())
		{
			JSONObject jo = Convert.readFileJO(f) ;
			JSONArray jarr = jo.optJSONArray("stations") ;
			if(jarr!=null)
			{
				int n = jarr.length() ;
				for(int i = 0 ; i < n ; i ++)
				{
					JSONObject tmpjo = jarr.getJSONObject(i) ;
					PStation ps = PStation.fromJO(tmpjo) ;
					if(ps==null)
						continue ;
					rets.put(ps.id,ps) ;
				}
			}
			
			String prjupdir=jo.optString("prj_up_dir") ;
			if(Convert.isNotNullEmpty(prjupdir))
			{
				prjUpDir = Config.parseDir(prjupdir) ;
				if(!prjUpDir.exists())
					prjUpDir.mkdirs() ;
			}
			
			String rtdatadir=jo.optString("rt_data_dir") ;
			if(Convert.isNotNullEmpty(rtdatadir))
			{
				rtDataDir = Config.parseDir(rtdatadir) ;
				if(!rtDataDir.exists())
					rtDataDir.mkdirs() ;
			}
			
			rtDataDB = jo.optJSONObject("rt_data_db") ;
		}
		return rets ;
	}
	
	public File getPrjUpDir()
	{
		return this.prjUpDir ;
	}
	
	public File getRTDataDir()
	{
		return this.rtDataDir ;
	}
	
	public JSONObject getRTDataDB()
	{
		return this.rtDataDB ;
	}
	
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
	void onRecvedRTData(PStation pstation,String prjname,String key,JSONObject rt_jo,boolean b_his) throws Exception
	{
		if(!b_his)
			pstation	.RT_onRecvedRTData(prjname,key,rt_jo,b_his) ;
		
		PlatformSaver ps = getSaver(pstation,prjname) ;
		ps.storeJson(rt_jo.toString(), key);
	}
	
	void onRecvedHisRTDatas(PStation pstation,String prjname,HashMap<String,JSONObject> key2rt_jo) throws Exception
	{
		PlatformSaver ps = getSaver(pstation,prjname) ;
		HashMap<String,String> k2txt = new HashMap<>() ;
		for(Map.Entry<String, JSONObject> k2j:key2rt_jo.entrySet())
		{
			k2txt.put(k2j.getKey(),k2j.getValue().toString()) ;
		}
		ps.storeJsonMulti(k2txt);
	}
	
	private PlatformSaver getSaver(PStation pstation,String prjname) throws Exception
	{
		String n = pstation.getId()+"_"+prjname ;
		PlatformSaver ps = prj2saver.get(n) ;
		if(ps!=null)
			return ps;
		
		synchronized(this)
		{
			ps = prj2saver.get(n) ;
			if(ps!=null)
				return ps;
			
			ps = new PlatformSaver(n) ;
			prj2saver.put(n,ps) ;
			return ps ;
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
