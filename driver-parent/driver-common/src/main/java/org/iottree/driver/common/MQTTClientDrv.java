package org.iottree.driver.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.iottree.core.util.Convert;
import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.UATagList;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.util.CompressUUID;
import org.json.JSONObject;

public class MQTTClientDrv extends DevDriver
{

	String host = null;

	int port = 1883;

	ArrayList<String> topics = null;


	private MqttClient client = null;
	private MqttConnectOptions options = null;

	
	@Override
	public DevDriver copyMe()
	{
		return new MQTTClientDrv();
	}

	@Override
	public String getName()
	{
		return "mqtt_client";
	}

	@Override
	public String getTitle()
	{
		return "MQTT Client";
	}

	@Override
	public List<PropGroup> getPropGroupsForCh()
	{
		PropGroup r = new PropGroup("mqtt_conn", "MQTT Connection");

		r.addPropItem(new PropItem("server_host", "Server Host", "MQTT Server IP or Address", PValTP.vt_str, false,
				null, null, "localhost"));
		r.addPropItem(
				new PropItem("server_port", "Server Port", "MQTT Server Port", PValTP.vt_int, false, null, null, 1883));

		r.addPropItem(new PropItem("clientid", "Client Id", "Client Id", PValTP.vt_str, false, null, null,
				CompressUUID.createNewId()));

		r.addPropItem(new PropItem("username", "User Name", "User Name", PValTP.vt_str, false, null, null, ""));
		r.addPropItem(new PropItem("userpsw", "User Password", "User Password", PValTP.vt_str, false, null, null, ""));

		r.addPropItem(new PropItem("conn_to_sec", "Connection Timeout Seconds", "Connection Timeout Seconds",
				PValTP.vt_int, false, null, null, 10));
		r.addPropItem(new PropItem("ka_int", "KeepAliveInterval Seconds", "Connection Timeout Seconds", PValTP.vt_int,
				false, null, null, 10));

		ArrayList<PropGroup> pgs = new ArrayList<>();
		pgs.add(r);
		return pgs;
	}

	public List<PropGroup> getPropGroupsForDev()
	{
		PropGroup gp = new PropGroup("mqtt_tag", "");

		gp.addPropItem(
				new PropItem("topic", "Subscribe Topic", "Subscribe Topic", PValTP.vt_str, false, null, null, ""));

		List<PropGroup> pgs = new ArrayList<PropGroup>();
		pgs.add(gp);
		return pgs;
	}

