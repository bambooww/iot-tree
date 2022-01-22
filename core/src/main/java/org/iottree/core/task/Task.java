package org.iottree.core.task;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.graalvm.polyglot.HostAccess;
import org.iottree.core.UACh;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.UAPrj.JSOb;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.node.PrjSharer;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;

/**
 * A task corresponds to a running thread, and there can be one or more actions
 * inside. Note that all actions share a thread, and there may be related delay
 * effects between actions.
 * 
 * @author jason.zhu
 */
@data_class
public class Task
{
	public static long DEFAULT_INT_MS = 10000;
	@data_val
	String id = null;

	@data_val
	String name = null;
	
	@data_val
	String title = null ;

	@data_val
	String desc = null;
	
	@data_val(param_name = "enable")
	boolean bEnable = false ;

	@data_val(param_name = "int_ms")
	long intervalMS = DEFAULT_INT_MS;

	@data_obj(param_name = "actions", obj_c = TaskAction.class)
	List<TaskAction> actions = new ArrayList<>();

	private transient String prjId = null ;
	
	//private transient UAPrj prj = null;

	private transient boolean bLastRunOk = false;

	// private transient String runScriptErr = null ;

	// private transient boolean bEndScriptOk = false;

	private transient long lastRunDT = -1;

	private transient String lastRunErr = null;

	public Task(String prjid)
	{
		this.prjId = prjid;
		this.id = CompressUUID.createNewId();
	}

	public UAPrj getPrj()
	{
		return UAManager.getInstance().getPrjById(this.prjId) ;
	}

	public String getId()
	{
		return id;
	}

	public Task withId(String id)
	{
		this.id = id;
		return this;
	}
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public Task withEnable(boolean b)
	{
		this.bEnable = b ;
		return this ;
	}

	@HostAccess.Export
	public String getName()
	{
		return name;
	}

	public Task withName(String n)
	{
		StringBuilder sb = new StringBuilder();
		if(!Convert.checkVarName(n, true, sb))
			throw new IllegalArgumentException(sb.toString()) ;
		this.name = n;
		return this;
	}
	
	public String getTitle()
	{
		if(this.title==null)
			return "" ;
		return this.title ;
	}
	
	public Task withTitle(String t)
	{
		this.title = t ;
		return this ;
	}

	public String getDesc()
	{
		return desc;
	}

	public Task withDesc(String d)
	{
		this.desc = d;
		return this;
	}

	public long getIntervalMS()
	{
		return this.intervalMS;
	}

	public Task withIntervalMS(long ms)
	{
		if (ms <= 0)
			throw new IllegalArgumentException("interval ms must >0");
		this.intervalMS = ms;
		return this;
	}
	
	void refreshActions()
	{
		for (TaskAction ta : this.actions)
		{
			ta.task = this ;
		}
	}

	public List<TaskAction> getActions()
	{
		return this.actions;
	}

	public TaskAction getActionById(String id)
	{
		for (TaskAction ta : this.actions)
		{
			if (ta.getId().equals(id))
				return ta;
		}
		return null;
	}

	public TaskAction setAction(TaskAction ta)
	{
		int s = this.actions.size();
		for (int i = 0; i < s; i++)
		{
			TaskAction act = this.actions.get(i);
			if (act.getId().equals(ta.getId()))
			{
				this.actions.set(i, ta);
				return ta;
			}
		}

		this.actions.add(ta);
		
		refreshActions();
		return ta;
	}

	public TaskAction delAction(String actid)
	{
		int s = this.actions.size();
		for (int i = 0; i < s; i++)
		{
			TaskAction act = this.actions.get(i);
			if (act.getId().equals(actid))
			{
				return this.actions.remove(i);
			}
		}
		return null;
	}
	
	
	public void save() throws Exception
	{
		TaskManager.getInstance().saveTask(this.prjId, this);
	}

	public boolean isValid()
	{
		if (this.actions == null || actions.size() <= 0)
			return false;
		boolean r = false;
		for (TaskAction ta : actions)
		{
			if (ta.isValid())
				r = true;
		}
		return r;
	}

	public long getLastRunDT()
	{
		return this.lastRunDT;
	}

	public String getLastRunErr()
	{
		return this.lastRunErr;
	}

	private Thread rtTh = null;

	private volatile boolean rtRun = false;

	private ScriptEngine scriptEng = null;

	private UAContext cxt = null;

	private boolean taskRunInit()
	{
		getContext() ;
		
		// scriptEng = this.prj.
		boolean bret = false;
		for (TaskAction ta : this.actions)
		{
			if(!ta.isEnable())
				continue ;
			if(ta.initTaskAction())
			{
				bret = true ;
			}
		}
		return bret ;
	}

	public ScriptEngine getScriptEngine()
	{
		if(scriptEng!=null)
			return scriptEng;
		
		getContext();
		return scriptEng;
	}
	
	public UAContext getContext()
	{
		if(cxt!=null)
			return cxt ;
		
		synchronized(this)
		{
			if(cxt!=null)
				return cxt ;
			
			cxt = new UAContext(this.getPrj());
			scriptEng = cxt.getScriptEngine();// .getClass();UAManager.createJSEngine(this.prj)
			// ;
			scriptEng.put("$task", this);
			return cxt ;
		}
	}

	private void taskRunInt()
	{
		for (TaskAction ta : this.actions)
		{
			if(!ta.isEnable())
				continue ;
			ta.runInterval(this.getPrj());
		}
	}

	private void taskRunEnd()
	{
		for (TaskAction ta : this.actions)
		{
			if(!ta.isEnable())
				continue ;
			ta.runEnd(this.getPrj());
		}
	}

	private Runnable taskRunner = new Runnable() {
		public void run()
		{
			if(!taskRunInit())
			{
				System.out.println("project ["+getPrj().getName()+"] task ["+Task.this.getName()+"] init failed,it cannot run") ;
				return ;
			}
			else
			{
				System.out.println("project ["+getPrj().getName()+"] task ["+Task.this.getName()+"] init ok to run") ;
			}
			
			try
			{
				while (rtRun)
				{
					Thread.sleep(intervalMS);
					taskRunInt();
				}
			}
			catch ( InterruptedException ie)
			{
				// do nothing
			}
			catch ( Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				taskRunEnd();

				rtRun = false;
				rtTh = null;
			}
		}
	};

	synchronized public boolean RT_start()
	{
		if(!this.isEnable())
			return false;
		
		if (!isValid())
			return false;

		if (rtTh != null)
			return true;

		rtTh = new Thread(taskRunner, "iottree-task-" + this.getPrj().getName() + "-" + this.getName());
		rtRun = true;
		rtTh.start();
		return true;
	}

	public void RT_stop()
	{
		Thread t = rtTh;
		if (t == null)
			return;

		t.interrupt();
		taskRunEnd();
		rtTh = null;
		rtRun = false;
	}

	public boolean RT_isRunning()
	{
		return rtTh != null;
	}

	public class JSOb
	{

	}
	
}
