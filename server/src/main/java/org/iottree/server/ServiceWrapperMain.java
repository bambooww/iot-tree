package org.iottree.server;

import org.tanukisoftware.wrapper.*;


public class ServiceWrapperMain implements WrapperListener
{
	private ServiceWrapperMain()
	{}
	
	public Integer start(String[] arg0)
	{
		try
		{
			Server.startServer() ;
			return 1;
		}
		catch(Throwable t)
		{
			t.printStackTrace() ;
			return -1 ;
		}
	}

	public int stop(int extcode)
	{
		try
		{
			//ServerBootCompMgr.getInstance().stopAllBootComp();
		}
		catch(Exception ee)
		{}
		
		//System.exit( extcode );
        return extcode;
	}

	public void controlEvent(int arg0)
	{
		
	}

	public static void main( String[] args )
    {
        System.out.println( "Initializing..." );
        
        // Start the application.  If the JVM was launched from the native
        //  Wrapper then the application will wait for the native Wrapper to
        //  call the application's start method.  Otherwise the start method
        //  will be called immediately.
        WrapperManager.start( new ServiceWrapperMain(), args );
    }
}
