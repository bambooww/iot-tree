package org.iottree.core;

import java.io.Closeable;
import java.util.*;

import org.iottree.core.DevAddr.ChkRes;
import org.iottree.core.DevAddr.IAddrDef;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.*;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsDef;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

/**
 * belong to channel and may limit devices in channel 1)driver self may has some
 * configuration 2)every device in channel may has its own configuration related
 * driver
 * 
 * @author zzj
 */
public abstract class DevDriver extends JSObMap implements IPropChecker
{
	protected static ILogger log = LoggerManager.getLogger(DevDriver.class);

	public static enum State
	{
		not_run(0), running(2), run_stopping(1);

		private final int val;

		State(int v)
		{
			val = v;
		}

		public int getInt()
		{
			return val;
		}

		public boolean isRunning()
		{
			return val == 2 || val == 1;
		}
	}
	
	public static void sleep(long ms)
	{
		try
		{
			Thread.sleep(ms);
		}catch(Exception e) {}
	}

	final class SubDevThread extends Thread
	{
		UADev subDev = null;

		ConnPt connPt = null;

		public SubDevThread(UADev subdev, ConnPt cpt)
		{
			subDev = subdev;
			this.connPt = cpt;
		}

		public ConnPt getConnPt()
		{
			return this.connPt;
		}

		public UADev getDev()
		{
			return subDev;
		}

		@Override
		public void run()
		{
			
			long drv_int = subDev.getOrDefaultPropValueLong("dev", "dev_intv", 100);
			if (drv_int < 0)
				drv_int = DevDriver.this.getBelongToCh().getDriverIntMS();
			if (drv_int < 0)
				drv_int = 1000;
			
			//subDev.getOrDefaultPropValueLong("", itemn, defv)
			try
			{
				StringBuilder failedr = new StringBuilder();
				while (bRun)
				{
					try
					{
						sleep(drv_int);
						
						if(!this.connPt.isConnReady())
							this.connPt.RT_checkConn();

						if(!this.connPt.isConnReady())
							continue ;
						
						//System.out.println("-----1-------");
						if (!RT_runInLoop(belongToCh, subDev, failedr))
							break;
					}
					catch ( InterruptedException ie)
					{
						if(log.isTraceEnabled())
							log.trace(ie);
					}
					catch (ConnException e)
					{// ConnException will not stop driver
						if(this.connPt instanceof Closeable)
						{
							try
							{
								((Closeable)this.connPt).close();
							}
							catch(Exception eee) {}
						}
						
						if(log.isDebugEnabled())
							log.debug(e);
					}
					finally
					{
						//System.out.println("-----2-------");
					}
				}
			}
			catch ( Exception e)
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			finally
			{
				stopDriver();
			}
		}
	}

	public static class Model
	{
		String name = null ;
		
		String title = null ;
		
		public Model(String name,String t)
		{
			this.name = name ;
			this.title = t ;
		}
		
		public String getName()
		{
			return name ;
		}
		
		public String getTitle()
		{
			return title ;
		}
		
		/**
		 * 
		 * @return null表示不支持
		 */
		public List<IAddrDef> getAddrDefs()
		{
			return null ;
		}
		
		/**
		 * not null will has owner addr help page
		 * @return
		 */
		public String getAddrHelpUrl(String lan)
		{
			return null;
		}
//		/**
//		 * impl will provider special prop for devive in ch
//		 * @param dev
//		 * @return
//		 */
//		public HashMap<String,String> getPropMapForDevInCh(UADev dev)
//		{
//			return null ;
//		}
	}
	// ---------------------dev cat and device

	// -------driver self
	UACh belongToCh = null;

	/**
	 * support running instance in ch with porp parameter's
	 * 
	 * @return
	 */
	public abstract DevDriver copyMe();

	public abstract String getName();

	public abstract String getTitle();

	public String getDesc()
	{
		return "";
	}

	/**
	 * driver may setup in ch
	 * 
	 * @return
	 */
	public UACh getBelongToCh()
	{
		return belongToCh;
	}

	
	public List<Model> getDevModels()
	{
		return null ;
	}
	
	final public Model getDevModel(String name)
	{
		List<Model> ms = this.getDevModels();
		if(ms==null)
			return null ;
		
		for(Model m:ms)
		{
			if(m.name.equals(name))
				return m ;
		}
		return null ;
	}
	/**
	 * driver implements support ConnPt
	 * 
	 * @return null implements the driver will support ch which has no connpt
	 *         joined
	 */
	public abstract Class<? extends ConnPt> supportConnPtClass();

	/**
	 * 实现类中可能会对特定的ConnPt不支持，此时需要重载此函数
	 * @return
	 */
	public List<Class<? extends ConnPt>> notsupportConnPtClass()
	{
		return null ;
	}
	/**
	 * connpt is connector to dev e.g Device based on IP addr will use this
	 * 
	 * @return
	 */
	public boolean isConnPtToDev()
	{
		return false;
	}
	
