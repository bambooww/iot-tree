package org.iottree.core.store.ttsr;

import java.util.HashMap;

import org.iottree.core.Config;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlHelper;
import org.w3c.dom.Element;

/**
 * 支持Tag进行一定缓存长度的记录支持功能，支持数据快速记录，能够快速获取Tags数据的所有变化
 * 
 * 可以认为是一个简化版时序数据库
 * 
 * 支持bool int float 三种数据类型
 * 
 * @author jason.zhu
 */
public abstract class RecAdapter
{
	//protected String name = null ;
	
	protected HashMap<String,RecTag<?>> tag2recorder = new HashMap<>() ;
	
	protected int maxPointNum = 10000 ;
	
	protected long minSaveGapMS = 3000 ;
	
	private Thread th = null ;
	
	/**
	 * 对应于独立的目录
	 * 
	 * @param name
	 */
	protected RecAdapter()
	{
	}
	
	protected abstract boolean RT_init(StringBuilder failedr) ;
	
//	protected abstract DBConnPool getConnPool() ;
	
	protected abstract RecIO getIO() ;
	
//	public String getName()
//	{
//		return this.name ;
//	}
	
	
	private Runnable runner = new Runnable() {

		@Override
		public void run()
		{
			try
			{

				StringBuilder failedr = new StringBuilder() ;
				if(!RT_init(failedr))
				{
					throw new RuntimeException(failedr.toString()) ;
				}
				
				while(th!=null)
				{
					doRunInLoop() ;
				}
			}
			finally
			{
				th = null ;
			}
		}
	};
	
	private long lastSaveDT = -1 ;
	
	private synchronized void doRunInLoop()
	{
		try
		{
			long ddt = System.currentTimeMillis()-lastSaveDT;
			if(ddt<this.minSaveGapMS)
				this.wait(this.minSaveGapMS-ddt);
			else
				this.wait(minSaveGapMS); //wait for notify
		}catch(Exception ee) {}
		
		if(System.currentTimeMillis()-lastSaveDT<this.minSaveGapMS)
			return ;
		
		try
		{
			doSave() ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		finally
		{
			try
			{//try save gracefully
				doSave() ;
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
			lastSaveDT = System.currentTimeMillis() ;
		}
	}
	
	private void doSave() throws Exception
	{
		for(RecTag<?> r:this.tag2recorder.values())
		{
			getIO().saveRecorder(r);
		}
		
	}
	
	synchronized void fireSaver()
	{
		this.notify();
	}
	
	public <T> void addTagValue(String tag,long dt, T val)
	{
		@SuppressWarnings("unchecked")
		RecTag<T> rt = (RecTag<T>)tag2recorder.get(tag) ;
		if(rt==null)
		{
			rt = new RecTag<T>(this,tag,100,5,20000) ;
			tag2recorder.put(tag, rt) ;
		}
		rt.addPoint(dt, true, val);
		
		fireSaver();
	}
	
	public synchronized boolean RT_start()
	{
		if(th!=null)
			return true;
		
		th = new Thread(runner) ;
		th.start(); 
		return true;
	}
	
	public synchronized void RT_stop()
	{
		th = null ;
	}
	
	public boolean RT_isRunning()
	{
		return th!=null ;
	}
}
