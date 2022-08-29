package org.iottree.core.conn;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.script.ScriptException;

import org.iottree.core.ConnDev;
import org.iottree.core.ConnJoin;
import org.iottree.core.ConnMsg;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UANode;
import org.iottree.core.UATag;
import org.iottree.core.ConnDev.Data;
import org.iottree.core.conn.mqtt.MqttEndPoint;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.XmlHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;

public class ConnPtMQTT extends ConnPtMSG // implements ConnDevFindable
{
	private List<String> topics = null;

	

	public ConnPtMQTT()
	{

	}

	@Override
	public String getConnType()
	{
		return "mqtt";
	}

	public List<String> getMsgTopics()
	{
		return topics;
	}

	public void RT_checkConn() 
	{}
	
	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();

//		xd.setParamValue("sor_tp", sorTp.toString());
//		if(initJS!=null)
//			xd.setParamValue("init_js", initJS);
//		if (transJS != null)
//			xd.setParamValue("trans_js", transJS);
		if (topics != null)
			xd.setParamValues("topics", topics);
//		if (encod != null)
//			xd.setParamValue("encod", encod);
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);

//		String stp = xd.getParamValueStr("sor_tp", null);
//		if (Convert.isNotNullEmpty(stp))
//			sorTp = DataTp.valueOf(stp);
//		if (sorTp == null)
//			sorTp = DataTp.json;
//		initJS = xd.getParamValueStr("init_js");
//		transJS = xd.getParamValueStr("trans_js");

		topics = xd.getParamXmlValStrs("topics");
//		encod = xd.getParamValueStr("encod");
//		clearCache();
		return r;
	}

	protected void injectByJson(JSONObject jo) throws Exception
	{
		JSONArray jotps = jo.optJSONArray("topics");
		ArrayList<String> tps = new ArrayList<>();
		if (jotps != null)
		{
			for (int i = 0, n = jotps.length(); i < n; i++)
			{
				String tp = jotps.getString(i);
				StringBuilder failedr = new StringBuilder();
				if (!MqttEndPoint.checkTopicValid(tp, failedr))
					throw new Exception(failedr.toString());
				tps.add(tp);
			}
		}

		super.injectByJson(jo);

//		String stp = jo.optString("sor_tp");
//		if (Convert.isNotNullEmpty(stp))
//			sorTp = DataTp.valueOf(stp);
//		if (sorTp == null)
//			sorTp = DataTp.json;
//		this.topics = tps;
//		this.initJS = jo.optString("init_js");
//		this.transJS = jo.optString("trans_js");
//		this.encod = jo.optString("encod");
//		clearCache();
	}

	@Override
	public String getStaticTxt()
	{
		// TODO Auto-generated method stub
		return null;
	}

	//transient private int initInitOk = 0 ;
	
	
	
	@Override
	public boolean isConnReady()
	{
		ConnProMQTT cp = (ConnProMQTT) this.getConnProvider();
		return cp.isMQTTConnected();
	}

	public String getConnErrInfo()
	{
		return "";

	}

	public boolean sendMsg(String topic, byte[] bs) throws Exception
	{
		// this.publish(topic, bs, 0);
		return true;
	}

	protected boolean readMsgToFile(File f) throws Exception
	{
		return false;
	}
	
//	@Override
//	public void writeBindBeSelectedTreeJson(Writer w, boolean list_tags_only, boolean force_refresh) throws Exception
//	{
//
//	}
//
//	@Override
//	public int writeBindBeSelectedListRows(Writer w, int idx, int size)
//	{
//
//		return 0;
//	}

	public void runOnWrite(UATag tag, Object val) throws Exception
	{
		throw new Exception("no impl");
		// it may send some msg
	}

	synchronized void disconnect() // throws IOException
	{
		// getMqttEP().disconnect();
	}

}
