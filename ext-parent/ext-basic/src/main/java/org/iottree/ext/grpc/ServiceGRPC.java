package org.iottree.ext.grpc;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.iottree.core.service.*;
import org.iottree.core.util.*;
import org.iottree.core.util.logger.*;

import io.grpc.Attributes;
import io.grpc.Grpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerTransportFilter;

public class ServiceGRPC extends AbstractService
{
	static ILogger log = LoggerManager.getLogger(ServiceGRPC.class) ;
	
	public static final String NAME = "grpc";

	private Server server;

	private boolean useUnixSocket = false;
	private String unixSocketPath = "";

	private int port = 9092;

	private ThreadPoolExecutor executor;

	private IOTTreeServerImpl serverImpl = new IOTTreeServerImpl();

	public ServiceGRPC()
	{
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(corePoolSize);
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public String getTitle()
	{
		return "gRPC Server";
	}

	@Override
	public String getBrief()
	{
		return "gRPC Server";
	}

	public int getPort()
	{
		return this.port;
	}

	public String getPortStr()
	{
		return "" + this.port;
	}

	@Override
	protected void initService(HashMap<String, String> pms) throws Exception
	{
		super.initService(pms);

		port = Convert.parseToInt32(pms.get("port"), 9092);
	}


	private Thread schedExec = null;
	
	private Runnable schedRunner = new Runnable() {

		@Override
		public void run()
		{
			while(schedExec!=null)
			{
				try
				{
					Thread.sleep(10);
				}
				catch(Exception ee)
				{}
				
				RT_doUpdate() ;
			}
		}
		
	};

	private void RT_doUpdate()
	{
		serverImpl.RT_sendSynTagsToClients();
	}

	@Override
	public boolean startService(StringBuilder failedr)
	{
		try
		{
			ConnStateMonitor intercepter = new ConnStateMonitor();
			server = ServerBuilder.forPort(port).addService(serverImpl)
					.intercept(intercepter)
					.addTransportFilter(intercepter)
					.executor(executor)
					.maxConnectionIdle(10, TimeUnit.SECONDS)
					.build().start();
			
			schedExec = new Thread(schedRunner) ;
			schedExec.start();

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run()
				{
					ServiceGRPC.this.stopService();
				}
			});
			return true;
		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
			server = null;
			schedExec = null ;
			return false;
		}
	}

	@Override
	public boolean stopService()
	{
		if (server != null)
		{
			try
			{
				if (schedExec != null)
					schedExec.interrupt();
				
				server.shutdownNow();          // send  goAway + break thread
                if (!server.awaitTermination(10, TimeUnit.SECONDS))
                {
                    System.err.println("⚠️  Server still alive after shutdownNow()");
                }
                
//				boolean termed = server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
//				if (!termed)
//				{
//	                server.shutdownNow();          // send  goAway + break thread
//	                if (!server.awaitTermination(10, TimeUnit.SECONDS))
//	                {
//	                    System.err.println("⚠️  Server still alive after shutdownNow()");
//	                }
//	            }
			}
			catch (InterruptedException e)
			{
	            server.shutdownNow();
	            //Thread.currentThread().interrupt();
	        }
			catch ( Exception ee)
			{
				server.shutdownNow();
				ee.printStackTrace();
				return false;
			}
			finally
			{
				log.warn("ServiceGPRC Stopped");
				server = null;
				schedExec = null;
			}
		}
		return true;
	}

	private void blockUntilShutdown() throws InterruptedException
	{
		if (server != null)
		{
			server.awaitTermination();
		}
	}

	@Override
	public boolean isRunning()
	{
		return server != null;
	}

	public List<ClientItem> RT_listClientItems()
	{
		return this.serverImpl.RT_listClientItems();
	}

	public List<String> RT_listClientSyning()
	{
		return this.serverImpl.RT_listClientSyning();
	}
	
}
