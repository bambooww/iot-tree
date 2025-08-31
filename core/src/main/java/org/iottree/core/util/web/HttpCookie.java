package org.iottree.core.util.web;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iottree.core.util.Convert;

public class HttpCookie
{
	/**
	 * 根据字符串映射值，用类似url参数的方式处理成一个字符串
	 * @param vm
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private static String valMap2StrVal(HashMap<String,String> vm) throws UnsupportedEncodingException
	{
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, String> nv:vm.entrySet())
		{
			sb.append(nv.getKey())
				.append('=')
				.append(java.net.URLEncoder.encode(nv.getValue(),"UTF-8"))
				.append('&');
		}
		return sb.toString() ;
	}
	
	private static HashMap<String,String> strVal2ValMap(String strval) throws UnsupportedEncodingException
	{
		HashMap<String,String> ret = new HashMap<String,String>() ;
		if(strval==null||(strval=strval.trim()).equals(""))
			return ret ;
		
		StringTokenizer st = new StringTokenizer(strval,"&");
		while(st.hasMoreTokens())
		{
			String nt = st.nextToken() ;
			int p = nt.indexOf('=');
			if(p<0)
			{
				ret.put(nt, "");
			}
			else
			{
				String nv = nt.substring(p+1);
				nv = java.net.URLDecoder.decode(nv, "UTF-8");
				ret.put(nt.substring(0,p), nv);
			}
		}
		return ret ;
	}
	
	private String name = null ;
	private String domain = null ;
	private String path = null ;
	private int maxAge = -1 ;
	private HashMap<String,String> valMap = null ;
	
	public HttpCookie(String n)
	{
		if(n==null||n.equals(""))
			throw new IllegalArgumentException("cookie name cannot be null!");
		valMap = new HashMap<String,String>() ;
		name = n ;
	}
	
	public HttpCookie(String n,HashMap<String,String> valm)
	{
		if(n==null||n.equals(""))
			throw new IllegalArgumentException("cookie name cannot be null!");
		
		name = n ;
		valMap = valm ;
		if(valMap==null)
			valMap = new HashMap<String,String>() ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getDomain()
	{
		return domain ;
	}
	
	public void setDomain(String dm)
	{
		domain = dm ;
	}
	
	public void setValue(String vn,String vv)
	{
		valMap.put(vn, vv);
	}
	
	public String getValue(String vn)
	{
		return valMap.get(vn);
	}
	
	public HashMap<String,String> getValues()
	{
		return valMap ;
	}
	
	public String getValuesStr()
	{
		if(valMap==null)
			return "" ;
		String ret = "" ;
		for(Map.Entry<String, String> n2v:valMap.entrySet())
		{
			if(ret.length()>0) ret += "&";
			ret+= n2v.getKey()+"="+n2v.getValue() ;
		}
		return ret ;
	}
	
	public String getPath()
	{
		return path ;
	}
	
	public void setPath(String p)
	{
		path = p ;
	}
	
	public int getMaxAge()
	{
		return maxAge ;
	}
	
	public void setMaxAge(int ma)
	{
		maxAge = ma ;
	}
	
	public Cookie toJavaCookie() throws UnsupportedEncodingException
	{
		Cookie jc = new Cookie(name,valMap2StrVal(valMap));
		if(domain!=null)
			jc.setDomain(domain);
		if(path!=null)
			jc.setPath(path);
		if(maxAge>=0)
			jc.setMaxAge(maxAge);
		return jc ;
	}
	
	public static HttpCookie fromJavaCookie(Cookie jc) throws UnsupportedEncodingException
	{
		HttpCookie hc = new HttpCookie(jc.getName()) ;
		hc.valMap = strVal2ValMap(jc.getValue()) ;
		hc.domain = jc.getDomain() ;
		hc.maxAge = jc.getMaxAge();
		hc.path = jc.getPath() ;
		
		return hc ;
	}
	
	public static HashMap<String,HttpCookie> fromCookieStr(String cookiestr)
	{
		if(Convert.isNullOrEmpty(cookiestr))
			return null ;
		
		HashMap<String,HttpCookie> ret = new HashMap<String,HttpCookie>() ;
		//HashMap<String,HttpCookie> n2c = new HashMap<String,HttpCookie>() ;
		StringTokenizer st = new StringTokenizer(cookiestr,";") ;
		while(st.hasMoreTokens())
		{
			String s = st.nextToken().trim() ;
			if(s.equals(""))
				continue ;
			
			int i = s.indexOf('=') ;
			if(i<=0)
				continue ;
			String n = s.substring(0,i) ;
			String vv = s.substring(i+1) ;
			
			HashMap<String,String> tmpnv = new HashMap<String,String>() ;
			StringTokenizer st0 = new StringTokenizer(vv,"&") ;
			while(st0.hasMoreTokens())
			{
				String ss = st0.nextToken() ;
				int k = ss.indexOf('=') ;
				String n0 = ss ;
				String v0="" ;
				if(k>0)
				{
					n0 = ss.substring(0,k) ;
					v0 = ss.substring(k+1) ;
					try
					{
						v0 = java.net.URLDecoder.decode(v0,"UTF-8") ;
					}
					catch(Exception ee)
					{}
					
				}
				tmpnv.put(n0, v0) ;
			}
			
			HttpCookie hc = new HttpCookie(n,tmpnv) ;
			ret.put(hc.name, hc) ;
		}
		return ret ;
	}
	
	public static void addResponseCookie(HttpCookie hc,HttpServletResponse hr) throws UnsupportedEncodingException
	{
		hr.addCookie(hc.toJavaCookie());
	}
	
	public static HttpCookie getRequestCookie(String name,HttpServletRequest hr) throws UnsupportedEncodingException
	{
		Cookie[] cks = hr.getCookies() ;
		if(cks==null)
			return null ;
		
		for(Cookie ck:cks)
		{
			if(ck.getName().equals(name))
			{
				return fromJavaCookie(ck);
			}
		}
		
		return null ;
	}
	
}
