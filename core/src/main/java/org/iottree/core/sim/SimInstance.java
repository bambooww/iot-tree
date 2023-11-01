package org.iottree.core.sim;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.iottree.core.cxt.JsProp;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;

@data_class
public class SimInstance extends SimNode
{
	@data_val(param_name = "en")
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

	@data_val(param_name = "auto_start")
	boolean bAutoStart = false;
	/**
	 * run once when task is stoping.
	 */
	@data_val(param_name = "end_script")
	String endScript = null;
	
	
	private transient boolean bInitScriptOk = false, bRunScriptReady = false, bEndScriptReady = false;
	
	private transient boolean bLastRunOk = false;

	// private transient String runScriptErr = null ;

	// private transient boolean bEndScriptOk = false;

	private transient long lastRunDT = -1;

	private transient String lastRunErr = null,initScriptErr=null;
	
	private transient Thread insTh = null ;
	
	private SimContext cxt = null ;
	//@data_obj(obj_c = SimChannel.class,param_name = "chs")
	private ArrayList<SimChannel> channels = null;//new ArrayList<>() ; 
	
	public SimInstance()
	{
		//this.id = CompressUUID.createNewId();
	}
	
	public SimInstance withBasic(SimChannel osch)
	{
		this.name = osch.name ;
		this.title = osch.title ;
		this.bEnable = osch.bEnable ;
		return this ;
	}
	
	public List<SimChannel> getChannels()
	{
		if(channels!=null)
			return channels ;
		
		synchronized(this)
		{
			if(channels!=null)
				return channels ;
			
			channels = loadChannels() ;
			return channels ;
		}
	}
	
	File getInsDir()
	{
		return SimManager.getInstance().getInsDir(this.id) ;
	}
	
	private ArrayList<SimChannel> loadChannels()
	{
		ArrayList<SimChannel> rets=  new ArrayList<>() ;
		File dir = getInsDir() ;
		if(!dir.exists())
			return rets ;
		
		final FilenameFilter ff = new FilenameFilter()
				{

					@Override
					public boolean accept(File dir, String name)
					{
						return name.startsWith("ch_")&&name.endsWith(".xml");
					}
			
				};
		File[] chfs = dir.listFiles(ff) ;
		if(chfs==null||chfs.length<=0)
			return rets ;
		for(File chf:chfs)
		{
			try
			{
				SimChannel ch = loadChannel(chf) ;
				if(ch==null)
					continue ;
				ch.belongTo = this;
				ch.init();
				rets.add(ch) ;
			}
			catch(Exception e)
			{
				System.out.println(" warn:load channel failed - "+chf.getName()) ;
			}
		}
		return rets ;
	}
	
	private SimChannel loadChannel(File f) throws Exception
	{
		if(!f.exists())
			return null ;
		XmlData tmpxd = XmlData.readFromFile(f);
		String cn = tmpxd.getParamValueStr(DataTranserXml.PN_CLASSN);
		if(Convert.isNullOrEmpty(cn))
			return null ;
		SimChannel sc = (SimChannel)Class.forName(cn).newInstance() ;
		DataTranserXml.injectXmDataToObj(sc, tmpxd);
		
		return sc ;
	}
	
	void saveChannel(SimChannel sc) throws Exception
	{
		File dir = getInsDir() ;
		if(!dir.exists())
			dir.mkdirs() ;
		
		File f = new File(dir,"ch_"+sc.getId()+".xml") ;
		XmlData xd = DataTranserXml.extractXmlDataFromObj(sc) ;
		//XmlData xd = rep.toUAXmlData();
		xd.setParamValue(DataTranserXml.PN_CLASSN, sc.getClass().getCanonicalName());
		XmlData.writeToFile(xd, f);
	}
	
	public boolean delChannel(String chid)
	{
		SimChannel ch = this.getChannel(chid) ;
		if(ch==null)
			return false;
		File dir = getInsDir() ;
		File f = new File(dir,"ch_"+chid+".xml") ;
		if(!f.exists())
			return false;
		f.delete() ;
		this.getChannels().remove(ch);
		return true ;
	}
	
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public boolean isAutoStart()
	{
		return this.bAutoStart;
	}

	public void setAutoStart(boolean b) throws Exception
	{
		if (this.bAutoStart == b)
			return;
		this.bAutoStart = b;
		this.saveSelf();
	}


	public SimInstance withEnable(boolean b)
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

	public SimInstance withInitScript(String c)
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

	public SimInstance withRunScript(String c)
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

	public SimInstance withEndScript(String c)
	{
		this.endScript = c;
		return this;
	}
	
	public boolean hasEndScript()
	{
		return Convert.isNotNullEmpty(this.endScript) ;
	}
	
	public long getSavedDT()
	{
		File f = SimManager.getInstance().getInsDir(this.getId()) ;
		if(!f.exists())
			return -1 ;
		return f.lastModified() ;	
	}
	
	public SimChannel getChannel(String chid)
	{
		for(SimChannel sc:getChannels())
		{
			if(chid.equals(sc.getId()))
				return sc ;
		}
		return null ;
	}
	
	public SimChannel getChannelByName(String n)
	{
		for(SimChannel sc:getChannels())
		{
			if(n.equals(sc.getName()))
				return sc ;
		}
		return null ;
	}
	
