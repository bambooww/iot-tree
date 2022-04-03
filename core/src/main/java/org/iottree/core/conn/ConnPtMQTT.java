package org.iottree.core.conn;

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
import org.json.JSONArray;
import org.json.JSONObject;

public class ConnPtMQTT extends ConnPtMSGTopic// implements ConnDevFindable
{
	public static enum SorTp
	{
		json(1), str(2), xml(3), bytes(4);

		private final int val;

		SorTp(int v)
		{
			val = v;
		}

		public int getInt()
		{
			return val;
		}

		public String getTitle()
		{
			switch (val)
			{
			case 1:
				return "json";
			case 2:
				return "string";
			case 3:
				return "xml";
			case 4:
				return "bytes";
			default:
				return null;
			}
		}
	}

	private List<String> topics = null;

	private SorTp sorTp = SorTp.json;

	public static final int RUN_ST_NOTRUN = 0;

	public static final int RUN_ST_OK = 1;

	public static final int RUN_ST_ERROR = 2;

	/**
	 * init js ,can be defined self func
	 */
	private String initJS = null; 
	
	
	private String transJS = null;

	private String encod = "UTF-8";

	public ConnPtMQTT()
	{

	}

	@Override
	public String getConnType()
	{
		return "mqtt";
	}

	public SorTp getSorTp()
	{
		return this.sorTp;
	}
	
	public String getInitJS()
	{
			if (this.initJS == null)
				return "";
			return this.initJS;
	}

	public String getTransJS()
	{
		if (this.transJS == null)
			return "";
		return this.transJS;
	}

	public List<String> getMsgTopics()
	{
		return topics;
	}

	public String getEncod()
	{
		if (encod == null)
			return "UTF-8";
		return encod;
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();

		xd.setParamValue("sor_tp", sorTp.toString());
		if(initJS!=null)
			xd.setParamValue("init_js", initJS);
		if (transJS != null)
			xd.setParamValue("trans_js", transJS);
		if (topics != null)
			xd.setParamValues("topics", topics);
		if (encod != null)
			xd.setParamValue("encod", encod);
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);

		String stp = xd.getParamValueStr("sor_tp", null);
		if (Convert.isNotNullEmpty(stp))
			sorTp = SorTp.valueOf(stp);
		if (sorTp == null)
			sorTp = SorTp.json;
		initJS = xd.getParamValueStr("init_js");
		transJS = xd.getParamValueStr("trans_js");

		topics = xd.getParamXmlValStrs("topics");
		encod = xd.getParamValueStr("encod");
		clearCache();
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

