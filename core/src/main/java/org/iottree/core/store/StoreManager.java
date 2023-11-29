package org.iottree.core.store;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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

	UAPrj prj = null;

	File prjDir = null;

	LinkedHashMap<String, Source> name2store = null;// 

	private StoreManager(String prjid)
	{
		prj = UAManager.getInstance().getPrjById(prjid);
		if (prj == null)
			throw new IllegalArgumentException("no prj found");
		prjDir = prj.getPrjSubDir();

		try
		{
			name2store = loadSors();
		}
		catch ( Exception e)
		{
			log.error(e);
		}
		finally
		{
			if(name2store==null)
				name2store = new LinkedHashMap<>();
		}
	}

	public List<Source> listSources()
	{
		ArrayList<Source> rets = new ArrayList<>();
		rets.addAll(name2store.values());
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

	public Source getSource(String name)
	{
		return name2store.get(name);
	}

	public void setSource(Source st, boolean bsave,boolean b_add) throws Exception
	{
		String stname = st.getName() ;
		StringBuilder failedr = new StringBuilder() ;
		if(!Convert.checkVarName(stname, true, failedr))
		{
			throw new Exception(failedr.toString());
		}
		if(b_add)
		{
			Source oldst = this.getSource(st.getName()) ;
			if(oldst!=null)
				throw new Exception("store with name="+st.getName()+" is existed") ;
		}
		
		name2store.put(st.getName(), st);
		if (bsave)
			saveSors();
	}
	
	public boolean delSource(String name) throws Exception
	{
		Source sor = this.getSource(name) ;
		if(sor==null)
		{
			return false;
		}
		name2store.remove(name) ;
		saveSors() ;
		return true ;
	}
	

	public void saveSors() throws Exception
	{
		XmlData xd = new XmlData();
		List<XmlData> xds = xd.getOrCreateSubDataArray("sources");
		for (Source st : name2store.values())
		{
			XmlData xd0 = DataTranserXml.extractXmlDataFromObj(st);
			xd0.setParamValue("_tp", st.getSorTp());
			xds.add(xd0);
		}
		File f = new File(prjDir, "store_sors.xml");
		XmlData.writeToFile(xd, f);
	}

	private LinkedHashMap<String, Source> loadSors() throws Exception
	{
		File f = new File(prjDir, "store_sors.xml");
		if (!f.exists())
			return null;

		LinkedHashMap<String, Source> n2st = new LinkedHashMap<>();

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
	
}
