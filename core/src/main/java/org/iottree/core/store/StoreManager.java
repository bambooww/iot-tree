package org.iottree.core.store;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;

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

	LinkedHashMap<String, Store> name2store = null;// 

	private StoreManager(String prjid)
	{
		prj = UAManager.getInstance().getPrjById(prjid);
		if (prj == null)
			throw new IllegalArgumentException("no prj found");
		prjDir = prj.getPrjSubDir();

		try
		{
			name2store = load();
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

	public List<Store> listStores()
	{
		ArrayList<Store> rets = new ArrayList<>();
		rets.addAll(name2store.values());
		return rets;
	}

	public Store getStoreById(String id)
	{
		for (Store st : name2store.values())
		{
			if (st.getId().equals(id))
				return st;
		}
		return null;
	}

	public Store getStore(String name)
	{
		return name2store.get(name);
	}

	public void setStore(Store st, boolean bsave,boolean b_add) throws Exception
	{
		String stname = st.getName() ;
		StringBuilder failedr = new StringBuilder() ;
		if(!Convert.checkVarName(stname, true, failedr))
		{
			throw new Exception(failedr.toString());
		}
		if(b_add)
		{
			Store oldst = this.getStore(st.getName()) ;
			if(oldst!=null)
				throw new Exception("store with name="+st.getName()+" is existed") ;
		}
		name2store.put(st.getName(), st);
		if (bsave)
			save();
	}

	public void save() throws Exception
	{
		XmlData xd = new XmlData();
		List<XmlData> xds = xd.getOrCreateSubDataArray("stores");
		for (Store st : name2store.values())
		{
			XmlData xd0 = DataTranserXml.extractXmlDataFromObj(st);
			xd0.setParamValue("_store_cn_", st.getClass().getCanonicalName());
			xds.add(xd0);
		}
		File f = new File(prjDir, "stores.xml");
		XmlData.writeToFile(xd, f);
	}

	private LinkedHashMap<String, Store> load() throws Exception
	{
		File f = new File(prjDir, "stores.xml");
		if (!f.exists())
			return null;

		LinkedHashMap<String, Store> n2st = new LinkedHashMap<>();

		XmlData xd = XmlData.readFromFile(f);
		List<XmlData> xds = xd.getSubDataArray("stores");
		if (xds == null)
			return n2st;
		for (XmlData tmpxd : xds)
		{
			String cn = tmpxd.getParamValueStr("_store_cn_");
			if (Convert.isNullOrEmpty(cn))
				continue;
			Class<?> c = Class.forName(cn);
			Store o = (Store) c.newInstance();
			if (!DataTranserXml.injectXmDataToObj(o, tmpxd))
				continue;
			n2st.put(o.getName(), o);
		}
		return n2st;
	}
}
