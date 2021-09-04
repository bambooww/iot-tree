package org.iottree.core.service;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

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

	String authUrl = null ;
	
	/**
	 * 
	 * @param auth_url 内部微服务提供的接入验证url接口
	 */
	public ActiveMQAuthPlugin(String auth_url)
	{
		authUrl = auth_url ;
		
	}
	
	public Broker installPlugin(Broker broker) throws Exception {
		System.out.println("AuthPlugin >>>installPlugin with url= "+authUrl) ;
		return new ActiveMQAuthBroker(broker,authUrl);
	}

}
