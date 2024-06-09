package org.iottree.web.admin;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.util.web.LoginUtil;
import org.iottree.core.ws.WSMsgNetRoot;
import org.iottree.core.ws.WSRoot;
import org.iottree.core.ws.WebSocketConfig;

@ServerEndpoint(value = "/_ws/net_msg/{prjid}/{netid}", configurator = WebSocketConfig.class)
public class WSNetMsgDebug extends WSMsgNetRoot
{
	static
	{
		//System.out.println(" cxt ws class is loading ................>>>>>>>>>>>>>>>>>>>>>>") ;
	}
	
	
	
	@OnOpen
	public void onOpen(Session session, @PathParam(value = "prjid") String prjid,
			@PathParam(value = "netid") String netid,EndpointConfig config) throws Exception //
	{
		HttpSession hs = WebSocketConfig.getHttpSession(config) ;
		if(!LoginUtil.checkAdminLogin(hs))
		{
			session.close();
			return ;
		}
		UAPrj rep = UAManager.getInstance().getPrjById(prjid) ;
		if(rep==null)
		{
			session.close();
			return ;
		}
		MNManager mnmgr = MNManager.getInstance(rep) ;
		MNNet net = mnmgr.getNetById(netid) ;

		SessionItem si = new SessionItem(session,rep,net,config);
		addSessionItem(si) ;
		
		//startTimer() ;
	}

	// 关闭连接时调用
	@OnClose
	public void onClose(Session session, @PathParam(value = "connid") String connid)
	{
		removeSessionItem(session);
		
//		if(getSessionNum()<=0)
//			stopTimer(false) ;
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
		removeSessionItem(session);
//		if(getSessionNum()<=0)
//			stopTimer(false) ;
	}
}
