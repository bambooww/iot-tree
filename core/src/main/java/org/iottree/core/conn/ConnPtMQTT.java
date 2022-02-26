package org.iottree.core.conn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.iottree.core.UATag;
import org.iottree.core.conn.mqtt.MqttEndPoint;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;


public class ConnPtMQTT extends ConnPtMSGTopic
{
//	private String mqttHost = null;
//	private int mqttPort = 1883;
//	private String mqttUser = null;
//
//	private String mqttPsw = null;
//
//	private int mqttConnTimeoutSec = 30;
//	private int mqttConnKeepAliveInterval = 60;
//
//	private ArrayList<String> topics = new ArrayList<>();

	private transient MqttEndPoint mqttEP = null ;
	
	public ConnPtMQTT()
	{
//		this.topics.add("iottree/node");
//		this.topics.add("iottree/syn");
	}

	@Override
	public String getConnType()
	{
		return "mqtt";
	}

	public MqttEndPoint getMqttEP()
	{
		if(mqttEP!=null)
			return mqttEP ;
		mqttEP = new MqttEndPoint("iottree_cpt_" + this.getId())
				.withCallback(this.mqttCB);;
		return mqttEP ;
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		// xd.setParamValue("opc_app_name", this.appName);
		if(mqttEP!=null)
			mqttEP.transParamsToXml(xd,false);
			
//		xd.setParamValue("mqtt_host", this.mqttHost);
//		xd.setParamValue("mqtt_port", this.mqttPort);
//		xd.setParamValue("mqtt_user", this.mqttUser);
//		xd.setParamValue("mqtt_psw", this.mqttPsw);
//		xd.setParamValue("mqtt_conn_to", this.mqttConnTimeoutSec);
//		xd.setParamValue("mqtt_conn_int", this.mqttConnKeepAliveInterval);

		return xd;
	}
	
	

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);

		MqttEndPoint ep = getMqttEP() ;
		ep.withParamsXml(xd) ;
		// this.appName = xd.getParamValueStr("opc_app_name",
		// "iottree_opc_client_"+this.getName());
//		this.mqttHost = xd.getParamValueStr("mqtt_host", "");
//		this.mqttPort = xd.getParamValueInt32("mqtt_port", -1);
//		this.mqttUser = xd.getParamValueStr("mqtt_user", "");
//		this.mqttPsw = xd.getParamValueStr("mqtt_psw", "");
//		this.mqttConnTimeoutSec = xd.getParamValueInt32("mqtt_conn_to", -1);
//		this.mqttConnKeepAliveInterval = xd.getParamValueInt32("mqtt_conn_int", -1);
		return r;
	}


	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);

		MqttEndPoint ep = getMqttEP() ;
		ep.withParamsJSON(jo) ;
		
//		this.mqttHost = optJSONString(jo, "mqtt_host", "");
//		this.mqttPort = optJSONInt(jo, "mqtt_port", -1);
//		this.mqttUser = optJSONString(jo, "mqtt_user", "");
//		this.mqttPsw = optJSONString(jo, "mqtt_psw", "");
//		this.mqttConnTimeoutSec = optJSONInt(jo, "mqtt_conn_to", -1);
//		this.mqttConnKeepAliveInterval = optJSONInt(jo, "mqtt_conn_int", -1);

	}

	@Override
	public String getStaticTxt()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnReady()
	{
		if (mqttEP == null)
			return false;
		return mqttEP.isConnReady();
	}
	
	public String getConnErrInfo()
	{
		if(mqttEP==null)
			return "no connection" ;
		return mqttEP.getConnErrInfo() ;
			
	}
	
	public List<String> getMsgTopics()
	{
		return getMqttEP().getMQTTTopics() ;
	}

	
	public boolean sendMsg(String topic,byte[] bs) throws Exception
	{
		this.publish(topic, bs, 0);
		return true;
	}

	@Override
	protected void onRecvedMsg(String topic,byte[] bs) throws Exception
	{

			System.out.println("mqtt onRecvedMsg=" + topic + " " + new String(bs,"utf-8"));

		
	}
	
	public void runOnWrite(UATag tag,Object val) throws Exception
	{
		throw new Exception("no impl") ;
		//it may send some msg
	}

	synchronized void disconnect() // throws IOException
	{
		getMqttEP().disconnect();
	}
	
	void checkConn()
	{
		MqttEndPoint ep = getMqttEP() ;
		ep.checkConn();
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
	
	public void RT_writeValByBind(String tagpath,String strv)
	{
		//TODO 
	}

	MqttCallback mqttCB = new MqttCallback() {

		@Override
		public void connectionLost(Throwable cause)
		{
			//MqttConnectionUtils.r();\
			System.out.println(" * conn lost") ;
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			
			onRecvedMsg(topic,message.getPayload());
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token)
		{
			MqttMessage mm;
			try
			{
				mm = token.getMessage();
				System.out.println("mqtt msg deliveryComplete=" + mm.getPayload().length);
			} catch (MqttException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// .getPayload().length

		}
	};

}
