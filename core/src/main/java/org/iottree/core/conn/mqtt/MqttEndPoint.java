package org.iottree.core.conn.mqtt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONArray;
import org.json.JSONObject;

public class MqttEndPoint
{
	String id = null ;
	
	private String mqttHost = null;
	private int mqttPort = 1883;
	private String mqttUser = null;

	private String mqttPsw = null;

	private int mqttConnTimeoutSec = 30;
	private int mqttConnKeepAliveInterval = 60;
	
	private ArrayList<String> topics = new ArrayList<>();
	
	transient private MqttClient client = null;
	
	transient private MqttCallback callback = null ;
	
	public MqttEndPoint(String id)
	{
		this.id = id;
		
	}
	
	public MqttEndPoint withMqttServer(String host,int port,String user,String psw)
	{
		mqttHost = host;
		mqttPort = port;
		mqttUser = user;

		mqttPsw = psw;

//		mqttConnTimeoutSec = 30;
//		mqttConnKeepAliveInterval = 60;
		return this;
	}
	
	public MqttEndPoint withTime(int conn_to_sec,int keep_interval_sec)
	{
		mqttConnTimeoutSec = conn_to_sec;
		mqttConnKeepAliveInterval = keep_interval_sec;
		return this;
	}
	
	public MqttEndPoint withListenTopic(String topic)
	{
		topics.add(topic);
		return this;
	}
	
	public MqttEndPoint withListenTopic(List<String> topics)
	{
		this.topics.addAll(topics);
		return this;
	}
	
	public MqttEndPoint withCallback(MqttCallback cb)
	{
		this.callback = cb;
		return this;
	}
	
	public boolean isValid()
	{
		if(Convert.isNullOrEmpty(mqttHost))
			return false;
		
		if(mqttPort<=0)
			return false;
		
		if(this.mqttCB==null)
			return false;
		
		return true;
	}
	
	public MqttEndPoint withParamsXml(XmlData xd)
	{
		return withParamsXml(xd,false) ;
	}
	
	public MqttEndPoint withParamsXml(XmlData xd,boolean ignore_topics)
	{
		if(xd==null)
			return this;
		this.mqttHost = xd.getParamValueStr("mqtt_host", "");
		this.mqttPort = xd.getParamValueInt32("mqtt_port", -1);
		this.mqttUser = xd.getParamValueStr("mqtt_user", "");
		this.mqttPsw = xd.getParamValueStr("mqtt_psw", "");
		this.mqttConnTimeoutSec = xd.getParamValueInt32("mqtt_conn_to", -1);
		this.mqttConnKeepAliveInterval = xd.getParamValueInt32("mqtt_conn_int", -1);
		if(!ignore_topics)
		{
			String[] tps = xd.getParamValuesStr("topics") ;
			if(tps!=null)
			{
				for(String tp:tps)
					this.topics.add(tp);
	
			}
		}
		return this;
	}
	

	public void transParamsToXml(XmlData xd,boolean ignore_topics)
	{
		xd.setParamValue("mqtt_host", this.mqttHost);
		xd.setParamValue("mqtt_port", this.mqttPort);
		xd.setParamValue("mqtt_user", this.mqttUser);
		xd.setParamValue("mqtt_psw", this.mqttPsw);
		xd.setParamValue("mqtt_conn_to", this.mqttConnTimeoutSec);
		xd.setParamValue("mqtt_conn_int", this.mqttConnKeepAliveInterval);
		
		if(!ignore_topics&&topics!=null&&topics.size()>0)
		{
			xd.setParamValues("topics", topics);
		}
	}
	

	private String optJSONString(JSONObject jo, String name, String defv)
	{
		String r = jo.optString(name);
		if (r == null)
			return defv;
		return r;
	}

	private int optJSONInt(JSONObject jo, String name, int defv)
	{
		Object v = jo.opt(name);
		if (v == null)
			return defv;
		return jo.optInt(name);
	}
	
	public MqttEndPoint withParamsJSON(JSONObject jo) throws Exception
	{
		return withParamsJSON(jo,false);
	}
	
