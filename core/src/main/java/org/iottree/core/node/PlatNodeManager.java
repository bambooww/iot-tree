package org.iottree.core.node;

import java.io.File;
import java.io.IOException;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * run as a platform node - plat_node.json config file exists,it will supported
 * 
 *  
 * @author jason.zhu
 *
 */
public class PlatNodeManager
{
	public static boolean isPlatNode()
	{
		File f = Config.getConfFile("plat_node.json") ;
		return f.exists() ;
	}
	
	private static PlatNodeManager instance = null ;
	
	public static PlatNodeManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		File f = Config.getConfFile("plat_node.json") ;
		if(!f.exists())
			throw new RuntimeException("no plat_node.json found") ;
		
		synchronized(PlatNodeManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new PlatNodeManager() ;
			return instance ;
		}
	}
	
	PlatNode platNode = null ;
	
	private PlatNodeManager()
	{
		try
		{
			this.platNode = loadNode() ;
			if(this.platNode==null)
				throw new Exception("load PlatNode failedr") ;
			
			System.out.println("load plat node config succ") ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	private PlatNode loadNode() throws IOException
	{
		File f = Config.getConfFile("plat_node.json") ;
		if(!f.exists())
			return null ;
		JSONObject jo = Convert.readFileJO(f) ;
		return PlatNode.fromJO(jo) ;
		
	}
	
	public PlatNode getNode()
	{
		return this.platNode ;
	}
}
