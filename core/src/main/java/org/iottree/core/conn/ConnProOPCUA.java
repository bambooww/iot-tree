package org.iottree.core.conn;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

public class ConnProOPCUA extends ConnProvider
{
	public static final String TP = "opc_ua" ;
	@Override
	public String getProviderType()
	{
		return TP;
	}
	
	public boolean isSingleProvider()
	{
		return true;
	}
	

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtOPCUA.class;
	}

	@Override
	protected long connpRunInterval()
	{
		return 500;
	}
	
	
	public void disconnAll() //throws IOException
	{
		for(ConnPt ci:this.listConns())
		{
			try
			{
				ConnPtOPCUA conn = (ConnPtOPCUA)ci ;
				conn.disconnect();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void stop()
	{
		super.stop() ;
		
		disconnAll();
	}
	
	@Override
	protected void connpRunInLoop() throws Exception
	{
		for(ConnPt ci:this.listConns())
		{
			ConnPtOPCUA citc = (ConnPtOPCUA)ci ;
			if(!citc.isEnable())
				continue;
			
			citc.checkConn() ;
		}
	}

}