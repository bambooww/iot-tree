package org.iottree.core.cxt;

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
import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;

public class UAContext
{

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
		scriptEng.put("$this", nodecxt);
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
