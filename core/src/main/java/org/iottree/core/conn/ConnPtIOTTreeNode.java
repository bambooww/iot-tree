package org.iottree.core.conn;

import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.iottree.core.UACh;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsGCxt;
import org.iottree.core.UATag;
import org.iottree.core.UAUtil;
import org.iottree.core.UAVal;
import org.iottree.core.conn.mqtt.MqttEndPoint;
import org.iottree.core.cxt.UACodeItem;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.node.PrjCaller;
import org.iottree.core.node.PrjCallerMQTT;
import org.iottree.core.node.PrjNodeAdapter;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConnPtIOTTreeNode  extends ConnPtMSG
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

	//private transient MqttEndPoint mqttEP = null ;
	
	private transient PrjCaller prjCaller = null ; 
	
	private transient long shareDT = -1 ;
	
	private transient boolean shareWritable = false ;
	
	private transient long lastPushDT = -1 ;
	
	public ConnPtIOTTreeNode()
	{
//		this.topics.add("iottree/node");
//		this.topics.add("iottree/syn");
	}

	@Override
	public String getConnType()
	{
		return ConnProIOTTreeNode.TP;
	}

	public PrjCaller getCaller()
	{
		if(prjCaller!=null)
			return prjCaller ;
		
		synchronized(this)
		{
			if(prjCaller!=null)
				return prjCaller ;
			
			prjCaller = new PrjCallerMQTT();
			prjCaller.withAdapter(nodeAdp);
			//prjCaller.init();
			//prjCaller.fromXmlData(xd);
			return prjCaller ;
		}
	}
	
	public boolean isShareWritable()
	{
		return shareWritable;
	}
	
	public long getShareDT()
	{
		return this.shareDT;
	}
	
	public long getLastPushDT()
	{
		return this.lastPushDT;
	}
	
	
	PrjNodeAdapter nodeAdp = new PrjNodeAdapter() {
		public void SW_callerOnResp(String shareprjid,byte[] cont) throws Exception
		{
			//if(!shareprjid.equals(this.sharePrjId))
			//	return ;
			
			XmlData xd = XmlData.parseFromByteArray(cont, "UTF-8");
			onNodeShareRespXmlData(xd);
			
		}
		
		public void SW_callerOnPush(byte[] cont) throws Exception
		{
			String jsonstr = new String(cont,"UTF-8") ;
			//System.out.println(" caller on push="+jsonstr) ;
			onNodeSharePush(jsonstr);
		}
	};

	private void onNodeShareRespXmlData(XmlData xd) throws Exception
	{
		UACh ch = this.getJoinedCh();
		if(ch==null)
			return ;
		//System.out.println("xmldata==="+xd.toXmlString());
		ch.Node_refreshByPrjXmlData(xd);
		//System.out.println("ch refresh by prj ok ") ;
	}
	
	private void onNodeSharePush(String jsonstr) throws Exception
	{
		try
		{
			UACh ch = this.getJoinedCh();
			if(ch==null)
				return ;
			
			if(Convert.isNullOrEmpty(jsonstr))
				return ;
			JSONObject jo = new JSONObject(jsonstr);
			shareWritable = jo.optBoolean("share_writable", false) ;
			shareDT = jo.optLong("share_dt", -1) ;
			
			updateChCxtDyn(ch,jo);
		}
		finally
		{
			lastPushDT = System.currentTimeMillis();
		}
	}
	
	private void updateChCxtDyn(UANodeOCTagsGCxt p,JSONObject curcxt)
	{
		JSONArray jos = curcxt.optJSONArray("tags");
		
		for(int i = 0,n = jos.length() ; i<n ; i++)
		{
			JSONObject tg = jos.getJSONObject(i);
			String name = tg.getString("n");
			UATag tag = p.getTagByName(name) ;
			if(tag==null)
				continue ;
			//var tagp =p+n ;
			boolean bvalid = tg.optBoolean("valid",false) ;
			long dt = tg.optLong("dt", -1) ;
			long chgdt = tg.optLong("chgdt",-1) ;
			
			Object ov = tg.opt("v") ;
			String strv = "";
			if(ov!=null&&ov!=JSONObject.NULL)
				strv =""+ov;
			//set to cxt
			ov = UAVal.transStr2ObjVal(tag.getValTp(), strv) ;
			UAVal uav = new UAVal(bvalid, ov,dt,chgdt);
			//tag.RT_setValStr(strv, true);
			tag.RT_setUAVal(uav);
			 
		}
		
		JSONArray subs = curcxt.optJSONArray("subs");
		for(int i = 0, n = subs.length(); i < n ; i ++)
		{
			JSONObject sub = subs.getJSONObject(i);
			
			String subn = sub.getString("n") ;
			
			UANode uan = p.getSubNodeByName(subn) ;
			if(uan==null)
				continue ;
			if(!(uan instanceof UANodeOCTagsGCxt))
				continue ;
			
			updateChCxtDyn((UANodeOCTagsGCxt)uan,sub) ;
		}
	}
	
	

