package org.iottree.core.station;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.PathParam;

import org.iottree.core.station.PlatformWSServer.SessionItem;
import org.iottree.core.util.Convert;
import org.iottree.core.util.encrypt.DES;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

public class PlatformWSServer
{
	private static ILogger log = LoggerManager.getLogger(PlatformWSServer.class) ;
	
	public static class SessionItem
	{
		private final Session session;
		
		PStation pStation ;
		
		String clientIP ;

		long openDT = -1 ;
		// private boolean bFirstTick=true ;
		
		long closeDT = -1 ;

		public SessionItem(Session s, PStation ps,String clientip)
		{
			this.session = s;
			this.pStation = ps;
			this.clientIP = clientip ;
			this.openDT = System.currentTimeMillis() ;
		}

		public Session getSession()
		{
			return session;
		}
		
		public boolean isConnOk()
		{
			if(session==null)
				return false;
			return session.isOpen() ;
		}

		public PStation getPStation()
		{
			return pStation;
		}
		
		public String getClientIP()
		{
			return this.clientIP ;
		}

		private void closeSessionOnError(Exception e)
		{
			CloseReason cr = new CloseReason(CloseCodes.CLOSED_ABNORMALLY, e.getMessage());
			try
			{
				session.close(cr);
			}
			catch ( IOException ioe2)
			{
				// Ignore
			}
			finally
			{
				
			}
		}

		public void sendTxt(String txt)
		{
			try
			{
				session.getBasicRemote().sendText(txt);
			}
			catch ( IllegalStateException ise)
			{
				closeSessionOnError(ise);
			}
			catch ( IOException ioe)
			{
				closeSessionOnError(ioe);
			}
		}
		
		public synchronized void sendBytes(byte[] bs)
		{
			try
			{
				ByteBuffer bybuf = ByteBuffer.wrap(bs)  ;
				session.getBasicRemote().sendBinary(bybuf);
			}
			catch ( IllegalStateException ise)
			{
				closeSessionOnError(ise);
			}
			catch ( IOException ioe)
			{
				closeSessionOnError(ioe);
			}
		}

		void onTick() throws IOException
		{
			PSCmdPlatformST cmdst = new PSCmdPlatformST() ;
			cmdst.asPlatform(PlatformManager.getInstance()) ;
			sendBytes(cmdst.packTo()) ;
		}
		
		public void sendCmd(PSCmd cmd)
		{
			sendBytes(cmd.packTo()) ;
		}
	}

	private static ConcurrentHashMap<Session, SessionItem> sess2item = new ConcurrentHashMap<>();

	public static synchronized void addSessionItem(SessionItem si)
	{
		sess2item.put(si.getSession(), si);

		// System.out.println(" add session ,num="+sess2item.size()) ;
	}

	public static synchronized void removeSessionItem(Session sess)
	{
		sess2item.remove(sess);

		// System.out.println(" remove session ,num="+sess2item.size()) ;
	}

	public static int getSessionNum()
	{
		return sess2item.size();
	}

	public static Collection<SessionItem> getSessionItems()
	{
		return Collections.unmodifiableCollection(sess2item.values());
	}
	
	public static SessionItem getSessionItem(Session ss)
	{
		return sess2item.get(ss) ;
	}

	private static final long TICK_DELAY = 60000;

	private static Thread th = null;

	protected static void tick()
	{
		for (SessionItem si : getSessionItems())
		{
			//long st = System.currentTimeMillis();
			try
			{
				si.onTick();
			}
			catch ( Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
			}
		}
	}

	public synchronized static void startTimer()
	{
		if (th != null)
			return;

		// System.out.println(" hmi rt -- start timer") ;
		th = new Thread(new Runnable() {
			public void run()
			{
				try
				{
					while(th!=null)
					{
						try
						{
							Thread.sleep(TICK_DELAY);
							if(th==null)
								break;
							tick();
						}
						catch (Exception e)
						{
							// log.error("Caught to prevent timer from shutting down" +
							// e.getMessage());
						}
					}
				}
				finally
				{
					th = null ;
				}
			}
		},PlatformWSServer.class.getSimpleName() + " Timer") ;
		
		th.start();
	}

