package org.iottree.web.oper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.iottree.core.UAHmi;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UARep;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.bind.BindDI;
import org.iottree.core.bind.EventBindItem;
import org.iottree.core.bind.PropBindItem;
import org.iottree.core.util.Convert;
import org.iottree.core.ws.WSServer;
import org.iottree.core.ws.WebSocketConfig;
import org.json.JSONObject;

@ServerEndpoint(value = "/_ws/hmi/{repname}/{hmiid}", configurator = WebSocketConfig.class)
public class WSHmiRT extends WSServer
{
	static
	{
		
		//System.out.println(" hml ws class is loading ................>>>>>>>>>>>>>>>>>>>>>>") ;
	}
	
	static class SessionItem
	{
		private final Session session;
		private final UARep rep;
		private final UANodeOCTagsCxt nodecxt;
		private final UAHmi hmi ;
		
		private long lastDT = -1 ;
		
		//private boolean bFirstTick=true ;
		
		public SessionItem(Session s,UARep rep,UANodeOCTagsCxt nodecxt,UAHmi hmi)
		{
			this.session = s ;
			this.rep = rep ;
			this.nodecxt = nodecxt ;
			this.hmi = hmi ;
		}
		
		public Session getSession()
		{
			return session ;
		}
		
		public UARep getRep()
		{
			return rep ;
		}
		
		public UAHmi getHmi()
		{
			return hmi ;
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
		
		void onTick()
		{
			UAHmi hmi = getHmi() ;
			
			List<BindDI> pbs = hmi.getBinds() ;
			
			//List<PropBindItem> items = 
			if(pbs==null||pbs.size()<=0)
				return ;
			
			long lastdt = lastDT ;
			lastDT = System.currentTimeMillis() ;
			
			UANodeOCTagsCxt ntags = hmi.getBelongTo() ;
			
			StringBuilder sb = new StringBuilder() ;
			String txt = null ;
			sb.append("{dt:\'"+""+"\',binds:[") ;
			boolean bfirst = true ;
			boolean bhas_data=false;
			for(BindDI pb:pbs)
			{
				if(!bfirst)
					sb.append(",") ;
				else
					bfirst = false ;
				sb.append("{id:\""+pb.getId()+"\",items:[") ;
				
				List<PropBindItem> pbis = pb.getPropBindItems() ;
				boolean bfirstitem = true ;
				
				for(PropBindItem pbi:pbis)
				{
					UAVal val = pbi.RT_getVal(ntags,lastdt) ;
					if(val==null)
						continue ;
					
					bhas_data = true;
					if(!bfirstitem)
						sb.append(",") ;
					else
						bfirstitem = false ;
					
					boolean bvalid = false;
					String vstr = "" ;
					if(val!=null)
					{
						bvalid = val.isValid() ;
						vstr = ""+val.getObjVal() ;
						
						//dt = Convert.toFullYMDHMS(new Date(val.getValDT())) ;
						//dt_chg = Convert.toFullYMDHMS(new Date(val.getValChgDT())) ;
					}
					sb.append("{name:\'");
					sb.append(pbi.getName()) ;
					sb.append("\',valid:"+bvalid+",v:\'"+vstr+"\',dt:"+val.getValDT()+",chgdt:"+val.getValChgDT()+"}") ;
				}
				sb.append("]}") ;
			}
			
			if(!bhas_data)
			{
				//System.out.println("session id="+this.getSession().getId()+" has no data") ;
				return ;//do nothing
			}
			
			//System.out.println("session id="+this.getSession().getId()+" has git data") ;
			
			sb.append("]}") ;
			txt = sb.toString();
			try
			{
				sendTxt(txt);
			} catch (IllegalStateException ise)
			{
				
			}
		}
	}
	
	
	private static ConcurrentHashMap<Session,SessionItem> sess2item = new ConcurrentHashMap<>() ;
	
	public static synchronized void addSessionItem(SessionItem si)
	{
		sess2item.put(si.getSession(), si) ;
		
		System.out.println(" add session ,num="+sess2item.size()) ;
	}
	
	public static synchronized void removeSessionItem(Session sess)
	{
		sess2item.remove(sess) ;
		
		System.out.println(" remove session ,num="+sess2item.size()) ;
	}
	
