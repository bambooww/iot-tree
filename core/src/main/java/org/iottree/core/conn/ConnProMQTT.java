package org.iottree.core.conn;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

public class ConnProMQTT extends ConnProvider
{
	public static final String TP ="mqtt" ;
	
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
		// TODO Auto-generated method stub
		return ConnPtMQTT.class;
	}

	@Override
	protected long connpRunInterval()
	{
		// TODO Auto-generated method stub
		return 1000;
	}
	
	public void stop()
	{
		super.stop() ;
		
		disconnAll();
	}
	
	public void disconnAll() //throws IOException
	{
		for(ConnPt ci:this.listConns())
		{
			try
			{
				ConnPtMQTT conn = (ConnPtMQTT)ci ;
				conn.disconnect();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void connpRunInLoop() throws Exception
	{
		for(ConnPt ci:this.listConns())
		{
			ConnPtMQTT citc = (ConnPtMQTT)ci ;
			citc.checkConn() ;
		}
	}
	
}
