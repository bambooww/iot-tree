package org.iottree.ext.msg_net;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.router.JoinOut;
import org.iottree.core.router.RouterObj;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.ext.roa.ROAKafka.RecvConf;
import org.json.JSONObject;

public class Kafka_M extends MNModule implements IMNRunner
{
	static ILogger log = LoggerManager.getLogger(Kafka_M.class) ;
	
//	static ArrayList<MNNode> supNodes = new ArrayList<>() ;
//	static
//	{
//		supNodes.add(new KafkaIn_NS()) ;
//		supNodes.add(new KafkaOut_NE()) ;
//	}
	

	public static enum SecurityProto {
	    /** Un-authenticated, non-encrypted channel */
	    PLAINTEXT(0, "PLAINTEXT"),
	    /** SSL channel */
//	    SSL(1, "SSL"),
	    /** SASL authenticated, non-encrypted channel */
	    SASL_PLAINTEXT(2, "SASL_PLAINTEXT");
	    /** SASL authenticated, SSL channel */
//	    SASL_SSL(3, "SASL_SSL");

	    private static final Map<Short, SecurityProto> CODE_TO_SECURITY_PROTOCOL;
	    private static final List<String> NAMES;

	    static {
	        SecurityProto[] protocols = SecurityProto.values();
	        List<String> names = new ArrayList<>(protocols.length);
	        Map<Short, SecurityProto> codeToSecurityProtocol = new HashMap<>(protocols.length);
	        for (SecurityProto proto : protocols) {
	            codeToSecurityProtocol.put(proto.id, proto);
	            names.add(proto.name);
	        }
	        CODE_TO_SECURITY_PROTOCOL = Collections.unmodifiableMap(codeToSecurityProtocol);
	        NAMES = Collections.unmodifiableList(names);
	    }

	    /** The permanent and immutable id of a security protocol -- this can't change, and must match kafka.cluster.SecurityProtocol  */
	    public final short id;

	    /** Name of the security protocol. This may be used by client configuration. */
	    public final String name;

	    SecurityProto(int id, String name) {
	        this.id = (short) id;
	        this.name = name;
	    }

	    public static List<String> names() {
	        return NAMES;
	    }

	    public static SecurityProto forId(short id) {
	        return CODE_TO_SECURITY_PROTOCOL.get(id);
	    }

	    /** Case insensitive lookup by protocol name */
	    public static SecurityProto forName(String name) {
	        return SecurityProto.valueOf(name.toUpperCase(Locale.ROOT));
	    }

	}
	
	public static enum SaslMech
	{
		PLAIN(0, "PLAIN"),
		SCRAM_SHA_256(1, "SCRAM-SHA-256"),
		SCRAM_SHA_512(2, "SCRAM-SHA-512");

		
	    public final int id;

	    
	    public final String name;

	    SaslMech(int id, String name) {
	        this.id = (short) id;
	        this.name = name;
	    }

//	    public static List<String> names() {
//	        return NAMES;
//	    }

	    public static SaslMech fromId(int id)
	    {
	    	switch(id)
	    	{
	    	case 0:
	    		return PLAIN ;
	    	case 1:
	    		return SCRAM_SHA_256;
	    	case 2:
	    		return SCRAM_SHA_512;
	    	default:
	    		return PLAIN; 
	    	}
	    }
	}
	
	String brokerHost ;

	int brokerPort = 9092;
	
	/**
	 * producer send time out
	 */
	long sendTo = 1000 ;
	
	String user = "" ;
	
	String psw = "" ;
	
	int producerAck = 1 ;
	
	int producerRetries = 3 ;
	
	SecurityProto securityProto = SecurityProto.PLAINTEXT ;
	
	SaslMech saslMech  = SaslMech.PLAIN;
	
	@Override
	public String getTP()
	{
		return "kafka";
	}

	@Override
	public String getTPTitle()
	{
		return "Kafka";
	}
	
