package org.iottree.web.oper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.EndpointConfig;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.iottree.core.UAHmi;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAUtil;
import org.iottree.core.UAVal;
import org.iottree.core.bind.BindDI;
import org.iottree.core.bind.EventBindItem;
import org.iottree.core.bind.PropBindItem;
import org.iottree.core.plugin.PlugAuth;
import org.iottree.core.plugin.PlugAuthUser;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;
import org.iottree.core.ws.WSServer;
import org.iottree.core.ws.WebSocketConfig;
import org.json.JSONObject;

@ServerEndpoint(value = "/_ws/hmi/{prjname}/{hmiid}", configurator = WebSocketConfig.class)
public class WSHmiRT extends WSServer
{
	
	private static final String PAU = "_pau_" ;
	static
	{

		//System.out.println(" hml ws class is loading................>>>>>>>>>>>>>>>>>>>>>>") ;
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
		
		PlugAuth pa = PlugManager.getInstance().getPlugAuth() ;
		if(pa!=null)
		{
			try
			{
				
				if(!pa.checkReadRight(nodecxt.getNodePath(), config))
				{//no right
					session.close();
					return;
				}
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				//PrintWriter w = resp.getWriter();
				e.printStackTrace();
				//w.write(e.getMessage());
				return ;
			}
			
			PlugAuthUser pau = pa.checkUserByWebSocket(config) ;
			if(pau!=null)
				session.getUserProperties().put(PAU, pau) ;
		}

		SessionItem si = new SessionItem(session, rep, nodecxt, hmi);
		addSessionItem(si);

		startTimer();
	}

	// 关闭连接时调用
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

	private boolean checkEventRight(Session session,String path)
	{
		PlugAuth pa = PlugManager.getInstance().getPlugAuth() ;
		if(pa==null)
			return true ;
		
		PlugAuthUser pau = (PlugAuthUser)session.getUserProperties().get(PAU) ;
		if(pau==null)
			return false;
		
		try
		{
			
			if(!pa.checkWriteRight(path, pau.getRegName()))
			{//no right
				return false;
			}
			return true;
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			//PrintWriter w = resp.getWriter();
			e.printStackTrace();
			//w.write(e.getMessage());
			return false;
		}
		
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
				// {tp:"event",cxtpath:cxtpath,hmipath:hmipath,diid:diid,name:eventn,val:eventv}
				// ;
				String cxtpath = job.getString("cxtpath");
				if(!checkEventRight(session,cxtpath))
				{
					return ;
				}
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
				onHmiEvent(cxtpath, hmipath, diid, eventn, strval);
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
		// getAgentServer().onSessionErr(connid, t);
		removeSessionItem(session);
		if(getSessionNum()<=0)
			stopTimer(false) ;
	}

	private void onHmiEvent(String cxtpath, String hmipath, String diid, String eventn, String val)
	{
		UANode cxtn = UAUtil.findNodeByPath(cxtpath);// .getPrjById(repid) ;
		if (cxtn == null)
			return;
		UAHmi hmi = UAUtil.findHmiByPath(hmipath);
		if (hmi == null)
			return;
		BindDI bdi = hmi.getBind(diid);
		if (bdi == null)
			return;
		EventBindItem ebi = bdi.getEventBindItem(eventn);
		ebi.RT_runEventJS(hmi.getBelongTo(), val);
	}
}
