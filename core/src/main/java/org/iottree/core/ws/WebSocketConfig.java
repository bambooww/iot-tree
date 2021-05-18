package org.iottree.core.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;


public class WebSocketConfig extends ServerEndpointConfig.Configurator{
	
	/**
	 * 握手加入客户端验证信息
	 */
	@Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession)request.getHttpSession();
        if (Objects.isNull(httpSession)){
            //log.error("httpSession为空, header = [{}], 请登录!", request.getHeaders());
            //throw new JMakerException("httpSession为空, 请登录!");
        	
        }
        Map<String,List<String>> reqhead = request.getHeaders() ;
        //log.debug("webSocket握手, sessionId = [{}]",reqhead);
        if(reqhead==null)
        {
        	reqhead = new HashMap<>() ;
        }
        config.getUserProperties().put("req_head", reqhead);
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
