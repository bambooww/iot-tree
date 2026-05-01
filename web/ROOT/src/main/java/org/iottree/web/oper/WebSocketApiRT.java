package org.iottree.web.oper;

import java.nio.ByteBuffer;
import java.util.List;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.nodes.NM_WebSocketApi;
import org.iottree.core.ws.WebSocketConfig;
import org.iottree.core.ws.WSServerMsgNet.SessItem;
import org.iottree.core.ws.WSServerMsgNet;

@ServerEndpoint(value = "/_ws/api/{prjname}/{net}/{api}", configurator = WebSocketConfig.class)
public class WebSocketApiRT extends WSServerMsgNet
{
	
	//private static final String PAU0 = "_pau_" ;
	static
	{

		//System.out.println(" hml ws class is loading................>>>>>>>>>>>>>>>>>>>>>>") ;
	}

	@OnOpen
	public void onOpen(Session session, 
			@PathParam(value = "prjname") String prjname,
			@PathParam(value = "net") String net,
			@PathParam(value = "api") String api,
			EndpointConfig config) throws Exception //
	{
		UAPrj prj = UAManager.getInstance().getPrjByName(prjname) ;
		if(prj==null)
		{
			session.close();
			return ;
		}
		MNNet nnn = MNManager.getInstance(prj).getNetByName(net) ;
		if(nnn==null)
		{
			session.close();
			return ;
		}
		List<NM_WebSocketApi> apis = nnn.findItemByTpMark(NM_WebSocketApi.class, null) ;
		NM_WebSocketApi nd = null ;
		if(apis!=null)
		{
			for(NM_WebSocketApi ii:apis)
			{
				if(api.equals(ii.getApiName()))
				{
					nd = ii ;
					break ;
				}
			}
		}
		if(nd==null)
		{
			session.close();
			return ;
		}
		SessItem si = new SessItem(session, prj, nnn, nd);
		addSessItem(si);
	}

	// 关闭连接时调用
	@OnClose
	public void onClose(Session session, @PathParam(value = "prjname") String prjname)
	{
		removeSessItem(session);
	}

	@OnMessage
	public void onMessage(Session session, byte[] msg) throws Exception
	{
		SessItem si = getSessItem(session) ;
		if(si==null)
			return ;
		sendBinOut(si.getNode(),ByteBuffer.wrap(msg));
	}

//	private boolean checkEventRight(Session session,String path)
//	{
//		PlugAuth pa = PlugManager.getInstance().getPlugAuth() ;
//		if(pa==null)
//			return true ;
//		
//		PlugAuthUser pau = (PlugAuthUser)session.getUserProperties().get(PAU) ;
//		if(pau==null)
//			return false;
//		
//		try
//		{
//			
//			if(!pa.checkWriteRight(path, pau.getRegName()))
//			{//no right
//				return false;
//			}
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
//		
//	}
	
	@OnMessage
	public void onMessageTxt(Session session, String msg) throws Exception
	{
		try
		{
			SessItem si = getSessItem(session) ;
			if(si==null)
				return ;
			sendTxtOut(si.getNode(),msg);
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
		// getAgentServer().onSessionErr(connid, t);
		removeSessItem(session);
	}
}
