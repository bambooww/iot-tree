package org.iottree.core.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.Config;
import org.iottree.core.cxt.JSObPk;
import org.iottree.core.util.Convert;
import org.w3c.dom.Element;

/**
 * 
 * @author jason.zhu
 *
 */
public class PlugManager
{
	private static PlugManager instance = null ;
	
	public static PlugManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(PlugManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new PlugManager() ;
			return instance ;
		}
	}
	
	LinkedHashMap<String,PlugDir> name2plug = new LinkedHashMap<>() ;
	
	HashMap<String,LinkedHashMap<String,PlugDir>> lib2plugs = new HashMap<>() ;
	
	private PlugManager()
	{
		findPlugs();
	}
	
	private void findPlugs()
	{
		File plugdir = new File(Config.getDataDirBase()+"/plugins/");
		if(!plugdir.exists())
			return ;//new ArrayList<>(0) ;
		
		File[] dirfs = plugdir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				return f.isDirectory() ;
			}}) ;
		
		for(File dirf:dirfs)
		{
			if(dirf.getName().startsWith("_"))
				continue ;
			PlugDir pd = PlugDir.parseDir(dirf);
			if(pd==null)
				continue ;
			name2plug.put(pd.getName(), pd) ;
		}
		//return plugname2cl ;
	}
	
	
	
	public Collection<PlugDir> listPlugs()
	{
		return this.name2plug.values();
	}
	
	public PlugDir getPlug(String name)
	{
		return this.name2plug.get(name) ;
	}
	
	
	private HashMap<String,PlugJsApi> name2jsapi = null ;
	
	
	public HashMap<String,PlugJsApi> getJsApiAll()
	{
		if(name2jsapi!=null)
			return name2jsapi;
		
		HashMap<String,PlugJsApi> ret = new HashMap<>() ;
		for(PlugDir pd:this.name2plug.values())
		{
			try
			{
				HashMap<String,PlugJsApi> n2o = pd.getOrLoadJsApiObjs() ;
				if(n2o==null)
					continue ;
				ret.putAll(n2o);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		name2jsapi = ret ;
		return ret ;
	}
	
//	public HashMap<String,JSObPk> getJsApiPkAll()
//	{
//		HashMap<String,JSObPk> ret = new HashMap<>() ;
//		for(PlugDir pd:this.name2plug.values())
//		{
//			try
//			{
//				HashMap<String,PlugJsApi> n2o = pd.getOrLoadJsApiObjs() ;
//				if(n2o==null)
//					continue ;
//				for(Map.Entry<String, Object> n2v:n2o.entrySet())
//				{
//					JSObPk obpk = new JSObPk(n2v.getValue()) ;
//					ret.put(n2v.getKey(), obpk);
//				}
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//		return ret ;
//	}
	
	
//	public HashMap<String,Object> getAuthAll()
//	{
//		HashMap<String,Object> ret = new HashMap<>() ;
//		for(PlugDir pd:this.name2plug.values())
//		{
//			try
//			{
//				HashMap<String,Object> n2o = pd.getOrLoadAuthObjs() ;
//				if(n2o==null)
//					continue ;
//				ret.putAll(n2o);
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//		return ret ;
//	}
	
	public Object getAuthObj()
	{
		Element ele = Config.getConfElement("plug_auth") ;
		if(ele==null)
			return null;
		String name = ele.getAttribute("name") ;
		if(Convert.isNullOrEmpty(name))
			return null ;
		
		for(PlugDir pd:this.name2plug.values())
		{
			try
			{
				Object o = pd.loadAuthObj(name) ;
				if(o!=null)
					return o ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return null ;
	}
	
	private transient boolean plugAuthGit = false;
	private transient PlugAuth plugAuth = null ;
	
	public PlugAuth getPlugAuth()// throws Exception
	{
		if(plugAuthGit)
			return plugAuth ;
		
		try
		{
			Element ele = Config.getConfElement("plug_auth") ;
			if(ele==null)
				return null;
			String name = ele.getAttribute("name") ;
			if(Convert.isNullOrEmpty(name))
				return null ;
			String token_cookie = ele.getAttribute("token_cookie_name") ;
			if(Convert.isNullOrEmpty(token_cookie))
				token_cookie = "token" ;
			
			String no_r_p = ele.getAttribute("no_read_right_prompt") ;
			String no_w_p = ele.getAttribute("no_write_right_prompt") ;
			
			Object ob = getAuthObj();
			if(ob==null)
			{
				//throw new Exception("no plug_auth ["+name+"] found") ;
				System.err.println("no plug_auth ["+name+"] found") ;
			}
			plugAuth = new PlugAuth(ob,token_cookie) ;
			plugAuth.asNoRightPrompt(no_r_p, no_w_p) ;
			plugAuth.initAuth() ;
			
			return plugAuth;
		}
		finally
		{
			plugAuthGit = true ;
		}
	}
	
	
	public LinkedHashMap<String,PlugDir> LIB_getPlugs(String lib_name)
	{
		LinkedHashMap<String,PlugDir> pds = lib2plugs.get(lib_name) ;
		if(pds!=null)
			return pds ;
		
		File plugdir = new File(Config.getDataDirBase()+"/plugins/_libs/"+lib_name+"/");
		if(!plugdir.exists())
			return null ;//new ArrayList<>(0) ;
		
		pds = new LinkedHashMap<String,PlugDir>();
		
		File[] dirfs = plugdir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				return f.isDirectory() ;
			}}) ;
		
		for(File dirf:dirfs)
		{
			if(dirf.getName().startsWith("_"))
				continue ;
			PlugDir pd = PlugDir.parseDir(dirf);
			if(pd==null)
				continue ;
			pds.put(pd.getName(), pd) ;
		}
		lib2plugs.put(lib_name,pds) ;
		
		return pds ;
	}
}
