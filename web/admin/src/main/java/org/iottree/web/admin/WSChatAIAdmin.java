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
import org.iottree.core.util.web.LoginUtil;
import org.iottree.core.ws.WSChatAI;
import org.iottree.core.ws.WSRoot;
import org.iottree.core.ws.WebSocketConfig;

@ServerEndpoint(value = "/_ws/chat_ai/{prjname}", configurator = WebSocketConfig.class)
public class WSChatAIAdmin extends WSChatAI
{
	static
	{
	}
	
	@OnOpen
	public void onOpen(Session session, @PathParam(value = "prjname") String prjname,EndpointConfig config) throws Exception
	{
		HttpSession hs = WebSocketConfig.getHttpSession(config) ;
		if(!LoginUtil.checkUserLogin(hs))
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
		
		SessionItem si = new SessionItem(session,rep,config);
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
		System.out.println("on msg="+msg) ;
	}

	@OnError
	public void onError(Session session, Throwable t, @PathParam(value = "connid") String connid)
	{
		removeSessionItem(session);
//		if(getSessionNum()<=0)
//			stopTimer(false) ;
	}
}
