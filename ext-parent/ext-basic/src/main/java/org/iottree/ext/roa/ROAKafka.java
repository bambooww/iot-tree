package org.iottree.ext.roa;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.iottree.core.router.JoinIn;
import org.iottree.core.router.JoinOut;
import org.iottree.core.router.RouterManager;
import org.iottree.core.router.RouterObj;
import org.iottree.core.router.RouterOuterAdp;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class ROAKafka extends RouterOuterAdp
{
	static ILogger log = LoggerManager.getLogger(ROAKafka.class);
	
	public static class SendConf
	{
		String id  ;
		
		String name ;
		
		String topic ;
		
		String title ;
		
		String desc ;
		
		
		
//		public SendConf(String topic,String title,String desc)
//		{
//			
//		}
		
		public String getShowTitle()
		{
			if(Convert.isNotNullEmpty(title))
				return title ;
			return name ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("id", id) ;
			jo.put("n", this.name) ;
			jo.put("topic", this.topic) ;
			jo.putOpt("t", this.title) ;
			jo.putOpt("d", this.desc) ;
			return jo ;
		}
		
		public boolean fromJO(JSONObject jo,StringBuilder failedr)
		{
			this.id = jo.getString("id") ;
			this.name = jo.getString("n") ;
			this.topic = jo.getString("topic") ;
			this.title = jo.optString("t","") ;
			this.desc = jo.optString("d","") ;
			return true ;
		}
	}
	
	public static class RecvConf
	{
		String id  ;
		
		String name ;
		
		String topic ;
		
		String title ;
		
		String desc ;
		
		public String getShowTitle()
		{
			if(Convert.isNotNullEmpty(title))
				return title ;
			return name ;
		}
		
//		public SendConf(String topic,String title,String desc)
//		{
//			
//		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("id", id) ;
			jo.putOpt("n", this.name) ;
			jo.put("topic", this.topic) ;
			jo.putOpt("t", this.title) ;
			jo.putOpt("d", this.desc) ;
			return jo ;
		}
		
		public boolean fromJO(JSONObject jo,StringBuilder failedr)
		{
			this.id = jo.getString("id") ;
			this.name = jo.optString("n") ;
			this.topic = jo.getString("topic") ;
			this.title = jo.optString("t","") ;
			this.desc = jo.optString("d","") ;
			return true ;
		}
	}
	
	
	private KafkaProducer<String, String> producer;

	private KafkaConsumer<String, String> consumer;

	String brokerHost ;

	int brokerPort = 9092;
	
	String user = "" ;
	
	String psw = "" ;
	
	int producerAck = 1 ;
	
	int producerRetries = 3 ;
	
	ArrayList<SendConf> sendConfs = new ArrayList<>() ;
	
	ArrayList<RecvConf> recvConfs = new ArrayList<>() ;
	
	private Thread th = null ;
	
	public ROAKafka(RouterManager rm)
	{
		super(rm);
	}

	@Override
	public String getTp()
	{
		return "kafka";
	}
	

	
	@Override
	public RouterOuterAdp newInstance(RouterManager rm)
	{
		return new ROAKafka(rm);
	}
	
	public String getBrokerHost()
	{
		if(this.brokerHost==null)
			return "" ;
		
		return this.brokerHost ;
	}
	
	public int getBrokerPort()
	{
		return this.brokerPort ;
	}
	
	public String getUser()
	{
		if(this.user==null)
			return "" ;
		return user ;
	}
	
	public String getPsw()
	{
		if(this.psw==null)
			return "" ;
		return this.psw ;
	}
	
	

	public ROAKafka asBroker(String host, int port)
	{
		this.brokerHost = host;
		this.brokerPort = port;
		return this;
	}
	
	public ROAKafka asBrokerAuth(String user,String psw)
	{
		this.user = user ;
		this.psw = psw ;
		return this ;
	}
	
	public ROAKafka asProducerPM(int ack,int retries)
	{
		this.producerAck = ack ;
		this.producerRetries = retries ;
		return this ;
	}
	
	public List<SendConf> getSendConfs()
	{
		return this.sendConfs ;
	}
	
	public SendConf getSendConfByName(String name)
	{
		if(this.sendConfs==null)
			return null ;
		for(SendConf sc:this.sendConfs)
		{
			if(sc.name.equals(name))
				return sc ;
		}
		return null ;
	}
	
	private ArrayList<JoinIn> leftJoinIns = null ;
	private ArrayList<JoinOut> leftJoinOuts = null ;
	
	@Override
	public List<JoinIn> getJoinInList()
	{
		if(leftJoinIns!=null)
			return leftJoinIns ;
		
		synchronized(this)
		{
			ArrayList<JoinIn> jis = new ArrayList<>() ;
			if(sendConfs!=null)
			{
				for(SendConf sc:sendConfs)
				{
					JoinIn ji = new JoinIn(this,sc.name) ;
					ji.setTitleDesc(sc.getShowTitle(),"");
					ji.setRelatedObj(sc);
					jis.add(ji) ;
				}
			}
			this.leftJoinIns = jis ;
		}
		return leftJoinIns ;
	}
	
	@Override
	public List<JoinOut> getJoinOutList()
	{
		if(leftJoinOuts!=null)
			return leftJoinOuts ;
		
		synchronized(this)
		{
			ArrayList<JoinOut> jis = new ArrayList<>() ;
			if(recvConfs!=null)
			{
				for(RecvConf sc:recvConfs)
				{
					JoinOut jo = new JoinOut(this,sc.name) ;
					jo.setTitleDesc(sc.getShowTitle(),"");
					jo.setRelatedObj(sc);
					jis.add(jo) ;
				}
			}
			this.leftJoinOuts = jis ;
		}
		return leftJoinOuts ;
	}
	
	@Override
	protected void RT_onRecvedFromJoinIn(JoinIn ji,RouterObj recved_data) throws Exception
	{
		if(!bRTInitOk)
		{
			this.RT_fireErr("ROAKafka is not init ok", null);
			return ;
		}
		
		String jin_n = ji.getName();
		SendConf sc = getSendConfByName(jin_n) ;
		if(sc==null)
			return ;
		String topic = sc.topic ;
		String txt = recved_data.getTxt();//.toString() ;
		if(txt==null)
			return ;
		ProducerRecord<String, String> pr = new ProducerRecord<>(topic,txt) ;
		send(pr) ;
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		jo.put("host", this.brokerHost) ;
		jo.put("port", this.brokerPort) ;
		jo.put("user", user) ;
		jo.put("psw", psw) ;
		jo.put("ack", this.producerAck) ;
		jo.put("retries", this.producerRetries) ;
		JSONArray jar = new JSONArray() ;
		for(SendConf sc:this.sendConfs)
		{
			jar.put(sc.toJO()) ;
		}
		jo.put("send_confs", jar) ;
		jar = new JSONArray() ;
		for(RecvConf rc:this.recvConfs)
		{
			jar.put(rc.toJO()) ;
		}
		jo.put("recv_confs", jar) ;
		return jo ;
	}
	
	protected boolean fromJO(JSONObject jo,StringBuilder failedr)
	{
		if(!super.fromJO(jo,failedr))
			return false;
		this.brokerHost = jo.optString("host","") ;
		this.brokerPort = jo.optInt("port",9092) ;
		this.user = jo.optString("user","") ;
		this.psw = jo.optString("psw","") ;
		this.producerAck = jo.optInt("ack",1) ;
		this.producerRetries= jo.optInt("retries",3) ;
		JSONArray jarr = jo.optJSONArray("send_confs") ;
		ArrayList<SendConf> scs = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				SendConf sc = new SendConf() ;
				if(sc.fromJO(tmpjo, failedr))
					scs.add(sc) ;
				else
					return false;
			}
			this.sendConfs = scs ;
		}
		jarr = jo.optJSONArray("recv_confs") ;
		ArrayList<RecvConf> rcs = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				RecvConf sc = new RecvConf() ;
				if(sc.fromJO(tmpjo, failedr))
					rcs.add(sc) ;
				else
					return false;
			}
			this.recvConfs = rcs ;
		}
		return true;
	}
	
	private boolean bRTInitOk = false;

	protected void RT_init()
	{
		bRTInitOk = false;
		
		if (Convert.isNullOrEmpty(brokerHost) || brokerPort <= 0)
			throw new RuntimeException("no borker host port set");

		try
		{
			Properties properties = new Properties();
			properties.put("bootstrap.servers", brokerHost + ":" + brokerPort);
			properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			properties.put("acks", "1"); // 0 1 -1
			properties.put("retries", "5"); //
			
			if(Convert.isNotNullEmpty(this.user))
			{
				properties.put("security.protocol", "SASL_PLAINTEXT");
				properties.put("sasl.mechanism", "PLAIN");
				properties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + this.user + "\" password=\"" + this.psw + "\";");
				
				//properties.put("security.protocol", "SASL_PLAINTEXT");
				//properties.put("sasl.mechanism", "SCRAM-SHA-256"); //SCRAM-SHA-512
				//properties.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + this.user + "\" password=\"" + this.psw + "\";");

			}
	
			producer = new KafkaProducer<String, String>(properties);
	
			ArrayList<String> recv_tps = new ArrayList<>() ;
			if(this.recvConfs!=null&&this.recvConfs.size()>0)
			{
				for(RecvConf rc:this.recvConfs)
				{
					String tps = rc.topic ;
					if(Convert.isNullOrEmpty(tps))
						continue ;
					recv_tps.add(tps) ;
				}
			}
			
			if(recv_tps.size()>0)
			{
				properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); 
		        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		
				properties.put("group.id", "experiment");
				
				consumer = new KafkaConsumer<>(properties);
				consumer.subscribe(recv_tps);
			}
			bRTInitOk = true ;
			this.RT_fireErr(null, null);
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			this.RT_fireErr(ee.getMessage(), ee);
		}
	}


	private void consumer()
	{
		if(consumer==null)
			return ;
		try
		{
			while (th!=null)
			{
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for (ConsumerRecord<String, String> record : records)
				{
//					String info = String.format("[Topic: %s][Partition:%d][Offset:%d][Key:%s][Message:%s]",
//							record.topic(), record.partition(), record.offset(), record.key(), record.value());
//					log.info("Received:" + info);
//					System.out.println("Received:" + info);
					String topic = record.topic() ;
					String msg = record.value() ;
					
					List<JoinOut> jos = getJoinOutList() ;
					if(jos==null||jos.size()<=0)
						continue ;
					for(JoinOut jo:jos)
					{
						RecvConf rc = (RecvConf)jo.getRelatedObj();
						if(topic.equals(rc.topic))
							this.RT_sendToJoinOut(jo, new RouterObj(msg));
					}
				}
			}
		}
		finally
		{
			th = null ;
			if(producer!=null)
				producer.close();
			if(consumer!=null)
				consumer.close();
		}
	}
	
	@Override
	public synchronized boolean RT_start()
	{
		if(th!=null)
			return true;
		
		RT_init() ;
		th = new Thread(this::consumer);
		th.start();
		return true ;
	}
	
	@Override
	public synchronized void RT_stop()
	{
		Thread tmpth = th ;
		if(tmpth!=null)
			tmpth.interrupt(); 
		th = null ;
	}
	
	@Override
	public boolean RT_isRunning()
	{
		return th!=null ;
	}

	/**
	 * 同步发送消息
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void send(ProducerRecord<String, String> record) throws InterruptedException, ExecutionException, TimeoutException
	{
		if(producer==null)
			return ;
		
//		try
//		{
			producer.send(record).get(200, TimeUnit.MILLISECONDS);
//		}
//		catch ( Exception ex)
//		{
//			log.error(ex.getMessage(), ex);
//		}

	}

	/**
	 * 异步发送消息
	 */
	public void sendAsync(ProducerRecord<String, String> record, Callback callback)
	{
		if(producer==null)
			return ;
//		try
//		{
			producer.send(record, callback);
//		}
//		catch ( Exception ex)
//		{
//			log.error(ex.getMessage(), ex);
//		}
	}

}