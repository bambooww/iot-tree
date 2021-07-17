package org.iottree.core;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.*;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
/**
 * belong to channel
 * and may limit devices in channel
 * 1)driver self may has some configuration
 * 2)every device in channel may has its own configuration related driver 
 * @author zzj
 */
public abstract class DevDriver  implements IPropChecker
{
	public static enum State
	{
		not_run(0),
		running(2),
		run_stopping(1);
		
		private final int val ;
		
		State(int v)
		{
			val = v ;
		}
		
		public int getInt()
		{
			return val ;
		}
		
		public boolean isRunning()
		{
			return val == 2 || val==1 ;
		}
	}
	
	//---------------------dev cat and device
	
	private static transient HashMap<String,List<DevCat>> drvName2devCats = new HashMap<>();
	
	DevCat reloadCat(String catid) throws Exception
	{
		DevCat c = this.loadCat(catid) ;
		if(c==null)
			return null;
		
		List<DevCat> cats = getDevCats() ;
		
		for(int i = 0,n=cats.size();i<n;i++)
		{
			DevCat cat = cats.get(i);
			if(cat.getId().equals(catid))
			{
				cats.set(i, c) ;
				return c;
			}
		}
		cats.add(c);
		return c ;
	}
	/**
	 * get or load dev cats
	 * @return
	 */
	public List<DevCat> getDevCats()
	{
		List<DevCat> devcats = drvName2devCats.get(this.getName()) ;
		if(devcats!=null)
			return devcats ;
		
		synchronized(this)
		{
			devcats = drvName2devCats.get(this.getName()) ;
			if(devcats!=null)
				return devcats ;
			
			devcats = loadCats() ;
			drvName2devCats.put(this.getName(),devcats) ;
		}
		return devcats ;
	}
	
	public DevCat addDevCat(String name,String title) throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		if(!Convert.checkVarName(name,true,failedr))
			throw new Exception(failedr.toString()) ;
		
		DevCat dc = this.getDevCatByName(name) ;
		if(dc!=null)
			throw new Exception("name_existed") ;
		dc = new DevCat(this,name,title) ;
		this.saveCat(dc) ;
		this.getDevCats().add(dc) ;
		return dc;
	}
	
	public DevDef getDevDefById(String id)
	{
		for(DevCat dc:getDevCats())
		{
			DevDef dd = dc.getDevDefById(id) ;
			if(dd!=null)
				return dd ;
		}
		return null ;
	}
	
	public DevCat getDevCatById(String id)
	{
		for(DevCat dc:getDevCats())
		{
			if(id.equals(dc.getId()))
				return dc ;
		}
		return null ;
	}
	
	File getDrvDir()
	{
		File fb = DevManager.getDevFileBase() ;
		return new File(fb,"devdef/drv_"+getName()+"/") ;
	}
	
	File getDevCatDir(String catname)
	{
		return new File(getDrvDir(),"cat_"+catname+"/") ;
	}

	private void saveCat(DevCat dc) throws Exception
	{
		File catdir=  getDevCatDir(dc.getId()) ;
		if(!catdir.exists())
			catdir.mkdirs() ;
		XmlData xd = DataTranserXml.extractXmlDataFromObj(dc) ;
		//XmlData xd = rep.toUAXmlData();
		XmlData.writeToFile(xd, new File(catdir,"cat.xml"));
	}
	
	private DevCat loadCat(String catid) throws Exception
	{
		File catdir=  getDevCatDir(catid) ;
		if(!catdir.exists())
			return null;
		File catf = new File(catdir,"cat.xml") ;
		if(!catf.exists())
			return null ;
		XmlData tmpxd = XmlData.readFromFile(catf);
		DevCat r = new DevCat(this) ;
		DataTranserXml.injectXmDataToObj(r, tmpxd);
		return r ;
	}

	private List<DevCat> loadCats()
	{
		ArrayList<DevCat> rets = new ArrayList<>() ;
		
		File drvdir = getDrvDir() ;
		if(!drvdir.exists())
			return rets;
		File[] fs = drvdir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(!f.isDirectory())
					return false;
				String n = f.getName() ;
				return n.startsWith("cat_");
			}
		});
		//ArrayList<DevCat> rets = new ArrayList<>() ;
		for(File tmpf:fs)
		{
			String catid = tmpf.getName().substring(4) ;
			try
			{
				DevCat dc = loadCat(catid) ;
				if(dc==null)
				{
					System.out.println("Warning,load DevCat failed ["+catid+"]");
					continue ;
				}
				rets.add(dc);
			}
			catch(Exception e)
			{
				System.out.println("Warning,load DevCat error ["+catid+"]");
				e.printStackTrace();
			}
		}
		return rets;
	}

	public DevCat getDevCatByName(String n)
	{
		for(DevCat dc:this.getDevCats())
		{
			if(n.contentEquals(dc.getName()))
				return dc ;
		}
		return null ;
	}
		
		//-------driver self
	UACh belongToCh = null ;
	
	/**
	 * support running instance in ch with porp parameter's
	 * @return
	 */
	public abstract DevDriver copyMe() ;
	
	public abstract String getName();
	
	public abstract String getTitle() ;
	
	public String getDesc()
	{
		return "";
	}
	
	/**
	 * driver may setup in ch
	 * @return
	 */
	public UACh getBelongToCh()
	{
		return belongToCh ;
	}
	
	
	/**
	 * driver implements support ConnPt
	 * 
	 * @return null implements the driver will support ch which has no connpt joined
	 */
	public abstract Class<? extends ConnPt> supportConnPtClass() ;
	
	/**
	 * check support device find or not
	 * @return
	 */
	public abstract boolean supportDevFinder() ;
	
	//----
	// prop definitions that driver must provide.
	//    1) DevDef will use it to edit DevDef.
	//    2) DevCh will use it to edit extends prop value,that driver instance itself will used
	//    3) UADev in ch will use it to edit some left prop. e.g devid etc.
	//----
	
	/**
	 * every DevDef in driver may has some prop needed to be setup
	 * @return
	 */
	public abstract List<PropGroup> getPropGroupsForDevDef();
	/**
	 * a driver instance may has it's own prop in UACh
	 * @return
	 */
	public abstract List<PropGroup> getPropGroupsForCh();
	/**
	 * Device in channel may has some special prop need to be set
	 * e.g devid
	 * @return
	 */
	public abstract List<PropGroup> getPropGroupsForDevInCh() ;
	
	
	
	
	
	
	/**
	 * a template address
	 * @return
	 */
	public abstract DevAddr getSupportAddr() ;
	
	

