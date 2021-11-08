package org.iottree.core.util.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.iottree.core.UAHmi;
import org.iottree.core.UANode;
import org.iottree.core.UAUtil;
import org.iottree.core.util.Convert;

public class PrjFilter implements Filter
{

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
		
		if(node instanceof UAHmi)
		{
			//UAHmi hmi = (UAHmi)node ;
			req.getRequestDispatcher("/hmi.jsp?path="+uri).forward(req, resp);
			return ;
		}
		else
		{
			String op = req.getParameter("op");
			if(op==null)
				op="" ;
			switch(op)
			{
			case "cxt":
				req.getRequestDispatcher("/node_cxt.jsp?path="+uri).forward(req, resp);
				break ;
			case "list":
				req.getRequestDispatcher("/node_list.jsp?path="+uri).forward(req, resp);
				break ;
			default:
				req.getRequestDispatcher("/node_cxt.jsp?path="+uri).forward(req, resp);
				break ;
			}
			
			return ;
		}
	}

	@Override
	public void destroy()
	{
		
	}

}