	public static int getSessionNum()
	{
		return sess2item.size() ;
	}
	
	public static Collection<SessionItem> getSessionItems()
	{
		return Collections.unmodifiableCollection(sess2item.values());
	}
	
	
	public static HashMap<UAHmi,List<SessionItem>> getHmiSessions()
	{
		Collection<SessionItem> sis = getSessionItems() ;
		if(sis==null)
			return null ;
		HashMap<UAHmi,List<SessionItem>> rets = new HashMap<>() ;
		for(SessionItem si:sis)
		{
			UAHmi h = si.getHmi() ;
			List<SessionItem> sss = rets.get(h) ;
			if(sss==null)
			{
				sss = new ArrayList<>() ;
				rets.put(h, sss) ;
			}
			sss.add(si) ;
		}
		return rets ;
	}

	
	
	private static Timer timer = null;

	private static final long TICK_DELAY = 100;
	
	protected static void tick()
	{
		for(SessionItem si : getSessionItems())
			si.onTick();
//		HashMap<UAHmi,List<SessionItem>> hmi2sis = getHmiSessions() ;
//		if(hmi2sis==null||hmi2sis.size()<0)
//			return ;
//		
//		for (Map.Entry<UAHmi,List<SessionItem>> h2si:hmi2sis.entrySet())
//		{
//			
//		}
	}
	
	public synchronized static void startTimer()
	{
		if(timer!=null)
			return ;
		
		System.out.println(" hmi rt  --  start timer") ;
		
		timer = new Timer(WSHmiRT.class.getSimpleName() + " Timer");
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
			System.out.println(" hmi rt  --  stop timer") ;
			timer.cancel();
			timer = null ;
		}
	}
	
	@OnOpen
	public void onOpen(Session session, @PathParam(value = "repname") String repname,@PathParam(value = "hmiid") String hmiid) throws Exception //
	{
		UARep rep = UAManager.getInstance().getRepByName(repname) ;
		if(rep==null)
		{
			session.close();
			return ;
		}
		UAHmi hmi = rep.findHmiById(hmiid) ;
		if(hmi==null)
		{
			session.close(); 
			return ;
		}
		
		UANodeOCTagsCxt nodecxt = hmi.getBelongTo() ;
		
		SessionItem si = new SessionItem(session,rep,nodecxt,hmi);
		addSessionItem(si) ;
		
		startTimer() ;
	}

	// 关闭连接时调用
	@OnClose
	public void onClose(Session session, @PathParam(value = "repname") String repname,@PathParam(value = "hmiid") String hmiid)
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
	{//{tp:"event",repid:this.repId,hmiid:this.hmiId,diid:diid,name:eventn,val:eventv} ;
		System.out.println("ws recv:"+msg) ;
		
		try
		{
			JSONObject job = new JSONObject(msg) ;
			String tp = job.optString("tp") ;
			switch(tp)
			{
			case "event":
				String repid = job.getString("repid") ;
				String hmiid = job.getString("hmiid") ;
				String diid = job.getString("diid") ;
				String eventn = job.getString("name") ;
				Object val = job.get("val") ;
				String strval = null ;
				if(val instanceof String)
					strval = (String)val ;
				else
					strval = JSONObject.valueToString(val);
				onHmiEvent(repid,hmiid,diid,eventn,strval);
				break ;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// 错误时调用
	@OnError
	public void onError(Session session, Throwable t, @PathParam(value = "connid") String connid)
	{
		//getAgentServer().onSessionErr(connid, t);
		removeSessionItem(session);
	}
	
	
	private void onHmiEvent(String repid,String hmiid,String diid,String eventn,String val)
	{
		UARep rep = UAManager.getInstance().getRepById(repid) ;
		if(rep==null)
			return ;
		UAHmi hmi = rep.findHmiById(hmiid) ;
		if(hmi==null)
			return ;
		BindDI bdi = hmi.getBind(diid) ;
		if(bdi==null)
			return ;
		EventBindItem ebi = bdi.getEventBindItem(eventn) ;
		ebi.RT_runEventJS(hmi.getBelongTo(),val) ;
	}
}
