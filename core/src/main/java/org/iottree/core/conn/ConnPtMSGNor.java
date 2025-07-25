package org.iottree.core.conn;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.iottree.core.Config;
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
import org.iottree.core.conn.ConnPtBinder.BindItem;
import org.iottree.core.conn.html.BindHandlerHtml;
import org.iottree.core.conn.mqtt.MqttEndPoint;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.XmlHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

public abstract class ConnPtMSGNor  extends ConnPtDevFinder
{

	public static final int RUN_ST_NOTRUN = 0;

	public static final int RUN_ST_OK = 1;

	public static final int RUN_ST_ERROR = 2;

	public static enum HandleSty
	{
		js_trans, bind;

//		private final int val;
//
//		HandleSty(int v)
//		{
//			val = v;
//		}
//
//		public int getInt()
//		{
//			return val;
//		}

		public String getTitle()
		{
			switch (this.name())
			{
			case "js_trans":
				return "JS Transfer";
			case "bind":
				return "Binder";
			default:
				return null;
			}
		}
	}
	
	
	
	private DataTp sorTp = DataTp.json;



	private String encod = "UTF-8";
	
	/**
	 * init js ,can be defined self func
	 */
	
	
	private HandleSty handleSty = HandleSty.js_trans ;
	
	
	public class TransHandler
	{
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
		
