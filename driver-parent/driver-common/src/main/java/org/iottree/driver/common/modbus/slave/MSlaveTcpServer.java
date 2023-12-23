package org.iottree.driver.common.modbus.slave;

import java.net.ServerSocket;
import java.net.Socket;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.w3c.dom.Element;

/**
 *  modbus slave
 * 
 * @author jasonzhu
 */
public class MSlaveTcpServer extends MSlave
{
	static ILogger log = LoggerManager.getLogger(MSlaveTcpServer.class);
	
	ServerSocket server = null ;
	
	int port = -1 ;
	
	boolean bRun = false;
	
	Thread serverThread = null ;
	
	public MSlaveTcpServer()
	{
		
	}
	
	void init(Element ele)
	{
		super.init(ele) ;
		
		this.port = Convert.parseToInt32(ele.getAttribute("port"), -1) ;
		if(port<=0)
			throw new IllegalArgumentException("port not found in slave tcp server") ;
		
		
	}
	
	Runnable acceptRuner = new Runnable()
    {
	    public void run()
	    {
	        try
	        {
	            //IPAddress.
	            //IPAddress localAddr = IPAddress.Parse("127.0.0.1");
	            server = new ServerSocket(port,1);
	            //server.
	            //server..Start();
	            // Enter the listening loop.
	            System.out.println("SlaveTcpServer start on port="+port+"<<");
	            
	            while (bRun)
	            {
	            	if(MSlaveTcpConn.getConnCount()>0)
	            	{
	            		Thread.sleep(5) ;
	            		continue ;
	            	}
	            	
	                Socket client = server.accept() ;
	                
	                MSlaveTcpConn stc = new MSlaveTcpConn(MSlaveTcpServer.this,client) ;
	                //sfc.setStateM(sm);
	                stc.start();
	            }
	        }
	        catch (Exception e)
	        {
	        	e.printStackTrace();
	            if (log.isErrorEnabled())
	                log.error("",e);
	        }
	        finally
	        {
	            // Stop listening for new clients.
	        	close();
	
	        	System.out.println("MCmd Asyn Server stoped..") ;
	            serverThread = null ;
	            //server = null;
	            bRun = false;
	        }
	    }
    };
    
    
    synchronized public void start()
    {
        if(serverThread!=null)
                return ;


        bRun = true ;
        serverThread = new Thread(acceptRuner,"mslave_tcp_server") ;
        serverThread.start() ;
        
        //monThread = new Thread(monRunner,"m_asynconn_mon") ;
        //monThread.start() ;

    }
    
    public void close()
    {
        stop() ;
    }

    synchronized public void stop()
    {
    	if (server != null)
        {
        	try
        	{
        		server.close();
        	}
        	catch(Exception e)
        	{}
        	
        	server = null ;
        }
        
        //pool.shutdownNow() ;
        
    	Thread st = serverThread;
    	//Thread t = serverScheTh;
    	
    	if(st!=null)
    	{//stop recv conn
    		st.interrupt() ;
    		serverThread = null ;
    	}
    	
//    	st = monThread;
//    	if(st!=null)
//    	{//stop recv conn
//    		st.interrupt() ;
//    		monThread = null ;
//    	}

    	
    	for(MSlaveTcpConn ep:MSlaveTcpConn.getAllClientsList())
    	{
    		ep.stopForce() ;
    	}
    	
        bRun = false;
        //serverScheTh = null ;
        serverThread = null ;
    }
    
    
    
    
}
