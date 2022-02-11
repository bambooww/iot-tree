package org.iottree.core.sim;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.iottree.core.cxt.IJSOb;
import org.iottree.core.dict.DictManager;
import org.iottree.core.dict.PrjDataClass;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;

public class SimContext
{
	private transient ScriptEngine scriptEng = null;
	
	private Debug debug = new Debug() ;
	
	private GSys sys = new GSys() ;


	private SimInstance simIns = null ;

	public SimContext(SimInstance si)// throws ScriptException
	{
		simIns = si ;
				
		scriptEng = createJSEngine();

		//for graal - object must be public and not inner class,
		  // and function must has HostAccess.Export annotation
		
		scriptEng.put("$this", simIns);
		//scriptEng.put("$prj", prj);
		
		if(si instanceof IJSOb)
		{
			scriptEng.put("$this", ((IJSOb)si).getJSOb());
		}
		
		List<Object> jsnames = si.JS_names() ;
		for(Object o:jsnames)
		{
			String n = o.toString() ;
			if(n==null||n.equals(""))
				continue ;
			Object v = si.JS_get(n) ;
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
		engine.put("polyglot.js.allowAllAccess",true);
		engine.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);

		engine.put("$debug",debug);
		engine.put("$system",sys);
		engine.put("$sys",sys);
		engine.put("$dict",sys);
		
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
		synchronized(SimContext.class)
		{
			Compilable cp = (Compilable)getScriptEngine() ;
			CompiledScript cs = cp.compile(jstxt) ;
			if(bblock)
				cs.eval() ;
			return cs ;
		}
	}
//	/**
//	 * templary code to run
//	 * 
//	 * @param txt
//	 * @return
//	 * @throws Exception
//	 */
//	public Object runCode(String txt) throws Exception
//	{
//		UACodeItem ci = new UACodeItem("", txt);
//		try
//		{
//			ci.initItem(this);
//			return ci.runCode();
//		} finally
//		{
//			ci.delBlockCode();
//		}
//	}

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