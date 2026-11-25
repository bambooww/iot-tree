package org.iottree.core.cxt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.script.ScriptException;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.iottree.core.msgnet.cxt.MNContext;
import org.iottree.core.plugin.PlugJsApi;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;
import org.iottree.core.util.js.GUtil;

public class JSContext
{
	
	
	public static  Debug debug = new Debug() ;
	
	public static GSys sys = new GSys() ;
	
	public static GUtil util = new GUtil() ;
	
	protected transient Context scriptEng = null;
	protected transient Value bindings = null ;
	
	public JSContext()
	{
		scriptEng = createJSContext();//createJSEngine();
		bindings = scriptEng.getBindings("js");
	}
	

	private Context createJSContext()
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
	
	public JSContext asCxtNameOb(String name,Object obj) throws ScriptException
	{
		putMember("$_"+name+"_", obj);
		scriptEng.eval("js","const $"+name+"=$_"+name+"_;Object.freeze($"+name+");") ;
		return this ;
	}
	
	public JSContext asNameOb(String name,Object obj) throws ScriptException
	{
		putMember("_"+name+"_", obj);
		scriptEng.eval("js","const "+name+"=_"+name+"_;Object.freeze("+name+");") ;
		return this ;
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
		//return res.as(Object.class) ; 
		return transGraalValueToMapListObj(res) ;
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
