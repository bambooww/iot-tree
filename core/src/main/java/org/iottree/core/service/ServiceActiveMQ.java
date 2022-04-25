package org.iottree.core.service;

import java.util.HashMap;
import java.util.Map;

import javax.management.ObjectName;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

public class ServiceActiveMQ extends AbstractService
{
	static ILogger log = LoggerManager.getLogger(ServiceActiveMQ.class) ;
	
	public static final String NAME = "active_mq";
	BrokerService broker = null;
	
	boolean mqttEn = false;
	int mqttPort = 1883;

	boolean tcpEn = false;
	int tcpPort = 60001;
	
	String authUser = null ;
	String authPsw = null ;
	
	String authUsers = null ;

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public String getTitle()
	{
		return "Apache Active MQ";
	}
	
	public String getBrief()
	{
		String ret = "" ;
		if(mqttEn)
			ret += "mqtt port="+this.mqttPort+"<br>" ;
		if(tcpEn)
			ret += "tcp port="+this.tcpPort+"<br>" ;
		return ret ;
	}

	@Override
	protected void initService(HashMap<String, String> pms) throws Exception
	{
		super.initService(pms);
		
		System.setProperty("org.apache.activemq.default.directory.prefix", Config.getDataDirBase()+"/active_mq/");
		System.setProperty("activemq.conf",Config.getDataDirBase()+"/active_mq/conf/");
		System.setProperty("activemq.data",Config.getDataDirBase()+"/active_mq/data/");
		
		mqttEn = "true".equals(pms.get("mqtt_en")) ;
		mqttPort = Convert.parseToInt32(pms.get("mqtt_port"), 1883) ;
		
		tcpEn = "true".equals(pms.get("tcp_en")) ;
		tcpPort = Convert.parseToInt32(pms.get("tcp_port"), 60001) ;
		
		authUser = pms.get("auth_user") ;
		authPsw = pms.get("auth_psw") ;
		
		authUsers = pms.get("auth_users") ;
	}
	
	public boolean isMqttEn()
	{
		return mqttEn ;
	}
	
	public String getMqttPortStr()
	{
		return ""+mqttPort ;
	}
	
	public boolean isTcpEn()
	{
		return tcpEn ;
	}
	
	public String getTcpPortStr()
	{
		return ""+tcpPort ;
	}
	
	public String getAuthUser()
	{
		return authUser ;
	}
	
	public String getAuthPsw()
	{
		return authPsw ;
	}

	public String getAuthUsers()
	{
		return authUsers;
	}
	
	@Override
	synchronized public boolean startService()
	{
		if(broker!=null)
			return true;
		
		if(!mqttEn && !tcpEn)
			return false;
		try
		{
			//bad code
//			BrokerFactory.resetStartDefault();
//			broker = BrokerFactory.createBroker("xbean:"+Config.getDataDirBase()+"active_mq/conf/activemq.xml");
			
			//good code
			broker = new BrokerService();
			broker.setBrokerName("iottree_activemq");
			if(tcpEn)
				broker.addConnector("tcp://0.0.0.0:"+this.tcpPort+"?maximumConnections=1000&wireFormat.maxFrameSize=104857600");
			
			if(mqttEn)
				broker.addConnector("mqtt://0.0.0.0:"+mqttPort+"?maximumConnections=1000&wireFormat.maxFrameSize=104857600");
			//broker.addConnector("ws://0.0.0.0:61614?maximumConnections=1000&wireFormat.maxFrameSize=104857600");
			//broker.getBrokerDataDirectory()
			//broker.setMessageAuthorizationPolicy(messageAuthorizationPolicy);
			if(Convert.isNullOrEmpty(authUser))
			{
				authUser = "" ;
			}
			if(Convert.isNullOrEmpty(authPsw))
			{
				authPsw = "" ;
			}

			Map<String,String> u2p = Convert.transPropStrToMap(authUsers);
			
			broker.setPlugins(new BrokerPlugin[] {
					new ActiveMQAuthPlugin().asUser(authUser, authPsw).asUsers(u2p)
					});
			//broker.
			ManagementContext cxt = new ManagementContext();
			
			//cxt.registerMBean(new ActiveMQAuthPlugin(""), new ObjectName("auth_plug"));
			broker.setManagementContext(cxt);
			
			broker.start();
			System.out.println(" service ["+this.getTitle()+"] started");
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean stopService()
	{
		if(broker==null)
			return true;
		try
		{
			broker.stop();
			//SpringBrokerContext sbc = (SpringBrokerContext)broker.getBrokerContext();
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			broker = null ;
		}

	}

	@Override
	public boolean isRunning()
	{
		return broker!=null&&broker.isStarted() ;
	}

}
