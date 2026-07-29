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
	static final int TCP_BIND_PORT = 4840;
	
	public static final String NAME = "opcua_server" ;
	Server server = null;
	
	int tcpPort = TCP_BIND_PORT;
	
	String authUsers = null ;
	
	List<String> prjIds = null ;
	
	boolean secModeNone = false;
	
	boolean secModeSign = false;
	
	boolean secModeSignEncrypt = true;
	
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
	
	public boolean isSecModeNone()
	{
		return this.secModeNone ;
	}
	
	public boolean isSecModeSign()
	{
		return this.secModeSign ;
	}
	
	public boolean isSecModeSignEncrypt()
	{
		return this.secModeSignEncrypt ;
	}
	
	public List<UAPrj> getPrjs()
	{
		ArrayList<UAPrj> rets = new ArrayList<>() ;
		if(this.prjIds==null||this.prjIds.size()<=0)
			return rets ;
		
		for(UAPrj prj:UAManager.getInstance().listPrjs())
		{
			//if(!prj.isOpcUAOpen())
			//	continue ;
			if(this.prjIds.contains(prj.getId()))
				rets.add(prj) ;
		}
		return rets ;
	}
	
	public boolean hasPrjId(String prjid)
	{
		if(this.prjIds==null||this.prjIds.size()<=0)
			return false ;
		return this.prjIds.contains(prjid) ;
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
		this.prjIds = Convert.splitStrWith(pms.get("prjs"), ",") ;
		this.secModeNone = "true".equals(pms.get("sm_none"));
		this.secModeSign = "true".equals(pms.get("sm_sign"));
		this.secModeSignEncrypt = !"false".equals(pms.get("sm_sign_enc"));
	}
	
	@Override
	synchronized public void startComp() throws Exception
	{
		if(server!=null)
			return ;
		
		server = new Server(this).asBindPort(this.tcpPort);
		//server.asTcpPort(this.tcpPort) ;
		server.RT_init();
		server.startup();
		System.out.println("OPC UA Service start with port="+this.tcpPort) ;
	}

	@Override
	synchronized public void stopComp() throws Exception
	{
		if(server==null)
			return ;
		server.shutdown().thenRun(()->{
			server = null ;
			System.out.println("OPC UA Service stoped") ;
		}).join() ;
	}


	@Override
	public boolean isRunning()// throws Exception
	{
		return server!=null;
	}

	@Override
	synchronized public boolean startService(StringBuilder failedr)
	{
		try
		{
			startComp() ;
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			failedr.append(e.getMessage()) ;
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
