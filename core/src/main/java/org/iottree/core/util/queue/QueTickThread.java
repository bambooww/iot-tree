package org.iottree.core.util.queue;

import java.util.LinkedList;

/**
 * queue thread with listener thread can run tick task
 * 
 * @author jason.zhu
 *
 */
public class QueTickThread<T>
{
	public static interface Handler<T>
	{
		public void onQueObj(T t) ;
		
		/**
		 * empty queue will couse tick
		 */
		public void onTick() ;
	}
	
	private Handler<T> handler ;
	
	private long tickIntv ;

	private LinkedList<T> queMsgList = new LinkedList<>();
	
	private boolean bRun = false;
	
	private Thread procTh = null ;
	
	
	
	public QueTickThread(Handler<T> h,long tick_intv)
	{
		this.handler = h ;
		this.tickIntv = tick_intv ;
	}
	
	private Runnable runner = new Runnable()
	{
		public void run()
		{
			try
			{
				while(bRun)
				{
					run_in_loop();
				}
			}
			finally
			{
				procTh = null ;
				bRun = false;
			}
		}
	};
	
	private void run_in_loop()
	{
		T qi = dequeue() ;
		if(qi==null)
		{
			this.handler.onTick();
			return ;
		}
		
		this.handler.onQueObj(qi);
		remove_first(1) ;
	}
	
	public synchronized void enqueue(T o)
	{
		queMsgList.addLast(o);		
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
				wait(this.tickIntv);
			}
			catch (InterruptedException ie)
			{}
		}
		
		qi = peekQueue();
		if(qi==null)
			return null ;
		return qi ;
	}
	
//	private synchronized List<T> dequeue(int min_num,int max_num,long timeout)
//	{
//		if(min_num<=0)
//			throw new IllegalArgumentException("invalid dequeue num "+min_num) ;
//		int len = getQueLen() ;
//		
//		if(len<min_num)
//		{
//			long st = System.currentTimeMillis() ;
//			do
//			{
//				try
//				{
//					wait(1);
//				}
//				catch (InterruptedException ie)
//				{}
//				len = getQueLen() ;
//				if(timeout>0 && System.currentTimeMillis()-st>=timeout)
//				{//timeout
//					break ;
//				}
//			}while(len<min_num) ;
//		}
//		
//		if(len<=0)
//			return null ;
//
//		if(max_num>0 && len>max_num)
//			len = max_num ;
//		
//		return this.queMsgList.subList(0, len);
//	}
	
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
