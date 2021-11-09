package org.iottree.core.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.iottree.core.Config;
import org.iottree.core.ext.AbstractPlugin;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class PlugDir
{
	private String name = null ;
	
	private String title = null ; 
	
	private File plugDirF = null ;

	private URLClassLoader plugCL = null ;
	
	private HashMap<String,String> jsapi_name2class = new HashMap<String, String>();
	
	private HashMap<String,Object> jsapi_name2ob = null;//new HashMap<>();
	
	public PlugDir(File plugdirf)
	{
		//this.name = name ;
		plugDirF = plugdirf;//new File(Config.getDataDirBase()+"/plugins/"+name+"/");
		if(!plugDirF.exists())
			throw new IllegalArgumentException("no plug dir founded,name="+name) ;
		
		 loadConfigJson() ;
	}
	
	private boolean loadConfigJson()// throws IOException
	{
		String txt = null ;
		try
		{
			txt = Convert.readFileTxt(new File(plugDirF,"config.json"), "UTF-8") ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		JSONObject jo = new JSONObject(txt);
		this.name = jo.getString("name") ;
		this.title = jo.optString("title") ;
		if(Convert.isNullOrEmpty(this.title))
			this.title = name ;
		
		JSONArray jsapis = jo.optJSONArray("js_api") ;
		if(jsapis!=null)
		{
			int n = jsapis.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject job = jsapis.getJSONObject(i) ;
				String name = job.optString("name") ;
				String classn = job.optString("class") ;
				if(Convert.isNullOrEmpty(name)||Convert.isNullOrEmpty(classn))
					continue ;
				
				jsapi_name2class.put(name, classn) ;
			}
		}
		
		return true ;
	}
	
	static boolean checkDirValid(File plugdirf)
	{
		File conf = new File(plugdirf,"config.json") ;
		if(!conf.exists())
			return false;
		File classesf = new File(plugdirf,"classes/") ;
		File libf = new File(plugdirf,"lib/") ;
		if(!classesf.exists() && !libf.exists())
			return false;
		return true ;
	}
	
	static PlugDir parseDir(File plugdirf)
	{
		if(!checkDirValid(plugdirf))
			return null ;
		
		PlugDir pdir = new PlugDir(plugdirf) ;
		if(!pdir.loadConfigJson())
			return null ;
		return pdir ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	private synchronized URLClassLoader getOrLoadCL() throws MalformedURLException, IOException
	{
		if(plugCL!=null)
			return plugCL ;
		
		//list js_api dir.
		File classesf = new File(plugDirF,"classes/");
		
		ArrayList<URL> urllist = new ArrayList<>() ;
		String ss = "file:"+classesf.getCanonicalPath().replace('\\', '/')+"/";
		urllist.add(new URL(ss));
		
		File libsf = new File(plugDirF,"libs/");
		File[] jarfs = libsf.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(f.isDirectory())
					return false;
				
				return f.getName().toLowerCase().endsWith(".jar");
			}});
		if(jarfs!=null&&jarfs.length>0)
		{
			for(File jarf:jarfs)
			{
				urllist.add(new URL("file:"+jarf.getCanonicalPath())) ;
			}
		}
		
		URL[] urls = new URL[urllist.size()] ;
		urllist.toArray(urls) ;
		plugCL  = new URLClassLoader(urls, Thread.currentThread()  
                .getContextClassLoader());  
		return plugCL;
	}
	
	public HashMap<String,Object> getOrLoadJsApiObjs() throws MalformedURLException, IOException //throws Exception
	{
		if(this.jsapi_name2ob!=null)
			return jsapi_name2ob;
		
		String cn = this.jsapi_name2class.get(name) ;
		if(Convert.isNullOrEmpty(cn))
			return null ;
		
		synchronized(this)
		{
			if(this.jsapi_name2ob!=null)
				return jsapi_name2ob;
			
			jsapi_name2ob = new HashMap<>() ;
			URLClassLoader cl = this.getOrLoadCL() ;
			for(Map.Entry<String, String> n2c:jsapi_name2class.entrySet())
			{
				try
				{
					Class<?> cc = cl.loadClass(n2c.getValue());
					if(cc==null)
						continue ;
			        Object ob =  cc.newInstance();
			        if(ob instanceof AbstractPlugin)
			        {
			        	
			        }
			        jsapi_name2ob.put(n2c.getKey(), ob) ;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
	        return jsapi_name2ob;
		}
	}

	
}
