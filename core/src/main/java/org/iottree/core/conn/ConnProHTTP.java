package org.iottree.core.conn;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

public class ConnProHTTP extends ConnProvider
{

	@Override
	public String getProviderType()
	{
		return "http";
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtMSG.class;
	}

	@Override
	protected long connpRunInterval()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void connpRunInLoop() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

}
