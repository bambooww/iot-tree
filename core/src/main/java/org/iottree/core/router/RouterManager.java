package org.iottree.core.router;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class RouterManager
{
	private static HashMap<String,RouterManager> prjid2mgr = new HashMap<>() ;
	
	public static RouterManager getInstance(UAPrj prj)
	{
		RouterManager instance = prjid2mgr.get(prj.getId()) ;
		if(instance!=null)
			return instance ;
		
		synchronized(RouterManager.class)
		{
			instance = prjid2mgr.get(prj.getId()) ;
			if(instance!=null)
				return instance ;
			
			instance = new RouterManager(prj) ;
			prjid2mgr.put(prj.getId(),instance) ;
			return instance ;
		}
	}
	
	UAPrj belongTo = null ;
	
	ArrayList<DataPacker> dataPksInn = new ArrayList<>() ;
	
	ArrayList<DataPacker> dataPks = null;// new ArrayList<>() ;
	
	ArrayList<RouterOuter> outers = null ;
	
	private RouterManager(UAPrj prj)
	{
		this.belongTo = prj ;
		
		dataPksInn.add(new DataPackerDef(this)) ;
		dataPksInn.add(new DataPackerRT(this)) ;
	}
	
	public List<DataPacker> getDataPackersInner()
	{
		return dataPksInn;
	}
	
	public List<DataPacker> getDataPackers()
	{
		if(dataPks!=null)
			return dataPks;
		
		synchronized(this)
		{
			if(dataPks!=null)
				return dataPks;
			
			try
			{
				dataPks = loadDataPackers() ;
				return dataPks ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null ;
			}
		}
	}
	
	private File getDataPackersFile()
	{
		return new File(this.belongTo.getPrjSubDir(),"router_pk.json") ;
	}
	
	void saveDataPackers() throws Exception
	{
		List<DataPacker> dps = getDataPackers() ;
		JSONArray jarr = new JSONArray() ;
		for(DataPacker dp:dps)
		{
			JSONObject jo = dp.toJO() ;
			jarr.put(jo) ;
		}
		
		Convert.writeFileTxt(getDataPackersFile(), jarr.toString(), "UTF-8");
	}
	
	private ArrayList<DataPacker> loadDataPackers() throws Exception
	{
		ArrayList<DataPacker> dps = new ArrayList<>() ;
		File f = this.getDataPackersFile() ;
		if(!f.exists())
			return dps ;
		String txt = Convert.readFileTxt(f, "UTF-8") ;
		JSONArray jarr = new JSONArray(txt) ;
		int n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			StringBuilder failedr = new StringBuilder() ;
			DataPacker dp = DataPacker.transFromJO(this,jo,failedr) ;
			if(dp==null)
			{
				System.out.println(" Warn: loadDataPackers failed="+failedr) ;
				continue ;
			}
			dps.add(dp) ;
		}
		
		return dps ;
	}
	
	
	//  --  outer
	
	static LinkedHashMap<String,RouterOuter> TP2OUTER = new LinkedHashMap<>() ;
	
	public static void registerRouterOuter(RouterOuter outer)
	{
		TP2OUTER.put(outer.getTp(), outer) ;
	}
	
	public static void registerRouterOuter(String class_name)
	{
		try
		{
			Class<?> c = Class.forName(class_name) ;
			Constructor<?> cs = c.getConstructor(RouterManager.class) ;
			RouterOuter ro = (RouterOuter)cs.newInstance((RouterManager)null) ;
			registerRouterOuter(ro) ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	static
	{
		registerRouterOuter("org.iottree.ext.kafka.KafkaRouterOuter") ;
	}
	
	
	
	public static List<RouterOuter> listRouterOuterTPS()
	{
		ArrayList<RouterOuter> rets = new ArrayList<>() ;
		rets.addAll(TP2OUTER.values()) ;
		return rets ;
	}
	
	public List<RouterOuter> getOuters()
	{
		if(outers!=null)
			return outers;
		
		synchronized(this)
		{
			if(outers!=null)
				return outers;
			
			try
			{
				outers = loadOuters() ;
				return outers ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null ;
			}
		}
	}
	
	private File getOutersFile()
	{
		return new File(this.belongTo.getPrjSubDir(),"router_outer.json") ;
	}
	
	void saveOuters() throws Exception
	{
		List<RouterOuter> dps = getOuters() ;
		JSONArray jarr = new JSONArray() ;
		for(RouterOuter dp:dps)
		{
			JSONObject jo = dp.toJO() ;
			jarr.put(jo) ;
		}
		
		Convert.writeFileTxt(getOutersFile(), jarr.toString(), "UTF-8");
	}
	
	private ArrayList<RouterOuter> loadOuters() throws Exception
	{
		ArrayList<RouterOuter> dps = new ArrayList<>() ;
		File f = this.getOutersFile() ;
		if(!f.exists())
			return dps ;
		String txt = Convert.readFileTxt(f, "UTF-8") ;
		JSONArray jarr = new JSONArray(txt) ;
		int n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			StringBuilder failedr = new StringBuilder() ;
			RouterOuter dp = RouterOuter.transFromJO(this,jo,failedr) ;
			if(dp==null)
			{
				System.out.println(" Warn: loadOuters failed="+failedr) ;
				continue ;
			}
			dps.add(dp) ;
		}
		
		return dps ;
	}
}
