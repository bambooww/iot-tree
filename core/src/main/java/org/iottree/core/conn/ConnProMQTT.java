package org.iottree.core.conn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.iottree.core.conn.mqtt.MqttEndPoint;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

public class ConnProMQTT extends ConnProvider
{
	public static final String TP = "mqtt";

	private transient MqttEndPoint mqttEP = null;

	@Override
	public String getProviderType()
	{
		return TP;
	}

	public String getTitle()
	{
		return super.getTitle();// "MQTT Client";
	}

	public boolean isSingleProvider()
	{
		return false;
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtMQTT.class;
	}

	@Override
	protected long connpRunInterval()
	{
		// TODO Auto-generated method stub
		return 1000;
	}

	public MqttEndPoint getMqttEP()
	{
		if (mqttEP != null)
			return mqttEP;
		mqttEP = new MqttEndPoint("iottree_cpt_" + this.getId()).withCallback(this.mqttCB);
		return mqttEP;
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		// xd.setParamValue("opc_app_name", this.appName);
		if (mqttEP != null)
			mqttEP.transParamsToXml(xd, false);

		// xd.setParamValue("mqtt_host", this.mqttHost);
		// xd.setParamValue("mqtt_port", this.mqttPort);
		// xd.setParamValue("mqtt_user", this.mqttUser);
		// xd.setParamValue("mqtt_psw", this.mqttPsw);
		// xd.setParamValue("mqtt_conn_to", this.mqttConnTimeoutSec);
		// xd.setParamValue("mqtt_conn_int", this.mqttConnKeepAliveInterval);

		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);

		MqttEndPoint ep = getMqttEP();
		ep.withParamsXml(xd);
		// this.appName = xd.getParamValueStr("opc_app_name",
		// "iottree_opc_client_"+this.getName());
		// this.mqttHost = xd.getParamValueStr("mqtt_host", "");
		// this.mqttPort = xd.getParamValueInt32("mqtt_port", -1);
		// this.mqttUser = xd.getParamValueStr("mqtt_user", "");
		// this.mqttPsw = xd.getParamValueStr("mqtt_psw", "");
		// this.mqttConnTimeoutSec = xd.getParamValueInt32("mqtt_conn_to", -1);
		// this.mqttConnKeepAliveInterval =
		// xd.getParamValueInt32("mqtt_conn_int", -1);
		return r;
	}

	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);

		MqttEndPoint ep = getMqttEP();
		ep.withParamsJSON(jo, true); // no topics

		// this.mqttHost = optJSONString(jo, "mqtt_host", "");
		// this.mqttPort = optJSONInt(jo, "mqtt_port", -1);
		// this.mqttUser = optJSONString(jo, "mqtt_user", "");
		// this.mqttPsw = optJSONString(jo, "mqtt_psw", "");
		// this.mqttConnTimeoutSec = optJSONInt(jo, "mqtt_conn_to", -1);
		// this.mqttConnKeepAliveInterval = optJSONInt(jo, "mqtt_conn_int", -1);

	}

	protected void onRecvedMsg(String topic, byte[] bs) throws Exception
	{
		for (ConnPt ci : this.listConns())
		{
			try
			{
				ConnPtMQTT conn = (ConnPtMQTT) ci;
				List<String> mtps = conn.getMsgTopics();
				if (mtps == null || mtps.size() <= 0)
					continue;
				boolean bm = false;
				for (String mtp : mtps)
				{
					if (MqttEndPoint.checkTopicMatch(mtp, topic))
					{
						bm = true;
						break;
					}
				}
				if (bm)
					conn.onRecvedMsg(topic, bs);
			}
			catch ( Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	MqttCallback mqttCB = new MqttCallback() {

		@Override
		public void connectionLost(Throwable cause)
		{
			// MqttConnectionUtils.r();\
			System.out.println(" * conn lost");
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			onRecvedMsg(topic, message.getPayload());
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

	@Override
	public void start() throws Exception
	{

		super.start();
	}

	public void stop()
	{
		super.stop();

		disconnAll();
	}

	public void disconnAll() // throws IOException
	{
		this.getMqttEP().disconnect();

	}

	protected void RT_connpInit() throws Exception
	{
		ArrayList<String> topics = new ArrayList<>();
		for (ConnPt ci : this.listConns())
		{
			ConnPtMQTT citc = (ConnPtMQTT) ci;
			List<String> tps = citc.getMsgTopics();
			if (tps != null)
				topics.addAll(tps);
		}
		this.getMqttEP().setListenTopics(topics);
	}

	@Override
	protected void connpRunInLoop() throws Exception
	{
		checkConn();

		for (ConnPt ci : this.listConns())
		{
			ConnPtMQTT citc = (ConnPtMQTT) ci;
			// citc.checkConn() ;
			// citc.
		}
	}

	private void checkConn()
	{
		MqttEndPoint ep = getMqttEP();
		ep.checkConn();
	}

	boolean isMQTTConnected()
	{
		if (mqttEP == null)
			return false;
		return mqttEP.isConnReady();
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