	public SimChannel setChannelBasic(SimChannel sch) throws Exception
	{
		String n = sch.getName() ;
		if(Convert.isNullOrEmpty(n))
			throw new Exception("name cannot be null or empty") ;
		StringBuilder sb=  new StringBuilder() ;
		if(!Convert.checkVarName(n, true, sb))
			throw new Exception(sb.toString()) ;
		
		String chid = sch.getId() ;
		SimChannel oldch = this.getChannel(chid) ;
		if(oldch!=null)
		{
			SimChannel tmpch = this.getChannelByName(n) ;
			if(tmpch!=null&&tmpch!=oldch)
				throw new Exception("Channel name ["+n+"] is already existed!") ;
		}
		else
		{
			SimChannel tmpch = this.getChannelByName(n) ;
			if(tmpch!=null)
				throw new Exception("Channel name ["+n+"] is already existed!") ;
		}
		
		List<SimChannel> chs = getChannels() ;
		int s = chs.size();
		for (int i = 0; i < s; i++)
		{
			SimChannel ch = chs.get(i);
			if (ch.getId().equals(sch.getId()))
			{
				ch.name = sch.name ;
				ch.title = sch.title ;
				ch.bEnable = sch.bEnable ;
				ch.save();
				ch.init();
				return ch;
			}
		}

		sch.belongTo = this;
		chs.add(sch);
		sch.save();
		sch.init();
		//refreshActions();
		return sch;
	}
	
	public SimContext getContext()
	{
		if(cxt!=null)
			return cxt ;
		
		synchronized(this)
		{
			if(cxt!=null)
				return cxt ;
			
			cxt = new SimContext(this);
			ScriptEngine se = cxt.getScriptEngine();// .getClass();UAManager.createJSEngine(this.prj)
			// ;
			se.put("$ins", this);
			return cxt ;
		}
	}
	
	public boolean isInitScriptOk()
	{
		return bInitScriptOk;
	}
	
	public Object JS_get(String  key)
	{
		Object ob = super.JS_get(key) ;
		if(ob!=null)
			return ob ;
		
		return this.getChannelByName(key) ;
	}
	
	public List<JsProp> JS_props()
	{
		List<JsProp> ss = super.JS_props() ;
		for(SimChannel ch:this.getChannels())
		{
			ss.add(new JsProp(ch.getName(),ch,SimChannel.class,true,ch.getTitle(),"")) ;
		}
		return ss ;
	}

	boolean initInstance(StringBuilder failedr)
	{
		if(!this.isEnable())
		{
			failedr.append("instance is disabled") ;
			return false;
		}
		
		
//		ScriptEngine engine = this.getScriptEngine();
//		engine.put("$task_action", this);
		// reg function

		try
		{
			//engine.eval("var __JsTaskAction_global_" + this.getId() + "={}\r\n");

			if (Convert.isNotNullTrimEmpty(this.initScript))
			{
				//engine.eval(this.initScript) ;
				this.getContext().scriptEval(this.initScript);
			}

			if (Convert.isNotNullTrimEmpty(this.runScript))
			{
				this.getContext().scriptEval("function __JsSimIns_run_" + this.getId() + "(){\r\n"
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
			System.out.println("SimInstance ["+this.getName()+"] init err") ;
			ee.printStackTrace();
			failedr.append(ee.getMessage()) ;
			this.initScriptErr = ee.getMessage();
			bInitScriptOk = false;
		}

		return bInitScriptOk;
	}

	void runInterval()
	{
		if(!this.isEnable())
			return;
		
		if (!bRunScriptReady)
			return;

		try
		{
			this.getContext().scriptInvoke("__JsSimIns_run_" + this.getId());
			//Invocable jsInvoke = (Invocable) this.task.getScriptEngine();
			//jsInvoke.invokeFunction("__JsTaskAction_run_" + this.getId());
			this.bLastRunOk = true;
		}
		catch ( ScriptException se)
		{
			this.lastRunErr = se.getMessage();
			System.err.println(se.getMessage()) ;
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

	public void runEnd()
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
				 this.getContext().scriptEval(this.endScript);
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
	
	public void saveSelf() throws Exception
	{
		SimManager.getInstance().saveInstance(this); ;
	}
	
	private Runnable insRunner = new Runnable()
			{

				@Override
				public void run()
				{
					try
					{
						while(insTh!=null)
						{
							try
							{
								Thread.sleep(10);
							}
							catch(Exception e) {}
							
							runInterval();
						}
					}
					finally
					{
						runEnd() ;
						//
						for(SimChannel ch:getChannels())
						{
							if(!ch.isEnable())
								continue ;
							ch.RT_stop();
						}
					}
				}
			};
	
	public synchronized boolean RT_start(StringBuilder failedr)
	{
		if(insTh!=null)
			return true ;
		
		if(!initInstance(failedr))
			return false;
		
		boolean ch_start_ok = true;
		for(SimChannel ch:this.getChannels())
		{
			if(!ch.isEnable())
				continue ;
			
			if(!ch.RT_start(failedr))
			{
				ch_start_ok = false;
				break ;
			}
		}
		
		if(!ch_start_ok)
		{
			for(SimChannel ch:this.getChannels())
			{
				if(!ch.isEnable())
					continue ;
				
				ch.RT_stop();
			}
			return false;
		}
		
		insTh = new Thread(insRunner);
		insTh.start(); 
		return true ;
	}
	
	public boolean RT_isRunning()
	{
		return insTh!=null ;
	}
	
	public synchronized void RT_stop()
	{
		insTh = null ;
	}
	
	public synchronized void RT_interrupt()
	{
		for(SimChannel ch:getChannels())
		{
			ch.RT_stop();
		}
		
		Thread t = insTh ;
		if(t==null)
			return ;
		t.interrupt();
		insTh = null ;
	}
}
