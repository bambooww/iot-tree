package org.iottree.core.util.queue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * thread will be triggered to start by enqueue msg
 * @author jason.zhu
 *
 */
public class QueTriggerThread<T>
{
	public static abstract class Handler<T>
	{
		public int getQueMultiMum()
		{
			return 0 ;//0 - single
		}
		
		public abstract void onQueObj(T t) ;
		
		public abstract void onQueObjs(List<T> t) ;
	}
	
	private Handler<T> handler ;

	private LinkedList<T> queMsgList = new LinkedList<>();
	
	private boolean bRun = false;
	
	private Thread procTh = null ;
	
	
	private long noMsgStopMS = -1 ;
	
	private long lastMsgDT = System.currentTimeMillis() ;
	/**
	 * 
	 * @param h
	 * @param no_msg_stop_to
	 */
	public QueTriggerThread(Handler<T> h,long no_msg_stop_to)
	{
		this.handler = h ;
		this.noMsgStopMS = no_msg_stop_to ;
	}
	
	private Runnable runner = new Runnable()
	{
		public void run()
		{
			try
			{
				int multi_mm = handler.getQueMultiMum() ;
					
				while(bRun)
				{
					if(multi_mm>0)
					{
						List<T> qis = dequeueMulti(multi_mm) ;
						if(qis==null)
						{
							if(noMsgStopMS>0 && System.currentTimeMillis()-lastMsgDT>noMsgStopMS)
								break ;
							else
								continue ;
						}
						
						handler.onQueObjs(qis);
						remove_first(qis.size()) ;
					}
					else
					{
						T qi = dequeue() ;
						if(qi==null)
						{
							if(noMsgStopMS>0 && System.currentTimeMillis()-lastMsgDT>noMsgStopMS)
								break ;
							else
								continue ;
						}
						
						handler.onQueObj(qi);
						remove_first(1) ;
					}
					
					lastMsgDT = System.currentTimeMillis() ;
				}
			}
			finally
			{
				procTh = null ;
				bRun = false;
			}
		}
	};
	
	public synchronized void enqueue(T o)
	{
		queMsgList.addLast(o);
		lastMsgDT = System.currentTimeMillis() ;
		RT_start() ;
		notify();
	}
	
	/**
	 * 实现队列查找--能够避免retry的delay不会造成不被处理的情况
	 * @return
	 */
	private T peekQueue()
	{
		if (queMsgList.isEmpty())
		{
			return null ;
		}
		else
		{
			return queMsgList.getFirst() ;
		}
	}

	private synchronized T dequeue()
	{
		T qi = peekQueue();
		if (qi==null)
		{
			try
			{
				wait(100);
			}
			catch (InterruptedException ie)
			{}
		}
		
		qi = peekQueue();
		if(qi==null)
			return null ;
		return qi ;
	}
	
	private synchronized List<T> dequeueMulti(int maxn)
	{
		int qlen = getQueLen() ;
		if(qlen==0)
		{
			try
			{
				wait(100);
			}
			catch (InterruptedException ie)
			{}
		}
		qlen = getQueLen() ;
		if(qlen==0)
			return null ;
	
		if(qlen>maxn)
			qlen = maxn ;
		
		return new ArrayList<T>(queMsgList.subList(0, qlen)) ;
	}
	
	private synchronized void remove_first(int n)
	{
		if(n<=0) return ;
		if(n>queMsgList.size())
			throw new IllegalArgumentException("n is bigger than size") ;
		
		for(int i = 0 ; i < n ; i ++)
			queMsgList.removeFirst();//.remove(msg) ;
	}

	public synchronized void emptyQueue()
	{
		queMsgList.clear();
	}
	
	public int getQueLen()
	{
		return queMsgList.size() ;
	}
	
	public synchronized boolean RT_start()
	{
		if (bRun)
			return true;

		bRun = true;
		procTh = new Thread(runner);
		procTh.start();
		return true;
	}

	public synchronized void RT_stop()
	{
		Thread th = procTh;
		if (th != null)
			th.interrupt();
		bRun = false;
		procTh = null;
	}

	public boolean RT_isRunning()
	{
		return bRun;
	}
}
