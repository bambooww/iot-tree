package org.iottree.driver.common.modbus.slave;

import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;


public class ServiceWrapper implements WrapperListener
{
	//ScadaClient sc = new ScadaClient() ;
	
	private MSlaveManager pm = MSlaveManager.getInstance();
	
	private ServiceWrapper()
	{
		
	}
	
	public Integer start(String[] args)
	{
		try
		{
			//ps.setConfig(null,Integer.parseInt(args[0]),args[1],Integer.parseInt(args[2]));
			
			
			pm.start() ;
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
			pm.stop() ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		
		System.exit( extcode );
        return extcode;
	}

	public void controlEvent(int arg)
	{
		
	}

	public static void main( String[] args )
    {
        System.out.println( "Initializing..." );
        
        // Start the application.  If the JVM was launched from the native
        //  Wrapper then the application will wait for the native Wrapper to
        //  call the application's start method.  Otherwise the start method
        //  will be called immediately.
        WrapperManager.start(new ServiceWrapper(), args);
    }
}