package org.iottree.conn.common.bacnet;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

public class ConnProBACnetClient extends ConnProvider
{
	public static final String TP = "bacnet_client" ;
	@Override
	public String getProviderType()
	{
		return TP;
	}
	
	@Override
	public String getProviderTpt()
	{
		return "BACnet Client" ;
	}
	
	public boolean isSingleProvider()
	{
		return true;
	}
	

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtBACnetClient.class;
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
				ConnPtBACnetClient conn = (ConnPtBACnetClient)ci ;
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
			ConnPtBACnetClient citc = (ConnPtBACnetClient)ci ;
			if(!citc.canRun())
				continue;
			
			citc.checkConn() ;
		}
	}

}
