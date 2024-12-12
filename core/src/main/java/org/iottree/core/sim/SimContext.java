package org.iottree.core.sim;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
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

import org.graalvm.polyglot.Value;
import org.iottree.core.cxt.IJSOb;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.dict.DictManager;
import org.iottree.core.dict.PrjDataClass;
import org.iottree.core.plugin.PlugJsApi;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;
import org.iottree.core.util.js.GUtil;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

public class SimContext
{
	private transient ScriptEngine scriptEng = null;
	
	private Debug debug = new Debug() ;
	
	private GSys sys = new GSys() ;

	private GUtil util = new GUtil() ;

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
		
		List<JsProp> jsnames = si.JS_props() ;
		for(JsProp o:jsnames)
		{
			String n = o.getName();
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
		engine.put("$util",util);
		//engine.put("$dict",sys);
		
		HashMap<String,PlugJsApi> gvar2obj = PlugManager.getInstance().getJsApiAll();
		if(gvar2obj!=null)
		{
			for(Map.Entry<String, PlugJsApi> n2o:gvar2obj.entrySet())
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
		//long st = System.currentTimeMillis() ;
		//long stn = System.nanoTime() ;
		try
		{
			GraalJSScriptEngine se = (GraalJSScriptEngine)getScriptEngine() ;
			Value func = se.getPolyglotContext().getBindings("js").getMember(fn) ;
			Value res = func.execute(paramvals) ;
			return res.as(Object.class) ; 
			//return transGraalValueToMapListObj(res) ;
		
		
//			Invocable inv = (Invocable)getScriptEngine() ;
//			return inv.invokeFunction(fn, paramvals) ;
		}
		finally
		{
			//System.out.println("cost="+(System.currentTimeMillis()-st)+" nano="+(System.nanoTime()-stn)) ;
		}
	}
	
	public static Object transGraalValueToMapListObj(Value res)
	{
		if(res.isNull())
			return null ;
		
		if(res.hasArrayElements())
		{
			ArrayList<Object> ret = new ArrayList<>() ;
			long sz = res.getArraySize() ;
			for(int i = 0 ; i < sz ; i ++)
			{
				Value tmpv = res.getArrayElement(i);//.as(Object.class) ;
				Object obj = transGraalValueToMapListObj(tmpv) ;
				ret.add(obj) ;
			}
			return ret ;
		}
		
		if(res.hasMembers())
		{
			HashMap<String,Object> tmpm = new HashMap<>();
			for(String mkey:res.getMemberKeys())
			{
				 Value val = res.getMember(mkey);
				 Object obj = transGraalValueToMapListObj(val) ;
				 if(obj==null)
					 continue ;
				 tmpm.put(mkey, obj) ;
			}
			return tmpm ;
		}
		
		return res.as(Object.class) ;
	}
	
	public static Map<String,?>  transGraalParamToMapListObj(Map<String,?> pm)
	{
		HashMap<String,Object> tmpm = new HashMap<>();
		for(String mkey:pm.keySet())
		{
			 Object val = pm.get(mkey) ;
			 if(val instanceof Value)
				 val = transGraalValueToMapListObj((Value)val) ;
			 else if(val instanceof Map)
			 {
				 //PolyglotMap polymm ;
				 val = transGraalParamToMapListObj((Map<String,?>)val) ;
			 }
			 if(val==null)
				 continue ;
			 tmpm.put(mkey, val) ;
		}
		return tmpm ;
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