package org.iottree.core.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ZipUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class PlugDir
{
	private String name = null;

	private String title = null;

	private File plugDirF = null;
	
	private JSONObject plugConfigJO = null ;

	// private HashMap<String,String> params = new HashMap<>() ;

	private ClassLoader plugCL = null;

	// private HashMap<String,JSONObject> jsapi_name2json = new HashMap<String,
	// JSONObject>();

	JSONArray js_api_arr = null;

	private HashMap<String, PlugJsApi> jsapi_name2ob = null;// new HashMap<>();
	
	JSONArray js_auth_arr = null ;
	
	JSONObject msg_net_jo = null ;
	
	//private HashMap<String, Object> auth_name2ob = null;

//	public PlugDir(File plugdirf)
//	{
////		// this.name = name ;
////		plugDirF = plugdirf;// new
////							// File(Config.getDataDirBase()+"/plugins/"+name+"/");
////		if (!plugDirF.exists())
////			throw new IllegalArgumentException("no plug dir founded,name=" + name);
////
////		loadConfigJson();
//		
//		this(plugdirf,null) ;
//	}
	
	private PlugDir(File plugdirf,ClassLoader cl)
	{
		// this.name = name ;
		plugDirF = plugdirf;// new
							// File(Config.getDataDirBase()+"/plugins/"+name+"/");
//		if (!plugDirF.exists())
//			throw new IllegalArgumentException("no plug dir founded,name=" + name);

		this.plugCL = cl ;
		//loadConfigJson();
	}

	private boolean loadConfigJson()// throws IOException
	{
		String txt = null;
		try
		{
			if(plugDirF.isFile())
				txt = ZipUtil.readZipTxt(plugDirF, "WEB-INF/config.json", "UTF-8") ;
			else
			{
				File jf = new File(plugDirF, "config.json") ;
				if(!jf.exists())
					return false;
				txt = Convert.readFileTxt(jf, "UTF-8");
			}
			
			if(Convert.isNullOrEmpty(txt))
				return false;
		}
		catch ( Exception e)
		{
			e.printStackTrace();
			return false;
		}
		JSONObject jo = new JSONObject(txt);
		this.plugConfigJO = jo ;
		this.name = jo.getString("name");
		this.title = jo.optString("title");
		if (Convert.isNullOrEmpty(this.title))
			this.title = name;

		js_api_arr = jo.optJSONArray("js_api");
		js_auth_arr = jo.optJSONArray("auth") ;
		msg_net_jo = jo.optJSONObject("msg_net") ;
		return true;
	}
	
	
	public JSONObject getConfigJO()
	{
		return this.plugConfigJO ;
	}

	static boolean checkDirValid(File plugdirf)
	{
		File conf = new File(plugdirf, "config.json");
		if (!conf.exists())
			return false;
//		File classesf = new File(plugdirf, "classes/");
//		File libf = new File(plugdirf, "lib/");
//		if (!classesf.exists() && !libf.exists())
//			return false;
		return true;
	}

	static PlugDir parseDir(File plugdirf,ClassLoader cl)
	{
//		if (!checkDirValid(plugdirf))
//			return null;

		PlugDir pdir = new PlugDir(plugdirf,cl);
		if (!pdir.loadConfigJson())
			return null;
		return pdir;
	}

	public String getName()
	{
		return name;
	}

	public String getTitle()
	{
		return this.title;
	}

	public synchronized ClassLoader getOrLoadCL() //throws MalformedURLException, IOException
	{
		if (plugCL != null)
			return plugCL;

		// list js_api dir.
		File classesf = new File(plugDirF, "classes/");
		File libsf = new File(plugDirF, "lib/");
		if(!classesf.exists() && !libsf.exists())
		{
			plugCL = Thread.currentThread().getContextClassLoader() ;
			return plugCL ;
		}

		try
		{
			ArrayList<URL> urllist = new ArrayList<>();
			String ss = "file:" + classesf.getCanonicalPath().replace('\\', '/') + "/";
			urllist.add(new URL(ss));
	
			
			File[] jarfs = libsf.listFiles(new FileFilter() {
	
				@Override
				public boolean accept(File f)
				{
					if (f.isDirectory())
						return false;
	
					return f.getName().toLowerCase().endsWith(".jar");
				}
			});
			if (jarfs != null && jarfs.length > 0)
			{
				for (File jarf : jarfs)
				{
					urllist.add(new URL("file:" + jarf.getCanonicalPath()));
				}
			}
	
			URL[] urls = new URL[urllist.size()];
			urllist.toArray(urls);
			plugCL = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
			return plugCL;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return null ;
		}
	}

	private void initPlugObj(Object ob, HashMap<String, String> params) // throws
																		// NoSuchMethodException,
																		// SecurityException
	{
		try
		{
			Method m = ob.getClass().getDeclaredMethod("init_plug", File.class, HashMap.class);
			m.setAccessible(true);
			m.invoke(ob, plugDirF, params);
		}
		catch ( NoSuchMethodException nse)
		{
			//nse.printStackTrace();
		}
		catch ( SecurityException se)
		{
			se.printStackTrace();
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
	}

	private PlugJsApi loadJsApi(JSONObject job) throws Exception
	{

		String name = job.optString("name");
		String classn = job.optString("class");
		String mode = job.optString("mode") ;
		String desc = job.optString("desc") ;
		if (Convert.isNullOrEmpty(name) || Convert.isNullOrEmpty(classn))
			return null;
		JSONObject paramjo = job.optJSONObject("params");
		HashMap<String, String> params = new HashMap<>();
		if (paramjo != null)
		{
			for (String pn : paramjo.keySet())
			{
				String pv = paramjo.get(pn).toString();
				params.put(pn, pv);
			}
		}

		ClassLoader cl = this.getOrLoadCL();
		Class<?> cc = cl.loadClass(classn);
		if (cc == null)
			return null;
		Object ob = cc.newInstance();
		if (ob instanceof AbstractPlugin)
		{
			AbstractPlugin ap = (AbstractPlugin) ob;
			ap.initPlugin(this);
		}
		// System.out.println("1");
		initPlugObj(ob, params);
		return new PlugJsApi(name,ob,desc);//,mode);

	}
	
	private Object loadJsOb(JSONObject job) throws Exception
	{

		String name = job.optString("name");
		String classn = job.optString("class");
		String mode = job.optString("mode") ;
		if (Convert.isNullOrEmpty(name) || Convert.isNullOrEmpty(classn))
			return null;
		JSONObject paramjo = job.optJSONObject("params");
		HashMap<String, String> params = new HashMap<>();
		if (paramjo != null)
		{
			for (String pn : paramjo.keySet())
			{
				String pv = paramjo.get(pn).toString();
				params.put(pn, pv);
			}
		}

		ClassLoader cl = this.getOrLoadCL();
		Class<?> cc = cl.loadClass(classn);
		if (cc == null)
			return null;
		Object ob = cc.newInstance();
		if (ob instanceof AbstractPlugin)
		{
			AbstractPlugin ap = (AbstractPlugin) ob;
			ap.initPlugin(this);
		}
		// System.out.println("1");
		initPlugObj(ob, params);
		//return new PlugJsApi(name,ob,mode);
		return ob ;
	}

	public HashMap<String, PlugJsApi> getOrLoadJsApiObjs() throws MalformedURLException, IOException
	{
		if (this.jsapi_name2ob != null)
			return jsapi_name2ob;

		synchronized (this)
		{
			if (this.jsapi_name2ob != null)
				return jsapi_name2ob;

			jsapi_name2ob = new HashMap<>();

			if (this.js_api_arr != null)
			{
				int n = js_api_arr.length();
				for (int i = 0; i < n; i++)
				{
					JSONObject job = js_api_arr.getJSONObject(i);
					String nn = job.getString("name") ;
					try
					{
						PlugJsApi ob = loadJsApi(job);
						jsapi_name2ob.put(nn, ob);
						
						System.out.println(" plug ["+name+"] load js api object [$$"+nn+"]") ;
					}
					catch ( Exception e)
					{
						e.printStackTrace();
					}
				}
			}

			return jsapi_name2ob;
		}
	}
	
	
	public Object loadAuthObj(String name) throws MalformedURLException, IOException
	{
			if (this.js_auth_arr == null)
				return null ;
			
			int n = js_auth_arr.length();
			for (int i = 0; i < n; i++)
			{
				JSONObject job = js_auth_arr.getJSONObject(i);
				String nn = job.getString("name") ;
				if(!name.equals(nn))
					continue ;
				try
				{
					Object ob = loadJsOb(job);					
					System.out.println(" plug ["+name+"] load js auth object ["+nn+"] ok") ;
					return ob ;
				}
				catch ( Exception e)
				{
					e.printStackTrace();
					System.err.println(" plug ["+name+"] load js auth object ["+nn+"] failed") ;
				}
			}
			
			return null ;
	}

	public JSONObject getMsgNetJO()
	{
		return this.msg_net_jo ;
	}
	
}
