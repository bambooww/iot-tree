package org.iottree.core.conn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.iottree.core.ConnDev;
import org.iottree.core.ConnProvider;
import org.iottree.core.UATag;
import org.iottree.core.conn.html.HtmlParser;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

/**
 * based IOTTree Web container,it proivder http server url for msg data receiving
 * 
 * this conn can recv http client post data (msg).
 *
 * the URL for client will be http://xxx.xx.xx:port/[prjname]/_conn/[connpt_name]
 * 
 * @author jason.zhu
 */
public class ConnPtHTTPSer  extends ConnPtMSGNor
{
	static ILogger log = LoggerManager.getLogger(ConnPtHTTPSer.class);
	
	String respOk = null ;
	
	String respErr = null ;
	
	String limit_ip = null ;
	
	String auth_head = null ;
	
	String auth_val = null ;

	@Override
	public String getConnType()
	{
		return "http_ser";
	}

	@Override
	public String getStaticTxt()
	{
		return null;
	}
	
	@Override
	public boolean isPassiveRecv() 
	{
		return true;
	}

	public String getRespOk()
	{
		if(this.respOk==null)
			return "" ;
		return this.respOk ;
	}
	
	public String getRespErr()
	{
		if(this.respErr==null)
			return "" ;
		return this.respErr ;
	}
	
	public String getLimitIP()
	{
		if(this.limit_ip==null)
			return "" ;
		return this.limit_ip ;
	}
	
	public String getAuthHead()
	{
		if(this.auth_head==null)
			return "" ;
		return this.auth_head ;
	}
	
	public String getAuthVal()
	{
		if(this.auth_val==null)
			return "" ;
		return this.auth_val ;
	}
	
	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		xd.setParamValue("resp_ok", this.respOk);
		xd.setParamValue("resp_err", this.respErr);
		xd.setParamValue("limit_ip", this.limit_ip);
		xd.setParamValue("auth_head", this.auth_head);
		xd.setParamValue("auth_val", this.auth_val);
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);
		this.respOk = xd.getParamValueStr("resp_ok") ;
		this.respErr = xd.getParamValueStr("resp_err") ;
		this.limit_ip = xd.getParamValueStr("limit_ip") ;
		this.auth_head = xd.getParamValueStr("auth_head") ;
		this.auth_val = xd.getParamValueStr("auth_val") ;
		return r;
	}

	private String optJSONString(JSONObject jo, String name, String defv)
	{
		String r = jo.optString(name);
		if (r == null)
			return defv;
		return r;
	}

	private long optJSONInt64(JSONObject jo, String name, long defv)
	{
		Object v = jo.opt(name);
		if (v == null)
			return defv;
		return jo.optLong(name);
	}

	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);

		// this.appName =optJSONString(jo,"opc_app_name",getOpcAppNameDef()) ;
		this.respOk = jo.optString("resp_ok") ;
		this.respErr = jo.optString("resp_err") ;
		this.limit_ip = jo.optString("limit_ip") ;
		this.auth_head = jo.optString("auth_head") ;
		this.auth_val = jo.optString("auth_val") ;
	}

	//private boolean bConnOk = false;

	@Override
	public boolean isConnReady()
	{
		ConnProvider cp = this.getConnProvider();
		if (cp == null)
			return false;
		if (!cp.isRunning())
			return false;
		return true;//bConnOk;
	}
	
	

	public String getConnErrInfo()
	{
		return null;
	}


	@Override
	public LinkedHashMap<String, ConnDev> getFoundConnDevs()
	{
		return null;
	}
	
	
	public void RT_checkConn() 
	{
		
	}

	transient private long lastChkDT = -1;

	/**
	 * called by web request in web container
	 * 
	 * @param topic
	 * @param bs
	 * @throws Exception
	 */
	public String onRecvedFromConn(String topic,byte[] bs) //throws Exception
	{
		try
		{
			this.onRecvedMsg(topic, bs);
			return this.respOk ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return this.respErr ;
		}
	}
	/**
	 * will be run interval in loop
	 */
	void checkUrl()
	{
//		if (System.currentTimeMillis() - lastChkDT < this.intervalMS)
//			return;

//		try
//		{
//			if(!runJsPage)
//			{
//				byte[] res = null;
//				if("POST".equals(this.method))
//					res = postData(this.getUrl(),postTxt, null);
//				else
//					res = getData(this.getUrl(), "");
//				this.onRecvedMsg("", res);
//			}
//			else
//			{
//				String str = HtmlParser.getRunJsPageXml(this.getUrl(),this.runJsTO) ;
//				this.onRecvedUrlHtml(str);
//			}
//			bConnOk = true;
//		}
//		catch ( Exception e)
//		{
//			if (log.isDebugEnabled())
//			{
//				log.debug("", e);
//			}
//			// e.printStackTrace();
//			bConnOk = false;
//		}
//		finally
//		{
//			lastChkDT = System.currentTimeMillis();
//		}
	}
	// @Override
	// public List<String> getMsgTopics()
	// {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public boolean sendMsg(String topic, byte[] bs) throws Exception
	{
		return false;
	}

	@Override
	public void runOnWrite(UATag tag, Object val) throws Exception
	{

	}
	@Override
	protected boolean readMsgToFile(File f) throws Exception
	{
		return false;
	}

}
