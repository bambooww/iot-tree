package org.iottree.core.devtree.cxt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.function.Predicate;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.iottree.core.cxt.JsProp;
import org.iottree.core.devtree.DTNode;
import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;
import org.iottree.core.util.js.GUtil;


public class DevContext implements AutoCloseable
{
	
	public static Debug debug = new Debug();

	public static GSys sys = new GSys();

	public static GUtil util = new GUtil();

	private transient ScriptEngine scriptEng = null;

	
	DTNode devNode;

	// DevNode rootNode ;
	// public static final String FN_TEMP_VAR = "_ua_cxt_tmp_var_";

	public DevContext(DTNode dn) throws ScriptException
	{
		this.devNode = dn;

		checkAndReset();
	}

	public synchronized void checkAndReset() throws ScriptException
	{
		this.close();

		// 2. 重新创建一个干净的引擎，彻底扔掉积攒了数十万个 AST 节点的旧引擎
		scriptEng = createJSEngine();

		scriptEng.put("__dev_node_", devNode);
		// scriptEng.put("$_prj_", prj);//prj.getJSOb());

		String init_eval = "";// "const $prj=$_prj_;Object.freeze($prj);";

		init_eval += "const $node=__dev_node_;Object.freeze($node);";

		List<JsProp> jsnames = this.devNode.JS_props();
		for (JsProp o : jsnames)
		{
			String n = o.getName();
			if (n == null || n.equals(""))
				continue;
			if (n.startsWith("_"))
				continue;// plug must use $this.$plugn
			Object v = this.devNode.JS_get(n);
			if (v != null)
				scriptEng.put(n, v);
		}

		scriptEng.eval(init_eval);
	}

	// public UAContext asTask(Task task) throws ScriptException
	// {
	// scriptEng.put("$_task_", task);
	// scriptEng.eval("const $task=$_task_;Object.freeze($task);") ;
	// return this ;
	// }

	private static final String JS_NAME = "graal.js";// "nashorn"; //
	
	private static boolean bFirst = true ;

	private static ScriptEngine createJSEngine() throws ScriptException
	{
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName(JS_NAME);

		// 2026加入测试
		if(bFirst)
		{
			System.out.println("create js engine>>") ;
			// 1. 核心配置：限制 Truffle 节点的内联和投机优化层级，防止高频调用时节点暴涨
			engine.put("polyglot.engine.BackgroundCompilation", true); // 异步编译，减少阻塞
			try
			{
					if(!"handler".equals(engine.get("polyglot.js.unhandled-rejections")))
						engine.put("polyglot.js.unhandled-rejections", "handler"); // 优化 Promise
																// 节点的内存
					// 2. 告诉底层引擎放松对象属性共享和缓存（这对 javax.script 长期运行非常有效）
					engine.put("polyglot.engine.RelaxedSharing", true);
					engine.put("polyglot.js.allowHostAccess", true);
					engine.put("polyglot.js.allowAllAccess", false);
					engine.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
					
			
					// 添加配置，支持本地java对接（找了很多资料才找到）
					Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
			
					// 可以考虑这个，开启一切可开启的..
					bindings.put("polyglot.js.allowHostAccess", true);
					bindings.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
					bFirst = false;
				
			}
			catch(Exception ee)
			{
				System.out.println("warn:"+ee.getMessage());
			}
			finally
			{
				bFirst = false;
			}
		}
				
		engine.put("__debug_", debug);
		engine.put("__sys_", sys);
		engine.put("__util_", util);

		String init_eval = "const $debug=__debug_;Object.freeze($debug);" + "const $sys=__sys_;Object.freeze($sys);"
		// + "const $dict=$_dict_;Object.freeze($dict);"
				+ "const $util=__util_;Object.freeze($util);";
		engine.eval(init_eval);
		return engine;
	}

	public ScriptEngine getScriptEngine()
	{
		return scriptEng;
	}

	public synchronized void scriptEval(String jstxt) throws ScriptException
	{
		scriptEng.eval(jstxt);
	}

	public synchronized Object scriptInvoke(String fn, Object... paramvals)
			throws NoSuchMethodException, ScriptException
	{
		Invocable inv = (Invocable) getScriptEngine();
		return inv.invokeFunction(fn, paramvals);
	}

	public CompiledScript scriptCompile(boolean bblock, String jstxt) throws ScriptException
	{
		synchronized (DevContext.class)
		{
			Compilable cp = (Compilable) getScriptEngine();
			CompiledScript cs = cp.compile(jstxt);
			if (bblock)
				cs.eval();
			return cs;
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
		DevNodeCodeItem ci = new DevNodeCodeItem("", txt);
		try
		{
			ci.initItem(this);
			return ci.runCode();
		}
		finally
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
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		try
		{
			debug.setOutPipe(pw);
			this.scriptEval(jstxt);
			pw.println("--- test script end ---");
			pw.flush();
			return sw.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace(pw);
			pw.flush();
			return sw.toString();
		}
		finally
		{
			debug.setOutPipe(null);
		}

	}

	@Override
	public void close() //throws Exception
	{
		if (this.scriptEng != null)
		{//1. 清理旧引擎的全局绑定，切断与 Java 对象的强引用
			Bindings b = this.scriptEng.getBindings(ScriptContext.ENGINE_SCOPE);
			if (b != null)
				b.clear();
		}
	}

}
