package org.iottree.driver.opc.opcua.server;

import org.iottree.core.util.IServerBootComp;

public class OpcUAServerComp implements IServerBootComp
{
	DrvServer server = null;
	
	@Override
	public String getBootCompName()
	{
		return "opcua_server";
	}

	@Override
	synchronized public void startComp() throws Exception
	{
		if(server!=null)
			return ;
		
		server = new DrvServer();
		server.startup();
	}

	@Override
	synchronized public void stopComp() throws Exception
	{
		if(server==null)
			return ;
		server.shutdown() ;
	}

	@Override
	public boolean isRunning() throws Exception
	{
		return server!=null;
	}

}
