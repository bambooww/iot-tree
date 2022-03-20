package org.iottree.web.admin;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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
import javax.websocket.server.ServerEndpoint;

import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.iottree.core.util.web.LoginUtil;
import org.iottree.core.ws.WSServer;
import org.iottree.core.ws.WebSocketConfig;
import org.json.JSONObject;

@ServerEndpoint(value = "/_ws/cxt_rt/{prjname}/{nodeid}", configurator = WebSocketConfig.class)
public class WSCxtRT// extends WSServer
{
	static
	{
		//System.out.println(" cxt ws class is loading ................>>>>>>>>>>>>>>>>>>>>>>") ;
	}
	
	static class SessionItem
	{
		private final Session session;
		private final UAPrj rep;
		private final UANodeOCTagsCxt nodecxt;
		EndpointConfig config = null;
		private long lastDT = -1 ;
		
		public SessionItem(Session s,UAPrj rep,UANodeOCTagsCxt nodecxt,EndpointConfig config)
		{
			this.session = s ;
			this.rep = rep ;
			this.nodecxt = nodecxt ;
			this.config = config;
		}
		
		public Session getSession()
		{
			return session ;
		}
		
		public boolean checkRight()
		{
			HttpSession hs = WebSocketConfig.getHttpSession(config) ;
			return LoginUtil.checkAdminLogin(hs) ;
		}
		
		public UAPrj getRep()
		{
			return rep ;
		}
		
		/**
		 * get dyn json for rep_editor
		 * @return
		 */
		public String getRepDynJsonStr()
		{
			long curt = System.currentTimeMillis() ;
			JSONObject jobj = rep.toOCDynJSON(lastDT);
			lastDT = curt ;
			return jobj.toString(2);
		}
		
		public UANodeOCTagsCxt getNodeTagCxt()
		{
			return nodecxt ;
		}
		
		public void sendTxt(String txt)
		{
			if(!checkRight())
				return ;
			try {
	            session.getBasicRemote().sendText(txt);
	        } catch (IOException ioe) {
	            CloseReason cr =
	                    new CloseReason(CloseCodes.CLOSED_ABNORMALLY, ioe.getMessage());
	            try {
	                session.close(cr);
	            } catch (IOException ioe2) {
	                // Ignore
	            }
	        }
		}
	}
	
	
	private static ConcurrentHashMap<Session,SessionItem> sess2item = new ConcurrentHashMap<>() ;
	
	public static synchronized void addSessionItem(SessionItem si)
	{
		sess2item.put(si.getSession(), si) ;
		
		//System.out.println(" add session ,num="+sess2item.size()) ;
	}
	
	public static synchronized void removeSessionItem(Session sess)
	{
		sess2item.remove(sess) ;
		
		//System.out.println(" remove session ,num="+sess2item.size()) ;
	}
	
	public static int getSessionNum()
	{
		return sess2item.size() ;
	}
	
	public static Collection<SessionItem> getSessionItems()
	{
		return Collections.unmodifiableCollection(sess2item.values());
	}
	
	private static Timer timer = null;

	private static final long TICK_DELAY = 100;
	
	protected static void tick()
	{
		for (SessionItem si : getSessionItems())
		{
			UANodeOCTagsCxt ntags = si.getNodeTagCxt() ;
			StringWriter sw = new StringWriter() ;
			String txt=  null ;
			try
			{
				ntags.CXT_renderJson(sw);
				txt = sw.toString();
			}
			catch(Exception e)
			{}
			/*
			String txt = null ;
			if(ntags==null)
			{
				txt = si.getRepDynJsonStr() ;
			}
			else
			{
				List<UATag> tags = ntags.listTagsAll() ;
				String parent_p = ntags.getNodePathName() ;
				if(Convert.isNotNullEmpty(parent_p))
					parent_p +="." ;

				StringBuilder sb = new StringBuilder() ;
				sb.append("{dt:\'"+""+"\',vals:[") ;
				boolean bfirst = true ;
				for(UATag tg : tags)
				{
					String pathn = tg.getBelongToNode().getNodePathName()+"."+tg.getName() ;
					pathn = pathn.substring(parent_p.length()) ;
					
					if(!bfirst)
						sb.append(",") ;
					else
						bfirst = false ;

					UAVal val = tg.RT_getVal() ;
					boolean bvalid = false;
					String vstr = "" ;
					String dt = "" ;
					String dt_chg="" ;
					if(val!=null)
					{
						bvalid = val.isValid() ;
						vstr = ""+val.getObjVal() ;
						
						dt = Convert.toFullYMDHMS(new Date(val.getValDT())) ;
						dt_chg = Convert.toFullYMDHMS(new Date(val.getValChgDT())) ;
					}
					sb.append("{path:\'");
					sb.append(pathn) ;
					
					sb.append("\',valid:"+bvalid+",v:\'"+vstr+"\',dt:\'"+dt+"\',chgdt:\'"+dt_chg+"\'}") ;
				}

				sb.append("]}") ;
				txt = sb.toString();
			}
			*/
			if(txt==null)
				continue ;
			try
			{
				si.sendTxt(txt);
			} catch (IllegalStateException ise)
			{
				
			}
		}
	}
	
	public synchronized static void startTimer()
	{
		if(timer!=null)
			return ;
		
		//System.out.println(" cxt rt  --  start timer") ;
		
		timer = new Timer(WSCxtRT.class.getSimpleName() + " Timer");
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run()
			{
				try
				{
					tick();
				} catch (RuntimeException e)
				{
					//log.error("Caught to prevent timer from shutting down" + e.getMessage());
				}
			}
		}, TICK_DELAY, TICK_DELAY);
	}

	public synchronized static void stopTimer()
	{
		if (timer != null)
		{
			//System.out.println(" cxt rt  --  stop timer") ;
			timer.cancel();
			timer = null ;
		}
	}
	
	
	
	@OnOpen
	public void onOpen(Session session, @PathParam(value = "prjname") String prjname,
			@PathParam(value = "nodeid") String nodeid,EndpointConfig config) throws Exception //
	{
		HttpSession hs = WebSocketConfig.getHttpSession(config) ;
		if(!LoginUtil.checkAdminLogin(hs))
		{
			session.close();
			return ;
		}
		UAPrj rep = UAManager.getInstance().getPrjByName(prjname) ;
		if(rep==null)
		{
			session.close();
			return ;
		}
		UANodeOCTagsCxt nodecxt = null ;
		
		UANode n = rep.findNodeById(nodeid) ;
		if(n!=null && (n instanceof UANodeOCTagsCxt) )
		{
			nodecxt = (UANodeOCTagsCxt)n;
		}
		
//		ConnAuth auth = getAuthFromSession(connid, session);
//		if (auth == null)
//		{
//			try
//			{
//				session.close();
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//		}

		SessionItem si = new SessionItem(session,rep,nodecxt,config);
		addSessionItem(si) ;
		
		startTimer() ;
	}

	// 关闭连接时调用
	@OnClose
	public void onClose(Session session, @PathParam(value = "connid") String connid)
	{
		removeSessionItem(session);
		//getAgentServer().onSessionUnset(session.getId());
		if(getSessionNum()<=0)
			stopTimer() ;
	}


	@OnMessage
	public void onMessage(Session session, byte[] msg) throws Exception
	{
	}
	
	@OnMessage
	public void onMessageTxt(Session session, String msg) throws Exception
	{
	}

	// 错误时调用
	@OnError
	public void onError(Session session, Throwable t, @PathParam(value = "connid") String connid)
	{
		//getAgentServer().onSessionErr(connid, t);
		removeSessionItem(session);
	}
}
