package org.iottree.core.cxt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Predicate;

import javax.script.ScriptException;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.iottree.core.*;
import org.iottree.core.dict.DictManager;
import org.iottree.core.dict.PrjDataClass;
import org.iottree.core.plugin.PlugJsApi;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.task.Task;
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
	//private transient ScriptEngine scriptEng = null;
	private transient Context scriptEng = null;
	private transient Value bindings = null ;
	
	public static  Debug debug = new Debug() ;
	
	public static GSys sys = new GSys() ;
	
	public static GUtil util = new GUtil() ;
	
	//public static final String FN_TEMP_VAR = "_ua_cxt_tmp_var_";

	public UAContext(UANodeOCTagsCxt nodecxt) throws ScriptException
	{

		nodeCxt = nodecxt;
		
		UAPrj prj = (UAPrj)nodecxt.getTopNode();

		//seMgr = new ScriptEngineManager();
		
		scriptEng = createJSContext();//createJSEngine();
		bindings = scriptEng.getBindings("js");
		//for graal - object must be public and not inner class,
		  // and function must has HostAccess.Export annotation
		
		bindings.putMember("$_this_", nodecxt);
		bindings.putMember("$_prj_", prj);//prj.getJSOb());
		
		String init_eval = "const $prj=$_prj_;Object.freeze($prj);";
		
		UACh ch = nodecxt.getBelongToCh() ;
		UADev dev = nodecxt.getBelongToDev() ;
		if(ch!=null)
		{
			bindings.putMember("$_ch_", ch);
			init_eval += "const $ch=$_ch_;Object.freeze($ch);";
		}
		if(dev!=null)
		{
			bindings.putMember("$_dev_", dev);
			init_eval += "const $dev=$_dev_;Object.freeze($dev);";
		}
		
		UANode pnode = nodecxt.getParentNode() ;
		if(pnode!=null)
		{
			bindings.putMember("$_parent_", pnode);
			init_eval += "const $parent=$_parent_;Object.freeze($parent);";
		}
		
		PrjDataClass pdc = DictManager.getInstance().getPrjDataClassByPrjId(prj.getId()) ;
		if(pdc!=null)
		{
			bindings.putMember("$_dict_", pdc);
			init_eval += "const $dict=$_dict_;Object.freeze($dict);" ;
		}
		
		if(nodecxt instanceof IJSOb)
		{
			bindings.putMember("$this", ((IJSOb)nodecxt).getJSOb());
		}
		
		init_eval += "const $this=$_this_;Object.freeze($this);" ;
		
		List<JsProp> jsnames = nodecxt.JS_props() ;
		for(JsProp o:jsnames)
		{
			String n = o.getName() ;
			if(n==null||n.equals(""))
				continue ;
			Object v = nodecxt.JS_get(n) ;
			if(v!=null)
				bindings.putMember(n, v);
		}
		
		
		scriptEng.eval("js",init_eval);
		//scriptEng.
	}
	
	public UAContext asTask(Task task) throws ScriptException
	{
		bindings.putMember("$_task_", task);
		scriptEng.eval("js","const $task=$_task_;Object.freeze($task);") ;
		return this ;
	}

