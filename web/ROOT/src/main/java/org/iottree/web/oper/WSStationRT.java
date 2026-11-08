package org.iottree.web.oper;

import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

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

