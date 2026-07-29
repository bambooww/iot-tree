package org.iottree.driver.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.iottree.core.Config;
import org.iottree.core.ConnException;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.plugin.PlugJsApi;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;
import org.iottree.core.util.js.GUtil;
import org.json.JSONObject;

public class CmdLineHandlerJS extends CmdLineHandler
{
	String name ;
	
	String title ;
	
	String desc ;
	
	int recvMaxLen = 1000 ;
	
	public CmdLineHandlerJS()
	{
		
	}
	
	public CmdLineHandlerJS asBasic(String name,String title,String desc)
	{
		this.name = name ;
		this.title = title ;
		this.desc = desc ;
		return this;
	}
	
	CmdLineHandlerJS(JSONObject jo)
	{
		this.name = jo.getString("n") ;
		this.title = jo.getString("t") ;
		this.desc = jo.optString("d") ;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getTitle()
	{
		return title;
	}

	@Override
	public String getDesc()
	{
		return desc;
	}

	@Override
	protected CmdLineHandler copyMe()
	{
		CmdLineHandlerJS r = new CmdLineHandlerJS();
		r.name = this.name ;
		r.title = this.title ;
		r.desc = this.desc ;
		r.recvMaxLen = this.recvMaxLen ;
		
		return r ;
	}

	@Override
	protected int getRecvMaxLen()
	{
		return recvMaxLen;
	}
	
	
	
	public static final String JS_NAME="graal.js";//"nashorn"; //
	
	static  Debug debug = new Debug() ;
	
	static GSys sys = new GSys() ;
	
	static GUtil util = new GUtil() ;
	
	private ScriptEngine se = null ;
	boolean bJustConn = false;
	boolean bConnOk = false;
	
	String recvTxt = null ;
	

	private ScriptEngine createJSEngine() throws ScriptException
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

		engine.put("$_debug_",debug);
		//engine.put("$system",sys);
		engine.put("$_sys_",sys);
		//engine.put("$_dict_",sys);
		engine.put("$_util_",util);
		
		String init_eval = "const $debug=$_debug_;Object.freeze($debug);"
				+ "const $sys=$_sys_;Object.freeze($sys);"
				//+ "const $dict=$_dict_;Object.freeze($dict);"
				+ "const $util=$_util_;Object.freeze($util);";
		
		engine.eval(init_eval);
		
		return engine ;
	}
	
	private ScriptEngine getJsEngine() throws ScriptException
	{
		if(se!=null)
			return se ;
		
		se = createJSEngine();
		return se ;	
	}
	
	public synchronized void scriptEval(String jstxt) throws ScriptException
	{
		getJsEngine().eval(jstxt) ;
	}
	
	public synchronized Object scriptInvoke(String fn,Object... paramvals) throws NoSuchMethodException, ScriptException
	{
		Invocable inv = (Invocable)getJsEngine() ;
		return inv.invokeFunction(fn, paramvals) ;
	}
	
	public synchronized Object scriptInvokeMethod(Object ob,String fn,Object... paramvals) throws NoSuchMethodException, ScriptException
	{
		Invocable inv = (Invocable)getJsEngine() ;
		return inv.invokeMethod(ob, fn, paramvals) ;
	}
	
	private String loadJsCode() throws IOException
	{
		File f = new File(Config.getDataDirBase()+"/dev_drv/cmd_line_js/"+this.name+".js") ;
		return Convert.readFileTxt(f, "UTF-8") ;
	}
	
	public static final String FN_INIT = "drv_init" ;

	public static final String FN_ON_CONN = "drv_on_conn" ;
	public static final String FN_ON_DISCONN = "drv_on_disconn" ;
	public static final String FN_RUN_IN_LOOP = "drv_run_in_loop" ;
	public static final String FN_ON_RECV = "drv_on_recv" ;

	private Object jsDrvOb = null ;
	
	@Override
	protected boolean init(CmdLineDrv cld,StringBuilder sb) throws Exception
	{
		if(!super.init(cld, sb))
			return false;
		
		String jscode = loadJsCode() ;
		jscode += "\r\n var __"+this.name+"=new JsDRV()" ;
		
		ScriptEngine se = getJsEngine() ;
		
		se.eval(jscode) ;
		jsDrvOb = se.get("__"+this.name) ;
		//init js engine and load js code
		
		scriptInvokeMethod(jsDrvOb,FN_INIT,this.name,this.title,this.belongTo,this.belongTo.getBelongToCh(),this) ;
		return true ;
	}
	
	

	@Override
	public void RT_onConned(ConnPtStream cpt)  throws Exception
	{
		super.RT_onConned(cpt);
		
		bJustConn = true ;
	}

	@Override
	public void RT_onDisconn(ConnPtStream cpt) throws Exception
	{
		super.RT_onDisconn(cpt);
		scriptInvokeMethod(jsDrvOb,FN_ON_DISCONN,cpt) ;
		bConnOk = false;
	}
	
	protected boolean RT_useNoWait()
	{
		return true;
	}
	
	private transient long lastLoopDT = -1 ;
	
	@Override
	public void RT_runInLoop(ConnPtStream cpt) throws Exception
	{
		if(bJustConn)
		{
			// on conned
			Object ret = scriptInvokeMethod(jsDrvOb,FN_ON_CONN,cpt) ;
			
			bConnOk = Boolean.TRUE.equals(ret) || "true".equalsIgnoreCase(ret.toString()) ;
			if(bConnOk)
				bJustConn = false;
			
			return ;
		}
		
		if(!bConnOk)
			return ;


		String recvtxt = gitRecvTxt() ;
		if(Convert.isNotNullEmpty(recvtxt))
		{
			scriptInvokeMethod(jsDrvOb,FN_ON_RECV,recvtxt) ;
		}
		
		//Thread.sleep(millis);
		if(System.currentTimeMillis()-lastLoopDT>=this.belongTo.getUsingInterval())
		{
			try
			{
				try
				{
					//run in loop
					scriptInvokeMethod(jsDrvOb,FN_RUN_IN_LOOP,cpt) ;
				}
				finally
				{
					lastLoopDT = System.currentTimeMillis() ;
				}
			}
			catch(Exception ee)
			{
				if(log.isDebugEnabled())
					log.debug("CmdLineHandlerJS "+FN_RUN_IN_LOOP+" error",ee);
				throw new ConnException(ee.getMessage()) ;//make conn close and ch driver is not stop
			}
		}
	}
	
	private synchronized String gitRecvTxt()
	{
		String r = this.recvTxt;
		this.recvTxt = null ;
		return r ;
	}

	@Override
	synchronized public void RT_onRecved(String cmd) throws Exception
	{
		recvTxt = cmd ;
	}

}
