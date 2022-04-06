package org.iottree.core.conn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.ConnDev;
import org.iottree.core.ConnJoin;
import org.iottree.core.ConnMsg;
import org.iottree.core.ConnPt;
import org.iottree.core.ConnPtDevFinder;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UANode;
import org.iottree.core.UATag;
import org.iottree.core.ConnDev.Data;
import org.iottree.core.ConnPt.DataTp;
import org.iottree.core.ConnPt.MonData;
import org.iottree.core.ConnPt.MonItem;
import org.iottree.core.conn.mqtt.MqttEndPoint;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.XmlHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;

public abstract class ConnPtMSG  extends ConnPtDevFinder
{

	public static final int RUN_ST_NOTRUN = 0;

	public static final int RUN_ST_OK = 1;

	public static final int RUN_ST_ERROR = 2;

	
	
	private DataTp sorTp = DataTp.json;



	private String encod = "UTF-8";
	/**
	 * init js ,can be defined self func
	 */
	private String initJS = null; 
	
	
	private String transJS = null;
	
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
	
	private String transRunRes = null ;
	

	
	private UACh joinedCh = null ;


	public DataTp getSorTp()
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
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);

		String stp = xd.getParamValueStr("sor_tp", null);
		if (Convert.isNotNullEmpty(stp))
			sorTp = DataTp.valueOf(stp);
		if (sorTp == null)
			sorTp = DataTp.json;
		initJS = xd.getParamValueStr("init_js");
		transJS = xd.getParamValueStr("trans_js");

		clearCache();
		return r;
	}

	protected void injectByJson(JSONObject jo) throws Exception
	{
//		JSONArray jotps = jo.optJSONArray("topics");
//		ArrayList<String> tps = new ArrayList<>();
//		if (jotps != null)
//		{
//			for (int i = 0, n = jotps.length(); i < n; i++)
//			{
//				String tp = jotps.getString(i);
//				StringBuilder failedr = new StringBuilder();
//				if (!MqttEndPoint.checkTopicValid(tp, failedr))
//					throw new Exception(failedr.toString());
//				tps.add(tp);
//			}
//		}

		super.injectByJson(jo);

		String stp = jo.optString("sor_tp");
		if (Convert.isNotNullEmpty(stp))
			sorTp = DataTp.valueOf(stp);
		if (sorTp == null)
			sorTp = DataTp.json;
		//this.topics = tps;
		this.initJS = jo.optString("init_js");
		this.transJS = jo.optString("trans_js");
		this.encod = jo.optString("encod");
		clearCache();
	}
	
	

	public int getTransRunST()
	{
		return this.transRunST;
	}

	public String getTransRunErr()
	{
		return this.transRunErr;
	}
	
	public String getTransRunRes()
	{
		return transRunRes;
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
			
			String tmps = "function __JsMqttTrans_" + this.getId() + "_sor($topic,$msg){\r\n" +

					this.transJS + "\r\n}\r\n	function __JsMqttTrans_" + this.getId() + "($topic,$msg){\r\n";
			if(this.sorTp==DataTp.json||this.sorTp==DataTp.xml)
			{
				tmps += "$msg=JSON.parse($msg);\r\n" ;
			}
			tmps += ("var r=__JsMqttTrans_" + this.getId() + "_sor($topic,$msg);\r\n"
						+ "return JSON.stringify(r);" + "\r\n}");
			transCxt.scriptEval(tmps);
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
	synchronized boolean runTransJS(String topic, Object jsonstr_bs) throws Exception
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
			transRunRes = (String) transCxt.scriptInvoke("__JsMqttTrans_" + this.getId(), topic, jsonstr_bs);
			if(Convert.isNullOrEmpty(transRunRes))
			{
				return true ;
			}
			JSONArray devs = new JSONArray(transRunRes) ;
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
	

	public void onJoinedChanged(ConnJoin cj)
	{
		clearCache();
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
	

	protected void onRecvedMsg(String topic, byte[] bs) throws Exception
	{
		String str = null ;
		MonData[] mds = null ;
		switch (this.sorTp)
		{
		case bytes:
			
			
			mds = new MonData[] {new MonData("sor",bs),null} ;
			return;
		case str:
			
			str = new String(bs, encod);
			mds = new MonData[] {new MonData("sor",DataTp.str,str),null} ;
			break;
		case xml:
			//str = new String(bs, encod);
			Element ele = XmlHelper.byteArrayToElement(bs) ;
			str = XmlHelper.transElement2JSONStr(ele,true,true,false) ;
			mds = new MonData[] {new MonData("sor",DataTp.xml,bs,this.encod),new MonData("xml-json",DataTp.json,str),null} ;
			break;
		case json:
		default:
			str = new String(bs, encod);
			mds = new MonData[] {new MonData("sor",DataTp.json,str),null} ;
			break;
		}

		if(runTransJS(topic, str))
		{
			mds[mds.length-1] = new MonData("result",DataTp.json,this.getTransRunRes()) ;
		}
		else
		{
			mds[mds.length-1] = new MonData("result",DataTp.str,"transfer error:"+this.getTransRunErr()) ;
		}
		MonItem mis = new MonItem(true,topic,mds) ;
		this.onMonDataRecv(mis);
	}

	public abstract boolean sendMsg(String topic,byte[] bs) throws Exception ;
	
	public abstract void runOnWrite(UATag tag,Object val) throws Exception;
}
