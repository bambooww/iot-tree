package org.iottree.core.conn;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.iottree.core.ConnDev;
import org.iottree.core.UATag;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlData;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class ConnPtWSClient extends ConnPtMSG
{
	ILogger log = LoggerManager.getLogger(ConnPtWSClient.class) ;
	
	String url = null;

	@Override
	public String getConnType()
	{
		return "ws_client";
	}

	public String getUrl()
	{
		if (url == null)
			return "";
		return url;
	}

	@Override
	public String getStaticTxt()
	{
		// javax.websocket.Session ss =
		return null;
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		xd.setParamValue("url", this.url);
		// xd.setParamValue("method", this.method);
		// xd.setParamValue("int_ms", intervalMS);
		// if(params!=null&&params.size()>0)
		// {
		// XmlData tmpxd = xd.getOrCreateSubDataSingle("params") ;
		// for(Map.Entry<String, String> n2v:params.entrySet())
		// {
		// tmpxd.setParamValue(n2v.getKey(), n2v.getValue());
		// }
		// }

		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);

		// this.appName = xd.getParamValueStr("opc_app_name",
		// "iottree_opc_client_"+this.getName());
		this.url = xd.getParamValueStr("url", "");
		// this.method = xd.getParamValueStr("method", "");
		// this.intervalMS = xd.getParamValueInt64("int_ms", 5000) ;
		// XmlData tmpxd = xd.getSubDataSingle("params") ;
		// if(tmpxd!=null)
		// {
		// params = tmpxd.toNameStrValMap();
		// }
		return r;
	}

	private String optJSONString(JSONObject jo, String name, String defv)
	{
		String r = jo.optString(name);
		if (r == null)
			return defv;
		return r;
	}

	private long optJSONInt64(JSONObject jo, String name, long defv)
	{
		Object v = jo.opt(name);
		if (v == null)
			return defv;
		return jo.optLong(name);
	}

	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);

		// this.appName =optJSONString(jo,"opc_app_name",getOpcAppNameDef()) ;
		this.url = optJSONString(jo, "url", "");
		// this.method = optJSONString(jo, "method", "GET");
		// this.intervalMS = optJSONInt64(jo,"int_ms", 5000) ;
	}

	static class WebSockClient extends WebSocketClient
	{

		public WebSockClient(URI serveruri)
		{
			super(serveruri);
			//

		}

		@Override
		public void onOpen(ServerHandshake handshakedata)
		{

		}

		@Override
		public void onMessage(String message)
		{

		}

		@Override
		public void onClose(int code, String reason, boolean remote)
		{

		}

		@Override
		public void onError(Exception ex)
		{

		}

	}

	private transient Session session = null ; 
	
	private transient long lastChk = -1 ;
	
	@Override
	public boolean isConnReady()
	{
		return session!=null;
	}

	public String getConnErrInfo()
	{
		return null;
	}
	
	void disconnect() // throws IOException
	{
		Session ss = session;
		if(ss==null)
			return ;
		
		try
		{
			ss.close();
		}
		catch(Exception e) {}
		finally
		{
			session = null ;
		}
	}

	private void connectToWS() throws DeploymentException, IOException
	{
		WebSocketContainer container = null;

		container = ContainerProvider.getWebSocketContainer();

		URI r = URI.create(url);
		Session session = container.connectToServer(WSClient.class, r);
		
		session.getBasicRemote().sendText("xxx");
	}
	
	void checkConn()
	{
		if(session!=null)
			return ;
		
		if(System.currentTimeMillis()-lastChk<5000)
			return ;
		
		try
		{
			connectToWS();
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
				log.debug("connect to websocket",e);
		}
		finally
		{
			lastChk = System.currentTimeMillis() ;
		}
	}
	

	@Override
	public LinkedHashMap<String, ConnDev> getFoundConnDevs()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@ClientEndpoint()
	class WSClient
	{
		@OnOpen
		public void onOpen(Session session)
		{
			System.out.println("on open ") ;
		}

		@OnMessage
		public void onMessage(String message)
		{
			System.out.println("Client onMessage: " + message);
		}

		@OnClose
		public void onClose()
		{
			session = null ;
		}
	}

	@Override
	public boolean sendMsg(String topic, byte[] bs) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void runOnWrite(UATag tag, Object val) throws Exception
	{
		// TODO Auto-generated method stub
		
	}
}


