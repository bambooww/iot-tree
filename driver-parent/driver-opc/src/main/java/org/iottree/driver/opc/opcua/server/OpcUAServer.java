package org.iottree.driver.opc.opcua.server;

import org.iottree.core.service.AbstractService;
import org.iottree.core.util.IServerBootComp;

public class OpcUAServer extends AbstractService implements IServerBootComp
{
	public static final String NAME = "opcua_server" ;
	ExampleServer server = null;
	
	@Override
	public String getBootCompName()
	{
		return NAME;
	}

	@Override
	synchronized public void startComp() throws Exception
	{
		if(server!=null)
			return ;
		
		server = new ExampleServer();
		server.startup();
	}

	@Override
	synchronized public void stopComp() throws Exception
	{
		if(server==null)
			return ;
		server.shutdown().thenRun(()->{
			server = null ;
		}).join() ;
	}

	@Override
	public boolean isRunning()// throws Exception
	{
		return server!=null;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public String getTitle()
	{
		return "OPC UA Server";
	}

	@Override
	public String getBrief()
	{
		return "OPC UA Server";
	}
	
	@Override
	synchronized public boolean startService()
	{
		try
		{
			startComp() ;
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	synchronized public boolean stopService()
	{
		try
		{
			stopComp();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

}
