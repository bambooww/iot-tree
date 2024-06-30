package org.iottree.ext.msg_net;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.iottree.core.conn.mqtt.MqttEndPoint;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.router.JoinOut;
import org.iottree.core.router.RouterObj;
import org.iottree.core.router.roa.ROAMqtt.RecvConf;
import org.iottree.core.router.roa.ROAMqtt.SendConf;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class Mqtt_M extends MNModule implements IMNRunner
{
	private static MqttIn_NS SUP_MQTT_IN = new MqttIn_NS() ;
	private static MqttOut_NE SUP_MQTT_OUT = new MqttOut_NE() ;
	static List<MNNode> supNodes = Arrays.asList(SUP_MQTT_IN,SUP_MQTT_OUT) ;

	public static enum OutFmt
	{
		txt,json
	}
	
	public static class SendConf
	{
		String id  ;
		
		String topic ;
		
		String title ;
		
		String desc ;

		
		public String getShowTitle()
		{
			if(Convert.isNotNullEmpty(title))
				return title ;
			return title ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("id", id) ;
			//jo.put("n", this.name) ;
			jo.put("topic", this.topic) ;
			jo.putOpt("t", this.title) ;
			jo.putOpt("d", this.desc) ;
			return jo ;
		}
		
		public boolean fromJO(JSONObject jo)
		{
			this.id = jo.getString("id") ;
			//this.name = jo.getString("n") ;
			this.topic = jo.getString("topic") ;
			this.title = jo.optString("t","") ;
			this.desc = jo.optString("d","") ;
			return true ;
		}
	}
	
	public static class RecvConf
	{
		String id  ;
		
		//String name ;
		
		String topic ;
		
		String title ;
		
		OutFmt fmt = OutFmt.txt ;
		
		String desc ;
		
		public String getShowTitle()
		{
			if(Convert.isNotNullEmpty(title))
				return title ;
			return title ;
		}
		
//		public SendConf(String topic,String title,String desc)
//		{
//			
//		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("id", id) ;
			//jo.putOpt("n", this.name) ;
			jo.put("topic", this.topic) ;
			jo.putOpt("t", this.title) ;
			jo.putOpt("d", this.desc) ;
			if(fmt!=null)
				jo.putOpt("fmt", fmt.name()) ;
			return jo ;
		}
		
		public boolean fromJO(JSONObject jo)
		{
			this.id = jo.getString("id") ;
			//this.name = jo.optString("n") ;
			this.topic = jo.getString("topic") ;
			this.title = jo.optString("t","") ;
			this.desc = jo.optString("d","") ;
			this.fmt = OutFmt.valueOf(jo.optString("fmt","txt")) ;
			if(this.fmt==null)
				this.fmt = OutFmt.txt ;
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
	
	
	public SendConf getSendConfById(String id)
	{
		for(SendConf sc:this.sendConfs)
		{
			if(sc.id.equals(id))
				return sc ;
		}
		return null ;
	}
	
	public RecvConf getRecvConfById(String id)
	{
		for(RecvConf sc:this.recvConfs)
		{
			if(sc.id.equals(id))
				return sc ;
		}
		return null ;
	}
	
	@Override
	protected List<MNNode> getSupportedNodes()
	{
		return supNodes;
	}

	@Override
	public String getTP()
	{
		return "mqtt";
	}

	@Override
	public String getTPTitle()
	{
		return "MQTT";
	}

	@Override
	public String getColor()
	{
		return "#debed7";
	}

	@Override
	public String getIcon()
	{
		return "PK_bridge";//"\\uf1eb-270,\\uf1eb-90";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
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

	@Override
	protected void setParamJO(JSONObject jo)
	{
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
				sc.fromJO(tmpjo);
				scs.add(sc) ;
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
				sc.fromJO(tmpjo);
				rcs.add(sc) ;
			}
			this.recvConfs = rcs ;
		}
	}

	@Override
	public void checkAfterSetParam()
	{
		try
		{
			updateSendRecvNodes() ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	private void updateSendRecvNodes() throws Exception
	{
		List<MNNode> nodes = this.getRelatedNodes() ;
		float me_x = this.getX() ;
		float me_y = this.getY() ;
		HashSet<String> sendmids = new HashSet<>() ;
		HashSet<String> recvids = new HashSet<>() ;
		if(this.sendConfs!=null)
		{
			for(SendConf sc:this.sendConfs)
			{
				sendmids.add(sc.id) ;
			}
			
		}
		if(this.recvConfs!=null)
		{
			for(RecvConf rc:this.recvConfs)
			{
				recvids.add(rc.id) ;
			}
		}
		
		MNNet net = this.getBelongTo() ;
		
		boolean bdirty = false;
		if(nodes!=null)
		{
			for(MNNode n:nodes)
			{
				if(n instanceof MqttOut_NE)
				{
					MqttOut_NE num = (MqttOut_NE)n ;
					if(Convert.isNullOrEmpty(num.sendId) || !sendmids.contains(num.sendId))
					{
						net.delNodeById(n.getId(), false) ;
						bdirty = true ;
					}
					else
					{
						SendConf dev = this.getSendConfById(num.sendId) ;
						sendmids.remove(num.sendId) ;
						num.setTitle(dev.getShowTitle()+"["+dev.topic+"]") ;
					}
				}
				
				if(n instanceof MqttIn_NS)
				{
					MqttIn_NS nnn = (MqttIn_NS)n ;
					if(Convert.isNullOrEmpty(nnn.recvId) || !recvids.contains(nnn.recvId))
					{
						net.delNodeById(n.getId(), false) ;
						bdirty = true ;
					}
					else
					{
						RecvConf dev = this.getRecvConfById(nnn.recvId) ;
						recvids.remove(nnn.recvId) ;
						nnn.setTitle(dev.getShowTitle()+"["+dev.topic+"]") ;
					}
				}
			}
		}
		
		int newcc = 0 ;
		for(String addid :sendmids)
		{
			newcc ++ ;
			SendConf dev = this.getSendConfById(addid) ;
			MqttOut_NE newn = (MqttOut_NE)net.createNewNodeInModule(this,SUP_MQTT_OUT,me_x-100, me_y-20-33*newcc,null,false) ;
			newn.sendId = addid ;
			newn.setTitle(dev.getShowTitle()+"["+dev.topic+"]") ;
			bdirty = true ;
		}
		
		newcc = 0 ;
		for(String addid :recvids)
		{
			newcc ++ ;
			RecvConf dev = this.getRecvConfById(addid) ;
			MqttIn_NS newn = (MqttIn_NS)net.createNewNodeInModule(this,SUP_MQTT_IN,me_x+200, me_y-20-53*newcc,null,false) ;
			newn.recvId = addid ;
			newn.setTitle(dev.getShowTitle()+"["+dev.topic+"]") ;
			bdirty = true ;
		}
		
		if(bdirty)
			net.save();
	}
	// mqtt
	

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
	
	// rt
	

	private transient MqttEndPoint mqttEP = null;
	

	private boolean bRTInitOk = false;
	
	Thread RT_th = null ;
	
	boolean RT_bRun = false;
	
	
	protected MqttEndPoint getMqttEP()
	{
		if (mqttEP != null)
			return mqttEP;
		mqttEP = new MqttEndPoint("iottree_mn_mqtt" + this.getId()).withCallback(this.RT_mqttCB);
		return mqttEP;
	}
	
	protected boolean RT_init(StringBuilder failedr)
	{
		bRTInitOk = false;
		
		if (Convert.isNullOrEmpty(brokerHost) || brokerPort <= 0)
		{
			failedr.append("no borker host port set");
			return false;
		}

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
				ep.setListenTopics(recv_tps) ;
			}
			bRTInitOk = true ;
			return true ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			failedr.append(ee.getMessage());
			return false;
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
			System.out.println(" Mqtt_M conn lost");
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
		}
	};

	

	protected void RT_onRecvedMsg(String topic, byte[] bs) throws Exception
	{
		String txt = new String(bs,"UTF-8") ;
		List<MNNode> ns=this.getRelatedNodes() ;
		if(ns==null)
			return ;
		
		for(MNNode n:ns)
		{
			if(n instanceof MqttIn_NS)
			{
				MqttIn_NS nin = (MqttIn_NS)n ;
				RecvConf rc = this.getRecvConfById(nin.recvId) ;
				if(rc==null)
					continue ;
				if(MqttEndPoint.checkTopicMatch(rc.topic, topic))
				{
					MNMsg msg = new MNMsg() ;
					if(rc.fmt==OutFmt.json)
					{
						msg.asPayloadJO(txt) ;
					}
					else
					{
						msg.asPayload(txt) ;
					}
					this.RT_sendMsgByRelatedNode(n, RTOut.createOutAll(msg));
				}
			}
		}
	}

	@Override
	public synchronized boolean RT_start(StringBuilder failedr)
	{
		if(RT_bRun)
			return true;
		
		if(!RT_init(failedr))
			return false;
		
		RT_bRun=true ;
		RT_th = new Thread(mqttMRunner);
		RT_th.start();
		return true;
	}

	@Override
	public void RT_stop()
	{
		Thread th = RT_th ;
		if(th==null)
			return ;
		
		if(th!=null)
			th.interrupt();
		
		RT_bRun = false ;
		RT_th  =null ;
		if(mqttEP!=null)
		{
			mqttEP.disconnect();
			mqttEP = null;
		}
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

	@Override
	public boolean RT_runnerEnabled()
	{
		return true;
	}

	@Override
	public boolean RT_runnerStartInner()
	{
		return false;
	}


	private Runnable mqttMRunner = new Runnable()
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
			while (RT_bRun)
			{
				this.checkConn();
			}//end of while
		}
		finally
		{
			RT_bRun=false;
			RT_th = null ;
			
			if(mqttEP!=null)
			{
				mqttEP.disconnect();
				mqttEP = null;
			}
		}
	}
	
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		MqttEndPoint ep = mqttEP ;
		if(ep!=null&&ep.isConnReady())
		{
			StringBuilder divsb = new StringBuilder() ;
			divsb.append("<div tp='run' class=\"rt_blk\"><span style=\"color:green\">Connected :</span>")
				.append(brokerHost).append(":").append(brokerPort)
				.append("</div>") ;
			
			divblks.add(new DivBlk("mqtt_m",divsb.toString())) ;
		}
		else
		{
			StringBuilder divsb = new StringBuilder() ;
			divsb.append("<div tp='run' class=\"rt_blk\"><span style=\"color:red\">Not Connect to ").append("</span>")
				.append(brokerHost).append(":").append(brokerPort)
				.append("</div>") ;
			
			divblks.add(new DivBlk("mqtt_m",divsb.toString())) ;
		}
		super.RT_renderDiv(divblks);
	}
}
