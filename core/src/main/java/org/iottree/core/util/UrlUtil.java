package org.iottree.core.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONObject;


public class UrlUtil
{
	public static String doGetToStr(String url,String encoding) throws IOException
	{
		return doGetToStr(url,encoding,null,null) ;
	}
	
	public static String doGetToStr(String url,String encoding,Integer conn_timeout,Integer read_timeout) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
		doGetToOut(url,baos,conn_timeout,read_timeout) ;
		if(Convert.isNullOrEmpty(encoding))
			encoding = "UTF-8" ;
		return baos.toString(encoding) ;
	}
	
	public static void doGetToOut(String url,OutputStream outputs) throws IOException
	{
		doGetToOut(url,outputs,null,null) ; 
	}
	
	public static void doGetToOut(String url,OutputStream outputs,Integer conn_timeout,Integer read_timeout) throws IOException
	{
		URL u = new URL(url);
		HttpURLConnection http_conn = null;

		try
		{
			http_conn = (HttpURLConnection) u.openConnection();
			
			if(conn_timeout!=null)
				http_conn.setConnectTimeout(conn_timeout);
			if(read_timeout!=null)
				http_conn.setReadTimeout(read_timeout);
			
			http_conn.setRequestMethod("GET");
			
			int responseCode = http_conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK)
			{
				//save to tmp
				try (InputStream inputs = http_conn.getInputStream();
						BufferedInputStream bufferedis = new BufferedInputStream(inputs);)
				{
					byte[] buffer = new byte[4096];
					int readlen;

					while ((readlen = bufferedis.read(buffer)) != -1)
					{
						outputs.write(buffer, 0, readlen);
					}
				}
			}
			else
			{
				throw new IOException("getUrlToOut error with HTTP code: " + responseCode);
			}
		}
		finally
		{
			if (http_conn != null)
				http_conn.disconnect();
		}
	}
	
	public static void doPostToOut(String url, Map<String,String> req_heads,Map<String,String> post_pm,OutputStream outputs)
			throws IOException
	{
		doPostToOut( url,req_heads, post_pm,outputs,null,null) ;
	}
	
	public static void doPostToOut(String url, Map<String,String> req_heads,Map<String,String> post_pm,OutputStream outputs
			,Integer conn_timeout,Integer read_timeout)
			throws IOException
	{
		String urlpm = "" ;
		if(post_pm!=null)
		{
			StringBuilder sb = new StringBuilder() ;
			boolean bfirst=true;
			for(Map.Entry<String, String> n2v:post_pm.entrySet())
			{
				if(bfirst) bfirst=false;
				else sb.append("&") ;
				sb.append(n2v.getKey()).append("=").append(URLEncoder.encode(n2v.getValue(),"UTF-8")) ;
			}
			urlpm = sb.toString() ;
		}
		
		doPostToOut(url, req_heads,"application/x-www-form-urlencoded",urlpm,outputs
				, conn_timeout, read_timeout) ;
		
	}
	
	
	public static String doPostJsonToOut(String url, Map<String,String> req_heads,String json_str
			,Integer conn_timeout,Integer read_timeout)
			throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
		doPostToOut(url, req_heads,"application/json; charset=utf-8",json_str.toString(),baos,conn_timeout,read_timeout);
		return new String(baos.toByteArray()) ;
	}
	
	
	public static void doPostToOut(String url, Map<String,String> req_heads,String content_tp,String post_txt,OutputStream outputs
			,Integer conn_timeout,Integer read_timeout)
			throws IOException
	{
		URL u = new URL(url);
		HttpURLConnection http_conn = null;

		try
		{
			http_conn = (HttpURLConnection) u.openConnection();
			if(conn_timeout!=null)
				http_conn.setConnectTimeout(conn_timeout);
			if(read_timeout!=null)
				http_conn.setReadTimeout(read_timeout);

			http_conn.setRequestMethod("POST");
			http_conn.setRequestProperty("Content-Type", content_tp);
			byte[] post_bs = post_txt.getBytes("UTF-8") ;
			http_conn.setRequestProperty("Content-Length", String.valueOf(post_bs.length));
			http_conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			if(req_heads!=null)
			{
				for(Map.Entry<String, String> n2v:req_heads.entrySet())
				{
					http_conn.setRequestProperty(n2v.getKey(), n2v.getValue());
				}
			}

			if(post_bs.length>0)
			{
				http_conn.setDoOutput(true);
				OutputStream outs = http_conn.getOutputStream();
				outs.write(post_bs);
				outs.flush();
			}
			
			int responseCode = http_conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK)
			{
				//save to tmp
				try (InputStream inputs = http_conn.getInputStream();
						BufferedInputStream bufferedis = new BufferedInputStream(inputs);)
				{
					byte[] buffer = new byte[4096];
					int readlen;

					while ((readlen = bufferedis.read(buffer)) != -1)
					{
						outputs.write(buffer, 0, readlen);
					}
				}
			}
			else
			{
				//http_conn.
				throw new IOException("doPostToOut error with HTTP code: " + responseCode);
			}
		}
		finally
		{
			if (http_conn != null)
				http_conn.disconnect();
		}
	}
}
