package org.iottree.core.conn;

import java.net.URI;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class ConnProWSClient extends ConnProvider
{

	public final static String TP = "ws_client" ;
	
	
	//WSClient wsClient = null ;
	
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
		return ConnPtWSClient.class;
	}

	public void stop()
	{
		super.stop() ;
		
		disconnAll();
	}
	
	private void disconnAll() //throws IOException
	{
		for(ConnPt ci:this.listConns())
		{
			try
			{
				ConnPtWSClient conn = (ConnPtWSClient)ci ;
//				long st = System.currentTimeMillis() ;
				conn.disconnect();
//				long et = System.currentTimeMillis() ;
//				System.out.println(" conn="+conn.getName()+" cost="+(et-st));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected long connpRunInterval()
	{
		return 500;
	}
	
	
	@Override
	protected void connpRunInLoop() throws Exception
	{
		for(ConnPt ci:this.listConns())
		{
			if(!ci.isEnable())
				continue ;
			
			ConnPtWSClient citc = (ConnPtWSClient)ci ;
			citc.RT_checkConn() ;
		}
	}
}