	protected List<DevDriver> supportMultiDrivers()
	{
		return null ;
	}

	/**
	 * check support device find or not
	 * 
	 * @return
	 */
	public abstract boolean supportDevFinder();
	
	/**
	 * @see supportDevFinder return true,this func may be override to update devices under ch
	 * @return
	 */
	public boolean updateFindedDevs(StringBuilder failedr)
	{
		failedr.append("no impl") ;
		return false;
	}

	// ----
	// prop definitions that driver must provide.
	// 1) DevDef will use it to edit DevDef.
	// 2) DevCh will use it to edit extends prop value,that driver instance
	// itself will used
	// 3) UADev in ch will use it to edit some left prop. e.g devid etc.
	// ----

	/**
	 * every DevDef in driver may has some prop needed to be setup
	 * 
	 * @return
	 */
	public abstract List<PropGroup> getPropGroupsForDevDef();

	/**
	 * a driver instance may has it's own prop in UACh
	 * 
	 * @return
	 */
	public abstract List<PropGroup> getPropGroupsForCh(UACh ch);

	/**
	 * Device in channel may has some special prop need to be set e.g devid
	 * 
	 * @return
	 */
	public abstract List<PropGroup> getPropGroupsForDevInCh(UADev d);

	/**
	 * a template address
	 * 
	 * @return
	 */
	public abstract DevAddr getSupportAddr();

	/**
	 * 
	 * @return
	 */
	public ValTP[] getLimitValTPs(UADev dev)
	{
		return null ;
	}
	// final protected boolean getPropValBool(String groupn,String itemn,boolean
	// defv)
	// {
	//// Object obj = getPropVal(groupn,itemn);
	//// if(obj==null)
	//// return defv ;
	//// return (Boolean)obj ;
	// return this.belongToCh.getPropValueBool(groupn, itemn, defv);
	// }
	//
	// final protected long getPropValInt(String groupn,String itemn,long defv)
	// {
	//// Object obj = getPropVal(groupn,itemn);
	//// if(obj==null)
	//// return defv ;
	//// return (Long)obj;
	// return this.belongToCh.getPropValueLong(groupn, itemn, defv);
	// }
	//
	// final protected double getPropValFloat(String groupn,String itemn,double
	// defv)
	// {
	//// Object obj = getPropVal(groupn,itemn);
	//// if(obj==null)
	//// return defv ;
	//// return (Double)obj;
	// return this.belongToCh.getPropValueDouble(groupn, itemn, defv);
	// }
	//
	// final protected String getPropValStr(String groupn,String itemn,String
	// defv)
	// {
	//// Object obj = getPropVal(groupn,itemn);
	//// if(obj==null)
	//// return defv ;
	//// return (String)obj;
	// return this.belongToCh.getPropValueStr(groupn, itemn, defv);
	// }

	private boolean bRun = false;
	private Thread rtTh = null;

	transient private ConnPt rtBindedChConnPt = null;

	/**
	 * if driver isConnPtToDev=true,then every devices is run indivil
	 */
	transient private HashMap<UADev, SubDevThread> rtBindedDev2SubTh = null;

	/**
	 * init driver before run. 1) implements may read ch device list and
	 * properties,and init special cmd in driver 2) init failed will make driver
	 * cannot start.
	 * 
	 * @param failedr
	 * @return
	 */
	protected boolean initDriver(StringBuilder failedr) throws Exception
	{
		return true;
	}

	private void bindConnPt() throws Exception
	{
		Class<?> c = this.supportConnPtClass();
		if (c == null)
			return;// need not bind

		UACh ch = this.getBelongToCh();
		if (ch == null)
			return;

		UAPrj prj = ch.getBelongTo();
		if (this.isConnPtToDev())
		{
			this.rtBindedDev2SubTh = new HashMap<>();
			for (UADev dev : ch.getDevs())
			{
				ConnPt cpt = ConnManager.getInstance().getConnPtByNode(prj.getId(), ch.getId(), dev.getId());
				if (cpt == null)
					continue;
				if (!c.isAssignableFrom(cpt.getClass()))
				{
					// throw new Exception("bind conn is not match driver");
					if (log.isErrorEnabled())
						log.error("bind conn is not match driver");
					continue;
				}
				SubDevThread sth = new SubDevThread(dev, cpt);
				rtBindedDev2SubTh.put(dev, sth);
				// callback
				cpt.onDriverBinded(this);
				cpt.setBindedObj1(sth); // sub thread set to bind1
			}
		}
		else
		{
			ConnPt cpt = ConnManager.getInstance().getConnPtByNode(prj.getId(), ch.getId(), null);
			if (cpt == null)
				return;// no conn
			if (!c.isAssignableFrom(cpt.getClass()))
			{
				throw new Exception("bind conn is not match driver");
			}
			rtBindedChConnPt = cpt;

			// callback
			cpt.onDriverBinded(this);
		}

	}

