package org.iottree.core.conn;

import org.iottree.core.ConnPt;

public class ConnProOpcAgent extends ConnProTcpServer
{
	private final static String TP = "opc_agent" ; 
	
	@Override
	public String getProviderType()
	{
		return TP;
	}
	
	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtOpcAgent.class;
	}
}
