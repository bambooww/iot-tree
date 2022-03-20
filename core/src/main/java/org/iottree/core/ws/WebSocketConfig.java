package org.iottree.core.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.iottree.core.util.Convert;


public class WebSocketConfig extends ServerEndpointConfig.Configurator{
	
	/**
	 * 握手加入客户端验证信息
	 */
	@Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession)request.getHttpSession();
        if (Objects.isNull(httpSession)){
            return ;
        }
        Map<String,List<String>> reqhead = request.getHeaders() ;
        //log.debug("webSocket握手, sessionId = [{}]",reqhead);
        if(reqhead==null)
        {
        	reqhead = new HashMap<>() ;
        }
        config.getUserProperties().put("req_head", reqhead);
        config.getUserProperties().put(HttpSession.class.getName(),httpSession);
    }
	
	
	public static HttpSession getHttpSession(EndpointConfig config)
	{
		return (HttpSession)config.getUserProperties().get(HttpSession.class.getName()) ;
	}
	
	public static String getCookieValue(EndpointConfig config,String name)
	{
		Map<String,List<String>> heads = (Map<String,List<String>>)config.getUserProperties().get("req_head");
		if(heads==null)
			return null ;
		List<String> cks = heads.get("cookie") ;
		if(cks==null||cks.size()<=0)
			return null ;
		
		String ckstr = cks.get(0) ;
		for(String tmps : Convert.splitStrWith(ckstr, ";"))
		{
			tmps = tmps.trim() ;
			int k = tmps.indexOf('=') ;
			String n = tmps ;
			String v="" ;
			if(k>=0)
			{
				n = tmps.substring(0,k) ;
				v = tmps.substring(k+1) ;
			}
			
			if(n.equals(name))
				return v ;
		}
		return null ;
	}
//    /**
//     * ServerEndpointExporter 作用
//     *
//     * 这个Bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
//     *
//     * @return
//     */
//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }
}
