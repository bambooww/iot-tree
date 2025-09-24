package org.iottree.core.plugin.inner;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsDef;
import org.iottree.core.util.Convert;

public class HttpURL extends JSObMap
{
	String url = null;

	private HashMap<String, String> reqHeaders = new HashMap<>();

	private HashMap<String, String> postPMS = null;

	@JsDef
	public HttpURL asURL(String url)
	{
		this.url = url;
		return this;
	}

	@JsDef
	public void setRequestHeader(String n, String v)
	{
		reqHeaders.put(n, v);
	}

	@JsDef
	public void setContentTypeJson()
	{
		reqHeaders.put("Content-Type", "application/json");
	}
	
	@JsDef
	public void setContentTypeXml()
	{
		reqHeaders.put("Content-Type", "application/xml");
	}
	
	@JsDef
	public void setContentTypeFormUrlEncoded()
	{
		reqHeaders.put("Content-Type", "application/x-www-form-urlencoded");
	}
	
	@JsDef
	public void setPostParam(String n,String v)
	{
		if(postPMS==null)
			postPMS = new HashMap<>() ;
		postPMS.put(n, v) ;
	}

	@JsDef
	public String doGet() throws Exception
	{
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpGet git = new HttpGet(this.url);
		// String result = "";
		try (CloseableHttpClient chc = httpClientBuilder.build())
		{
			if (reqHeaders.size() > 0)
			{
				for (Map.Entry<String, String> n2v : reqHeaders.entrySet())
				{
					git.setHeader(n2v.getKey(), n2v.getValue());
				}
			}
			try (CloseableHttpResponse resp = chc.execute(git))
			{
				//HttpResponse resp = chc.execute(git);
				InputStream respIs = resp.getEntity().getContent();
				byte[] bs = IOUtils.toByteArray(respIs);
				return new String(bs, "UTF-8");
			}
		}
	}
	
//	@JsDef
//	public String doPostParams() throws Exception
//	{
//		return doPost() ;
//	}

	@JsDef
	public String doPost() throws Exception
	{
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpPost git = new HttpPost(this.url);
		try (CloseableHttpClient chc = httpClientBuilder.build())
		{
			if (reqHeaders.size() > 0)
			{
				for (Map.Entry<String, String> n2v : reqHeaders.entrySet())
				{
					git.setHeader(n2v.getKey(), n2v.getValue());
				}
			}

			if (postPMS != null && postPMS.size() > 0)
			{
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (Iterator<String> iter = postPMS.keySet().iterator(); iter.hasNext();)
				{
					String key = (String) iter.next();
					String value = String.valueOf(postPMS.get(key));
					nvps.add(new BasicNameValuePair(key, value));
				}
				git.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
			}

			try (CloseableHttpResponse resp = chc.execute(git))
			{
				//HttpResponse resp = chc.execute(git);
				InputStream respIs = resp.getEntity().getContent();
				byte[] bs = IOUtils.toByteArray(respIs);
				return new String(bs, "UTF-8");
			}
		}
	}
	
	@JsDef
	public String doPostRaw(String post_txt) throws Exception
	{
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpPost git = new HttpPost(this.url);
		try (CloseableHttpClient chc = httpClientBuilder.build())
		{
			if (reqHeaders.size() > 0)
			{
				for (Map.Entry<String, String> n2v : reqHeaders.entrySet())
				{
					git.setHeader(n2v.getKey(), n2v.getValue());
				}
			}

			git.setEntity(new StringEntity(post_txt));
			try (CloseableHttpResponse resp = chc.execute(git))
			{
				//HttpResponse resp = chc.execute(git);
				InputStream respIs = resp.getEntity().getContent();
				byte[] bs = IOUtils.toByteArray(respIs);
				return new String(bs, "UTF-8");
			}
		}
	}

}
