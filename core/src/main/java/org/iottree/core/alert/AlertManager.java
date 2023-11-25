package org.iottree.core.alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.store.Source;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;

public class AlertManager
{
	private static HashMap<String,AlertManager> prjid2mgr = new HashMap<>() ;
	
	public static AlertManager getInstance(String prjid)
	{
		AlertManager instance = prjid2mgr.get(prjid) ;
		if(instance!=null)
			return instance ;
		
		synchronized(AlertManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new AlertManager(prjid) ;
			return instance ;
		}
	}
	
	//String prjId = null ;
	UAPrj prj = null ;
	
	File prjDir = null ;
	
	private LinkedHashMap<String,AlertItem> name2alert = null ;
	
	private AlertManager(String prjid)
	{
		this.prj = UAManager.getInstance().getPrjById(prjid) ;
		if(this.prj==null)
			throw new IllegalArgumentException("no prj found with id="+prjid) ;
		this.prjDir = prj.getPrjSubDir();
		
		try
		{
			name2alert =  loadItems();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	public UAPrj getPrj()
	{
		return prj ;
	}
	
	public LinkedHashMap<String,AlertItem> getItemsAll()
	{
		return name2alert ;
	}
	
	private LinkedHashMap<String, AlertItem> loadItems() throws Exception
	{
		File f = new File(prjDir, "alerts.xml");
		if (!f.exists())
			return null;

		LinkedHashMap<String, AlertItem> n2st = new LinkedHashMap<>();

		XmlData xd = XmlData.readFromFile(f);
		List<XmlData> xds = xd.getSubDataArray("alerts");
		if (xds == null)
			return n2st;
		for (XmlData tmpxd : xds)
		{
			String cn = tmpxd.getParamValueStr("_alert_cn_");
			if (Convert.isNullOrEmpty(cn))
				continue;
			Class<?> c = Class.forName(cn);
			AlertItem o = (AlertItem) c.newInstance();
			if (!DataTranserXml.injectXmDataToObj(o, tmpxd))
				continue;
			n2st.put(o.getName(), o);
		}
		return n2st;
	}
	
	public void save() throws Exception
	{
		XmlData xd = new XmlData();
		List<XmlData> xds = xd.getOrCreateSubDataArray("alerts");
		for (AlertItem st : getItemsAll().values())
		{
			XmlData xd0 = DataTranserXml.extractXmlDataFromObj(st);
			xd0.setParamValue("_alert_cn_", st.getClass().getCanonicalName());
			xds.add(xd0);
		}
		File f = new File(prjDir, "alerts.xml");
		XmlData.writeToFile(xd, f);
	}
}
