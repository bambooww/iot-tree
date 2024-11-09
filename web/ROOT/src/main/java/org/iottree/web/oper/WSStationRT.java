package org.iottree.web.oper;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.iottree.core.station.PStation;
import org.iottree.core.station.PlatInsManager;
import org.iottree.core.station.PlatInsWSServer;
import org.iottree.core.ws.WebSocketConfig;
import org.json.JSONObject;

@ServerEndpoint(value = "/_ws/station/{stationid}", configurator = WebSocketConfig.class)
public class WSStationRT extends PlatInsWSServer
{
	//private static final String PAU = "_pau_" ;
	static
	{
//System.out.println("WSStationRT fined..............") ;
	}



}