		String stp = jo.optString("sor_tp");
		if (Convert.isNotNullEmpty(stp))
			sorTp = SorTp.valueOf(stp);
		if (sorTp == null)
			sorTp = SorTp.json;
		this.topics = tps;
		this.initJS = jo.optString("init_js");
		this.transJS = jo.optString("trans_js");
		this.encod = jo.optString("encod");
		clearCache();
	}

	protected void onRecvedMsg(String topic, byte[] bs) throws Exception
	{
		switch (this.sorTp)
		{
		case str:
			break;
		case xml:
			break;
		case bytes:
			break;
		case json:
		default:
			String jsonstr = new String(bs, encod);
			runTransJS(topic, jsonstr);
			break;
		}

		// System.out.println("mqtt conn "+this.getName()+" onRecvedMsg=" +
		// topic + " " + new String(bs,encod));

	}

	public void onJoinedChanged(ConnJoin cj)
	{
		clearCache();
	}

	@Override
	public String getStaticTxt()
	{
		// TODO Auto-generated method stub
		return null;
	}

	//transient private int initInitOk = 0 ;
	/**
	 * 0 - unknown 1 = ok -1 = err
	 */
	transient private int transInitOk = 0;
	transient private UAContext transCxt = null;
	// transient private UACodeItem onWriteCI = null ;

	transient private long lastRunMS = -1;

	private int transRunST = RUN_ST_NOTRUN;

	private String initErr = null ;
	
	private String transRunErr = null;
	
	private UACh joinedCh = null ;

	public int getTransRunST()
	{
		return this.transRunST;
	}

	public String getTransRunErr()
	{
		return this.transRunErr;
	}

	private void clearCache()
	{
		transInitOk = 0;// to known
	}

	private int initTransJS()// throws ScriptException
	{
		if (transInitOk != 0)
			return transInitOk;
		transInitOk = -1;
		if (Convert.isNullOrTrimEmpty(this.transJS))
			return transInitOk;

		try
		{
			UACh ch = this.getJoinedCh();
			if (ch == null)
			{
				return transInitOk;
			}

			this.joinedCh = ch ;
			this.lastRunMS = System.currentTimeMillis();

			transCxt = ch.RT_getContext();
			if(Convert.isNotNullEmpty(initJS))
				transCxt.scriptEval(initJS) ;
			
			transCxt.scriptEval("function __JsMqttTrans_" + this.getId() + "_sor($topic,$msg){\r\n" +

					this.transJS + "\r\n}\r\n	function __JsMqttTrans_" + this.getId() + "($topic,$msg){\r\n"
					+ "$msg=JSON.parse($msg);\r\n"
					+ "var r=__JsMqttTrans_" + this.getId() + "_sor($topic,$msg);\r\n"
					+ "return JSON.stringify(r);" + "\r\n}");
			// this.bRunScriptReady = true;
			transInitOk = 1;
			return transInitOk;
		}
		catch ( Exception e)
		{
			initErr = e.getMessage() ;
			transInitOk = -1;
			return transInitOk;
		}
	}
	
	protected void RT_connInit()
	{
		transInitOk = 0 ;
		initTransJS();
	}

	/**
	 js return format
	 [
    	{"dev_name":"dev1","dev_title":"Device1","data":[
	    	{"n":"g1.v1","vt":"float","v":18.5},
	    	{"n":"st","vt":"bool","v":true}
	    	]
	    },
	    {"dev_name":"dev2","dev_title":"Device2","data":[
	    	{"n":"g1.v1","vt":"float","v":13.5},
	    	{"n":"st","vt":"bool","v":false}
	    	]
	    }
    ]
    
	 * @param topic
	 * @param jsonstr
	 * @return
	 * @throws Exception
	 */
	synchronized boolean runTransJS(String topic, String jsonstr) throws Exception
	{
		//int st = initTransJS();
		if (transInitOk <= 0)
		{
			this.transRunST = RUN_ST_ERROR;
			this.transRunErr = "init error";
			return false;
		}
		try
		{
			String ret = (String) transCxt.scriptInvoke("__JsMqttTrans_" + this.getId(), topic, jsonstr);
			if(Convert.isNullOrEmpty(ret))
			{
				return true ;
			}
			
			JSONArray devs = new JSONArray(ret) ;
			int s = devs.length() ;
			ArrayList<ConnDev> mdevs = new ArrayList<>() ;
			StringBuilder failedr = new StringBuilder() ;
			for(int i = 0 ; i < s ; i ++)
			{
				JSONObject jo = devs.getJSONObject(i);
				ConnDev mmd = ConnDev.transFromJO(jo,failedr);
				if(mmd==null)
				{
					this.transRunST = RUN_ST_ERROR;
					this.transRunErr = failedr.toString() ;
					return false;
				}
				mdevs.add(mmd) ;
			}
			onMsgDevsFound(mdevs);
			this.transRunST = RUN_ST_OK;
			return true;
		}
		catch ( Exception e)
		{
			this.transRunST = RUN_ST_ERROR;
			this.transRunErr = e.getMessage();
			e.printStackTrace();
			return false;
		}
	}
	
	private void onMsgDevsFound(List<ConnDev> devs)
	{
		for(ConnDev dev:devs)
		{
			if(updateToCh(dev))
			{
				foundNewDevs.remove(dev.getName()) ;
				continue ;
			}
			//find new
			foundNewDevs.put(dev.getName(), dev) ;
		}
	}
	
	private LinkedHashMap<String,ConnDev> foundNewDevs = new LinkedHashMap<>() ;
	
	private boolean updateToCh(ConnDev d)
	{
		String devn = d.getName() ;
		UADev dev = this.joinedCh.getDevByName(devn) ;
		if(dev==null)
		{
			return false;
		}
		for(Data md:d.getDatas())
		{
			UANode tmpn = dev.getDescendantNodeByPath(md.getPath()) ;
			if(tmpn==null)
				continue ;
			if(!(tmpn instanceof UATag))
				continue ;
			
			UATag tag = (UATag)tmpn ;
			
			tag.RT_setValRaw(md.getVal());
		}
		return true;
	}

	public LinkedHashMap<String,ConnDev> getFoundConnDevs()
	{
		return foundNewDevs;
	}
	
	ConnMsg jsInitErrMsg = new ConnMsg().asTitle("JS Init err")
			.asIcon("fa-regular fa-rectangle-xmark fa-lg fa-beat-fade").asIconColor("red");
	
	@Override
	public List<ConnMsg> getConnMsgs()
	{
		ArrayList<ConnMsg> rets = null ;
		if(transInitOk<0)
		{//show err msg
			 jsInitErrMsg.asDesc(initErr);
			 rets = new ArrayList<>(3) ;
			 rets.add(jsInitErrMsg) ;
		}
		List<ConnMsg> ret = super.getConnMsgs() ;
		if(ret==null||ret.size()<=0)
			return rets ;
		if(rets==null)
			return ret ;
		rets.addAll(ret) ;
		return rets ;
	}
	
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

	public void RT_writeValByBind(String tagpath, String strv)
	{
		// TODO
	}

}