	@Override
	public DevAddr getSupportAddr()
	{

		return new MQTTClientAddr();
	}

	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return true;
	}

	protected boolean RT_initDriver(StringBuilder failedr) throws Exception
	{
		UACh ch = this.getBelongToCh() ;
		host = ch.getOrDefaultPropValueStr("mqtt_conn", "server_host", null);
		if (Convert.isNullOrEmpty(host))
		{
			failedr.append("no server host set");
			return false;
		}
		topics = new ArrayList<>();
		port =  ch.getOrDefaultPropValueInt("mqtt_conn", "server_port", 1833);
		String serveruri = "tcp://" + host + ":" + port;

		int qos = 1;
		String clientId = ch.getOrDefaultPropValueStr("mqtt_conn", "clientid", "client1");
		String userName = ch.getOrDefaultPropValueStr("mqtt_conn", "username", "");
		String userPsw = ch.getOrDefaultPropValueStr("mqtt_conn", "userpsw", "");

		/**
		 * connection timeout with second
		 */
		int connTimeout = (int) ch.getOrDefaultPropValueInt("mqtt_conn", "conn_to_sec", 10);
		

		int keepAliveInterval = (int) ch.getOrDefaultPropValueInt("mqtt_conn", "ka_int", 20);
		
		

		List<UADev> devs = this.getBelongToCh().getDevs();
		if (devs == null || devs.size() <= 0)
		{
			failedr.append("no device found ");
			return false;
		}
		for (UADev dev : devs)
		{
			String topic = dev.getOrDefaultPropValueStr("mqtt_tag", "topic", null);
			if (Convert.isNullOrEmpty(topic))
				continue;
			topics.add(topic);
		}
		if (topics.size() <= 0)
		{
			failedr.append("no device mqtt topic found");
			return false;
		}
		// hostΪ��������testΪclientid������MQTT�Ŀͻ���ID��һ���Կͻ���Ψһ��ʶ����ʾ��MemoryPersistence����clientid�ı�����ʽ��Ĭ��Ϊ���ڴ汣��
		client = new MqttClient(serveruri, clientId, new MemoryPersistence());
		// MQTT����������
		options = new MqttConnectOptions();
		// �����Ƿ����session,�����������Ϊfalse��ʾ�������ᱣ���ͻ��˵����Ӽ�¼����������Ϊtrue��ʾÿ�����ӵ������������µ��������
		options.setCleanSession(true);
		// �������ӵ��û���
		options.setUserName(userName);
		// �������ӵ�����
		options.setPassword(userPsw.toCharArray());
		// ���ó�ʱʱ�� ��λΪ��
		options.setConnectionTimeout(connTimeout);
		// ���ûỰ����ʱ�� ��λΪ�� ��������ÿ��1.5*20���ʱ����ͻ��˷��͸���Ϣ�жϿͻ����Ƿ����ߣ������������û�������Ļ���
		options.setKeepAliveInterval(keepAliveInterval);
		// ���ûص�����
		client.setCallback(new MqttCallback() {

			public void connectionLost(Throwable cause)
			{
				// System.out.println("connectionLost");
			}

			public void messageArrived(String topic, MqttMessage message) throws Exception
			{
				String cont = new String(message.getPayload());
				//System.out.println("topic:" + topic);
				//System.out.println("Qos:" + message.getQos());
				//System.out.println("message content:" + cont);
				//byte[] pl = message.getPayload();
				
				
				//publish(topic+"/echo", 1, "echo - "+cont) ;
				onJsonMsgArrived(topic,cont) ;
			}

			public void deliveryComplete(IMqttDeliveryToken token)
			{
				System.out.println("deliveryComplete---------" + token.isComplete());
			}

		});

		return true;
	}
	
	
	private void onJsonMsgArrived(String topic,String jsonstr)
	{
		JSONObject jobj = null ;
		try
		{
			jobj = new JSONObject(jsonstr) ;
		}
		catch(Exception e)
		{
			System.out.println("invalid payload,it's not json {} format") ;
			return ;
		}
		
		UACh ch = this.getBelongToCh() ;
		
		for(UADev uad:ch.getDevs())
		{
			String topicfilter = uad.getOrDefaultPropValueStr("mqtt_tag", "topic", null);
			if(Convert.isNullOrEmpty(topicfilter))
				continue ;
			if(!MqttTopic.isMatched(topicfilter, topic))
				continue ;
			//UATagList tl = uad.listTags();//.getTagList();
			List<UATag> tags = uad.listTags();//tl.listTags();
			StringBuilder failedr = new StringBuilder() ; 
			for(UATag tag:tags)
			{
				MQTTClientAddr da = (MQTTClientAddr)tag.getDevAddr(failedr);
				if(da==null)
					continue ;
				String tagtopic = da.getMQTTTopic() ;
				if(!topic.contentEquals(tagtopic))
					continue ;
				List<String> jsonpath = da.getPayloadJSONPath() ;
				int s ;
				if(jsonpath==null||(s=jsonpath.size())<=0)
					continue ;
				JSONObject curjob = jobj ;
				
				Object objv = null ;
				for(int i = 0 ; i < s ; i ++)
				{
					String pn = jsonpath.get(i) ;
					if(i<s-1)
					{
						curjob = curjob.optJSONObject(pn) ;
						if(curjob==null)
							break ;
					}
					else
					{
						objv = jobj.opt(pn) ;
						break ;
					}
				}
				//if(objv==null)
				da.RT_setVal(objv);
			}
		}
		
	}

	protected void RT_runInLoop() throws Exception
	{
		if (client.isConnected())
			return;

		try
		{
			// client.connect();
			client.connect(options);

			for (String topic : topics)
				client.subscribe(topic, 1);
			System.out.println(this.getBelongToCh().getTitle() + " MQTTClientDrv connect to " + this.host + ":"
					+ this.port + " ok");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void RT_endDriver() throws Exception
	{
		client.disconnect();
	}

	/**
	 * ������Ϣ
	 * 
	 * @param topic
	 *            ��Ϣ����
	 * @param qos
	 *            ��Ϣ��������
	 * @param message
	 *            ��Ϣ����
	 */
	public void publish(String topic, int qos, String message) throws Exception
	{
		MqttTopic mt = client.getTopic(topic);
		MqttMessage msg = new MqttMessage();
		msg.setQos(qos);
		// �Ƿ����ñ�����Ϣ����Ϊtrue�������Ķ����߶��ĸ�����ʱ�Կɽ��յ�����Ϣ
		msg.setRetained(false);
		msg.setPayload(message.getBytes());
		MqttDeliveryToken token = mt.publish(msg);
		//token.waitForCompletion();
//		log.info("[MQTT] publish message : " + token.isComplete() + ",{topic : " + topic + ", message : " + message
//				+ "}");
	}


	@Override
	public boolean supportDevFinder()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void RT_onConnReady(ConnPt cp)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean RT_runInLoop(StringBuilder failedr) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean RT_writeVal(UADev dev,DevAddr da,Object v)
	{
		return false;
	}
	
	@Override
	public boolean RT_writeVals(UADev dev,DevAddr[] da,Object[] v)
	{
		return false;
	}
}
