package org.iottree.core.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONArray;
import org.json.JSONObject;

public class StoreManager
{
	static ILogger log = LoggerManager.getLogger(StoreManager.class);

	static HashMap<String, StoreManager> prj2ins = new HashMap<>();

	public static StoreManager getInstance(String prjid)
	{
		StoreManager ins = prj2ins.get(prjid);
		if (ins != null)
			return ins;

		synchronized (StoreManager.class)
		{
			ins = prj2ins.get(prjid);
			if (ins != null)
				return ins;

			ins = new StoreManager(prjid);
			prj2ins.put(prjid, ins);
			return ins;
		}
	}
	
	static HashMap<String,SourceJDBC> name2innerSor = new HashMap<>() ;
	
	static LinkedHashMap<String, Source> name2sor = null;//
	
	public static SourceJDBC getInnerSource(String name)
	{
		SourceJDBC innersor = name2innerSor.get(name) ;
		if(innersor!=null)
			return innersor ;
		
		synchronized(StoreManager.class)
		{
			innersor = name2innerSor.get(name) ;
			if(innersor!=null)
				return innersor ;
			
			innersor = new SourceJDBC() ;
			innersor.setJDBCInfo("sqlite","", -1, "_inner_"+name, "", "") ;
			innersor.id="_inner" ;
			innersor.name="_inner" ;
			innersor.title="Inner" ;
			
			name2innerSor.put(name,innersor) ;
			return innersor ;
		}
	}
	
	private static LinkedHashMap<String, Source> getName2Source()
	{
		if(name2sor!=null)
			return name2sor ;
		
		try
		{
			name2sor = loadSors();
			//name2sor.
			return name2sor;
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			log.error("load Sors err",e);
			return null ;
		}
	}
	
	public static List<Source> listSources()
	{
		ArrayList<Source> rets = new ArrayList<>();
		rets.addAll(getName2Source().values());
		return rets;
	}

	public static Source getSourceById(String id)
	{
		for (Source st : getName2Source().values())
		{
			if (st.getId().equals(id))
				return st;
		}
		return null;
	}

	public static Source getSourceByName(String name)
	{
		return getName2Source().get(name);
	}

	public static void setSource(Source st, boolean bsave) throws Exception
	{
		String stname = st.getName() ;
		StringBuilder failedr = new StringBuilder() ;
		
		
		if(!Convert.checkVarName(stname, true, failedr))
		{
			throw new Exception(failedr.toString());
		}
		
		if(Convert.isNullOrEmpty(st.getId()))
		{
			st.id = CompressUUID.createNewId() ;
		}
		
		if(!st.checkValid(failedr))
			throw new Exception(failedr.toString());
		
		Source oldst = getSourceByName(st.getName()) ;
		if(oldst!=null && !oldst.getId().equals(st.getId()))
		{
			throw new Exception("store with name="+st.getName()+" is existed") ;
		}
		
		
		getName2Source().put(st.getName(), st);
		if (bsave)
			saveSors();
	}
	
	public static void setSourceByJO(JSONObject jo) throws Exception
	{
		//JSONObject jo = new JSONObject(jstr) ;
		String tp = jo.getString("_tp") ;
		Source nsor = Source.newInsByTp(tp) ;
		if(nsor==null)
		{
			throw new IllegalArgumentException("no source type found with "+tp) ;
		}
		//SourceJDBC st = new SourceJDBC();
		DataTranserJSON.injectJSONToObj(nsor, jo) ;
		StoreManager.setSource(nsor, true);
	}
	
	public static boolean delSourceById(String id) throws Exception
	{
		Source sor = getSourceById(id) ;
		if(sor==null)
		{
			return false;
		}
		name2sor.remove(sor.getName()) ;
		saveSors() ;
		return true ;
	}
	
	private static File getSorDir()
	{
		return new File(Config.getDataDirBase(),"store/") ; 
	}

	public static void saveSors() throws Exception
	{
		XmlData xd = new XmlData();
		List<XmlData> xds = xd.getOrCreateSubDataArray("sources");
		for (Source st : getName2Source().values())
		{
			XmlData xd0 = DataTranserXml.extractXmlDataFromObj(st);
			xd0.setParamValue("_tp", st.getSorTp());
			xds.add(xd0);
		}
		
		File storedir = getSorDir() ;
		if(!storedir.exists())
			storedir.mkdirs() ;
		
		File f = new File(getSorDir(), "store_sors.xml");
		XmlData.writeToFile(xd, f);
	}