//	/**
//	 * based on channel joined to this connpt.
//	 * write 
//	 * @param nodepath
//	 * @param strv
//	 */
//	public boolean writeShareTag(String nodepath,String strv)
//	{
//		if(!shareWritable)
//			return false;//cannot write
//		
//		
//		this.getCaller().sendMsg(tarprjid, mt, msg);
//		UATag uat = (UATag)UAUtil.findNodeByPath(nodepath);
//		this.
//	}
//	public MqttEndPoint getMqttEP()
//	{
//		if(mqttEP!=null)
//			return mqttEP ;
//		mqttEP = new MqttEndPoint("iottree_cpt_" + this.getId())
//				.withCallback(this.mqttCB);;
//		return mqttEP ;
//	}
	
	public void runOnWrite(UATag tag,Object val) throws Exception
	{
		if(!this.shareWritable)
			throw new Exception("remote node cannot be write");
		
		UACh ch = this.getJoinedCh();
		if(ch==null)
		{
			return ;
		}
		
		String path = tag.getNodeCxtPathIn(ch);
		String strv = "" ;
		if(val!=null)
			strv = val.toString() ;
		this.getCaller().callShareWriter(path, strv);
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		
		if(prjCaller!=null)
		{
			XmlData tmpxd = prjCaller.toXmlData() ;
			xd.setSubDataSingle("caller",tmpxd);
		}
		// xd.setParamValue("opc_app_name", this.appName);
//		if(mqttEP!=null)
//			mqttEP.transParamsToXml(xd);
			
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

		PrjCaller pc = getCaller() ;
		
//		MqttEndPoint ep = getMqttEP() ;
//		ep.withParamsXml(xd) ;
		
		XmlData tmpxd = xd.getSubDataSingle("caller") ;
		if(tmpxd!=null)
			pc.fromXmlData(tmpxd);
		
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

		PrjCaller pc = getCaller() ;
		pc.fromJSON(jo);
		
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
		if (prjCaller == null)
			return false;
		if(System.currentTimeMillis()-this.lastPushDT>30000)
			return false;
		return prjCaller.isConnReady();
	}
	
	public String getConnErrInfo()
	{
		if (prjCaller == null)
			return null;
		if(prjCaller.isConnReady())
			return null ;
		return prjCaller.getConnErrInfo() ;
	}
	
	public boolean sendMsg(String topic,byte[] bs) throws Exception
	{
		//this.publish(topic, bs, 0);
		if(prjCaller==null)
			return false;
		
		//prjCaller.sendMsg(tarprjid, mt, msg);
		return false;//not support in it
	}

	@Override
	protected void onRecvedMsg(String topic,byte[] bs) throws Exception
	{

			System.out.println("ConnPtIOTTreeNode onRecvedMsg=" + topic + " " + new String(bs,"utf-8"));

		
	}

	synchronized void disconnect() // throws IOException
	{
		prjCaller.disconnect();
	}
	
	void checkConn()
	{
		PrjCaller pc = getCaller() ;
		pc.checkConn();
	}
	

	@Override
	public List<String> getMsgTopics()
	{
		return null;
	}
	
//	public void publish(String topic, byte[] data) throws MqttPersistenceException, MqttException
//	{
//		publish(topic, data, 0);
//	}
//
//	public void publish(String topic, byte[] data, int qos) throws MqttPersistenceException, MqttException
//	{
//		getMqttEP().publish(topic, data, qos);
//	}
//
//	public void publish(String topic, String txt) throws Exception
//	{
//		publish(topic, txt.getBytes("utf-8"), 1);
//	}

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