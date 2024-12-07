package org.iottree.core.conn;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

public class ConnProUDP extends ConnProvider
{
	public static final String TP ="udp" ;
	

	@Override
	public String getProviderType()
	{
		return TP;	
	}

	@Override
	public String getProviderTpt()
	{
		return "UDP" ;
	}

	@Override
	public boolean isSingleProvider()
	{
		return true;
	}


	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtUDPMsg.class;
	}


	@Override
	protected long connpRunInterval()
	{
		return 1000;
	}

	@Override
	protected void connpRunInLoop() throws Exception
	{
		
	}
}
