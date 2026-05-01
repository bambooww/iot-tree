package org.iottree.core.ws;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;

import org.iottree.core.UAPrj;
import org.iottree.core.util.web.LoginUtil;
import org.json.JSONObject;

public abstract class WSChatAI
{
	protected static class SessionItem
	{
		private final Session session;
		private final UAPrj prj;
		EndpointConfig config = null;
		private long lastDT = -1;
		
		private boolean bSys = true ;
		private boolean bSub = false;

		public SessionItem(Session s, UAPrj rep, EndpointConfig config)
		{
			this.session = s;
			this.prj = rep;
			this.config = config;
			
			Map<String,List<String>> ppm = s.getRequestParameterMap();//.getPathParameters() ;
			if(ppm!=null)
			{
				bSys = !"false".equals(getReqPm(s,"sys")) ;
				bSub = "true".equals(getReqPm(s,"sub")) ;
			}
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

		public Session getSession()
		{
			return session;
		}

		public boolean checkRight()
		{
			HttpSession hs = WebSocketConfig.getHttpSession(config);
			return LoginUtil.checkUserLogin(hs);
		}

		public UAPrj getPrj()
		{
			return prj;
		}

		/**
		 * get dyn json for rep_editor
		 * 
		 * @return
		 */
		public String getRepDynJsonStr()
		{
			long curt = System.currentTimeMillis();
			JSONObject jobj = prj.toOCDynJSON(lastDT);
			lastDT = curt;
			return jobj.toString(2);
		}

		public void sendTxt(String txt)
		{
			if (!checkRight())
				return;
			try
			{
				session.getBasicRemote().sendText(txt);
			}
			catch ( IOException ioe)
			{
				CloseReason cr = new CloseReason(CloseCodes.CLOSED_ABNORMALLY, ioe.getMessage());
				try
				{
					session.close(cr);
				}
				catch ( IOException ioe2)
				{
					// Ignore
				}
			}
		}
	}

	private static ConcurrentHashMap<Session, SessionItem> sess2item = new ConcurrentHashMap<>();

	public static synchronized void addSessionItem(SessionItem si)
	{
		sess2item.put(si.getSession(), si);
	}

	public static synchronized void removeSessionItem(Session sess)
	{
		sess2item.remove(sess);
	}

	public static int getSessionNum()
	{
		return sess2item.size();
	}

	public static Collection<SessionItem> getSessionItems()
	{
		return Collections.unmodifiableCollection(sess2item.values());
	}

//	private static final long TICK_DELAY = 100;
//
//	private static Thread th = null;
//
//	public synchronized static void startTimer()
//	{
//		if (th != null)
//			return;
//
//		// System.out.println(" cxt rt -- start timer") ;
//
//		// timer = new Timer(WSRoot.class.getSimpleName() + " Timer");
//		th = new Thread(new Runnable() {
//			public void run()
//			{
//				try
//				{
//					while (th != null)
//					{
//						try
//						{
//							Thread.sleep(TICK_DELAY);
//							if (th == null)
//								break;
//							for (SessionItem si : getSessionItems())
//							{
//								on_tick(si);
//							}
//						}
//						catch ( Exception e)
//						{
//							// log.error("Caught to prevent timer from shutting
//							// down" + e.getMessage());
//						}
//					}
//				}
//				finally
//				{
//					th = null;
//				}
//			}
//		}, WSChatAI.class.getSimpleName() + " Timer");
//		// timer.scheduleAtFixedRate(new TimerTask() {
//		// @Override
//		//
//		// }, TICK_DELAY, TICK_DELAY);
//		th.start();
//	}

//	private static void on_tick(SessionItem si)
//	{
//		UAPrj prj = si.getPrj() ;
//
//		StringWriter sw = new StringWriter();
//		String txt = null;
//		try
//		{
//			//first line is server global inf
//			sw.append("{\"dt\":"+System.currentTimeMillis()+"}\r\n");
//			
//			//ntags.CXT_renderJson(sw);
//			ntags.CXT_renderJson(sw, null, -1,null,
//					!si.bSys,//boolean ignore_sys_tag,
//					si.bSub //boolean inc_sub
//					) ;
//			txt = sw.toString();
//		}
//		catch ( Exception e)
//		{
//		}
//
//		if (txt == null)
//			return;
//		try
//		{
//			si.sendTxt(txt);
//		}
//		catch ( IllegalStateException ise)
//		{
//
//		}
//	}

//	public synchronized static void stopTimer(boolean bforce)
//	{
//		Thread t = th;
//		if (t == null)
//			return;
//
//		if (bforce)
//			t.interrupt();
//		th = null;
//		
//		if(!bforce)
//		{
//			try
//			{
//				Thread.sleep(TICK_DELAY);
//			}
//			catch(Exception e) {}
//		}
//	}
	
	public abstract void onMessage(Session session, byte[] msg) throws Exception;
	
	public abstract void onMessageTxt(Session session, String msg) throws Exception;

	static
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run()
			{
				//stopTimer(false);
			}
		});
	}
}
