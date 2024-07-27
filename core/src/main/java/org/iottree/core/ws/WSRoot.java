package org.iottree.core.ws;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;

import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.station.PlatformManager;
import org.iottree.core.util.web.LoginUtil;
import org.iottree.core.ws.WSRoot.SessionItem;
import org.json.JSONObject;

/**
 * for web root WSCxtRT
 * 
 * @author jason.zhu
 *
 */
public abstract class WSRoot
{

	protected static class SessionItem
	{
		private final Session session;
		private final UAPrj prj;
		private final UANodeOCTagsCxt nodecxt;
		EndpointConfig config = null;
		private long lastDT = -1;

		public SessionItem(Session s, UAPrj rep, UANodeOCTagsCxt nodecxt, EndpointConfig config)
		{
			this.session = s;
			this.prj = rep;
			this.nodecxt = nodecxt;
			this.config = config;
		}

		public Session getSession()
		{
			return session;
		}

		public boolean checkRight()
		{
			HttpSession hs = WebSocketConfig.getHttpSession(config);
			return LoginUtil.checkAdminLogin(hs);
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

		public UANodeOCTagsCxt getNodeTagCxt()
		{
			return nodecxt;
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

	// private static Timer timer = null;

	private static final long TICK_DELAY = 100;

	private static Thread th = null;

	public synchronized static void startTimer()
	{
		if (th != null)
			return;

		// System.out.println(" cxt rt -- start timer") ;

		// timer = new Timer(WSRoot.class.getSimpleName() + " Timer");
		th = new Thread(new Runnable() {
			public void run()
			{
				try
				{
					while (th != null)
					{
						try
						{
							Thread.sleep(TICK_DELAY);
							if (th == null)
								break;
							for (SessionItem si : getSessionItems())
							{
								on_tick(si);
							}
						}
						catch ( Exception e)
						{
							// log.error("Caught to prevent timer from shutting
							// down" + e.getMessage());
						}
					}
				}
				finally
				{
					th = null;
				}
			}
		}, WSRoot.class.getSimpleName() + " Timer");
		// timer.scheduleAtFixedRate(new TimerTask() {
		// @Override
		//
		// }, TICK_DELAY, TICK_DELAY);
		th.start();
	}

	private static void on_tick(SessionItem si)
	{
		UAPrj prj = si.getPrj() ;
		if(!prj.RT_isRunning() && !PlatformManager.isInPlatform())
			return ;
		
		UANodeOCTagsCxt ntags = si.getNodeTagCxt();
		StringWriter sw = new StringWriter();
		String txt = null;
		try
		{
			//first line is server global inf
			sw.append("{\"dt\":"+System.currentTimeMillis()+"}\r\n");
			
			ntags.CXT_renderJson(sw);
			txt = sw.toString();
		}
		catch ( Exception e)
		{
		}

		if (txt == null)
			return;
		try
		{
			si.sendTxt(txt);
		}
		catch ( IllegalStateException ise)
		{

		}
	}

	public synchronized static void stopTimer(boolean bforce)
	{
		Thread t = th;
		if (t == null)
			return;

		if (bforce)
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
		// if (timer != null)
		// {
		// System.out.println(" SWRoot -- stop timer") ;
		// timer.purge();
		// timer.cancel();
		// timer = null ;
		// }
	}

	static
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run()
			{
				stopTimer(false);
			}
		});
	}

}
