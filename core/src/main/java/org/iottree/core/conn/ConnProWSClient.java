package org.iottree.core.conn;

import java.net.URI;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class ConnProWSClient extends ConnProvider
{

	static class WSClient extends WebSocketClient
	{

		public WSClient(URI serveruri)
		{
			super(serveruri);
			// 
			
		}

		@Override
		public void onOpen(ServerHandshake handshakedata)
		{
			
		}

		@Override
		public void onMessage(String message)
		{
			
		}

		@Override
		public void onClose(int code, String reason, boolean remote)
		{
			
		}

		@Override
		public void onError(Exception ex)
		{
			
		}
		
	}
	
	
	WSClient wsClient = null ;
	
	@Override
	public String getProviderType()
	{
		return "ws_client";
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
		return 0;
	}

	@Override
	protected void connpRunInLoop() throws Exception
	{
		
	}

}
