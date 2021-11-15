package org.iottree.core.node;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.iottree.core.conn.mqtt.MqttEndPoint;
import org.iottree.core.util.xmldata.XmlData;

public class PrjShareMQTT extends PrjSharer
{
	MqttEndPoint mqttEP = null ;
	
	//private Thread th = null ;
	
	public synchronized void init()
	{
		if(mqttEP!=null)
			return ;
		
		String prjid = this.getPrjId() ;
		mqttEP = new MqttEndPoint("iottree_prj_sharer_"+prjid) ;
		
		XmlData pxd = this.getParamXD() ;
		mqttEP.withParamsXml(pxd).withCallback(mqttCB);
		for(NodeMsg.MsgTp mt:NodeMsg.MsgTp.values())
		{
			mqttEP.withListenTopic("_n/+/"+prjid+"/"+mt) ;
		}
	}
	
	public MqttEndPoint getMqttEP()
	{
		init() ;
		
		return mqttEP ;
	}

	private MqttCallback mqttCB = new MqttCallback()
		{

			@Override
			public void connectionLost(Throwable cause)
			{
				
			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception
			{
				NodeMsg nm = parseNodeMsg(topic,message.getPayload());
				if(nm==null)
					return ;//do nothing
				onRecvedMsg(nm);
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token)
			{
				
			}
	
		};
		
	@Override
	public boolean isValid()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void sendMsg(NodeMsg nm) throws Exception
	{
		String topic = MqttUtil.calMqttTopic(nm) ;
		//System.out.println(" Share MQTT send="+topic) ;
		this.getMqttEP().publish(topic, nm.getContent());
		//this.getMqttEP().publish(topic, data);
	}

	@Override
	protected NodeMsg parseNodeMsg(String mqtt_topic, byte[] msg)
	{
		return MqttUtil.calNodeMsg(mqtt_topic, msg);
	}
	
	private transient long lastR = -1 ;

	@Override
	public void runInLoop()
	{
		super.runInLoop();
		
		if(System.currentTimeMillis()-lastR<10000)
			return ;
		
		lastR = System.currentTimeMillis();
		
		getMqttEP().checkConn();
		
	}
	
	public void runStop()
	{
		if(mqttEP==null)
			return ;
		mqttEP.disconnect();
		mqttEP = null;
	}

	public boolean isRunning()
	{
		if(mqttEP==null)
			return false;
		return mqttEP.isConnReady();
	}
	
//	private Runnable runner = new Runnable() {
//
//		@Override
//		public void run()
//		{
//			try
//			{
//				while(th!=null)
//				{
//					try
//					{
//						Thread.sleep(10000);
//					}
//					catch(Exception e) {}
//					
//					mqttEP.checkConn();
//				}
//			}
//			finally
//			{
//				th = null ;
//			}
//		}} ;
//
//	@Override
//	public synchronized void start()
//	{
//		if(th!=null)
//			return  ;
//		
//		th = new Thread(runner) ;
//		th.start();
//		
//	}
//
//	@Override
//	public void stop()
//	{
//		Thread t = th ;
//		if(t==null)
//			return ;
//		
//		t.interrupt();
//		
//		mqttEP.disconnect();
//		th = null ;
//	}
//
//	@Override
//	public boolean isRunning()
//	{
//		return th!=null;
//	}

}