//	public static final String JS_NAME="graal.js";//"nashorn"; //
//
//	private ScriptEngine createJSEngine() throws ScriptException
//	{
//		ScriptEngineManager manager = new ScriptEngineManager();
//		for (ScriptEngineFactory factory : manager.getEngineFactories()) {
//		    System.out.println("Engine: " + factory.getEngineName() + " Names: " + factory.getNames());
//		}
//		ScriptEngine engine = manager.getEngineByName(JS_NAME);
//		engine.put("polyglot.js.allowHostAccess", true);
//		engine.put("polyglot.js.allowAllAccess",false);
//		engine.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
//		
//		//添加配置，支持本地java对接（找了很多资料才找到）
//		Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
//
//		//可以考虑这个，开启一切可开启的..
//		bindings.put("polyglot.js.allowHostAccess", true);
//		bindings.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
//
//		engine.put("$_debug_",debug);
//		//engine.put("$system",sys);
//		engine.put("$_sys_",sys);
//		//engine.put("$_dict_",sys);
//		engine.put("$_util_",util);
//		
//		String init_eval = "const $debug=$_debug_;Object.freeze($debug);"
//				+ "const $sys=$_sys_;Object.freeze($sys);"
//				//+ "const $dict=$_dict_;Object.freeze($dict);"
//				+ "const $util=$_util_;Object.freeze($util);";
//		
//		HashMap<String,PlugJsApi> gvar2obj = PlugManager.getInstance().getJsApiAll();
//		if(gvar2obj!=null)
//		{
//			for(Map.Entry<String, PlugJsApi> n2o:gvar2obj.entrySet())
//			{
//				String k = n2o.getKey();
//				engine.put("$$_"+k+"_", n2o.getValue());
//				init_eval += "const $$"+k+"=$$_"+k+"_;Object.freeze($$"+k+");";
//			}
//		}
//		
//		engine.eval(init_eval);
//		
//		return engine ;
//	}
	
	
	public Context createJSContext()
	{
        Context context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)                 // polyglot.js.allowHostAccess = true
                .allowAllAccess(false)                           //  polyglot.js.allowAllAccess = false
                .allowHostClassLookup(new Predicate<String>() {  //  polyglot.js.allowHostClassLookup
                    @Override
                    public boolean test(String s) {
                        return true;
                    }
                })
                .build();

        //  =  ScriptContext.ENGINE_SCOPE）
        Value bindings = context.getBindings("js");

        bindings.putMember("$_debug_", debug);
        bindings.putMember("$_sys_", sys);
        bindings.putMember("$_util_", util);

        StringBuilder initEval = new StringBuilder();
        initEval.append("const $debug=$_debug_;Object.freeze($debug);")
                .append("const $sys=$_sys_;Object.freeze($sys);")
                .append("const $util=$_util_;Object.freeze($util);");

        HashMap<String, PlugJsApi> gvar2obj = PlugManager.getInstance().getJsApiAll();
        if (gvar2obj != null) {
            for (Map.Entry<String, PlugJsApi> n2o : gvar2obj.entrySet()) {
                String k = n2o.getKey();
                bindings.putMember("$$_" + k + "_", n2o.getValue());
                initEval.append("const $$").append(k).append("=$$_").append(k).append("_;Object.freeze($$").append(k).append(");");
            }
        }

        context.eval(Source.newBuilder("js", initEval.toString(), "init.js").buildLiteral());

        return context;
    }
	
	public void putMember(String name,Object obj)
	{
		this.bindings.putMember(name, obj);
	}

//	public ScriptEngine getScriptEngine()
//	{
//		return scriptEng;
//	}

	public synchronized void scriptEval(String jstxt) throws ScriptException
	{
		scriptEng.eval("js",jstxt) ;
	}
	
	public synchronized Object scriptEvalSrc(Source sor)
	{
		Value val = this.scriptEng.eval(sor) ;
		return val.as(Object.class) ;
	}
	
	public synchronized Object scriptInvoke(String fn,Object... paramvals) throws NoSuchMethodException, ScriptException
	{
		//GraalJSScriptEngine se = (GraalJSScriptEngine)getScriptEngine() ;
		//Value func = se.getPolyglotContext().getBindings("js").getMember(fn) ;
		Value func = this.bindings.getMember(fn) ;
		Value res = func.execute(paramvals) ;
		return res.as(Object.class) ; 
		
//		Invocable inv = (Invocable)getScriptEngine() ;
//		return inv.invokeFunction(fn, paramvals) ;
	}
	
	public  Source scriptCompile(boolean bblock,String jstxt) throws ScriptException
	{
		synchronized(UAContext.class)
		{
//			Compilable cp = (Compilable)getScriptEngine() ;
//			CompiledScript cs = cp.compile(jstxt) ;
//			if(bblock)
//				cs.eval() ;
//			return cs ;
			
			return Source.newBuilder("js", jstxt, null)
            .cached(true)  // 
            .buildLiteral();
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
			debug.setOutPipe(pw);
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
			debug.setOutPipe(null);
		}
		
	}
}