	private void unbindConnPt()
	{
		if (rtBindedChConnPt != null)
		{
			rtBindedChConnPt.onDriverUnbinded();
			rtBindedChConnPt = null;
		}

		if (this.rtBindedDev2SubTh != null)
		{
			for (SubDevThread th : this.rtBindedDev2SubTh.values())
			{
				th.getConnPt().onDriverUnbinded();
			}

			this.rtBindedDev2SubTh = null;
		}
	}

	/**
	 * check addr invalid before tag added
	 * 
	 * @param addr
	 * @param vtp
	 * @return
	 */
	public ChkRes checkAddr(UADev dev,String addr, ValTP vtp)
	{
		DevAddr daddr = this.getSupportAddr();
		if (daddr == null)
			return null;

		// StringBuilder failedr = new StringBuilder() ;
		return daddr.checkAddr(dev,addr, vtp);
	}

	/**
	 * after bind before driver run in thread loop override to do something.
	 * 
	 * @param failedr
	 * @return
	 */
	protected void beforeDriverRun() throws Exception
	{
		return;
	}

	synchronized final boolean RT_start(StringBuilder failedr) throws Exception
	{
		if (bRun)
			return true;

		if (rtTh != null)
			return true;

		// bind,or not
		bindConnPt();

		beforeDriverRun();

		// init
		if (!initDriver(failedr))
			return false;

		bRun = true;
		if(this.isConnPtToDev())
		{
			if( this.rtBindedDev2SubTh!=null)
			{
				for (SubDevThread dst : this.rtBindedDev2SubTh.values())
				{
					dst.start();
				}
			}
		}
		else
		{
			rtTh = new Thread(runner, "iottree-devdriver-" + this.getBelongToCh().getName() + " " + this.getName());
			rtTh.start();
		}
		return true;
	}

	public boolean hasDriverConfigPage()
	{
		return false;
	}
	/**
	 * when >= 0,it will override DriverIntMS in channel
	 * 
	 * @return
	 */
	protected long getRunInterval()
	{
		return -1;
	}
	
	public final long getUsingInterval()
	{
		long drv_int = getRunInterval();
		if (drv_int < 0)
			drv_int = DevDriver.this.getBelongToCh().getDriverIntMS();
		if (drv_int < 0)
			drv_int = 0;
		return drv_int ;
	}
	
	private long lastLoopNoWaitDT =-1 ;

	Runnable runner = new Runnable() {
		public void run()
		{
			try
			{
				long drv_int = getUsingInterval();

				StringBuilder failedr = new StringBuilder();
				log.info(" driver [" + DevDriver.this.getName() + " @ " + DevDriver.this.getBelongToCh().getName()
						+ "] started");

				
				UACh ch = getBelongToCh();
				ConnPt cpt = null;
				while (bRun)
				{
					try
					{
						if(RT_useLoopNoWait())
						{
							if(System.currentTimeMillis()-lastLoopNoWaitDT>=drv_int)
							{
								lastLoopNoWaitDT = System.currentTimeMillis() ; 
								if(!DevDriver.this.isConnPtToDev())
								{
									cpt = belongToCh.getConnPt();
									if(cpt!=null&&!cpt.isConnReady())
										cpt.RT_checkConn();
								}
							}
							
							if (!RT_runInLoopNoWait(ch, null, failedr))
								break;
							
							sleep(1);
							continue ;
						}
						else
						{
							sleep(drv_int);
							
							if(!DevDriver.this.isConnPtToDev())
							{
								cpt = belongToCh.getConnPt();
								if(cpt!=null&&!cpt.isConnReady())
									cpt.RT_checkConn();
							}
	
							// System.out.println("-----1-------");
							if (!RT_runInLoop(ch, null, failedr))
								break;
						}
					}
					catch(InterruptedException ie)
					{
						ie.printStackTrace();
						if(log.isDebugEnabled())
							log.debug(ie.getMessage());
					}
					catch ( ConnException e)
					{// ConnException will not stop driver
						if(cpt instanceof Closeable)
						{
							try
							{
								((Closeable)cpt).close();
							}
							catch(Exception eee) {}
						}
					}
				}
			}
			catch (Exception e)
			{
				if(log.isWarnEnabled())
					log.warn(e.getMessage());
				if(log.isDebugEnabled())
					log.debug(e);
			}
			finally
			{
				stopDriver();
			}
		}
	};

