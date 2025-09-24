package org.iottree.core.msgnet.nodes;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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
		GET,POST
		//,PUT,DELETE
	}
	
	public static enum RespFmt
	{
		utf8string,json;
	}
	
	Method method = Method.POST ;
	
	MNCxtValSty urlSty = MNCxtValSty.vt_str; //FOR_STR_LIST
	String urlSubN = null ;
	
	RespFmt respFmt = RespFmt.utf8string ;
	
	String headTxt = null ;
	
	String postBody = null ;
	
	@Override
	public int getOutNum()
	{
		return 2;
	}
	
	@Override
	public String getOutColor(int idx)
	{
		if(idx==0)
			return null;
		if(idx==1)
			return "red";
		return null ;
	}
	

	@Override
	public String RT_getOutTitle(int idx)
	{
		if(idx==0)
			return null;
		if(idx==1)
			return "HTTP Response Error";
		return null ;
	}
	
	@Override
	public String getTP()
	{
		return "http_client";
	}

	@Override
	public String getTPTitle()
	{
		return g("http_client");
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
	
	public Map<String,String> getHeadMap()
	{
		return Convert.transPropStrToMap(this.headTxt) ;
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
		jo.putOpt("head_txt", headTxt) ;
		jo.putOpt("post_body", this.postBody) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.method = Method.valueOf(jo.optString("method",Method.GET.name())) ;
		this.urlSty = MNCxtValSty.valueOf(jo.optString("url_sty",MNCxtValSty.vt_str.name())) ;
		this.urlSubN = jo.optString("url_subn","") ;
		this.respFmt = RespFmt.valueOf(jo.optString("resp_fmt",RespFmt.utf8string.name()));
		this.headTxt = jo.optString("head_txt") ;
		this.postBody = jo.optString("post_body","") ;
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
		
		String ret = null ;
		JSONObject failed_jo = new JSONObject() ;
		
		switch(method)
		{
		case GET:
			ret = this.getData(url,failed_jo) ;
			break ;
		case POST:
			ret = postData(url, this.postBody,failed_jo) ;
			break ;
		//case PUT:
		//case DELETE:
		default:
			return null ;
		}
		
		if(ret==null)
		{// send error
			MNMsg m = new MNMsg().asPayload(failed_jo) ;
			return RTOut.createOutIdx().asIdxMsg(1, m) ;
		}
		
		//String ss = new String(bs,"UTF-8") ;
		Object pld = ret ;
		switch(respFmt)
		{
		case json:
			pld = MNMsg.transStrJoJArr(ret) ;
			if(pld instanceof String)
			{
				this.RT_DEBUG_ERR.fire("httpc", "not json format response");
				MNMsg m = new MNMsg().asPayload(failed_jo) ;
				return RTOut.createOutIdx().asIdxMsg(1, m) ;
			}
			break ;
		default:
			//pld = new String(bs,"UTF-8") ;
			break ;
		}
		
		this.RT_DEBUG_ERR.clear("httpc");
		MNMsg m = new MNMsg().asPayload(pld) ;
		return RTOut.createOutIdx().asIdxMsg(0, m) ;
	}
	
	
	

	private String getData(String url,JSONObject failed_jo) throws Exception
	{
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpGet git = new HttpGet(url);
		//String result = "";
		try (CloseableHttpClient chc = httpClientBuilder.build())
		{
			Map<String,String> heads = getHeadMap() ;
			if(heads!=null)
			{
				for(Map.Entry<String, String> n2v:heads.entrySet())
				{
					git.setHeader(n2v.getKey(), n2v.getValue());
				}
			}
			
			try (CloseableHttpResponse response = chc.execute(git))
			{
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
               
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                EntityUtils.consume(entity);

                if (statusCode >= 200 && statusCode < 300)
                {//succ
                    return responseBody;
                } else
                {// err
                	failed_jo.put("code",statusCode);
                    failed_jo.put("reason",statusLine.getReasonPhrase());
                    failed_jo.putOpt("resp_body",responseBody);
                    return null;
                }
            }
		    
//			HttpResponse resp = chc.execute(git);
//			
//			InputStream respIs = resp.getEntity().getContent();
//			return IOUtils.toByteArray(respIs);
		}
	}


	private String postData(String url, String post_txt,JSONObject failed_jo) throws Exception
	{
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpPost post = new HttpPost(url);
		//String result = "";
		try (CloseableHttpClient chc = httpClientBuilder.build())
		{
			if(Convert.isNotNullEmpty(post_txt))
			{
				HttpEntity entity = new StringEntity(post_txt, "UTF-8");
				post.setEntity(entity);
			}
			Map<String,String> heads = getHeadMap() ;
			if(heads!=null)
			{
				for(Map.Entry<String, String> n2v:heads.entrySet())
				{
					post.setHeader(n2v.getKey(), n2v.getValue());
				}
			}
			
			try (CloseableHttpResponse response = chc.execute(post))
			{
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
               
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                EntityUtils.consume(entity);

                if (statusCode >= 200 && statusCode < 300)
                {//succ
                    return responseBody;
                } else
                {// err
                	failed_jo.put("code",statusCode);
                    failed_jo.put("reason",statusLine.getReasonPhrase());
                    failed_jo.putOpt("resp_body",responseBody);
                    return null;
                }
            }
			
//			//if (Convert.isNotNullEmpty(token))
//			//	post.setHeader("token", token);
//			HttpResponse resp = chc.execute(post);
//			InputStream respIs = resp.getEntity().getContent();
//			return IOUtils.toByteArray(respIs);
		}
	}
}
