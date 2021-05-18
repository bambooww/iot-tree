package org.iottree.core.util.web;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;


public class LangTag extends BodyTagSupport
{
	private static final long serialVersionUID = 1L;

	public static String CXT_ATTRN = "_PAGE_LANG";
	
	private String lang = null ;
	
	public void setLang(String lan)
	{
		lang = lan ;
	}

	
	private static HashMap<String,Object> path2lang = new HashMap<>() ;
	
	private static String getPath(PageContext pc)
	{
		HttpServletRequest req = (HttpServletRequest)pc.getRequest();
		String p = req.getServletPath() ;
		String cxtp = pc.getServletContext().getContextPath() ;
		if(p.startsWith(cxtp))
		{
			return p ;
		}
		else
		{
			return cxtp+p;
		}
	}
	 
	/**
	 * 
	 * @param pc
	 * @param path
	 * @param brefresh
	 * @return
	 * @throws JspTagException
	 */
	public static Lang getLang(PageContext pc,String path,boolean brefresh)
			throws JspTagException
	{
		Lang wpl = null;
		if(!brefresh)
		{
			Object o = path2lang.get(path);
			if(o!=null)
			{
				if(o instanceof Lang)
					return (Lang)o;
				else
					return null ;
			}
		}
		
		if(pc==null)
			return null ;
		
		try
		{
			HttpServletRequest req = (HttpServletRequest)pc.getRequest();
			String realp = pc.getServletContext().getRealPath(req.getServletPath()) ;
			if(realp!=null)
			{
				File reqf = new File(realp) ;
				if(reqf.isFile())
					reqf = reqf.getParentFile() ;
				String fp = null ;
				if(path.startsWith("/"))
				{
					String cxtp = pc.getServletContext().getContextPath() ;
					if(path.startsWith(cxtp))
					{
						fp = Config.getWebappBase()+path;
					}
					else
					{
						fp = Config.getWebappBase()+cxtp+path;
					}
				}
				else
				{
					fp = reqf.getCanonicalPath()+"/"+path ;
				}
				
				File f = new File(new File(fp),"lang.xml");
				if (f.exists())
				{
					wpl = new Lang(f, null);
				}
			}

			if(wpl==null)
				path2lang.put(path, "");
			else
				path2lang.put(path, wpl);
			
			return wpl;
		}
		catch (Exception e)
		{
			throw new JspTagException(e);
		}
	}
	
	/**
	 * 获得对应路径地语言链路，从最下一级开始，一步一步往上走
	 * @return
	 */
	private static List<Lang> getLangChain(PageContext pc,String path,boolean brefresh)
			throws JspTagException
	{
		ArrayList<Lang> rets = new ArrayList<>() ;
		do
		{
			Lang lan = getLang(pc, path,brefresh) ;
			if(lan!=null)
				rets.add(lan) ;
			
			int k = path.lastIndexOf("/", path.length()-2) ;
			if(k<=0)
			{
				return rets ;
			}
			path = path.substring(0,k+1) ;
		}
		while(path!=null) ;
		return rets ;
	}
	
	public static String getLangValue(PageContext pc,String key) throws JspTagException
	{
		HttpServletRequest req = (HttpServletRequest)pc.getRequest();
		boolean brefresh = "true".equals(req.getParameter("lang_refresh")) ;
		String path = getPath(pc) ;
		if(!path.endsWith("/")&&!"/".equals(path))
		{
			int k = path.lastIndexOf('/') ;
			if(k>0)
			{
				path = path.substring(0,k+1) ;
			}
		}
		List<Lang> lans = getLangChain(pc, path,brefresh) ;
		if(lans==null||lans.size()<=0)
		{
			return "[x]"+key+"[x]" ;
		}
		for(Lang lan:lans)
		{
			String v = lan.getLangValue(key) ;
			if(v!=null)
				return v ;
		}
		return "[x]"+key+"[x]" ;
	}

	public int doEndTag() throws JspTagException
	{
		try
		{
			if (bodyContent != null)
			{
				String tmps = bodyContent.getString();
				if(tmps!=null&&!(tmps=tmps.trim()).equals(""))
				{
					String vv  = getLangValue(pageContext,tmps);
					pageContext.getOut().write(vv);
				}
			}
		}
		catch (java.io.IOException e)
		{
			throw new JspTagException("IO Error: " + e.getMessage());
		}
		return EVAL_PAGE;
	}
}
