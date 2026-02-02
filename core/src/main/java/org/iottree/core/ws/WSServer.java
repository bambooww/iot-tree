package org.iottree.core.ws;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;

import org.iottree.core.UAHmi;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.basic.ValEvent;
import org.iottree.core.bind.PropBindItem;
import org.iottree.core.station.PlatInsManager;
import org.iottree.core.util.Convert;
import org.iottree.core.ws.WSRoot.SessionItem;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class WSServer// extends ConnServer
{

	// protected String getSessionHead(Session session,String name)
	// {
	// Map<String, List<String>> heads = (Map<String, List<String>>)
	// session.getUserProperties().get("req_head");
	//
	// List<String> vvs = heads.get(name);
	// if (vvs == null || vvs.size() <= 0)
	// {
	// return null;
	// }
	// return vvs.get(0);
	// }

	public static class SessionItem
	{
		private final Session session;
		private final UAPrj prj;
		private final UANodeOCTagsCxt nodecxt;
		private final UAHmi hmi;

		// private long lastDT = -1;
		private transient HashMap<UATag, Long> tag2lastdt = new HashMap<>();

		//first create
		private boolean bFirstTick = true ;

		public SessionItem(Session s, UAPrj rep, UANodeOCTagsCxt nodecxt, UAHmi hmi)
		{
			this.session = s;
			this.prj = rep;
			this.nodecxt = nodecxt;
			this.hmi = hmi;
		}

		public Session getSession()
		{
			return session;
		}

		public UAPrj getPrj()
		{
			return prj;
		}

		public UAHmi getHmi()
		{
			return hmi;
		}

		// /**
		// * get dyn json for rep_editor
		// *
		// * @return
		// */
		// public String getRepDynJsonStr()
		// {
		// long curt = System.currentTimeMillis();
		// JSONObject jobj = prj.toOCDynJSON(lastDT);
		// lastDT = curt;
		// return jobj.toString(2);
		// }

		public UANodeOCTagsCxt getNodeTagCxt()
		{
			return nodecxt;
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
		
		void onTick() throws IOException
		{
			UAHmi hmi = getHmi();
			UAPrj prj = getPrj();

			UANodeOCTagsCxt ntags = hmi.getBelongTo();
			JSONObject out_jo = new JSONObject() ;
			out_jo.put("server", new JSONObject().put("dt",System.currentTimeMillis())) ;
			
			if(bFirstTick)
			{
				out_jo.put("first_tick",true) ;
				bFirstTick = false;
				List<PropBindItem> tag_cache_pbis = hmi.getPropBindItem_NeedTagCached() ;
				if(tag_cache_pbis!=null&&tag_cache_pbis.size()>0)
				{
					JSONObject tags_mc = new JSONObject() ;
					for(PropBindItem pbi:tag_cache_pbis)
					{
						if(!pbi.isNeedTagCached() )
							continue ;
						UATag tag = pbi.getBindedTag() ;
						if(tag==null)
							continue ;
						List<UAVal> hisval = tag.HIS_getVals(-1) ;
						if(hisval==null||hisval.size()<=0)
							continue ;
						String tagp = pbi.getTxt();
						JSONArray his_jarr = new JSONArray() ;
						for(UAVal v:hisval)
						{
							Object objv =v.getObjVal();
							if(objv==null)
								continue ;
							his_jarr.put(new JSONObject().put("ts", v.getValDT()).put("v", objv)) ;
						}
						tags_mc.put(tagp,his_jarr) ;
					}
					out_jo.put("tags_mem_cached", tags_mc) ;
				}
			}
			
			JSONObject data = new JSONObject() ;
			out_jo.put("data",data) ;
			data.put("prj_id",prj.getId()).put("cxt_path", ntags.getNodePathCxt()).put("prj_run",prj.RT_isRunning()) ;
			
			if (prj.RT_isRunning() || prj.isPrjPStationIns()) //PlatInsManager.isInPlatform())
			{
				StringWriter sw_rt = new StringWriter();

				if (ntags.CXT_renderJson(sw_rt, tag2lastdt, null))
				{
					JSONObject cxt_rt = new JSONObject(sw_rt.toString()) ;
					data.put("cxt_rt",cxt_rt) ;
				}
				
				//render alerts
				JSONArray jarr = ntags.CXT_getAlertsJArr() ;
				if(jarr!=null && jarr.length()>0)
				{
					data.put("has_alert",true).put("alerts",jarr) ;
				}

				List<ValEvent> vas = ntags.CXT_listAlerts();
				if(vas!=null&&vas.size()>0)
				{
					jarr = new JSONArray() ;
					for(ValEvent va:vas)
					{
						JSONObject jo = va.RT_get_triggered_jo() ;
						if(jo==null)
							continue ;
						jarr.put(jo) ;
					}
					data.put("has_tag_alert",true).put("tag_alerts",jarr) ;
				}
			}

			String txt = out_jo.toString();
			try
			{
				sendTxt(txt);
			}
			catch ( IllegalStateException ise)
			{
				ise.printStackTrace();
			}
		}

		void onTick_old() throws IOException
		{
			UAHmi hmi = getHmi();
			UAPrj prj = getPrj();

			UANodeOCTagsCxt ntags = hmi.getBelongTo();

			StringWriter sw = new StringWriter();
			sw.append("{\"dt\":"+System.currentTimeMillis()+"}\r\n");
			sw.write("{\"prj_id\":\"" + prj.getId() + "\",\"cxt_path\":\"" + ntags.getNodePathCxt() + "\",\"prj_run\":"
					+ prj.RT_isRunning());
			if (prj.RT_isRunning() || prj.isPrjPStationIns()) //PlatInsManager.isInPlatform())
			{
				StringWriter sw_rt = new StringWriter();

				if (ntags.CXT_renderJson(sw_rt, tag2lastdt, null))
				{
					sw.write(",\"cxt_rt\":");
					sw.write(sw_rt.toString());
				}
				
				//render alerts
				JSONArray jarr = ntags.CXT_getAlertsJArr() ;
				if(jarr!=null && jarr.length()>0)
				{// alert_handlers/alert_items
					sw.write(",\"has_alert\":true,\"alerts\":");
					jarr.write(sw) ;
				}

				List<ValEvent> vas = ntags.CXT_listAlerts();
				if(vas!=null&&vas.size()>0)
				{
					sw.write(",\"has_tag_alert\":true,\"tag_alerts\":");
					
					jarr = new JSONArray() ;
					for(ValEvent va:vas)
					{
						JSONObject jo = va.RT_get_triggered_jo() ;
						if(jo==null)
							continue ;
						jarr.put(jo) ;
					}
					jarr.write(sw) ;
				}
			}
			
			sw.write("}");

			String txt = sw.toString();
			try
			{
				sendTxt(txt);
			}
			catch ( IllegalStateException ise)
			{
				ise.printStackTrace();
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

	public static HashMap<UAHmi, List<SessionItem>> getHmiSessions()
	{
		Collection<SessionItem> sis = getSessionItems();
		if (sis == null)
			return null;
		HashMap<UAHmi, List<SessionItem>> rets = new HashMap<>();
		for (SessionItem si : sis)
		{
			UAHmi h = si.getHmi();
			List<SessionItem> sss = rets.get(h);
			if (sss == null)
			{
				sss = new ArrayList<>();
				rets.put(h, sss);
			}
			sss.add(si);
		}
		return rets;
	}

	// private static Timer timer = null;

	private static final long TICK_DELAY = 100;

	private static Thread th = null;

	protected static void tick()
	{
		for (SessionItem si : getSessionItems())
		{
			long st = System.currentTimeMillis();
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
				// System.out.println("tick
				// cost="+(System.currentTimeMillis()-st));
			}
		}

		// HashMap<UAHmi,List<SessionItem>> hmi2sis = getHmiSessions() ;
		// if(hmi2sis==null||hmi2sis.size()<0)
		// return ;
		//
		// for (Map.Entry<UAHmi,List<SessionItem>> h2si:hmi2sis.entrySet())
		// {
		//
		// }
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
		},WSServer.class.getSimpleName() + " Timer") ;
		
		th.start();
//		timer = new Timer(WSServer.class.getSimpleName() + " Timer");
//		timer.scheduleAtFixedRate(new TimerTask() {
//			@Override
//			public void run()
//			{
//				try
//				{
//					tick();
//				}
//				catch ( RuntimeException e)
//				{
//					// log.error("Caught to prevent timer from shutting down" +
//					// e.getMessage());
//				}
//			}
//		}, TICK_DELAY, TICK_DELAY);
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
		// if (timer != null)
		// {
		// // System.out.println(" hmi rt -- stop timer") ;
		// timer.cancel();
		// timer = null;
		// }
	}

}
