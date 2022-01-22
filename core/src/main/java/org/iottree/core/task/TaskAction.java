package org.iottree.core.task;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.graalvm.polyglot.HostAccess;
import org.iottree.core.UAPrj;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;

/**
 * one task
 * 
 * @author jason.zhu
 *
 */
@data_class
public class TaskAction
{
	@data_val
	String id = null;

	@data_val
	String name = null;

	@data_val
	String desc = null;
	
	@data_val(param_name = "enable")
	boolean bEnable = true ;
	/**
	 * run once before run
	 */
	@data_val(param_name = "init_script")
	String initScript = null;

	/**
	 * run interval script
	 */
	@data_val(param_name = "run_script")
	String runScript = null;

	/**
	 * run once when task is stoping.
	 */
	@data_val(param_name = "end_script")
	String endScript = null;

	private transient boolean bInitScriptOk = false, bRunScriptReady = false, bEndScriptReady = false;

	private transient String initScriptErr = null;

	private transient boolean bLastRunOk = false;

	// private transient String runScriptErr = null ;

	// private transient boolean bEndScriptOk = false;

	private transient long lastRunDT = -1;

	private transient String lastRunErr = null;

	transient Task task = null;

	public TaskAction()
	{
		this.id = CompressUUID.createNewId();
	}
	
	public Task getTask()
	{
		return this.task ;
	}

	public String getId()
	{
		return id;
	}

	public TaskAction withId(String id)
	{
		this.id = id;
		return this;
	}

	@HostAccess.Export
	public String getName()
	{
		return name;
	}

	public TaskAction withName(String n)
	{
		this.name = n;
		return this;
	}

	public String getDesc()
	{
		return desc;
	}

	public TaskAction withDesc(String d)
	{
		this.desc = d;
		return this;
	}
	

	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public TaskAction withEnable(boolean b)
	{
		this.bEnable = b ;
		return this ;
	}

	public String getInitScript()
	{
		if (this.initScript == null)
			return "";
		return this.initScript;
	}

	public TaskAction withInitScript(String c)
	{
		this.initScript = c;
		return this;
	}
	
	public boolean hasInitScript()
	{
		return Convert.isNotNullEmpty(this.initScript) ;
	}

	public String getRunScript()
	{
		if (this.runScript == null)
			return "";
		return this.runScript;
	}

	public TaskAction withRunScript(String c)
	{
		this.runScript = c;
		return this;
	}
	
	public boolean hasRunScript()
	{
		return Convert.isNotNullEmpty(this.runScript) ;
	}

	public String getEndScript()
	{
		if (this.endScript == null)
			return "";
		return this.endScript;
	}

	public TaskAction withEndScript(String c)
	{
		this.endScript = c;
		return this;
	}
	
	public boolean hasEndScript()
	{
		return Convert.isNotNullEmpty(this.endScript) ;
	}

	public boolean isValid()
	{
		return true;
	}

	public boolean isInitScriptOk()
	{
		return bInitScriptOk;
	}

	boolean initTaskAction()
	{
		if(!this.isEnable())
			return false;
		
		
		ScriptEngine engine = this.task.getScriptEngine();
		engine.put("$task_action", this);
		// reg function

		try
		{
			//engine.eval("var __JsTaskAction_global_" + this.getId() + "={}\r\n");

			if (Convert.isNotNullTrimEmpty(this.initScript))
			{
				//engine.eval(this.initScript) ;
				this.task.getContext().scriptEval(this.initScript);
			}

			if (Convert.isNotNullTrimEmpty(this.runScript))
			{
				this.task.getContext().scriptEval("function __JsTaskAction_run_" + this.getId() + "(){\r\n"
						//+ "var $global=__JsTaskAction_global_" + this.getId() + ";\r\n" + this.runScript + "}\r\n");
						+ this.runScript + "\r\n}");
				this.bRunScriptReady = true;
			}

			
			// engine.eval(reader)
			this.initScriptErr = null;
			// js set ok

			bInitScriptOk = true;
		}
		catch ( Exception ee)
		{
			System.out.println("Task ["+this.task.getName()+"] init action ["+this.getName()+"] err") ;
			ee.printStackTrace();
			this.initScriptErr = ee.getMessage();
			bInitScriptOk = false;
		}

		return bInitScriptOk;
	}

	void runInterval(UAPrj p)
	{
		if(!this.isEnable())
			return;
		
		if (!bRunScriptReady)
			return;

		try
		{
			this.task.getContext().scriptInvoke("__JsTaskAction_run_" + this.getId());
			//Invocable jsInvoke = (Invocable) this.task.getScriptEngine();
			//jsInvoke.invokeFunction("__JsTaskAction_run_" + this.getId());
			this.bLastRunOk = true;
		}
		catch ( ScriptException se)
		{
			this.lastRunErr = se.getMessage();
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			lastRunDT = System.currentTimeMillis();
		}
	}

	public void runEnd(UAPrj p)
	{
		if(!this.isEnable())
			return;
//		if (!bEndScriptReady)
//			return;

		try
		{
			if (Convert.isNotNullTrimEmpty(this.endScript))
			{
//				engine.eval("function __JsTaskAction_end_" + this.getId() + "(){\r\n"
//						+ "var $global=__JsTaskAction_global_" + this.getId() + ";\r\n" + this.endScript + "}\r\n");
				 //this.task.getScriptEngine().eval(this.endScript) ;
				 this.task.getContext().scriptEval(this.endScript);
				//this.bEndScriptReady = true;
			}

			//Invocable jsInvoke = (Invocable) p.getJSEngine();
			//jsInvoke.invokeFunction("__JsTaskAction_end_" + this.getId(), p.getJSOb(), this);
		}
		catch ( ScriptException se)
		{
			this.lastRunErr = se.getMessage();
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// lastRunDT = System.currentTimeMillis();
		}
	}
}