	private static LinkedHashMap<String, Source> loadSors() throws Exception
	{
		LinkedHashMap<String, Source> n2st = new LinkedHashMap<>();
		
		File f = new File(getSorDir(), "store_sors.xml");
		if (!f.exists())
			return n2st ;
		
		XmlData xd = XmlData.readFromFile(f);
		List<XmlData> xds = xd.getSubDataArray("sources");
		if (xds == null)
			return n2st;
		for (XmlData tmpxd : xds)
		{
			String tp = tmpxd.getParamValueStr("_tp");
			if (Convert.isNullOrEmpty(tp))
				continue;
			
			Source o = Source.newInsByTp(tp) ;
			if (!DataTranserXml.injectXmDataToObj(o, tmpxd))
				continue;
			if(Convert.isNullOrEmpty(o.getId()))
				o.id = CompressUUID.createNewId() ;
			n2st.put(o.getName(), o);
		}
		return n2st;
	}
	
	


	UAPrj prj = null;

	File prjDir = null;

	private LinkedHashMap<String,StoreHandler> id2handlers = null ;

	private StoreManager(String prjid)
	{
		prj = UAManager.getInstance().getPrjById(prjid);
		if (prj == null)
			throw new IllegalArgumentException("no prj found");
		prjDir = prj.getPrjSubDir();

		
	}
	
	private LinkedHashMap<String, StoreHandler> getId2Handler()
	{
		if(id2handlers!=null)
			return id2handlers ;
		
		try
		{
			id2handlers = loadHandlers();
			return id2handlers;
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			log.error("loadHandlers err",e);
			return null ;
		}
	}

	public List<StoreHandler> listHandlers()
	{
		ArrayList<StoreHandler> rets = new ArrayList<>();
		rets.addAll(getId2Handler().values());
		return rets;
	}
	

	public StoreHandler getHandlerById(String id)
	{
		return getId2Handler().get(id) ;
	}
	
	public StoreHandler getHandlerByName(String name)
	{
		for(StoreHandler sh: getId2Handler().values())
		{
			if(name.equals(sh.getName()))
				return sh ;
		}
		return null ;
	}
	
	public void setHandler(StoreHandler ah) throws Exception
	{
		String n = ah.getName() ;
		if(Convert.isNullOrEmpty(n))
			throw new IllegalArgumentException("Handler name cannot be null or empty") ;
		
		if(Convert.isNullOrEmpty(ah.getId()))
		{
			ah.id = CompressUUID.createNewId() ;
		}
		
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(n, true, sb))
			throw new IllegalArgumentException(sb.toString()) ;
		
		StoreHandler old_ah = this.getHandlerByName(n) ;
		if(old_ah!=null && !old_ah.getId().equals(ah.getId()))
		{
			throw new IllegalArgumentException("Handler with name="+n+" is already existed!") ;
		}
		ah.prj = this.prj ;
		this.getId2Handler().put(ah.getId(), ah) ;
		this.saveHandlers();
	}
	
	public void setHandlerByJSON(JSONObject jo) throws Exception
	{
		String id = jo.optString("id") ;
		//ah.ou
		
		StoreHandler ah = null;
		if(Convert.isNotNullEmpty(id))
		{
			ah = this.getHandlerById(id) ;
			if(ah==null)
				throw new Exception("no handler with id="+id) ;
		}
		else
		{
			String tp = jo.getString("_tp");
			ah = StoreHandler.newInsByTp(tp);
		}
		ah.fromJO(jo,false,false);
		//DataTranserJSON.injectJSONToObj(ah, jo) ;
		this.setHandler(ah);
	}
	
	public void setHandlerSelTagIds(String hid,List<String> tag_nps) throws Exception
	{
		StoreHandler ah = this.getHandlerById(hid) ;
		if(ah==null)
			throw new Exception("no handler with id="+hid) ;
		ah.setSelectTags(tag_nps);
		this.saveHandlers();
	}
