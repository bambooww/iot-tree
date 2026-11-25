package org.iottree.core.msgnet.cxt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.graalvm.polyglot.Value;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UAPrj;
import org.iottree.core.cxt.JSContext;
import org.iottree.core.msgnet.IMNContainer;
import org.iottree.core.msgnet.MNBase;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.plugin.PlugJsApi;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;
import org.iottree.core.util.js.GUtil;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

public class MNContext extends JSContext
{

	//UAPrj prj ;
	
	IMNContainer container ;
	
	MNNet net ;
	
	public MNContext(MNNet net) throws ScriptException
	{
		this(net,null) ;
	}

	public MNContext(MNNet net,MNBase node) throws ScriptException
	{
		super() ;
		//this.prj = net.getPrj() ;
		
		this.container = net.getContainer() ;
		
		this.net = net ;

		//scriptEng.put("$_net_", net);
		asCxtNameOb("net",net) ; 
		asCxtNameOb("node",node) ;
		
		if(this.container instanceof UAPrj)
		{
			UAPrj prj = (UAPrj)this.container ;
		
//			scriptEng.put("$_prj_", prj);//prj.getJSOb());
//			String init_eval = "const $prj=$_prj_;Object.freeze($prj);";
			asCxtNameOb("prj",prj) ;
			
//			List<UACh> chs = prj.getChs() ;
//			if(related_dev_id2tt!=null && related_dev_id2tt.size()>0 && chs!=null && chs.size()>0)
//			{
//				for(UACh ch:chs)
//				{
//					ArrayList<UADev> r_devs = new ArrayList<>() ;
//					for(UADev dev:ch.getDevs())
//					{
//						String did = dev.getId() ;
//						if(related_dev_id2tt.containsKey(did))
//						{
//							String t = related_dev_id2tt.get(did) ;
//							dev.setUseTitle(t);
//							r_devs.add(dev) ;
//						}
//					}
//					
//					if(r_devs.size()<=0)
//						continue ;
//					for(UADev rdev:r_devs)
//					{
//						String varn = ch.getName()+"$"+rdev.getName() ;
//						asNameOb(varn,rdev) ;
//					}
//				}
//			}
		
//		UACh ch = nodecxt.getBelongToCh() ;
//		UADev dev = nodecxt.getBelongToDev() ;
//		if(ch!=null)
//		{
//			scriptEng.put("$_ch_", ch);
//			init_eval += "const $ch=$_ch_;Object.freeze($ch);";
//		}
//		if(dev!=null)
//		{
//			scriptEng.put("$_dev_", dev);
//			init_eval += "const $dev=$_dev_;Object.freeze($dev);";
//		}
//		
//		init_eval += "const $this=$_this_;Object.freeze($this);" ;
//		
//		List<JsProp> jsnames = nodecxt.JS_props() ;
//		for(JsProp o:jsnames)
//		{
//			String n = o.getName() ;
//			if(n==null||n.equals(""))
//				continue ;
//			Object v = nodecxt.JS_get(n) ;
//			if(v!=null)
//				scriptEng.put(n, v);
//		}
		
		}
	}
	
	

//	public static final String JS_NAME="graal.js";//"nashorn"; //
//
//	private ScriptEngine createJSEngine() throws ScriptException
//	{
//		ScriptEngineManager manager = new ScriptEngineManager();
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
	

//	public ScriptEngine getScriptEngine()
//	{
//		return scriptEng;
//	}

//	public synchronized void scriptEval(String jstxt) throws ScriptException
//	{
//		scriptEng.eval(jstxt) ;
//	}
	
//	public synchronized Object scriptInvoke(String fn,Object... paramvals) throws NoSuchMethodException, ScriptException
//	{
//		GraalJSScriptEngine se = (GraalJSScriptEngine)getScriptEngine() ;
//		//se.getContext();
//		//long st = System.currentTimeMillis() ;
//		Value func = se.getPolyglotContext().getBindings("js").getMember(fn) ;
//		long et1= System.currentTimeMillis() ;
//		//System.out.println("c1="+(et1-st)) ;
//		Value res = func.execute(paramvals) ;
//		//System.out.println("c2="+(System.currentTimeMillis()-et1)) ;
//		//Invocable inv = (Invocable)getScriptEngine() ;
//		//inv.
//		//return inv.invokeFunction(fn, paramvals) ;
//		return transGraalValueToMapListObj(res) ;
//	}
	
	
	
//	public  CompiledScript scriptCompile(boolean bblock,String jstxt) throws ScriptException
//	{
//		synchronized(MNContext.class)
//		{
//			Compilable cp = (Compilable)getScriptEngine() ;
//			CompiledScript cs = cp.compile(jstxt) ;
//			if(bblock)
//				cs.eval() ;
//			return cs ;
//		}
//	}
	/**
	 * templary code to run
	 * 
	 * @param txt
	 * @return
	 * @throws Exception
	 */
	public Object runCode(String txt) throws Exception
	{
		MNCodeItem ci = new MNCodeItem("", txt);
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
