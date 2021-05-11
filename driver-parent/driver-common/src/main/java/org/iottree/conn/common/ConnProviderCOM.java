package org.iottree.conn.common;

import java.util.List;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

/**
 * uart interface with COM
 * @author jason.zhu
 *
 */
public class ConnProviderCOM extends ConnProvider
{

	@Override
	public String getProviderType()
	{
		return null;
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return null;
	}

	@Override
	public List<ConnPt> listConns()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnPt getConnById(String id)
	{
		// TODO Auto-generated method stub
		return null;
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
