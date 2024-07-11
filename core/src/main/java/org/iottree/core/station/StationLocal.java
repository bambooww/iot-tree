package org.iottree.core.station;

import java.io.File;
import java.net.URI;
import java.nio.ByteBuffer;
import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

/**
 * for station local manager
 * @author zzj
 *
 */
public class StationLocal
{
	static ILogger log = LoggerManager.getLogger(StationLocal.class) ;
	
	static StationLocal instance = null ;
	
	public static StationLocal getInstance()
	{
		if(instance!=null)
		{
			if(!instance.bValid)
				return null ;
			return instance ;
		}
		
		synchronized(StationLocal.class)
		{
			if(instance!=null)
			{
				if(!instance.bValid)
					return null ;
				return instance ;
			}
			
			instance = new StationLocal() ;
			if(!instance.bValid)
				return null ;
			return instance ;
		}
		
	}
	
	class WebSockClient extends WebSocketClient
	{

		public WebSockClient(URI serveruri)
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
		
		public void onMessage(ByteBuffer bytes)
		{
			try
			{
				RT_onRecvedMsg(bytes.array());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void onClose(int code, String reason, boolean remote)
		{

		}

		@Override
		public void onError(Exception ex)
		{

		}

		private void checkAndConn() throws Exception
		{
			switch(this.getReadyState())
			{
			case NOT_YET_CONNECTED:
				if(isClosed())
					reconnectBlocking();
				else
					connectBlocking();
				//Thread.sleep(5000);
				break;
			case OPEN:
//				if(wsLis.hasSendData())
//				{
//					byte[] bs = wsLis.getNextSendData();
//					WSClient.this.sendByRandomKey(bs) ;
//				}
				//send("hello from pro");
				//Thread.sleep(checkSendIntv);
				break;
			
			case CLOSED:
				reconnectBlocking() ;
				//Thread.sleep(5000);
				break;
			default:
				//Thread.sleep(5000);
				break;
			}
		}
	}
	
	String id = null ;
	
	
	/**
	 * local station's parent address
	 */
	String platformHost = null ;
	
	int platformPort = 9090 ;
	
	String key = null ;
	
	private boolean bValid = false;
	
	private StationLocal()
	{
		bValid = loadConfig() ;
	}
	
	private boolean loadConfig()
	{
		File fstat = Config.getConfFile("station.json") ;
		if(!fstat.exists())
			return false ;
		try
		{
			JSONObject jo = Convert.readFileJO(fstat) ;
			this.id = jo.getString("id") ;
			this.platformHost = jo.getString("platform_host") ;
			this.platformPort = jo.optInt("platform_port",9090) ;
			this.key = jo.getString("key") ;
			return true ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return false;
		}
	}
	
	public boolean isStationValid()
	{
		return this.bValid ;
	}
	
	public String getStationId()
	{
		return id ;
	}
	
	public String getPlatfromHost()
	{
		return this.platformHost ;
	}
	
	public int getPlatfromPort()
	{
		return this.platformPort ;
	}
	
	public String getStationKey()
	{
		return this.key ;
	}
	
	// rt
	
	WebSockClient wsClient =null;
	
	private transient long lastChk = -1 ;
	
	private Thread th = null ;
	
	public boolean isConnReady()
	{
		if(this.wsClient==null)
			return false;
		return wsClient.getReadyState()==ReadyState.OPEN ;
	}
	
	protected void RT_onRecvedMsg(byte[] bs) throws Exception
	{
		if(log.isTraceEnabled())
		{
			log.trace(" StationLocal RT_onRecvedMsg bs len="+bs.length);
		}
		
		PSCmd psc = PSCmd.parseFrom(bs) ;
		if(psc==null)
			return ;
		
		if(log.isTraceEnabled())
		{
			log.trace(" StationLocal RT_onRecvedMsg "+psc);
		}
		psc.RT_onRecvedInStationLocal(this);
	}
	

	void disconnect() // throws IOException
	{
		WebSockClient ss = wsClient;
		if(ss==null)
			return ;
		
		try
		{
			ss.close();
		}
		catch(Exception e) {}
		finally
		{
			wsClient = null ;
		}
	}

	private void connectToWS() throws Exception
	{
		if(this.wsClient==null)
		{
			String url = "ws://"+this.platformHost+":"+this.platformPort+"/_ws/station/"+id ;
			System.out.println(" station local to platform ->"+url) ;
			this.wsClient = new WebSockClient(new URI(url)) ;
		}
		
		this.wsClient.checkAndConn() ;
	}
	
	private void RT_checkConn()
	{
		if(System.currentTimeMillis()-lastChk<5000)
			return ;
		
		try
		{
			connectToWS();
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
				log.debug("connect to websocket",e);
		}
		finally
		{
			lastChk = System.currentTimeMillis() ;
		}
	}
	
	private void RT_run()
	{
		while(th!=null)
		{
			try
			{
				Thread.sleep(5000);
			}
			catch(Exception ee)
			{}

			try
			{
				RT_checkConn() ;
				
				if(wsClient.getReadyState()==ReadyState.OPEN)
				{
					PSCmdStationST cmd_st = new PSCmdStationST() ;
					cmd_st.asStationLocal(this) ;
					byte[] bs = cmd_st.packTo() ;
					wsClient.send(bs);
				}
			}
			catch(Exception eee)
			{
				if(log.isDebugEnabled())
					log.debug(eee.getMessage(), eee);
			}
		}
	}
	
	private Runnable runner = new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						RT_run();
					}
					finally
					{
						th = null ;
						disconnect();
					}
				}
			} ;
	
		
	public synchronized void RT_start()
	{
		if(th!=null)
			return ;
		
		th = new Thread(runner) ;
		th.start(); 
	}
	
	public synchronized void RT_stop()
	{
		Thread t = th ;
		if(t==null)
			return ;
		t.interrupt(); 
		th = null ;
		disconnect();
	}
	
	public boolean isRunning()
	{
		return th!=null ;
	}
	
	public boolean RT_sendCmd(PSCmd cmd,StringBuilder failedr)
	{
		if(wsClient==null)
		{
			failedr.append("no connected to platform") ;
			return false;
		}
		if(!wsClient.isOpen())
		{
			failedr.append("connection is not open") ;
			return false;
		}
		wsClient.send(cmd.packTo());
		return true;
	}
}
