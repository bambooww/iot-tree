package org.iottree.core.conn;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

public class ConnProHTTP extends ConnProvider
{
	public static final String TP = "http" ;
	
	@Override
	public String getProviderType()
	{
		return TP;
	}
	
	public boolean isSingleProvider()
	{
		return false;
	}
	

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtHTTP.class;
	}

	@Override
	protected long connpRunInterval()
	{
		return 100;
	}

	@Override
	protected void connpRunInLoop() throws Exception
	{
		for(ConnPt ci:this.listConns())
		{
			ConnPtHTTP citc = (ConnPtHTTP)ci ;
			citc.checkUrl() ;
		}
	}

}
