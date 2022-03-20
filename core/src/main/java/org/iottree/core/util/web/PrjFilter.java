package org.iottree.core.util.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.iottree.core.UAHmi;
import org.iottree.core.UANode;
import org.iottree.core.UATag;
import org.iottree.core.UAUtil;
import org.iottree.core.plugin.PlugAuth;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;

public class PrjFilter implements Filter
{
	private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";
    
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		
	}

	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		//this.getServletContext()..getRequestDispatcher(getServletInfo())
		response.setContentType("text/html;charset=UTF-8");
		String uri = req.getRequestURI();
		String qs = req.getQueryString();
		//System.out.println("uri="+uri +"  qs="+qs);
		if(uri.startsWith("/_ws"))
		{
			chain.doFilter(request, response);
			return ;
		}
		if(uri.startsWith("/iottree"))
			uri = uri.substring(8) ;
		
		if(uri.endsWith(".jsp"))
		{
//			String u = uri ;
//			if(qs!=null)
//				u += "?"+qs ;
			//req.getRequestDispatcher(u).forward(req, resp);
			chain.doFilter(request, response);
			return ;
		}
		
		if(uri.contentEquals("/"))
		{
			chain.doFilter(request, response);
			return ;
		}
		
		if(uri.startsWith("/_res"))
		{//res_node_id="+resnodeid+"&name="+name
			java.util.List<String> ss = Convert.splitStrWith(uri.substring(5), "/") ;
			if(ss.size()<=1)
				return ;
			String tmpu = "/res.jsp?resnodeid="+ss.get(0)+"&name="+ss.get(1);
			if(qs!=null)
				tmpu += "?"+qs ;
			req.getRequestDispatcher(tmpu).forward(req, resp);
			return ;
		}
		
		UANode node = UAUtil.findNodeByPath(uri) ;
		if(node==null)
		{
			return ;
		}
		
		//check right
		PlugAuth pa = PlugManager.getInstance().getPlugAuth() ;
		if(pa!=null)
		{
			try
			{
				if(!pa.checkReadRight(node.getNodePath(), req))
				{//no right
					resp.getWriter().write(pa.getNoReadRightPrompt());
					return ;
				}
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				PrintWriter w = resp.getWriter();
				w.write("check read right exception:");
				e.printStackTrace(w);
				//w.write(e.getMessage());
				return ;
			}
		}
		
		if(node instanceof UAHmi)
		{
			//UAHmi hmi = (UAHmi)node ;
			req.getRequestDispatcher("/hmi.jsp?path="+uri).forward(req, resp);
			return ;
		}
		else
		{
			String op = req.getParameter("op");
			String tp = req.getParameter("tp") ;
			if(tp==null)
				tp = "" ;
			if(op==null)
				op="" ;
			String method = req.getMethod();

			if(node instanceof UATag)
			{
				if (method.equals(METHOD_POST)||method.equals(METHOD_PUT))
				{
					if(pa!=null)
					{
						try
						{
							if(!pa.checkWriteRight(node.getNodePath(), req))
							{//no right
								resp.getWriter().write(pa.getNoWriteRightPrompt());
								return ;
							}
						}
						catch(Exception e)
						{
							//e.printStackTrace();
							PrintWriter w = resp.getWriter();
							w.write("check write right exception:");
							e.printStackTrace(w);
							//w.write(e.getMessage());
							return ;
						}
					}
					doPut(req, resp);
				    return ;
				}
			}
			switch(op)
			{
			case "cxt":
				req.getRequestDispatcher("/node_cxt.jsp?path="+uri+"&tp="+tp).forward(req, resp);
				break ;
			case "list":
				req.getRequestDispatcher("/node_list.jsp?path="+uri+"&tp="+tp).forward(req, resp);
				break ;
			default:
				req.getRequestDispatcher("/node_cxt.jsp?path="+uri+"&tp="+tp).forward(req, resp);
				break ;
			}
			
			return ;
		}
	}
	
	
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		//super.doPut(req, resp);
		//update restful api
		resp.setContentType("text/html;charset=UTF-8");
		String uri = req.getRequestURI();
		//String qs = req.getQueryString();
		
		if(uri.startsWith("/iottree"))
			uri = uri.substring(8) ;
		
		UANode node = UAUtil.findNodeByPath(uri) ;
		if(node==null)
			return ;
		
		if(!(node instanceof UATag))
			return ;
		
		UATag tag = (UATag)node ;
		//String pv0 = req.getParameter("_pv") ;
		for(Enumeration<String> ens = req.getParameterNames() ;ens.hasMoreElements();)
		{
			String pn = ens.nextElement() ;
			String pv = req.getParameter(pn) ;
			tag.JS_set(pn, pv);
		}
		
	}

	@Override
	public void destroy()
	{
		
	}

}
