package org.iottree.driver.zigbee.zcl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

public class ZCLClusterManager
{
	private static ILogger log = LoggerManager.getLogger(ZCLClusterManager.class);
	
	private static LinkedHashMap<String,ZCLCluster> NAME_CLUSTER = null;//new LinkedHashMap<>() ;
	private static LinkedHashMap<Integer,ZCLCluster> ID_CLUSTER = null;// new LinkedHashMap<>() ;
	
	static boolean loadClusters() throws IOException
	{
		File f = new File(Config.getDataDirBase()+"/zigbee/def_clusters.json") ;
		JSONObject jo = Convert.readFileJO(f) ;
		if(jo==null)
			return false;
		
		LinkedHashMap<String,ZCLCluster> n2c = new LinkedHashMap<>() ;
		LinkedHashMap<Integer,ZCLCluster> i2c = new LinkedHashMap<>() ;
		
		Iterator<String> ikey = jo.keys() ;
		while(ikey.hasNext())
		{
			String k = ikey.next() ;
			JSONObject joc = jo.getJSONObject(k) ;
			
			try
			{
				ZCLCluster c = ZCLCluster.parseCluster(joc) ;
				if(c==null)
				{
					if(log.isWarnEnabled())
						log.warn("parse cluster err:"+joc.toString());
					continue ;
				}
				n2c.put(c.name,c) ;
				i2c.put(c.id, c) ;
			}
			catch(Exception ee)
			{
				if(log.isWarnEnabled())
					log.warn(ee);
			}
		}
		NAME_CLUSTER = n2c ;
		ID_CLUSTER = i2c ;
		return true ;
	}
	
	private static ZCLClusterManager instance = null ;
	
	public static ZCLClusterManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(ZCLClusterManager.class)
		{
			if(instance!=null)
				return instance ;
			
			try
			{
				loadClusters() ;
			}
			catch(Exception eee)
			{
				eee.printStackTrace();
			}
			return instance = new ZCLClusterManager() ;
		}
	}
	
	public ZCLCluster getCluserByID(int id)
	{
		return ID_CLUSTER.get(id) ;
	}
	
	public ZCLCluster getClusterByName(String n)
	{
		return NAME_CLUSTER.get(n) ;
	}
	
	public ZCLCmd getCmd(int cluster_id,int cmd_id)
	{
		ZCLCluster c = getCluserByID(cluster_id) ;
		if(c==null)
			return null ;
		return c.getCmdById(cmd_id) ;
	}
	
	public ZCLParam getCmdPm(int cluster_id,int cmd_id,int pm_idx)
	{
		ZCLCmd c = getCmd(cluster_id,cmd_id);
		if(c==null)
			return null ;
		return c.getParamByIdx(pm_idx) ;
	}
	
	public List<ZCLCluster> listClusters()
	{
		ArrayList<ZCLCluster> ret = new ArrayList<>() ;
		ret.addAll(ID_CLUSTER.values()) ;
		Collections.sort(ret);
		return ret ;
	}
}