	public synchronized static void stopTimer(boolean bforce)
	{
		Thread t = th;
		if (t == null)
			return;

		if(bforce)
			t.interrupt();
		th = null;
		
		if(!bforce)
		{
			try
			{
				Thread.sleep(TICK_DELAY);
			}
			catch(Exception e) {}
		}
	}
	
//	public void onMessage(Session session, byte[] msg) throws Exception
//	{
//		
//	}


	@OnOpen
	public void onOpen(Session session,
			@PathParam(value = "stationid") String stationid,EndpointConfig config) throws Exception //
	{
		HttpSession httpSession = (HttpSession) session.getUserProperties().get(HttpSession.class.getName());
		String clientip = (String)httpSession.getAttribute("ClientIP");
        
		PStation pss = PlatformManager.getInstance().getStationById(stationid) ;
		
		if (pss == null)
		{
			PlatformManager.getInstance().fireUnknownStation(stationid) ;
			session.close();
			return;
		}
		
		String key = pss.getKey() ;
		if(Convert.isNotNullEmpty(key))
		{
			String dt = getReqPm(session,"dt") ;
			String tk = getReqPm(session,"tk") ;
			if(Convert.isNullOrEmpty(dt) || Convert.isNullOrEmpty(tk))
			{
				if(log.isWarnEnabled())
					log.warn("station ["+stationid+"] no tk and dt input");
				PlatformManager.getInstance().fireUnknownStation(stationid) ;
				session.close();
				return ;
			}
			
			String decss = DES.decode(tk, key) ;
			if(!dt.equals(decss))
			{
				if(log.isWarnEnabled())
					log.warn("station ["+stationid+"] tk check failed ,may be key is not match");
				PlatformManager.getInstance().fireUnknownStation(stationid) ;
				session.close();
				return ;
			}
		}
		
		SessionItem si = new SessionItem(session, pss,clientip);
		addSessionItem(si);

		startTimer();
	}
	
	private static String getReqPm(Session s,String pn)
	{
		Map<String,List<String>> ppm = s.getRequestParameterMap();
		if(ppm==null)
			return null ;
		List<String> ss = ppm.get(pn) ;
		if(ss==null||ss.size()<=0)
			return null ;
		return ss.get(0) ;
	}


	// 关闭连接时调用
	@OnClose
	public void onClose(Session session,
			@PathParam(value = "stationid") String stationid)
	{
		doClose(session) ;
	}

	private void doClose(Session session)
	{
		SessionItem si = getSessionItem(session) ;
		if(si!=null)
			si.closeDT = System.currentTimeMillis() ;
		
		removeSessionItem(session);
		if (getSessionNum() <= 0)
			stopTimer(false);
	}
	
	@OnMessage
	public void onMessage(Session session, byte[] msg) throws Exception
	{
		SessionItem si = getSessionItem(session) ;
		if(si==null)
			return ;
		
		si.getPStation().RT_onMsg(si,msg) ;
		//super.onMessage(session, msg);
	}
	
	@OnMessage
	public void onMessageTxt(Session session, String msg) throws Exception
	{// {tp:"event",repid:this.repId,hmiid:this.hmiId,diid:diid,name:eventn,val:eventv}
		// ;
		// System.out.println("ws recv:"+msg) ;

		try
		{
			JSONObject job = new JSONObject(msg);
			String tp = job.optString("tp");
			switch (tp)
			{
			case "event":
				
//				String cxtpath = job.getString("cxtpath");
//				if(!checkEventRight(session,cxtpath))
//				{
//					return ;
//				}
//				String hmipath = job.getString("hmipath");
//				String diid = job.getString("diid");
//				String eventn = job.getString("name");
//				Object val = job.opt("val");
//				String strval = null;
//				if(val!=null)
//				{
//					if (val instanceof String)
//						strval = (String) val;
//					else
//						strval = JSONObject.valueToString(val);
//				}
//				onHmiEvent(cxtpath, hmipath, diid, eventn, strval);
				break;
			}
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
	}

	// 错误时调用
	@OnError
	public void onError(Session session, Throwable t, @PathParam(value = "connid") String connid)
	{
		doClose(session) ;
	}
}