	@Override
	public String getColor()
	{
		return "#debed7";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0ec";
	}
	
//	protected List<MNNode> getSupportedNodes() 
//	{
//		return supNodes;
//	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.brokerHost))
		{
			failedr.append("no host set") ;
			return false;
		}
		if(this.brokerPort<=0)
		{
			failedr.append("invalid port") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("host", brokerHost) ;
		jo.putOpt("port", brokerPort>0?brokerPort:9092) ;
		jo.putOpt("sec_proto",securityProto.id) ;
		jo.putOpt("sec_sasl_mech", saslMech.id) ;
		jo.putOpt("send_to",sendTo) ;
		jo.putOpt("user", user ) ;
		jo.putOpt("psw", psw ) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		brokerHost = jo.optString("host", "") ;
		brokerPort = jo.optInt("port", 9092) ;
		securityProto =SecurityProto.forId((short)jo.optInt("sec_proto",0)) ;
		saslMech = SaslMech.fromId((short) jo.optInt("sec_sasl_mech",0)) ;
		sendTo = jo.optLong("send_to",1000) ;
		user = jo.optString("user", "") ;
		psw = jo.optString("psw", "") ;
	}
	
	//
	
	Thread RT_th = null ;
	
	boolean RT_bRun = false;

	private KafkaProducer<String, String> producer = null;

	private KafkaConsumer<String, String> consumer =null;

	private transient HashMap<String,KafkaIn_NS> topic2in = null;//new HashMap<>() ;

	private boolean RT_init(List<String> recv_topics,StringBuilder failedr)
	{
		if (Convert.isNullOrEmpty(brokerHost) || brokerPort <= 0)
		{
			failedr.append("no borker host port set");
			return false;
		}

		try
		{
			Properties properties = new Properties();
			properties.put("bootstrap.servers", brokerHost + ":" + brokerPort);
			properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			properties.put("acks", "1"); // 0 1 -1
			properties.put("retries", "5"); //
			
			switch(securityProto)
			{
			case PLAINTEXT:
				break ; //Un-authenticated, non-encrypted channel
			case SASL_PLAINTEXT:
				properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
				//properties.put("security.protocol", "SASL_PLAINTEXT");
				
				properties.put("sasl.mechanism", this.saslMech.name) ;//"PLAIN"); SCRAM-SHA-256");SCRAM-SHA-512
				switch(this.saslMech)
				{
				case PLAIN:
					properties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + this.user + "\" password=\"" + this.psw + "\";");
					break ;
				case SCRAM_SHA_256:
				case SCRAM_SHA_512:
					properties.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + this.user + "\" password=\"" + this.psw + "\";");
					break ;
				}
				break ;
			}
			
//			if(Convert.isNotNullEmpty(this.user))
//			{
//				properties.put("security.protocol", "SASL_PLAINTEXT");
//				properties.put("sasl.mechanism", "SCRAM-SHA-256"); //SCRAM-SHA-512
//				properties.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + this.user + "\" password=\"" + this.psw + "\";");
//			}
	
			producer = new KafkaProducer<String, String>(properties);
	
			
			if(recv_topics.size()>0)
			{
				properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); 
		        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		
				properties.put("group.id", "experiment");
				
