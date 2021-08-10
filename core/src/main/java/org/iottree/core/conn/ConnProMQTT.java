package org.iottree.core.conn;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

public class ConnProMQTT extends ConnProvider
{

	@Override
	public String getProviderType()
	{
		return "mqtt";
	}
	
	public boolean isSingleProvider()
	{
		return true;
	}
	

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		// TODO Auto-generated method stub
		return ConnPtMSG.class;
	}

	@Override
	protected long connpRunInterval()
	{
		// TODO Auto-generated method stub
		return 1000;
	}

	@Override
	protected void connpRunInLoop() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

}