//	final protected boolean getPropValBool(String groupn,String itemn,boolean defv)
//	{
////		Object obj = getPropVal(groupn,itemn);
////		if(obj==null)
////			return defv ;
////		return (Boolean)obj ;
//		return this.belongToCh.getPropValueBool(groupn, itemn, defv);
//	}
//	
//	final protected long getPropValInt(String groupn,String itemn,long defv)
//	{
////		Object obj = getPropVal(groupn,itemn);
////		if(obj==null)
////			return defv ;
////		return (Long)obj;
//		return this.belongToCh.getPropValueLong(groupn, itemn, defv);
//	}
//	
//	final protected double getPropValFloat(String groupn,String itemn,double defv)
//	{
////		Object obj = getPropVal(groupn,itemn);
////		if(obj==null)
////			return defv ;
////		return (Double)obj;
//		return this.belongToCh.getPropValueDouble(groupn, itemn, defv);
//	}
//	
//	final protected String getPropValStr(String groupn,String itemn,String defv)
//	{
////		Object obj = getPropVal(groupn,itemn);
////		if(obj==null)
////			return defv ;
////		return (String)obj;
//		return this.belongToCh.getPropValueStr(groupn, itemn, defv);
//	}
	
	
	
	
	private boolean bRun = false;
	private Thread rtTh = null ;
	
	transient private ConnPt rtBindedConnPt = null ;

	/**
	 * init driver before run.
	 * 1) implements may read ch device list and properties,and init special cmd in driver
	 * 2) init failed will make driver cannot start.
	 * @param failedr
	 * @return
	 */
	protected  boolean initDriver(StringBuilder failedr) throws Exception
	{
		
		return true ;
	}
	
	private void bindConnPt() throws Exception
	{
		Class<?> c = this.supportConnPtClass() ;
		if(c==null)
			return  ;//need not bind
		
		
		UACh ch = this.getBelongToCh() ;
		ConnPt cpt = ConnManager.getInstance().getConnPtByCh(ch.getBelongTo().getId(), ch.getId()) ;
		if(!c.isAssignableFrom(cpt.getClass()))
		{
			throw new Exception("bind conn is not match driver") ;
		}
		rtBindedConnPt = cpt ;
		
		//callback
		cpt.onDriverBinded(this);
		return ;		
	}
	
	private void unbindConnPt()
	{
		if(rtBindedConnPt==null)
			return ;
		rtBindedConnPt.onDriverUnbinded();
		rtBindedConnPt = null ;
	}
	
	/**
	 * after bind before driver run in thread loop
	 * override to do something.
	 * @param failedr
	 * @return
	 */
	protected void beforeDriverRun() throws Exception
	{
		return ;
	}
	

	synchronized final boolean RT_start(StringBuilder failedr) throws Exception
	{
		if(bRun)
			return true ;
		
		if(rtTh!=null)
			return true;
		
		//init
		if(!initDriver(failedr))
			return false;

		bRun = true ;
		rtTh = new Thread(runner);
		rtTh.start();
		return true;
	}

	
	private Runnable runner = new Runnable() {

		@Override
		public void run()
		{
			try
			{
				StringBuilder failedr = new StringBuilder() ;
				System.out.println(" driver ["+DevDriver.this.getName()+" "+DevDriver.this+"] started");
				
				//bind
				bindConnPt();
				
				beforeDriverRun() ;
				
				while(bRun)
				{
					try
					{
						Thread.sleep(1000);
					
					
						//System.out.println("-----1-------");
						if(!RT_runInLoop(failedr))
							break ;
					}
					catch(ConnException e)
					{//ConnException will not stop driver
						
					}
				}
			}
			catch(Exception e)
			{
				System.err.println(e.getMessage()) ;
				//e.printStackTrace();
			}
			finally
			{
				stopDriver();
			}
		}
		
	};

	private void stopDriver()
	{
		System.out.println(" drv stop -- "+this.getBelongToCh().getTitle()) ;
		try
		{
			bRun = false;
			unbindConnPt();
			
			afterDriverRun() ;
			
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		rtTh = null ;
	}

	synchronized final void RT_stop(boolean bforce)
	{
		if(!bRun)
			return ;
		Thread t = rtTh ;
		if(t==null)
			return ;
		if(bforce)
		{
			t.interrupt();
			stopDriver();
		}
		else
		{
			bRun = false;
		}
	}

	public boolean RT_isRunning()
	{
		return rtTh!=null;
	}
	
	public State getDriverState()
	{
		if(bRun)
			return State.running ;
		if(rtTh!=null)
			return State.run_stopping ;
		return State.not_run ;
	}
	
	protected final ConnPt getBindedConnPt()
	{
		return this.rtBindedConnPt ;
	}
	/**
	 * ConnPt related to this driver is in ready state.
	 * driver then can do something here.
	 * @param cp
	 */
	protected abstract void RT_onConnReady(ConnPt cp);
	
	/**
	 * connpt related to this driver is broken or invalid.
	 * driver then can do something here
	 * @param cp
	 */
	protected abstract void RT_onConnInvalid(ConnPt cp) ;
	
	
	/**
	 * implementor will use this interface method to check self state
	 * and may reconnect or other thing.
	 * it may make driver running more robust.
	 * 
	 * @throws Exception
	 */
	protected abstract boolean RT_runInLoop(StringBuilder failedr) throws Exception;
	
	/**
	 * if driver RT_initDriver or RT_runInLoop failed,or to be stopped
	 * this method will be called.
	 * 
	 * implements should release important resource at this point
	 */
	protected void afterDriverRun() throws Exception
	{
		
	}
	

	public abstract boolean RT_writeVal(UADev dev,DevAddr da,Object v) ;
	
	public abstract boolean RT_writeVals(UADev dev,DevAddr[] da,Object[] v) ;
	
	public boolean RT_writeValStr(UADev dev,DevAddr da,String strv)
	{
		ValTP tp = da.getValTP();
		Object v = UAVal.transStr2ObjVal(tp, strv);
		if(v==null)
			return false;
		RT_writeVal(dev,da,v);
		return true;
	}
	
	public boolean RT_writeValsStr(UADev dev,DevAddr[] da,String[] strv)
	{
		Object[] objvs = new Object[strv.length];
		for(int i = 0 ; i< strv.length ; i ++)
		{
			ValTP tp = da[i].getValTP();
			Object v = UAVal.transStr2ObjVal(tp, strv[i]);
			objvs[i] = v ;
		}
		
		RT_writeVals(dev,da,objvs);
		return true;
	}
}
