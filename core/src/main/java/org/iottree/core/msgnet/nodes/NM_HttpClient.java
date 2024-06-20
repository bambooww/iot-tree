package org.iottree.core.msgnet.nodes;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class NM_HttpClient extends MNNodeMid
{
	public static enum Method
	{
		GET,POST,PUT,DELETE
	}
	
	public static enum RespFmt
	{
		utf8string,json;
		
		
	}
	
	Method method = Method.GET ;
	
	MNCxtValSty urlSty = MNCxtValSty.vt_str; //FOR_STR_LIST
	String urlSubN = null ;
	
	RespFmt respFmt = RespFmt.utf8string ;
	
	HashMap<String,String> heads = new HashMap<>() ;
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "http_client";
	}

	@Override
	public String getTPTitle()
	{
		return "todo "+g("http_client");
	}

	@Override
	public String getColor()
	{
		return "#e8e7af";
	}

	@Override
	public String getIcon()
	{
		return "PK_whiteg";
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
		jo.put("method", this.method.name()) ;
		jo.put("url_sty", this.urlSty.name()) ;
		jo.putOpt("url_subn", this.urlSubN) ;
		jo.put("resp_fmt", respFmt.name()) ;
		jo.put("heads", new JSONObject(heads)) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.method = Method.valueOf(jo.optString("method",Method.GET.name())) ;
		this.urlSty = MNCxtValSty.valueOf(jo.optString("url_sty",MNCxtValSty.vt_str.name())) ;
		this.urlSubN = jo.optString("url_subn","") ;
		this.respFmt = RespFmt.valueOf(jo.optString("resp_fmt",RespFmt.utf8string.name()));
		JSONObject tmpjo = jo.optJSONObject("heads") ;
		if(tmpjo!=null)
		{
			HashMap<String,String> hds = new HashMap<>() ;
			for(String n:tmpjo.keySet())
			{
				String v = tmpjo.getString(n) ;
				hds.put(n,v) ;
			}
			this.heads=hds ;
		}
	}
	
	// ---- rt
	
	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		Object urlob = urlSty.RT_getValInCxt(urlSubN,this.getBelongTo(), this, msg) ;
		if(urlob==null || !(urlob instanceof String))
		{
			this.RT_DEBUG_ERR.fire("httpc", "no or invalid url");
			return null ;
		}
		
		String url = (String)urlob ;
		
		byte[] bs = null ;
		
		switch(method)
		{
		case GET:
			bs = this.getData(url) ;
			break ;
		case POST:
		case PUT:
		case DELETE:
		default:
			return null ;
		}
		
		if(bs==null)
			return null ;
		
		String ss = new String(bs,"UTF-8") ;
		Object pld = null ;
		switch(respFmt)
		{
		case json:
			pld = MNMsg.transStrJoJArr(ss) ;
			if(pld instanceof String)
			{
				this.RT_DEBUG_ERR.fire("httpc", "not json format response");
				return null ;
			}
			break ;
		default:
			pld = new String(bs,"UTF-8") ;
			break ;
		}
		
		this.RT_DEBUG_ERR.clear("httpc");
		MNMsg m = new MNMsg().asPayload(pld) ;
		return RTOut.createOutIdx().asIdxMsg(0, m) ;
	}
	
	
	

	private byte[] getData(String url) throws Exception
	{
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpGet git = new HttpGet(url);
		//String result = "";
		try (CloseableHttpClient chc = httpClientBuilder.build())
		{
			for(Map.Entry<String, String> n2v:this.heads.entrySet())
			{
				git.setHeader(n2v.getKey(), n2v.getValue());
			}
			HttpResponse resp = chc.execute(git);
			InputStream respIs = resp.getEntity().getContent();
			return IOUtils.toByteArray(respIs);
		}
	}


	private byte[] postData(String url, String post_txt, String token) throws Exception
	{
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpPost post = new HttpPost(url);
		String result = "";
		try (CloseableHttpClient chc = httpClientBuilder.build())
		{
			if(Convert.isNotNullEmpty(post_txt))
			{
				HttpEntity entity = new StringEntity(post_txt, "UTF-8");
				post.setEntity(entity);
			}
			for(Map.Entry<String, String> n2v:this.heads.entrySet())
			{
				post.setHeader(n2v.getKey(), n2v.getValue());
			}
			
			if (Convert.isNotNullEmpty(token))
				post.setHeader("token", token);
			HttpResponse resp = chc.execute(post);
			InputStream respIs = resp.getEntity().getContent();
			return IOUtils.toByteArray(respIs);
		}
	}
}
