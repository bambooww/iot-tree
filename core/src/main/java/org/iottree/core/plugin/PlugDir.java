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
import java.util.Map;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class PlugDir
{
	private String name = null;

	private String title = null;

	private File plugDirF = null;

	// private HashMap<String,String> params = new HashMap<>() ;

	private URLClassLoader plugCL = null;

	// private HashMap<String,JSONObject> jsapi_name2json = new HashMap<String,
	// JSONObject>();

	JSONArray js_api_arr = null;

	private HashMap<String, Object> jsapi_name2ob = null;// new HashMap<>();

	public PlugDir(File plugdirf)
	{
		// this.name = name ;
		plugDirF = plugdirf;// new
							// File(Config.getDataDirBase()+"/plugins/"+name+"/");
		if (!plugDirF.exists())
			throw new IllegalArgumentException("no plug dir founded,name=" + name);

		loadConfigJson();
	}

	private boolean loadConfigJson()// throws IOException
	{
		String txt = null;
		try
		{
			txt = Convert.readFileTxt(new File(plugDirF, "config.json"), "UTF-8");
		}
		catch ( Exception e)
		{
			e.printStackTrace();
			return false;
		}
		JSONObject jo = new JSONObject(txt);
		this.name = jo.getString("name");
		this.title = jo.optString("title");
		if (Convert.isNullOrEmpty(this.title))
			this.title = name;

		js_api_arr = jo.optJSONArray("js_api");

		return true;
	}

	static boolean checkDirValid(File plugdirf)
	{
		File conf = new File(plugdirf, "config.json");
		if (!conf.exists())
			return false;
		File classesf = new File(plugdirf, "classes/");
		File libf = new File(plugdirf, "lib/");
		if (!classesf.exists() && !libf.exists())
			return false;
		return true;
	}

	static PlugDir parseDir(File plugdirf)
	{
		if (!checkDirValid(plugdirf))
			return null;

		PlugDir pdir = new PlugDir(plugdirf);
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

	private synchronized URLClassLoader getOrLoadCL() throws MalformedURLException, IOException
	{
		if (plugCL != null)
			return plugCL;

		// list js_api dir.
		File classesf = new File(plugDirF, "classes/");

		ArrayList<URL> urllist = new ArrayList<>();
		String ss = "file:" + classesf.getCanonicalPath().replace('\\', '/') + "/";
		urllist.add(new URL(ss));

		File libsf = new File(plugDirF, "lib/");
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

		}
		catch ( SecurityException se)
		{

		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
	}

	private Object loadJsApiOb(JSONObject job) throws Exception
	{

		String name = job.optString("name");
		String classn = job.optString("class");
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

		URLClassLoader cl = this.getOrLoadCL();
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
		return ob;

	}

	public HashMap<String, Object> getOrLoadJsApiObjs() throws MalformedURLException, IOException
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
						Object ob = loadJsApiOb(job);
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

}
