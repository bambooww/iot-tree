package org.iottree.driver.opc.opcua.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.service.AbstractService;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IServerBootComp;

public class OpcUAService extends AbstractService implements IServerBootComp
{
	public static final String NAME = "opcua_server" ;
	Server server = null;
	
	int tcpPort = 4840;
	String authUsers = null ;
	
	
	@Override
	public String getBootCompName()
	{
		return NAME;
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
	
	public String getTcpPortStr()
	{
		return ""+tcpPort ;
	}
	

	public String getAuthUsers()
	{
		if(this.authUsers==null)
			return "";
		return authUsers;
	}
	
	public List<UAPrj> getPrjs()
	{
		ArrayList<UAPrj> rets = new ArrayList<>() ;
		for(UAPrj prj:UAManager.getInstance().listPrjs())
		{
			if(!prj.isOpcUAOpen())
				continue ;
			rets.add(prj) ;
		}
		return rets ;
	}
	
	public boolean checkUserPsw(String user_name,String psw)
	{
		if(Convert.isNullOrEmpty(user_name) || Convert.isNullOrEmpty(psw))
			return false;
		
		HashMap<String,String> pms = Convert.transPropStrToMap(this.authUsers) ;
		return psw.equals(pms.get(user_name)) ;
	}
	

	@Override
	protected void initService(HashMap<String, String> pms) throws Exception
	{
		super.initService(pms);
		
		tcpPort = Convert.parseToInt32(pms.get("tcp_port"), 4840) ;
		authUsers = pms.get("auth_users") ;
	}
	
	@Override
	synchronized public void startComp() throws Exception
	{
		if(server!=null)
			return ;
		
		server = new Server(this);
		//server.asTcpPort(this.tcpPort) ;
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
