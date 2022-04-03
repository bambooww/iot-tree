package org.iottree.core.conn;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.iottree.core.ConnDev;
import org.iottree.core.ConnPt;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

public class ConnPtHTTP extends ConnPtMSG
{
	String url = null ;
	
	String method = "GET" ;
	
	HashMap<String,String> params = null ;
	
	long intervalMS = 5000 ;
	
	
	@Override
	public String getConnType()
	{
		return "http";
	}
	
	public String getUrl()
	{
		if(url==null)
			return "" ;
		return url ;
	}
	
	public String getMethod()
	{
		return this.method;
	}
	
	public long getIntervalMS()
	{
		return this.intervalMS;
	}

	
	@Override
	public String getStaticTxt()
	{
		return null;
	}
	

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		xd.setParamValue("url", this.url);
		xd.setParamValue("method", this.method);
		xd.setParamValue("int_ms", intervalMS);
		if(params!=null&&params.size()>0)
		{
			XmlData tmpxd = xd.getOrCreateSubDataSingle("params") ;
			for(Map.Entry<String, String> n2v:params.entrySet())
			{
				tmpxd.setParamValue(n2v.getKey(), n2v.getValue());
			}
		}
			
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);

		// this.appName = xd.getParamValueStr("opc_app_name",
		// "iottree_opc_client_"+this.getName());
		this.url = xd.getParamValueStr("url", "");
		this.method = xd.getParamValueStr("method", "");
		this.intervalMS = xd.getParamValueInt64("int_ms", 5000) ;
		XmlData tmpxd = xd.getSubDataSingle("params") ;
		if(tmpxd!=null)
		{
			params = tmpxd.toNameStrValMap(); 
		}
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
		this.url = optJSONString(jo, "url", "");
		this.method = optJSONString(jo, "method", "GET");
		this.intervalMS = optJSONInt64(jo,"int_ms", 5000) ;
	}

	@Override
	public boolean isConnReady()
	{
		return false;
	}
	
	public String getConnErrInfo()
	{
		return null ;
	}

	public static String postData(String url, String post_txt,String token) throws Exception
	{
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpPost post = new HttpPost(url);
		String result = "";
		try (CloseableHttpClient chc = httpClientBuilder.build())
		{
			HttpEntity entity = new StringEntity(post_txt, "UTF-8");
			post.setEntity(entity);
			post.setHeader("Content-type", "application/json");
			if(Convert.isNotNullEmpty(token))
				post.setHeader("token",token);
			HttpResponse resp = chc.execute(post);
			InputStream respIs = resp.getEntity().getContent();
			byte[] rbs = IOUtils.toByteArray(respIs);
			result = new String(rbs, "UTF-8");
			return result;
		}
	}

	@Override
	public LinkedHashMap<String, ConnDev> getFoundConnDevs()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
