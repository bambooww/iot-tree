package com.xxx.app;

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

public class AppFilter implements Filter
{
	public static final String TK = "token";
    
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
		String cxtp = req.getContextPath() ;
		String path = req.getServletPath() ;
		//System.out.println("uri="+uri +"  qs="+qs);
		if(path.startsWith("/login/"))
		{
			chain.doFilter(request, response);
			return ;
		}
		
		String tk = (String)session.getAttribute(TK) ;
		if(tk!=null&&!tk.equals(""))
		{
			chain.doFilter(request, response);
			return ;
		}
		
		resp.sendRedirect(cxtp+"/login/login.jsp");
	}
	
	
	@Override
	public void destroy()
	{
		
	}

}