//	public void setHandlerInOutIds(JSONArray jarr) throws Exception
//	{
//		int len = jarr.length() ;
//		boolean bdirty=false;
//		for(int i = 0 ; i < len ; i ++)
//		{
//			JSONObject jo = jarr.getJSONObject(i) ;
//			String id = jo.getString("id") ;
//			String alert_uids = jo.getString("alert_uids") ;
//			String out_ids = jo.getString("out_ids") ;
//			StoreHandler ah = this.getHandlerById(id) ;
//			if(ah==null)
//				continue ;
//			//ah.setInOutIds(alert_uids, out_ids);
//			bdirty=true;
//		}
//		
//		if(bdirty)
//			this.saveHandlers();
//	}
	
	public boolean delHandlerById(String id) throws Exception
	{	
		StoreHandler ao = this.getHandlerById(id) ;
		if(ao==null)
			return false;
		this.getId2Handler().remove(id) ;
		this.saveHandlers();
		return true ;
	}
	
	
	public void setHandlerOutByJSON(JSONObject jo) throws Exception
	{
		String hid = jo.getString("hid") ;
		StoreHandler h = this.getHandlerById(hid) ;
		if(h==null)
			throw new Exception("no handler found with id="+hid) ;
		String tp = jo.getString("tp");
		StoreOut so = StoreOut.newInsByTp(tp) ;
		if(so==null)
			throw new Exception("unknown StoreOut type="+tp) ;
		DataTranserJSON.injectJSONToObj(so, jo) ;
		h.setOut(so);
		
		this.saveHandlers();
	}
	
	public StoreOut delHandlerOutById(String hid,String id) throws Exception
	{
		StoreHandler h = this.getHandlerById(hid) ;
		if(h==null)
			throw new Exception("no handler found with id="+hid) ;
		StoreOut so = h.delOutById(id) ;
		if(so!=null)
			this.saveHandlers();
		return so ;
	}
	
	private LinkedHashMap<String, StoreHandler> loadHandlers() throws Exception
	{
		LinkedHashMap<String, StoreHandler> n2st = new LinkedHashMap<>();
		
		File f = new File(prjDir, "store_handlers.json");
		if (!f.exists())
			return n2st;
		
		String txt = Convert.readFileTxt(f,"utf-8") ;
		JSONObject jo =new JSONObject(txt) ;

		JSONArray jarr = jo.getJSONArray("handlers") ;
		int n = jarr.length() ;
		for (int i = 0 ; i < n ; i ++)
		{
			JSONObject tmpjo = jarr.getJSONObject(i) ;
			
			String tp = tmpjo.getString("_tp");//tmpxd.getParamValueStr("_tp") ;
			StoreHandler o = StoreHandler.newInsByTp(tp) ;
			if(o==null)
				continue ;
			o.fromJO(tmpjo,true,true);
			o.prj = this.prj ;
			n2st.put(o.getId(), o);
		}
		return n2st;
	}
	
	public void saveHandlers() throws Exception
	{
		JSONObject jo = new JSONObject() ;
		JSONArray jarr = new JSONArray();
		jo.put("handlers",jarr) ;
		for (StoreHandler st : getId2Handler().values())
		{
			JSONObject tmpjo = st.toJO() ;
			jarr.put(tmpjo);
		}
		File f = new File(prjDir, "store_handlers.json");
		
		try(FileOutputStream fos= new FileOutputStream(f);OutputStreamWriter osw = new OutputStreamWriter(fos,"utf-8") ;)
		{
			jo.write(osw) ;
		}
	}
	
	
	public void RT_start()
	{
		for(StoreHandler h:this.listHandlers())
		{
			h.RT_start();
		}
	}
	
	public void RT_stop()
	{
		for(StoreHandler h:this.listHandlers())
		{
			h.RT_stop();
		}
	}
	
	/**
	 * output RT info
	 * @return
	 */
	public JSONObject RT_toJO()
	{
		JSONObject jo = new JSONObject() ;
		
		JSONArray h_jarr = new JSONArray() ;
		jo.put("handlers", h_jarr) ;
		for(StoreHandler sh:this.listHandlers())
		{
			JSONObject tmpjo = sh.RT_toJO() ;
			h_jarr.put(tmpjo) ;
		}
		return jo ;
	}
}