		private int initTransJS()// throws ScriptException
		{
			if (transInitOk != 0)
				return transInitOk;
			transInitOk = -1;
			if (Convert.isNullOrTrimEmpty(this.transJS))
				return transInitOk;

			try
			{
				
				this.lastRunMS = System.currentTimeMillis();

				if(joinedCh==null)
					return transInitOk ;
				transCxt = joinedCh.RT_getContext();
				if(Convert.isNotNullEmpty(initJS))
					transCxt.scriptEval(initJS) ;
				
				String tmps = "function __JsMqttTrans_" + getId() + "_sor($topic,$msg){\r\n" +

						this.transJS + "\r\n}\r\n	function __JsMqttTrans_" + getId() + "($topic,$msg){\r\n";
				if(sorTp==DataTp.json||sorTp==DataTp.xml)
				{
					tmps += "$msg=JSON.parse($msg);\r\n" ;
				}
				tmps += ("var r=__JsMqttTrans_" + getId() + "_sor($topic,$msg);\r\n"
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
				transRunRes = (String) transCxt.scriptInvoke("__JsMqttTrans_" + getId(), topic, jsonstr_bs);
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
	}
	
	
	public static class PathItem<T>
	{
		String path = null ;
		
		String vt = null ;
		
		T probOb = null ;
		
		public PathItem(String p,String vt,T prob)
		{
			this.path = p ;
			this.vt = vt;
			this.probOb = prob ;
		}
		
		public String getPath()
		{
			return this.path ;
		}
		
		public String getVT()
		{
			return vt ;
		}
		
		public T getProbeObj()
		{
			return probOb ;
		}
	}
	
	public static abstract class BindHandler
	{
		protected String bindProbeStr = null ;
		
		protected String bindMapStr = null ;
		
			
		transient protected String bindRunErr = null;
		
		transient protected String bindRunRes = null ;
		
		transient protected ConnPtMSGNor connPtMsg = null ;
		
		public BindHandler(ConnPtMSGNor cpm)
		{
			connPtMsg = cpm ;
		}

		public String getBindProbeStr()
		{
			return this.bindProbeStr ;
		}
		
		public String getBindMapStr()
		{
			return this.bindMapStr ;
		}
		

		public String getBindRunErr()
		{
			return this.bindRunErr;
		}
		
		public String getBindRunRes()
		{
			return bindRunRes;
		}

		protected abstract boolean initBind();
		
		protected abstract boolean runBind(String topic,String txt) throws Exception;
		
		public abstract List<String> listBindTagPathInCh() ;
		

		public List<UATag> listBindTags()
		{
			
			if(connPtMsg.joinedCh==null)
				return null ;
			
			List<String> tagps = listBindTagPathInCh() ;
			if(tagps==null)
				return null ;
			
			ArrayList<UATag> tags = new ArrayList<>() ;
			for(String tagp:tagps)
			{
				UATag t = connPtMsg.joinedCh.getTagByPath(tagp) ;
				if(t==null)
					continue ;
				tags.add(t) ;
			}
			return tags ;
		}
		
	}
	
	public class BindHandlerJson extends BindHandler
	{
		private transient HashMap<String,PathItem<JsonPath>> tag2JsonPathItem = null ;
		
		public BindHandlerJson(ConnPtMSGNor cpm)
		{
			super(cpm) ;
		}
				
		protected boolean initBind()
		{
			if(Convert.isNullOrEmpty(bindProbeStr))
			{
				bindRunErr = "no bind setup" ;
				return false;
			}
			
			try
			{
				JSONArray bps = new JSONArray(bindProbeStr) ;
				int len = bps.length() ;
				HashMap<String,String> prob_map = new HashMap<>() ;
				for(int i = 0 ; i < len ; i ++)
				{
					JSONObject ob = bps.getJSONObject(i);
					String path = ob.optString("path") ;
					String vt = ob.optString("vt") ;
					if(Convert.isNotNullEmpty(path)&&Convert.isNotNullEmpty(vt))
						prob_map.put(path, vt);
				}
				initBindJson(bps);
				
				return true;
			}
			catch(Exception e)
			{
				bindRunErr = "bind init err:"+e.getMessage() ;
				return false;
			}
		}
		
		private void initBindJson(JSONArray bps)
		{
			bps = new JSONArray(this.bindMapStr) ;
			int len = bps.length() ;
			tag2JsonPathItem = new HashMap<>() ;
			for(int i = 0 ; i < len ; i ++)
			{
				JSONObject ob = bps.getJSONObject(i);
				String bindp = ob.optString("bindp") ;
				String tagp = ob.optString("tagp") ;
				if(Convert.isNotNullEmpty(bindp)&&Convert.isNotNullEmpty(tagp))
				{
					int k = bindp.indexOf(":") ;
					if(k<=0)
						continue ;
					String pp = bindp.substring(0,k) ;
					String vt = bindp.substring(k+1) ;
					JsonPath jp = JsonPath.compile(pp);
					PathItem<JsonPath> pi = new PathItem<>(pp,vt,jp) ;
					tag2JsonPathItem.put(tagp, pi);
				}
			}
			
			if(tag2JsonPathItem.size()<=0)
			{
				bindRunErr = "no valid bind setup" ;
			}
		}
		
		public List<String> listBindTagPathInCh()
		{
			if(this.tag2JsonPathItem==null||this.tag2JsonPathItem.size()<=0)
				return null;
			
			ArrayList<String> tags = new ArrayList<>() ;
			for(String tagp:this.tag2JsonPathItem.keySet())
			{
				tags.add(tagp) ;
			}
			
			Collections.sort(tags) ;
			return tags ;
		}
		

		
		protected boolean runBind(String topic,String json_xml_str)
		{
			if(ConnPtMSGNor.this.getSorTp()!=DataTp.json)
				return false;
				
			try
			{
				//run probe
				
					if(this.tag2JsonPathItem==null||this.tag2JsonPathItem.size()<=0)
						return false;
					
					 
					ObjectMapper mapper = new ObjectMapper ( );
			        HashMap<?,?> respjson = mapper.readValue(json_xml_str, HashMap.class );
			        
			        StringBuilder ressb = new StringBuilder() ;
			        HashMap<UATag,Object> tag2v = new HashMap<>() ;
			        
					for(Map.Entry<String, PathItem<JsonPath>> tag2jp:this.tag2JsonPathItem.entrySet())
					{
						String tagp = tag2jp.getKey() ;
						PathItem<JsonPath> pi = tag2jp.getValue() ;
						//System.out.println("ms="+System.currentTimeMillis()) ;
						try
						{
							Object v = pi.getProbeObj().read(respjson);
							
							ressb.append(pi.getPath()+" → "+tagp+"="+v+"\r\n") ;
							if(v==null)
								continue ;
	
							if(joinedCh!=null)
							{
								UATag t = joinedCh.getTagByPath(tagp) ;
								if(t==null)
								{
									continue ;
								}
								t.RT_setValRaw(v);
								tag2v.put(t, v) ;
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					
					//tag2v
					MNManager mgr = MNManager.getInstance(ConnPtMSGNor.this.getConnProvider().getBelongTo()) ;
					mgr.RT_CONN_triggerConnMsg(ConnPtMSGNor.this,json_xml_str,tag2v) ;
					
					bindRunRes = ressb.toString() ;
					return true ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return false;
			}
				
		}
		
	}
	
	public class BindHandlerXml extends BindHandler
	{

		private transient HashMap<String,PathItem<XPathExpression>> tag2XmlPathItem = null ;
		
		public BindHandlerXml(ConnPtMSGNor cpm)
		{
			super(cpm) ;
		}
		
		protected boolean initBind()
		{
			if(Convert.isNullOrEmpty(bindProbeStr))
			{
				bindRunErr = "no bind setup" ;
				return false;
			}
			
			try
			{
				JSONArray bps = new JSONArray(bindProbeStr) ;
				int len = bps.length() ;
				HashMap<String,String> prob_map = new HashMap<>() ;
				for(int i = 0 ; i < len ; i ++)
				{
					JSONObject ob = bps.getJSONObject(i);
					String path = ob.optString("path") ;
					String vt = ob.optString("vt") ;
					if(Convert.isNotNullEmpty(path)&&Convert.isNotNullEmpty(vt))
						prob_map.put(path, vt);
				}
				initBindXml(bps) ;
				
				return true;
			}
			catch(Exception e)
			{
				bindRunErr = "bind init err:"+e.getMessage() ;
				return false;
			}
		}
		
		private void initBindXml(JSONArray bps)
		{
			bps = new JSONArray(this.bindMapStr) ;
			int len = bps.length() ;
			tag2XmlPathItem = new HashMap<>() ;
			
			XPathFactory factory = XPathFactory.newInstance();
	        XPath xpath = factory.newXPath();
	            
			for(int i = 0 ; i < len ; i ++)
			{
				JSONObject ob = bps.getJSONObject(i);
				String bindp = ob.optString("bindp") ;
				String tagp = ob.optString("tagp") ;
				if(Convert.isNotNullEmpty(bindp)&&Convert.isNotNullEmpty(tagp))
				{
					int k = bindp.indexOf(":") ;
					if(k<=0)
						continue ;
					String pp = bindp.substring(0,k) ;
					String vt = bindp.substring(k+1) ;
					
					try
					{
						//JsonPath jp = JsonPath.compile(pp);
						XPathExpression xpe = xpath.compile(pp) ;
						PathItem<XPathExpression> pi = new PathItem<>(pp,vt,xpe) ;
						tag2XmlPathItem.put(tagp, pi);
					}
					catch(Exception e)
					{
						System.out.println("warn : xpath compile err="+pp) ;
						e.printStackTrace();
					}
				}
			}
			
			if(tag2XmlPathItem.size()<=0)
			{
				bindRunErr = "no valid bind setup" ;
			}
		}
		
		public List<String> listBindTagPathInCh()
		{
			if(this.tag2XmlPathItem==null||this.tag2XmlPathItem.size()<=0)
				return null;
			
			ArrayList<String> tags = new ArrayList<>() ;
			for(String tagp:this.tag2XmlPathItem.keySet())
			{
				tags.add(tagp) ;
			}
			
			Collections.sort(tags) ;
			return tags ;
		}
		
		protected boolean runBind(String topic,String txt)
		{
			//if(ConnPtMSG.this.getSorTp()!=DataTp.xml)
			//	return false;
			Document doc = XmlHelper.stringToDoc(txt) ;
			try
			{
					if(this.tag2XmlPathItem==null||this.tag2XmlPathItem.size()<=0)
						return false;
					
					UACh joinedch = this.connPtMsg.getJoinedCh() ;
					
			        StringBuilder ressb = new StringBuilder() ;
					for(Map.Entry<String, PathItem<XPathExpression>> tag2jp:this.tag2XmlPathItem.entrySet())
					{
						String tagp = tag2jp.getKey() ;
						PathItem<XPathExpression> pi = tag2jp.getValue() ;
						
						try
						{
							String strv = (String)pi.getProbeObj().evaluate(doc, XPathConstants.STRING) ;
							
							ressb.append(pi.getPath()+" → "+tagp+"="+strv+"\r\n") ;
							if(strv==null)
								continue ;
							
							if(joinedch!=null)
							{
								UATag t = joinedch.getTagByPath(tagp) ;
								if(t==null)
								{
									continue ;
								}
								t.RT_setValRawStr(strv, true, System.currentTimeMillis());
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					bindRunRes = ressb.toString() ;
					return true;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return false;
			}
		}
	}

	public static class MsgItem
	{
		String topic = null ;
		
		byte[] data = null ;
		
		private MsgItem(String topic,byte[] data)
		{
			this.topic = topic ;
			this.data = data ;
		}
		
		public String getTopic()
		{
			return this.topic ;
		}
		
		public byte[] getDataRaw()
		{
			return data ;
		}
		
		public String getDataTxt(String enc) throws UnsupportedEncodingException
		{
			return new String(data,enc) ;
		}
		
		public String getDataTxt() throws UnsupportedEncodingException
		{
			return getDataTxt("utf-8") ;
		}
	}
	
	private UACh joinedCh = null ;

	private BindHandler bindH = null;//new BindHandler() ;
	
	private TransHandler transH = new TransHandler() ;

	public DataTp getSorTp()
	{
		return this.sorTp;
	}
	
	public List<String> getMsgTopics()
	{
		return null ;
	}
	
	public HandleSty getHandleSty()
	{
		return this.handleSty ;
	}
	
	public BindHandler getBindHandler()
	{
		return bindH ;
	}
	
	public TransHandler getTransHandler()
	{
		return transH ;
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
		xd.setParamValue("handle", handleSty.toString());
		if(transH.initJS!=null)
			xd.setParamValue("init_js", transH.initJS);
		if (transH.transJS != null)
			xd.setParamValue("trans_js", transH.transJS);
		if(bindH!=null)
		{
			if(bindH.bindProbeStr!=null)
				xd.setParamValue("bind_probe", bindH.bindProbeStr);
			if(bindH.bindMapStr!=null)
				xd.setParamValue("bind_map", bindH.bindMapStr);
		}
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);

		String stp = xd.getParamValueStr("sor_tp", null);
		String hdv = xd.getParamValueStr("handle") ;
		if(Convert.isNotNullEmpty((hdv)))
			handleSty = HandleSty.valueOf(hdv) ;
		if(handleSty==null)	
			handleSty = HandleSty.js_trans;
			
		if (Convert.isNotNullEmpty(stp))
			sorTp = DataTp.valueOf(stp);
		if (sorTp == null)
			sorTp = DataTp.json;
		
		
		transH.initJS = xd.getParamValueStr("init_js");
		transH.transJS = xd.getParamValueStr("trans_js");
		
		switch(this.sorTp)
		{
		case json:
			bindH = new BindHandlerJson(this) ;
			break ;
		case xml:
			bindH = new BindHandlerXml(this) ;
			break ;
		case html:
			bindH=  new BindHandlerHtml(this) ;
			break ;
		default:
			break ;
		}
		
		if(bindH!=null)
		{
			bindH.bindProbeStr = xd.getParamValueStr("bind_probe") ;
			bindH.bindMapStr = xd.getParamValueStr("bind_map") ;
		}
		
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
		String hdv = jo.optString("handle") ;
		if (Convert.isNotNullEmpty(stp))
			sorTp = DataTp.valueOf(stp);
		if (sorTp == null)
			sorTp = DataTp.json;
		
		if(Convert.isNotNullEmpty((hdv)))
			handleSty = HandleSty.valueOf(hdv) ;
		if(handleSty==null)	
			handleSty = HandleSty.js_trans;
		//this.topics = tps;
		transH.initJS = jo.optString("init_js");
		transH.transJS = jo.optString("trans_js");
		this.encod = jo.optString("encod");
		
		switch(this.sorTp)
		{
		case json:
			bindH = new BindHandlerJson(this) ;
			break ;
		case xml:
			bindH = new BindHandlerXml(this) ;
			break ;
		case html:
			bindH=  new BindHandlerHtml(this) ;
			break ;
		default:
			break ;
		}
		
		bindH.bindProbeStr =  jo.optString("bind_probe") ;
		bindH.bindMapStr =  jo.optString("bind_map") ;
		
		clearCache();
	}
	
	public final boolean isRunAsBind()
	{
		if(this.handleSty!=HandleSty.bind)
			return false;
		
		return this.sorTp == DataTp.json || this.sorTp == DataTp.xml||this.sorTp==DataTp.html ;
	}

	private void clearCache()
	{
		transH.transInitOk = 0;// to known
	}

	
	
	
	protected void RT_connInit() throws Exception
	{
		joinedCh = getJoinedCh();
		
		if(isRunAsBind())
		{
			if(!bindH.initBind())
			{
				//bindRunErr
			}
		}
		else
		{
			transH.transInitOk = 0 ;
			transH.initTransJS();
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
		if(transH.transInitOk<0 && Convert.isNotNullEmpty(transH.transJS))
		{//show err msg
			 jsInitErrMsg.asDesc(transH.initErr);
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
	
	protected void onRecvedUrlHtml(String htmlstr) throws Exception
	{
		MonData[] mds = null ;
		if(handleSty==HandleSty.bind)
		{
			mds = new MonData[] {new MonData("sor",DataTp.html,htmlstr),null} ;
			if(bindH.runBind("", htmlstr))
			{
				mds[mds.length-1] = new MonData("result",DataTp.str,bindH.getBindRunRes()) ;
			}
			else
			{
				mds[mds.length-1] = new MonData("result",DataTp.str,"bind error:"+bindH.getBindRunErr()) ;
			}
		}
		
		if(mds!=null)
		{
			MonItem mis = new MonItem(true,"",mds) ;
			this.onMonDataRecv(mis);
		}
	}
	
	//transient private MsgItem lastMsgItem = null ;
	
	protected void onRecvedMsg(String topic, byte[] bs) throws Exception
	{
		super.RT_onMsgRecved(topic, bs);
		
		String str = null ;
		MonData[] mds = null ;
		boolean canbind = false;
		switch (this.sorTp)
		{
		case bytes:
			mds = new MonData[] {new MonData(MON_NAME_SOR,bs),null} ;
			return;
		case str:
			
			str = new String(bs, encod);
			mds = new MonData[] {new MonData(MON_NAME_SOR,DataTp.str,str),null} ;
			break;
		case xml:
			canbind = true ;
			//str = new String(bs, encod);
			
			if(handleSty==HandleSty.bind)
			{
				str=  new String(bs,encod) ;
				//Document doc = XmlHelper.byteArrayToDoc(bs) ;
				
				mds = new MonData[] {new MonData(MON_NAME_SOR,DataTp.xml,bs,this.encod),null} ;
				if(bindH.runBind(topic, str))
				{
					mds[mds.length-1] = new MonData(MON_NAME_RESULT,DataTp.str,bindH.getBindRunRes()) ;
				}
				else
				{
					mds[mds.length-1] = new MonData(MON_NAME_RESULT,DataTp.str,"bind error:"+bindH.getBindRunErr()) ;
				}
			}
			else
			{
				Element ele = XmlHelper.byteArrayToElement(bs) ;
				str = XmlHelper.transElement2JSONStr(ele,true,true,false) ;
				mds = new MonData[] {new MonData(MON_NAME_SOR,DataTp.xml,bs,this.encod),new MonData("xml-json",DataTp.json,str),null} ;
				if(transH.runTransJS(topic, str))
				{
					mds[mds.length-1] = new MonData(MON_NAME_RESULT,DataTp.json,transH.getTransRunRes()) ;
				}
				else
				{
					mds[mds.length-1] = new MonData(MON_NAME_RESULT,DataTp.str,"transfer error:"+transH.getTransRunErr()) ;
				}
			}
			break;
		case html:
			if(handleSty==HandleSty.bind)
			{
				str=  new String(bs,encod) ;
				//Document doc = XmlHelper.byteArrayToDoc(bs) ;
				
				mds = new MonData[] {new MonData(MON_NAME_SOR,DataTp.xml,bs,this.encod),null} ;
				if(bindH.runBind(topic, str))
				{
					mds[mds.length-1] = new MonData(MON_NAME_RESULT,DataTp.str,bindH.getBindRunRes()) ;
				}
				else
				{
					mds[mds.length-1] = new MonData(MON_NAME_RESULT,DataTp.str,"bind error:"+bindH.getBindRunErr()) ;
				}
			}
			break ;
		case json:
		default:
			canbind = true ;
			str = new String(bs, encod);
			mds = new MonData[] {new MonData(MON_NAME_SOR,DataTp.json,str),null} ;
			
			if(handleSty==HandleSty.bind)
			{
				if(bindH.runBind(topic, str))
				{
					mds[mds.length-1] = new MonData(MON_NAME_RESULT,DataTp.str,bindH.getBindRunRes()) ;
				}
				else
				{
					mds[mds.length-1] = new MonData(MON_NAME_RESULT,DataTp.str,"bind error:"+bindH.getBindRunErr()) ;
				}
			}
			else
			{
				if(transH.runTransJS(topic, str))
				{
					mds[mds.length-1] = new MonData(MON_NAME_RESULT,DataTp.json,transH.getTransRunRes()) ;
				}
				else
				{
					mds[mds.length-1] = new MonData(MON_NAME_RESULT,DataTp.str,"transfer error:"+transH.getTransRunErr()) ;
				}
			}
			
			break;
		}
		
		MonItem mis = new MonItem(true,topic,mds) ;
		this.onMonDataRecv(mis);
	}

	public abstract boolean sendMsg(String topic,byte[] bs) throws Exception ;
	
	public abstract void runOnWrite(UATag tag,Object val) throws Exception;
	
	@Override
	public boolean RT_supportSendMsgOut()
	{
		return true;
	}
	
	@Override
	public boolean RT_sendMsgOut(String topic,byte[] msg,StringBuilder failedr) throws Exception
	{
		return sendMsg(topic,msg) ;
	}
	/**
	 * judge msg is received in passive mode,if true {@see readMsgToFile}will not to be used
	 * {@see getMsgTxtFromTmpBuf}will not to be used.
	 * 
	 *  instead,{@see getLastMsgFromMon} will be used.
	 * 
	 * @return
	 */
	public abstract boolean isPassiveRecv() ; 
	
	protected abstract boolean readMsgToFile(File f) throws Exception ;
	
	public MonData getLastMsgFromMon()
	{
		MonItem mi = getMonItemLast();
		if(mi==null)
			return null ;
		MonData[] mds = mi.getMonDatas() ;
		if(mds==null || mds.length<=0)
			return null ;
		 return mds[mds.length-1] ;
	}
	
	public File getTmpBufFile()
	{
		return new File(Config.getDataTmpDir()+"connpt_msg/"+this.getId()+"."+this.getSorTp()) ;
	}
	
	public File readMsgToTmpBuf() throws Exception
	{
		File f = getTmpBufFile() ;
		if(!readMsgToFile(f))
			return null ;
		return f ;
	}
	
	public String getMsgTxtFromTmpBuf() throws IOException
	{
		byte[] bs = getMsgBSFromTmpBuf();
		if(bs==null)
			return null ;
		
		return new String(bs,"UTF-8") ;
	}
	
	public Date getMsgTmpBufLastDT()
	{
		File f = getTmpBufFile() ;
		if(!f.exists())
			return null ;
		
		return new Date(f.lastModified()) ;
	}
	
	public byte[] getMsgBSFromTmpBuf() throws IOException
	{
		File f = getTmpBufFile();
		if(!f.exists())
			return null ;
		
		return Convert.readFileBuf(f) ;
	}

	@Override
	public void clearBindBeSelectedCache()
	{
		
	}

	@Override
	public List<BindItem> getBindBeSelectedItems() throws Exception
	{
		return Arrays.asList(new BindItem("$.xx.xx[1].xy@a=2","$.xx.xx[1].xy@a=2","int32"));
	}

	@Override
	public void writeBindBeSelectedTreeJson(Writer w, boolean list_tags_only, boolean force_refresh) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	public void RT_writeValByBind(String tagpath, String strv)
	{
		// TODO
	}

}
