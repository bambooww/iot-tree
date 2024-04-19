package org.iottree.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;

import org.iottree.core.Config;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

public class Moner implements WrapperListener
{
	public static final String SERVICE_NAME = "iottree_server";
	public static final String SERVICE_NAME_LIST = "IOT Tree Server";

	Thread th = null;

	private Moner()
	{
	}

	private Runnable runner = new Runnable() {

		@Override
		public void run()
		{
			try
			{
				runMon();
			}
			catch ( Exception ee)
			{
				ee.printStackTrace();
			}
		}
	};
	
	
	private long curIntv = 300000 ;
	private long lastMF = -1 ;
	
	private long getInterval()
	{
		File f = new File("./conf.txt") ;
		if(!f.exists())
			return 300000 ;
		
		if(lastMF==f.lastModified())
			return curIntv ;
		

		try(FileInputStream fis = new FileInputStream(f);)
		{
			HashMap<String,String> mp = Convert.readStringMapFromStream(fis, "UTF-8") ;
			long intv = Convert.parseToInt64(mp.get("interval_ms"),300000) ;
			if(intv<=0)
				intv = 300000 ;
			
			curIntv = intv ;
			System.out.println(Convert.toFullYMDHMS(new Date()) + ": check interval change to [" + curIntv + "]MS in service");
			return curIntv ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return curIntv ;
		}
		finally
		{
			lastMF = f.lastModified() ;
		}
	}

	private void runMon() throws Exception
	{
		System.out.println("run mon started ");
		File wf = new File("./mon_running.flag");
		wf.createNewFile();
		wf.deleteOnExit();

		while (th != null)
		{
			long intv = getInterval() ;
			try
			{
				Thread.sleep(intv);
			}
			catch ( Exception eee)
			{
			}

			if (ProcessUtil.runCmdNetCheckRun(SERVICE_NAME_LIST))
			{
				// System.out.println("find ["+SERVICE_NAME_LIST+"] start in
				// service") ;
			}
			else
			{
				System.out.println(Convert.toFullYMDHMS(new Date()) + ":try start [" + SERVICE_NAME + "] in service");
				ProcessUtil.runCmdNetStart(SERVICE_NAME);
			}
		}
	}

	public Integer start(String[] arg0)
	{
		try
		{
			th = new Thread(runner);
			th.start();
			return null;
		}
		catch ( Throwable t)
		{
			t.printStackTrace();
			return -1;
		}
	}

	public int stop(int extcode)
	{
		try
		{
			Thread tmpth = th;
			if (tmpth != null)
			{
				tmpth.interrupt();
			}
			th = null;
		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
		}

		// System.exit( extcode );
		return extcode;
	}

	public void controlEvent(int arg0)
	{

	}

	public static void main(String[] args)
	{
		System.out.println("Initializing iottree moner ...");

		// Start the application. If the JVM was launched from the native
		// Wrapper then the application will wait for the native Wrapper to
		// call the application's start method. Otherwise the start method
		// will be called immediately.
		WrapperManager.start(new Moner(), args);
	}
}
