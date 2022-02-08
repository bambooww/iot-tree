package org.iottree.core.sim;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class SimConn implements Closeable
{
	Thread connTh = null ;
	
	SimChannel ch = null ;
	
	SimCP cp = null ;
	
	public SimConn(SimChannel ch,SimCP cp)
	{
		this.ch = ch ;
		this.cp = cp ;
	}
	
	public abstract InputStream getConnInputStream() ;
	
	public abstract OutputStream getConnOutputStream() ;


	public abstract void pulseConn()  throws Exception;
	
	private Runnable runner = new Runnable()
			{

				@Override
				public void run()
				{
					try
					{
						while(connTh!=null)
						{
							try
							{
								ch.RT_runConnInLoop(SimConn.this) ;
							}
							catch(IOException e)
							{
								System.out.println(" sim conn will close with err:"+e.getMessage()) ;
								break ;
							}
							catch(Exception e)
							{
								e.printStackTrace();
								break ;
							}
						}
					}
					finally
					{
						connTh=null ;
						try
						{
							close();
						}
						catch(Exception e)
						{}
					}
				}};
	
	synchronized public void RT_start()
	{
		if(connTh!=null)
			return ;
		
		connTh = new Thread(runner) ;
		connTh.start();
	}
	
	public boolean RT_isRunning()
	{
		return connTh!=null ;
	}
	
	synchronized public void RT_stop()
	{
		if(connTh==null)
			return ;
		
		connTh.interrupt(); 
		connTh = null ;
	}
	
	@Override
	public void close() throws IOException
	{
		RT_stop() ;
	}
}
