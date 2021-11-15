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
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.*;

import org.iottree.core.*;
import org.iottree.core.plugin.AbstractPlugin;
import org.iottree.core.util.Convert;
import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;

public class UAContext
{
//	private static HashMap<String,Object> GVAR2OBJ = null ; 
//			
//	synchronized public static HashMap<String,Object> getOrLoadJsApi()
//	{
//		if(GVAR2OBJ!=null)
//			return GVAR2OBJ ;
//		
//		try
//		{
//			GVAR2OBJ = loadJsApi() ;
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
//		if(GVAR2OBJ==null)
//			GVAR2OBJ = new HashMap<>() ;
//		return GVAR2OBJ;
//	}
	
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
		
		UAPrj prj = (UAPrj)nodecxt.getTopNode();

		seMgr = new ScriptEngineManager();
		
		scriptEng = UAManager.createJSEngine(prj);

		//for graal - object must be public and not inner class,
		  // and function must has HostAccess.Export annotation
		
		
		scriptEng.put("$this", nodecxt);
		if(nodecxt instanceof IJSOb)
		{
			scriptEng.put("$this", ((IJSOb)nodecxt).getJSOb());
		}
		
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
	

	private ScriptEngine getScriptEngine()
	{
		return scriptEng;
	}

	public synchronized void scriptEval(String jstxt) throws ScriptException
	{
		scriptEng.eval(jstxt) ;
	}
	
	public synchronized Object scriptInvoke(String fn,Object... paramvals) throws NoSuchMethodException, ScriptException
	{
		Invocable inv = (Invocable)getScriptEngine() ;
		return inv.invokeFunction(fn, paramvals) ;
	}
	
	public  CompiledScript scriptCompile(boolean bblock,String jstxt) throws ScriptException
	{
		synchronized(UAContext.class)
		{
			Compilable cp = (Compilable)getScriptEngine() ;
			CompiledScript cs = cp.compile(jstxt) ;
			if(bblock)
				cs.eval() ;
			return cs ;
		}
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
