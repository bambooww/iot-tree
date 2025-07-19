package org.iottree.server;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.tanukisoftware.wrapper.*;


public class ServiceWrapperMain implements WrapperListener
{
	static {
	    System.setProperty("file.encoding", "UTF-8");
	    try {
	        System.setOut(new PrintStream(System.out, true, "UTF-8"));
	    } catch (UnsupportedEncodingException ignored) {}
	}
	
	private ServiceWrapperMain()
	{}
	
	public Integer start(String[] arg0)
	{
		try
		{
			Server.startServer(true) ;
			//start ok return null *
			return null;
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
			Server.stopServer() ;
			//ServerBootCompMgr.getInstance().stopAllBootComp();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		
		//System.exit( extcode );
        return extcode;
	}

	public void controlEvent(int arg0)
	{
		
	}

	public static void main( String[] args )
    {
        System.out.println( "Initializing iottree..." );
        
        // Start the application.  If the JVM was launched from the native
        //  Wrapper then the application will wait for the native Wrapper to
        //  call the application's start method.  Otherwise the start method
        //  will be called immediately.
        WrapperManager.start( new ServiceWrapperMain(), args );
    }
}
