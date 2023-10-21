package org.iottree.core.cxt;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import org.iottree.core.dict.DictManager;
import org.iottree.core.dict.PrjDataClass;
import org.iottree.core.plugin.AbstractPlugin;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;
import org.iottree.core.util.js.GUtil;

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

	//private transient ScriptEngineManager seMgr = null;
	private transient ScriptEngine scriptEng = null;
	
	private  Debug debug = new Debug() ;
	
	private GSys sys = new GSys() ;
	
	private GUtil util = new GUtil() ;
	
	//public static final String FN_TEMP_VAR = "_ua_cxt_tmp_var_";

	public UAContext(UANodeOCTagsCxt nodecxt)// throws ScriptException
	{

		nodeCxt = nodecxt;
		
		UAPrj prj = (UAPrj)nodecxt.getTopNode();

		//seMgr = new ScriptEngineManager();
		
		scriptEng = createJSEngine();

		//for graal - object must be public and not inner class,
		  // and function must has HostAccess.Export annotation
		
		scriptEng.put("$this", nodecxt);
		scriptEng.put("$prj", prj);//prj.getJSOb());
		
		UACh ch = nodecxt.getBelongToCh() ;
		UADev dev = nodecxt.getBelongToDev() ;
		if(ch!=null)
			scriptEng.put("$ch", ch);
		if(dev!=null)
			scriptEng.put("$dev", dev);
		
		UANode pnode = nodecxt.getParentNode() ;
		if(pnode!=null)
			scriptEng.put("$parent", pnode);
		
		PrjDataClass pdc = DictManager.getInstance().getPrjDataClassByPrjId(prj.getId()) ;
		if(pdc!=null)
			scriptEng.put("$dict", pdc);
		
		if(nodecxt instanceof IJSOb)
		{
			scriptEng.put("$this", ((IJSOb)nodecxt).getJSOb());
		}
		
		List<String> jsnames = nodecxt.JS_names() ;
		for(String o:jsnames)
		{
			String n = o.toString() ;
			if(n==null||n.equals(""))
				continue ;
			Object v = nodecxt.JS_get(n) ;
			if(v!=null)
				scriptEng.put(n, v);
		}
		//scriptEng.eval("var " + FN_TEMP_VAR + "={};");
	}
	
	

	public static final String JS_NAME="graal.js";//"nashorn"; //

	private ScriptEngine createJSEngine()
	{
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName(JS_NAME);
		engine.put("polyglot.js.allowHostAccess", true);
		engine.put("polyglot.js.allowAllAccess",false);
		engine.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
		
		//添加配置，支持本地java对接（找了很多资料才找到）
		Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

		//可以考虑这个，开启一切可开启的..
		bindings.put("polyglot.js.allowHostAccess", true);
		bindings.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);

		engine.put("$debug",debug);
		engine.put("$system",sys);
		engine.put("$sys",sys);
		engine.put("$dict",sys);
		engine.put("$util",util);
		
		HashMap<String,Object> gvar2obj = PlugManager.getInstance().getJsApiAll();
		if(gvar2obj!=null)
		{
			for(Map.Entry<String, Object> n2o:gvar2obj.entrySet())
			{
				engine.put("$$"+n2o.getKey(), n2o.getValue());
			}
		}
		
		return engine ;
	}
	

	public ScriptEngine getScriptEngine()
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
	
	/**
	 * 
	 * @param jstxt
	 * @return
	 */
	public String testScript(String jstxt)
	{
		StringWriter sw =new StringWriter() ;
		PrintWriter pw = new PrintWriter(sw) ;
		try
		{
			this.debug.setOutPipe(pw);
			this.scriptEval(jstxt);
			pw.println("--- test script end ---") ;
			pw.flush(); 
			return sw.toString() ;
		}
		catch(Exception e)
		{
			e.printStackTrace(pw);
			pw.flush(); 
			return sw.toString() ;
		}
		finally
		{
			this.debug.setOutPipe(null);
		}
		
	}
}
