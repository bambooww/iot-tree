package org.iottree.core.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.iottree.core.util.Convert;

/**
 * automation用来支持activemq接入用户权限验证的
 * 基于rest json接口的active接入插件
 * 
 * 在activemq.xml 中的<broker>节点中的最后添加配置：

		<plugins>
            <bean xmlns="http://www.springframework.org/schema/beans" id="myPlugin" class="com.iwhr.waterss.automation.core.activemq.AuthPlugin">						
                <constructor-arg index="0">   
        			<value>http://192.168.1.12:9992/automation/mqtt/auth?</value>   
    			</constructor-arg>   
            </bean>
        </plugins>
        
 * @author zzj
 *
 */
public class ActiveMQAuthPlugin implements BrokerPlugin {

	//String authUrl = null ;
	
	private HashMap<String,String> user2psw = new HashMap<>() ;
	/**
	 * 
	 * @param auth_url 内部微服务提供的接入验证url接口
	 */
	public ActiveMQAuthPlugin() //String auth_url)
	{
		//authUrl = auth_url ;
		
	}
	
	public Broker installPlugin(Broker broker) throws Exception {
	//	System.out.println("AuthPlugin >>>installPlugin with url= "+authUrl) ;
		return new ActiveMQAuthBroker(broker,this.user2psw);
	}

	public ActiveMQAuthPlugin asUser(String username,String psw)
	{
		if(Convert.isNullOrEmpty(username))
				return this ;
		user2psw.put(username, psw) ;
		return this ;
	}
	
	public ActiveMQAuthPlugin asUsers(Map<String,String> u2p)
	{
		if(user2psw==null)
			return this ;
		user2psw.putAll(u2p);
		return this ;
	}
}
