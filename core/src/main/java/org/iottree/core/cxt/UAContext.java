package org.iottree.core.cxt;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Predicate;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.*;

import org.iottree.core.*;
import org.iottree.core.ext.AbstractPlugin;
import org.iottree.core.util.Convert;
import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;

public class UAContext
{
	private static HashMap<String,Object> GVAR2OBJ = null ; 
			
	synchronized public static HashMap<String,Object> getOrLoadJsApi()
	{
		if(GVAR2OBJ!=null)
			return GVAR2OBJ ;
		
		try
		{
			GVAR2OBJ = loadJsApi() ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(GVAR2OBJ==null)
			GVAR2OBJ = new HashMap<>() ;
		return GVAR2OBJ;
	}
	
	private static URLClassLoader GVAR_CL = null ;
	
	private static URLClassLoader createCL() throws MalformedURLException, IOException
	{
		//list js_api dir.
		File classesf = new File(Config.getDataDirBase()+"/plugins/js_api/classes/");
		
		ArrayList<URL> urllist = new ArrayList<>() ;
		String ss = "file:"+classesf.getCanonicalPath().replace('\\', '/')+"/";
		urllist.add(new URL(ss));
		
		File libsf = new File(Config.getDataDirBase()+"/plugins/js_api/libs/");
		File[] jarfs = libsf.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(f.isDirectory())
					return false;
				
				return f.getName().toLowerCase().endsWith(".jar");
			}});
		for(File jarf:jarfs)
		{
			urllist.add(new URL("file:"+jarf.getCanonicalPath())) ;
		}
		
		URL[] urls = new URL[urllist.size()] ;
		urllist.toArray(urls) ;
		GVAR_CL  = new URLClassLoader(urls, Thread.currentThread()  
                .getContextClassLoader());  
		return GVAR_CL;
	}
	
	private static URLClassLoader getOrLoadJsApiLoader() throws MalformedURLException, IOException
	{
		if(GVAR_CL!=null)
			return GVAR_CL ;
		GVAR_CL =  createCL();
		return  GVAR_CL;
	}
	
	private static HashMap<String,Object> loadJsApi() throws Exception
	{
		String dir = Config.getDataDirBase()+"/plugins/js_api/";
		
		File dirf = new File(dir) ;
		File conf = new File(dirf,"config.txt") ;
		if(!conf.exists())
			return null ;
		
		HashMap<String,Object> ret = new HashMap<String, Object>();
		for(String ln:Convert.readFileTxtLines(conf, "utf-8"))
		{
			ln = ln.trim() ;
			if(ln.startsWith("#"))
				continue ;
			int i = ln.indexOf("=") ;
			String n = ln.substring(0,i).trim() ;
			if(Convert.isNullOrEmpty(n))
				continue ;
			StringBuilder fr = new StringBuilder() ;
			if(!Convert.checkVarName(n,true,fr))
			{
				System.err.print(" load js api error, invalid var name:"+n);
			}
			
			String cn = ln.substring(i+1).trim() ;
			
			URLClassLoader cl = getOrLoadJsApiLoader() ;
            Class<?> cc = cl.loadClass(cn);
            Object ob =  cc.newInstance();
            if(ob instanceof AbstractPlugin)
            {
            	
            }
            ret.put(n, ob) ;
		}
		return ret ;
	}
	//private SimpleScriptContext scriptCxt = null;

//	@SuppressWarnings("serial")
//	private UAContext.JSProxyOb jsObBindings = null;

	UANodeOCTagsCxt nodeCxt = null;

	private transient ScriptEngineManager seMgr = null;
	private transient ScriptEngine scriptEng = null;
	
	
	public static final String FN_TEMP_VAR = "_ua_cxt_tmp_var_";

	public UAContext(UANodeOCTagsCxt nodecxt) throws ScriptException
	{

		nodeCxt = nodecxt;

		seMgr = new ScriptEngineManager();
		
		scriptEng = seMgr.getEngineByName(UANode.JS_NAME);


		scriptEng.put("polyglot.js.allowHostAccess", true);
		scriptEng.put("polyglot.js.allowAllAccess", true);
		scriptEng.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);

		//for graal - object must be public and not inner class,
		  // and function must has HostAccess.Export annotation
		HashMap<String,Object> gvar2obj = getOrLoadJsApi();
		if(gvar2obj!=null)
		{
			for(Map.Entry<String, Object> n2o:gvar2obj.entrySet())
			{
				scriptEng.put("$$"+n2o.getKey(), n2o.getValue());
			}
		}
		
		scriptEng.put("$this", nodecxt);
		if(nodecxt instanceof IJSOb)
		{
			scriptEng.put("$this", ((IJSOb)nodecxt).getJSOb());
		}
		scriptEng.put("$sys", new GSys());
		scriptEng.put("$debug", new Debug());//UANode.SCRIPT_DEBUG);
		
		List<Object> jsnames = nodecxt.JS_names() ;
		for(Object o:jsnames)
		{
			String n = o.toString() ;
			if(n==null||n.equals(""))
				continue ;
			Object v = nodecxt.JS_get(n) ;
			if(v!=null)
				scriptEng.put(n, v);
		}
		scriptEng.eval("var " + FN_TEMP_VAR + "={};");
	}

//	public Bindings getJsObBindings()
//	{
//		return jsObBindings;
//	}
	

	ScriptEngine getScriptEngine()
	{
		return scriptEng;
	}

	/**
	 * templary code to run
	 * 
	 * @param txt
	 * @return
	 * @throws Exception
	 */
	public Object runCode(String txt) throws Exception
	{
		UACodeItem ci = new UACodeItem("", txt);
		try
		{
			ci.initItem(this);
			return ci.runCode();
		} finally
		{
			ci.delBlockCode();
		}
	}

	// public Compl
	
}