				consumer = new KafkaConsumer<>(properties);
				consumer.subscribe(recv_topics);
			}

		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			//this.RT_fireErr(ee.getMessage(), ee);
			failedr.append(ee.getMessage()) ;
			return false;
		}
		return true;
	}

	@Override
	public synchronized boolean RT_start(StringBuilder failedr)
	{
		if(RT_bRun)
			return true;
		
		List<MNNode> rns = this.getRelatedNodes() ;
		if(rns==null||rns.size()<=0)
		{
			failedr.append("no related nodes found") ;
			return false;
		}
		
		HashMap<String,KafkaIn_NS> topic2in = new HashMap<>() ;
		ArrayList<String> recvtopics = new ArrayList<>() ;
		ArrayList<String> sendtopics = new ArrayList<>() ;
		for(MNNode rn:rns)
		{
			if(rn instanceof KafkaIn_NS)
			{
				KafkaIn_NS kin = (KafkaIn_NS)rn ;
				String topic = kin.getTopic() ;
				if(Convert.isNullOrEmpty(topic))
					continue ;
				topic2in.put(topic,kin) ;
				recvtopics.add(topic) ;
				continue ;
			}
			
			if(rn instanceof KafkaOut_NE)
			{
				KafkaOut_NE kout = (KafkaOut_NE)rn ;
				String topic = kout.getTopic() ;
				if(Convert.isNullOrEmpty(topic))
					continue ;
				sendtopics.add(topic) ;
				continue ;
			}
		}
		
		
		if(topic2in.size()<=0 && sendtopics.size()<=0)
		{
			failedr.append("no related Kafka In or Out topic found") ;
			return false;
		}
		this.topic2in = topic2in ;
		
		if(!RT_init(recvtopics,failedr))
			return false;

		RT_bRun=true ;
		RT_th = new Thread(consumerRunner);
		RT_th.start();
		return true;
		
	}

	@Override
	public synchronized void RT_stop()
	{
//		Thread tmpth = th ;
//		if(tmpth!=null)
//			tmpth.interrupt(); 
//		
//		if(producer!=null)
//		{
//			producer.close();
//			producer = null ;
//		}
//		if(consumer!=null)
//		{
//			consumer.close();
//			consumer = null ;
//		}
		
		RT_bRun = false ;
	}

	@Override
	public boolean RT_isRunning()
	{
		return RT_th!=null;
	}
	
	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		return false;
	}
	
	/**
	 * false will not support runner
	 * @return
	 */
	public boolean RT_runnerEnabled()
	{
		return true ;
	}
	
	/**
	 * true will not support manual trigger to start
	 * @return
	 */
	public boolean RT_runnerStartInner()
	{
		return false;
	}
	
	void RT_send(String topic,String msg)
	{
		if(producer==null)
		{
			this.RT_DEBUG_WARN.fire("send","not send msg : ["+topic+"] size="+msg.length()+",may be Module is not running.");
			return ;
		}
		
		long st = System.currentTimeMillis() ;
		try
		{
			this.RT_DEBUG_INF.fire("send","before send msg : ["+topic+"] size="+msg.length(),msg);
			ProducerRecord<String, String> record  = new ProducerRecord<>(topic, msg) ;
			producer.send(record);//.get(sendTo, TimeUnit.MILLISECONDS);
			this.RT_DEBUG_INF.fire("send","send msg : ["+topic+"] size="+msg.length()+" cost="+(System.currentTimeMillis()-st)+"MS",msg);
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			this.RT_DEBUG_ERR.fire("send","send msg : ["+topic+"] size="+msg.length(), ee);
		}
	}
	
	private Runnable consumerRunner = new Runnable()
	{
		public void run()
		{
			consumerRun() ;
		}
	};
	
	private void consumerRun()
	{
		try
		{
			if(consumer==null)
				return ;
			
			while (RT_bRun)
			{
				try
				{
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
					for (ConsumerRecord<String, String> record : records)
					{
						String topic = record.topic() ;
						String msg = record.value() ;
						KafkaIn_NS kin = this.topic2in.get(topic) ;
						if(kin!=null)
							kin.RT_onTopicMsgRecv(topic, msg);
					}
				}
				catch(Throwable ee)
				{
					if(log.isDebugEnabled())
						log.debug("consumer error",ee);
					
					try
					{
					Thread.sleep(100);
					}
					catch(Exception se) {}
				}
			}//end of while
		}
		finally
		{
			if(producer!=null)
			{
				try
				{
					producer.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					producer = null ;
				}
			}
			if(consumer!=null)
			{
				try
				{
					consumer.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					consumer = null ;
				}
			}
			RT_bRun=false;
			RT_th = null ;
		}
	}
}
