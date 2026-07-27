package org.iottree.web.oper;

import javax.servlet.http.HttpSession;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.EndpointConfig;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.iottree.core.UAHmi;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UAUtil;
import org.iottree.core.ws.WSServer;
import org.iottree.core.ws.WebSocketConfig;
import org.json.JSONObject;

@ServerEndpoint(value = "/_ws/hmi/{prjname}/{hmiid}", configurator = WebSocketConfig.class)
public class WSHmiRT extends WSServer
{
	
	private static final String PAU = "_pau_" ;
	static
	{

	}

	@OnOpen
	public void onOpen(Session session, @PathParam(value = "prjname") String prjname,
			@PathParam(value = "hmiid") String hmiid,EndpointConfig config) throws Exception //
	{
		UAPrj rep = UAManager.getInstance().getPrjByName(prjname);
		if (rep == null)
		{
			session.close();
			return;
		}
		UAHmi hmi = rep.findHmiById(hmiid);
		if (hmi == null)
		{
			session.close();
			return;
		}

		UANodeOCTagsCxt nodecxt = hmi.getBelongTo();
		UAHmi.OperUser ou = UAHmi.OPER_checkSessionAuthOk(config, rep, hmi) ;
		//LoginUtil.SessionItem login_si = LoginUtil.getUserLoginSession(config) ;
		//System.out.println(" hmi ws open user >>"+login_si) ;
		
//		if(!hmi.RT_checkReadUserRight(login_si))
//		{
//			session.close();
//			return;
//		}
//		PlugAuth pa = PlugManager.getInstance().getPlugAuth() ;
//		if(pa!=null)
//		{
//			try
//			{
//				
//				if(!pa.checkReadRight(nodecxt.getNodePath(), config))
//				{//no right
//					session.close();
//					return;
//				}
//			}
//			catch(Exception e)
//			{
//				//e.printStackTrace();
//				//PrintWriter w = resp.getWriter();
//				e.printStackTrace();
//				//w.write(e.getMessage());
//				return ;
//			}
//			
//			PlugAuthUser pau = pa.checkUserByWebSocket(config) ;
//			if(pau!=null)
//				session.getUserProperties().put(PAU, pau) ;
//		}
		HttpSession hs = WebSocketConfig.getHttpSession(config) ;
		SessionItem<UAHmi.OperUser> si = new SessionItem<>(session,hs, rep, nodecxt, hmi,ou);
		addSessionItem(si);

		startTimer();
	}

	//
	@OnClose
	public void onClose(Session session, @PathParam(value = "prjname") String prjname,
			@PathParam(value = "hmiid") String hmiid)
	{
		removeSessionItem(session);
		// getAgentServer().onSessionUnset(session.getId());
		if (getSessionNum() <= 0)
			stopTimer(false);
	}

	@OnMessage
	public void onMessage(Session session, byte[] msg) throws Exception
	{
	}

	@OnMessage
	public void onMessageTxt(Session session, String msg) throws Exception
	{// {tp:"event",repid:this.repId,hmiid:this.hmiId,diid:diid,name:eventn,val:eventv}
		// ;
		// System.out.println("ws recv:"+msg) ;
		WSServer.SessionItem<UAHmi.OperUser> ws_si = getSessionItem(session) ; //session on open,it may be null
		try
		{
			JSONObject job = new JSONObject(msg);
			String tp = job.optString("tp");
			switch (tp)
			{
			case "event":
				// {tp:"event",cxtpath:cxtpath,hmipath:hmipath,diid:diid,name:eventn,val:eventv}
				// ;
				String cxtpath = job.getString("cxtpath");
				String hmipath = job.getString("hmipath");
				String diid = job.getString("diid");
				String eventn = job.getString("name");
				Object val = job.opt("val");
				String strval = null;
				if(val!=null)
				{
					if (val instanceof String)
						strval = (String) val;
					else
						strval = JSONObject.valueToString(val);
				}
				StringBuilder failedr = new StringBuilder() ;
				
				String retmsg = "done" ;
				
				if(!onHmiEvent(ws_si,cxtpath, hmipath, diid, eventn, strval,failedr))
					retmsg = failedr.toString() ;
				JSONObject out_jo = new JSONObject() ;
				out_jo.put("__tp","evt_ret").put("msg", retmsg) ;
				ws_si.sendTxt(out_jo.toString());
				break;
			}
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
	}

	//
	@OnError
	public void onError(Session session, Throwable t, @PathParam(value = "connid") String connid)
	{
		// getAgentServer().onSessionErr(connid, t);
		removeSessionItem(session);
		if(getSessionNum()<=0)
			stopTimer(false) ;
	}

	private boolean onHmiEvent(WSServer.SessionItem<UAHmi.OperUser> siou,
			String cxtpath, String hmipath, String diid, String eventn, String val,StringBuilder failedr)
	{
		UANode cxtn = UAUtil.findNodeByPath(cxtpath);// .getPrjById(repid) ;
		if (cxtn == null)
			return false;
		UAHmi hmi = UAUtil.findHmiByPath(hmipath);
		if (hmi == null)
		{
			failedr.append("no hmi found") ;
			return false;
		}
		
		return hmi.OPER_onOperEvent(siou,diid, eventn, val,failedr) ;
	}
	

//	private boolean checkEventRight(Session session,String path,LoginUtil.SessionItem login_si)
//	{
//		WSServer.SessionItem wss =  getSessionItem(session) ;
//		if(wss)
//		try
//		{
//			
//			return true;
//		}
//		catch(Exception e)
//		{
//			//e.printStackTrace();
//			//PrintWriter w = resp.getWriter();
//			e.printStackTrace();
//			//w.write(e.getMessage());
//			return false;
//		}
//	}
	
}
