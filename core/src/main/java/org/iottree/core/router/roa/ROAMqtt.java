package org.iottree.core.router.roa;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.iottree.core.ConnPt;
import org.iottree.core.conn.ConnPtMQTT;
import org.iottree.core.conn.mqtt.MqttEndPoint;
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

public class ROAMqtt extends RouterOuterAdp
{
static ILogger log = LoggerManager.getLogger(ROAMqtt.class);
	
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
	
	
	String brokerHost ;

	int brokerPort = 1883;
	
	String user = "" ;
	
	String psw = "" ;

	int connTimeoutSec = 30 ;
	
	int connKeepAliveInterval = -1;
	
	ArrayList<SendConf> sendConfs = new ArrayList<>() ;
	
	ArrayList<RecvConf> recvConfs = new ArrayList<>() ;
	
	private Thread th = null ;
	
	public ROAMqtt(RouterManager rm)
	{
		super(rm);
	}

	@Override
	public String getTp()
	{
		return "mqtt";
	}
	
	@Override
	public RouterOuterAdp newInstance(RouterManager rm)
	{
		return new ROAMqtt(rm);
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
	
	public int getConnTimeSec()
	{
		return this.connTimeoutSec ;
	}
	
	public int getConnKeepAliveIntv()
	{
		return this.connKeepAliveInterval ;
	}

	public ROAMqtt asBroker(String host, int port)
	{
		this.brokerHost = host;
		this.brokerPort = port;
		return this;
	}
	
	public ROAMqtt asBrokerAuth(String user,String psw)
	{
		this.user = user ;
		this.psw = psw ;
		return this ;
	}
	
	public ROAMqtt asTime(int conn_to_sec, int keep_interval_sec)
	{
		connTimeoutSec = conn_to_sec;
		connKeepAliveInterval = keep_interval_sec;
		return this;
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
			this.RT_fireErr("ROAMqtt is not init ok", null);
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
		//ProducerRecord<String, String> pr = new ProducerRecord<>(topic,txt) ;
		this.publish(topic, txt.getBytes("UTF-8"));
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		jo.put("host", this.brokerHost) ;
		jo.put("port", this.brokerPort) ;
		jo.put("user", user) ;
		jo.put("psw", psw) ;
		jo.put("to_sec", this.connTimeoutSec) ;
		jo.put("keep_intv", this.connKeepAliveInterval) ;
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
		this.connTimeoutSec = jo.optInt("to_sec",30) ;
		this.connKeepAliveInterval= jo.optInt("keep_intv",-1) ;
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
	
	private transient MqttEndPoint mqttEP = null;
	
	protected MqttEndPoint getMqttEP()
	{
		if (mqttEP != null)
			return mqttEP;
		mqttEP = new MqttEndPoint("iottree_roa_mqtt_" + this.getId()).withCallback(this.RT_mqttCB);
		return mqttEP;
	}
	
	private boolean bRTInitOk = false;

	protected void RT_init()
	{
		bRTInitOk = false;
		
		if (Convert.isNullOrEmpty(brokerHost) || brokerPort <= 0)
			throw new RuntimeException("no borker host port set");

		try
		{
			MqttEndPoint ep = getMqttEP();
			ep.withMqttServer(this.brokerHost, this.brokerPort, user, psw);
			ep.withTime(connTimeoutSec, connKeepAliveInterval) ;
			
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
				ep.withListenTopic(recv_tps) ;
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

	private void checkConn()
	{
		try
		{
			Thread.sleep(5000);
		}
		catch(Exception ee)
		{}
		
		MqttEndPoint ep = getMqttEP();
		ep.checkConn();
	}
	

	private MqttCallback RT_mqttCB = new MqttCallback() {

		@Override
		public void connectionLost(Throwable cause)
		{
			// MqttConnectionUtils.r();\
			System.out.println(" * conn lost");
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			RT_onRecvedMsg(topic, message.getPayload());
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token)
		{
			MqttMessage mm;
			try
			{
				mm = token.getMessage();
				// System.out.println("mqtt msg deliveryComplete=" +
				// mm.getPayload().length);
			}
			catch ( MqttException e)
			{
				e.printStackTrace();
			}
			// .getPayload().length

		}
	};

	

	protected void RT_onRecvedMsg(String topic, byte[] bs) throws Exception
	{
		String msg = new String(bs,"UTF-8") ;
		
		List<JoinOut> jos = getJoinOutList() ;
		if(jos==null||jos.size()<=0)
			return ;
		for(JoinOut jo:jos)
		{
			RecvConf rc = (RecvConf)jo.getRelatedObj();
			if(MqttEndPoint.checkTopicMatch(rc.topic, topic))
				this.RT_sendToJoinOut(jo, new RouterObj(msg));
		}
	}

	
	@Override
	protected synchronized boolean RT_start_ov()
	{
		if(th!=null)
			return true;
		
		RT_init() ;
		th = new Thread(this::checkConn);
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


	public void publish(String topic, byte[] data) throws MqttPersistenceException, MqttException
	{
		publish(topic, data, 0);
	}

	public void publish(String topic, byte[] data, int qos) throws MqttPersistenceException, MqttException
	{
		getMqttEP().publish(topic, data, qos);
	}

	public void publish(String topic, String txt) throws Exception
	{
		publish(topic, txt.getBytes("utf-8"), 1);
	}
}