	public MqttEndPoint withParamsJSON(JSONObject jo,boolean ignore_topics) throws Exception
	{
		this.mqttHost = optJSONString(jo, "mqtt_host", "");
		this.mqttPort = optJSONInt(jo, "mqtt_port", -1);
		this.mqttUser = optJSONString(jo, "mqtt_user", "");
		this.mqttPsw = optJSONString(jo, "mqtt_psw", "");
		this.mqttConnTimeoutSec = optJSONInt(jo, "mqtt_conn_to", -1);
		this.mqttConnKeepAliveInterval = optJSONInt(jo, "mqtt_conn_int", -1);
		
		if(!ignore_topics)
		{
			JSONArray jotps = jo.optJSONArray("topics") ;
			if(jotps!=null)
			{
				
				for(int i = 0 ,n = jotps.length() ; i < n ; i ++)
				{
					String tp = jotps.getString(i) ;
					this.topics.add(tp);
				}
			}
		}
		return this;
	}
	
	

	public List<String> getMQTTTopics()
	{
		return topics;
	}

	public String getMQTTHost()
	{
		if (mqttHost == null)
			return "";
		return mqttHost;
	}

	public int getMQTTPort()
	{
		return this.mqttPort;
	}

	public String getMQTTPortStr()
	{
		if (mqttPort <= 0)
			return "";
		return "" + mqttPort;
	}

	public String getMQTTUser()
	{
		if (mqttUser == null)
			return "";
		return this.mqttUser;
	}

	public String getMQTTPsw()
	{
		if (mqttPsw == null)
			return "";
		return mqttPsw;
	}

	public int getMQTTConnTimeout()
	{
		return mqttConnTimeoutSec;
	}

	public int getMQTTKeepAliveInterval()
	{
		return mqttConnKeepAliveInterval;
	}
	
	public boolean isConnReady()
	{
		if (client == null)
			return false;
		return client.isConnected();
	}
	
	public synchronized void disconnect() // throws IOException
	{
		if (client == null)
			return;

		try
		{
			client.disconnect();
			client.close(true);
		} catch (Exception e)
		{
			//e.printStackTrace();
		} finally
		{
			client = null;
		}
	}
	
	private boolean bLastConnFailed=false;
	private int lastChkCC = 0 ;

	public void checkConn()
	{
		if (client != null)
		{
			if (client.isConnected())
				return;
			
			if(bLastConnFailed)
			{
				lastChkCC ++ ;
				if(lastChkCC<5)
					return ;
			}
			
			lastChkCC = 0 ;
			
			disconnect();
		}
		
		try
		{
			client = new MqttClient("tcp://" + mqttHost + ":" + mqttPort, this.id,
					new MemoryPersistence());

			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(true);
			if (this.mqttUser != null)
				options.setUserName(mqttUser);
			if (this.mqttPsw != null)
				options.setPassword(mqttPsw.toCharArray());
			if (mqttConnTimeoutSec > 0)
				options.setConnectionTimeout(mqttConnTimeoutSec);
			if (mqttConnKeepAliveInterval > 0)
				options.setKeepAliveInterval(mqttConnKeepAliveInterval);

			client.setCallback(mqttCB);
			client.connect(options);
			// topic = client.getTopic(TOPIC);
			// topic125 = client.getTopic(TOPIC125);
			
			for (String topic : this.topics)
			{
				client.subscribe(topic, 0);
				System.out.println(" mqtt ep subscribe topic="+topic) ;
			}
			
			bLastConnFailed = false;
		} catch (Exception e)
		{
			if(!bLastConnFailed)
				e.printStackTrace();
			
			bLastConnFailed = true;
		}
	}
	
	public void publish(String topic, byte[] data) throws MqttPersistenceException, MqttException
	{
		publish(topic, data, 0);
	}

	public void publish(String topic, byte[] data, int qos) throws MqttPersistenceException, MqttException
	{
		MqttMessage message = new MqttMessage();
		//message.setQos(qos);
		message.setQos(0); //1-cannot send ok
		message.setRetained(false);
		if(data!=null)
			message.setPayload(data);
		MqttTopic mt = client.getTopic(topic);
		MqttDeliveryToken token = mt.publish(message);

		// token.waitForCompletion();
		// System.out.println("message is published completely! " +
		// token.isComplete());
	}

	public void publish(String topic, String txt) throws Exception
	{
		publish(topic, txt.getBytes("utf-8"), 1);
	}

	MqttCallback mqttCB = new MqttCallback() {

		@Override
		public void connectionLost(Throwable cause)
		{
			//MqttConnectionUtils.r();\
			//System.out.println(" * conn lost") ;
			if(callback!=null)
				callback.connectionLost(cause);
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			if(callback!=null)
				callback.messageArrived(topic, message);
			//onRecvedMsg(topic,message.getPayload());
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token)
		{
			if(callback!=null)
				callback.deliveryComplete(token);

		}
	};
}