	private void stopDriver()
	{
		if (!bRun)
			return;

		try
		{
			bRun = false;
			unbindConnPt();
			afterDriverRun();
		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
		}

		if (log.isInfoEnabled())
			log.info(" driver [" + DevDriver.this.getName() + " @ " + DevDriver.this.getBelongToCh().getName()
					+ "] stopped");

		rtTh = null;
	}

	synchronized final void RT_stop(boolean bforce)
	{
		if (!bRun)
			return;

		if (bforce)
		{
			Thread t = rtTh;
			if (t != null)
			{
				t.interrupt();
			}

			if (rtBindedDev2SubTh != null)
			{
				for (SubDevThread sdt : this.rtBindedDev2SubTh.values())
				{
					sdt.interrupt();
				}
			}

			stopDriver();
		}
		else
		{
			bRun = false;
		}
	}

	public boolean RT_isRunning()
	{
		return bRun;// rtTh != null; 
	}

	public State getDriverState()
	{
		if (bRun)
			return State.running;
		if (rtTh != null)
			return State.run_stopping;
		return State.not_run;
	}

	protected final ConnPt getBindedConnPt()
	{
		return this.rtBindedChConnPt;
	}

	protected final UADev getBindedDevByConnPt(ConnPt cp)
	{
		SubDevThread subdt = (SubDevThread)cp.getBindedObj1() ;
		if(subdt==null)
			return null ;
		return subdt.getDev() ;
//		for (Map.Entry<UADev, SubDevThread> d2th : this.rtBindedDev2SubTh.entrySet())
//		{
//			if (d2th.getValue().getConnPt() == cp)
//				return d2th.getKey();
//		}
//		return null;
	}

	/**
	 * ConnPt related to this driver is in ready state. driver then can do
	 * something here.
	 * 
	 * @param cp
	 */
	protected abstract void RT_onConnReady(ConnPt cp, UACh ch, UADev dev) throws Exception;

	final void RT_onConnInvalid(ConnPt cp) throws Exception
	{
		UACh ch = this.getBelongToCh();
		if (this.isConnPtToDev())
		{
			UADev d = getBindedDevByConnPt(cp);
			if (d == null)
				return;
			RT_onConnInvalid(cp, ch, d);
		}
		else
		{
			RT_onConnInvalid(cp, ch, null);
		}
	}

	/**
	 * connpt related to this driver is broken or invalid. driver then can do
	 * something here
	 * 
	 * @param cp
	 */
	protected abstract void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev) throws Exception;

	/**
	 * implementor will use this interface method to check self state and may
	 * reconnect or other thing. it may make driver running more robust.
	 * 
	 * this method may run in multi thread when connector is bind to device.
	 * 
	 * @throws Exception
	 */
	protected abstract boolean RT_runInLoop(UACh ch, UADev dev, StringBuilder failedr) throws Exception;


	protected boolean RT_runInLoopNoWait(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		return false;
	}
	
	protected boolean RT_useLoopNoWait()
	{
		return false;
	}
	/**
	 * if driver RT_initDriver or RT_runInLoop failed,or to be stopped this
	 * method will be called.
	 * 
	 * implements should release important resource at this point
	 */
	protected void afterDriverRun() throws Exception
	{

	}

	public abstract boolean RT_writeVal(UACh ch,UADev dev,UATag tag, DevAddr da, Object v);

	public abstract boolean RT_writeVals(UACh ch,UADev dev,UATag[] tags, DevAddr[] da, Object[] v);

	public boolean RT_writeValStr(UACh ch,UADev dev,UATag tag, DevAddr da, String strv)
	{
		ValTP tp = da.getValTP();
		Object v = UAVal.transStr2ObjVal(tp, strv);
		if (v == null)
			return false;
		RT_writeVal(ch,dev,tag, da, v);
		return true;
	}

	public boolean RT_writeValsStr(UACh ch,UADev dev,UATag[] tags,  DevAddr[] da, String[] strv)
	{
		Object[] objvs = new Object[strv.length];
		for (int i = 0; i < strv.length; i++)
		{
			ValTP tp = da[i].getValTP();
			Object v = UAVal.transStr2ObjVal(tp, strv[i]);
			objvs[i] = v;
		}

		RT_writeVals(ch,dev,tags, da, objvs);
		return true;
	}
	
	private long warnDT = -1 ;
	
	private String warnInf = null ;
	
	@JsDef
	public void RT_fireDrvWarn(String msg)
	{
		this.warnDT = System.currentTimeMillis() ;
		this.warnInf = msg ;
	}
	
	public long RT_getWarnDT()
	{
		return this.warnDT ;
	}
	
	/**
	 * may be show in admin UI
	 * @return
	 */
	public String RT_getWarnInf()
	{
		return this.warnInf ;
	}
}
