package org.iottree.core.service;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

/**
 * 基于automation用来支持activemq接入用户权限验证的Broker
 * 
 * @author zzj
 *
 */
public class ActiveMQAuthBroker extends AbstractAuthenticationBroker
{
	static ILogger log = LoggerManager.getLogger(ServiceActiveMQ.class);

	// String jsonUrl = null;
	HashMap<String, String> user2Psw = null;

	public ActiveMQAuthBroker(Broker next, HashMap<String, String> user2psw)
	{
		super(next);
		// jsonUrl = json_url;
		user2Psw = user2psw;
	}

	/**
	 * called before connection
	 *
	 * @param context
	 * @param info
	 * @throws Exception
	 */
	@Override
	public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception
	{
		SecurityContext securityContext = context.getSecurityContext();
		if (securityContext == null)
		{
			securityContext = authenticate(info.getUserName(), info.getPassword(), null);
			context.setSecurityContext(securityContext);
			securityContexts.add(securityContext);
		}

		try
		{
			super.addConnection(context, info);
		}
		catch ( Exception e)
		{
			securityContexts.remove(securityContext);
			context.setSecurityContext(null);
			throw e;
		}
	}

	@Override
	public void send(ProducerBrokerExchange producerExchange, Message messageSend) throws Exception
	{
		ConnectionContext cc = producerExchange.getConnectionContext();
		SecurityContext sc = cc.getSecurityContext();
		String username = sc.getUserName();
		ActiveMQDestination dest = messageSend.getDestination();
		String topicn = null;
		if (dest instanceof ActiveMQTopic)
		{
			ActiveMQTopic topic = (ActiveMQTopic) dest;
			topicn = topic.getTopicName();
		}

		if (log.isDebugEnabled())
			log.debug("AuthBroker [" + username + "] send msg (" + topicn + ")->" + dest + " "
					+ dest.getClass().getCanonicalName() + "\r\n" + messageSend.getClass().getCanonicalName() + "\r\n"
					+ messageSend);
		super.send(producerExchange, messageSend);
	}

	@Override
	public Subscription addConsumer(ConnectionContext context, ConsumerInfo info) throws Exception
	{
		SecurityContext sc = context.getSecurityContext();
		String username = sc.getUserName();
		ActiveMQDestination dest = info.getDestination();

		String topicn = null;
		if (dest instanceof ActiveMQTopic)
		{
			ActiveMQTopic topic = (ActiveMQTopic) dest;
			topicn = topic.getTopicName();
		}

		System.out.println("AuthBroker [" + username + "] addConsumer	 (" + topicn + ")->" + dest + " "
				+ dest.getClass().getCanonicalName() + "\r\n");

		return super.addConsumer(context, info);
	}

	//
	@Override
	public void addProducer(ConnectionContext context, ProducerInfo info) throws Exception
	{
		SecurityContext sc = context.getSecurityContext();
		String username = sc.getUserName();
		ActiveMQDestination dest = info.getDestination();

		String topicn = null;
		if (dest instanceof ActiveMQTopic)
		{
			ActiveMQTopic topic = (ActiveMQTopic) dest;
			topicn = topic.getTopicName();
		}

		System.out.println("AuthBroker [" + username + "] addProducer	 (" + topicn + ")->" + dest + " " + "\r\n");

		super.addProducer(context, info);
	}

	public SecurityContext authenticate(String username, String password, X509Certificate[] peerCertificates)
			throws SecurityException
	{
		SecurityContext securityContext = null;
		// User user = getUser(username);
		if (user2Psw != null && user2Psw.size() > 0)
		{
			String psw = user2Psw.get(username);
			if (Convert.isNullOrEmpty(psw)||!psw.equals(password))
			{
				if(log.isDebugEnabled())
					log.debug("user ["+username+"] auth failed") ;
				throw new SecurityException("AuthPlugin authenticate failed");
			}
			
		}

		if (log.isDebugEnabled())
			log.debug("AuthBroker authenticate user =" + username);

		// if (user != null && user.getPass_word().equals(password))
		securityContext = new SecurityContext(username) {
			@Override
			public Set<Principal> getPrincipals()
			{
				Set<Principal> groups = new HashSet<Principal>();
				groups.add(new GroupPrincipal("users"));// default add users
														// group
				return groups;
			}
		};

		return securityContext;
	}

}
