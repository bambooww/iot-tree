package org.iottree.core.store;

import java.io.File;
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
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
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
	
	static LinkedHashMap<String, Source> name2sor = null;//
	
	private static LinkedHashMap<String, Source> getName2Source()
	{
		if(name2sor!=null)
			return name2sor ;
		
		try
		{
			name2sor = loadSors();
			return name2sor;
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			log.error(e);
			return null ;
		}
	}
	
	public static List<Source> listSources()
	{
		ArrayList<Source> rets = new ArrayList<>();
		rets.addAll(getName2Source().values());
		return rets;
	}

//	public Source getSourceById(String id)
//	{
//		for (Source st : name2store.values())
//		{
//			if (st.getId().equals(id))
//				return st;
//		}
//		return null;
//	}

	public static Source getSource(String name)
	{
		return getName2Source().get(name);
	}

	public static void setSource(Source st, boolean bsave,boolean b_add) throws Exception
	{
		String stname = st.getName() ;
		StringBuilder failedr = new StringBuilder() ;
		if(!Convert.checkVarName(stname, true, failedr))
		{
			throw new Exception(failedr.toString());
		}
		if(b_add)
		{
			Source oldst = getSource(st.getName()) ;
			if(oldst!=null)
				throw new Exception("store with name="+st.getName()+" is existed") ;
		}
		
		name2sor.put(st.getName(), st);
		if (bsave)
			saveSors();
	}
	
	public static boolean delSource(String name) throws Exception
	{
		Source sor = getSource(name) ;
		if(sor==null)
		{
			return false;
		}
		name2sor.remove(name) ;
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
			n2st.put(o.getName(), o);
		}
		return n2st;
	}
	
	


	UAPrj prj = null;

	File prjDir = null;

	

	private StoreManager(String prjid)
	{
		prj = UAManager.getInstance().getPrjById(prjid);
		if (prj == null)
			throw new IllegalArgumentException("no prj found");
		prjDir = prj.getPrjSubDir();

		
	}

}
