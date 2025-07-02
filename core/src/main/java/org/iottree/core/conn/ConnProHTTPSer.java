package org.iottree.core.conn;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

public class ConnProHTTPSer  extends ConnProvider
{
	public static final String TP = "http_ser" ;
	
	@Override
	public String getProviderType()
	{
		return TP;
	}
	
	@Override
	public String getProviderTpt()
	{
		return "HTTP Server" ;
	}
	
	public boolean isSingleProvider()
	{
		return true;
	}
	

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtHTTPSer.class;
	}

	@Override
	protected long connpRunInterval()
	{
		return 100;
	}

	@Override
	protected void connpRunInLoop() throws Exception
	{
//		for(ConnPt ci:this.listConns())
//		{
//			ConnPtHTTPSer citc = (ConnPtHTTPSer)ci ;
//			citc.checkUrl() ;
//		}
	}

}
