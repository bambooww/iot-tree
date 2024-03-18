package org.iottree.core.util.web;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.Lan;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

/**
 * 
 * @author jason.zhu
 *
 */
public class AdminFilter implements Filter,ILang
{
	static
	{
		try
		{

		}
		catch(Exception eee)
		{
			eee.printStackTrace() ;
		}
	}

	
	static ILogger log = LoggerManager.getLogger(AdminFilter.class);
	
	public static final String KEY_AUTH_SESSION_NAME = "access_auth_timelimit" ;

	public AdminFilter()
	{

	}

	public void init(FilterConfig config) throws ServletException
	{
			//Config.appConfigInitSucc = true;
	}


	
	static int runJspNum = 0 ;
	
	
	public static int getRunJspNum()
	{
		return runJspNum ;
	}
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain fc) throws ServletException, IOException
	{
		String lan = request.getParameter("_lan_") ;//req url first
		if(Convert.isNullOrEmpty(lan))
		{
			HttpServletRequest req = (HttpServletRequest) request;
			LoginUtil.SessionItem si = LoginUtil.getAdminLoginSession(req.getSession()) ;
			if(si!=null)
				lan = si.lan ; // then session
		}
		
		try
		{
			if(Convert.isNotNullEmpty(lan))
				Lan.setLangInThread(lan);
			
			synchronized(AdminFilter.class)
			{
				runJspNum ++ ;
			}
			
			doFilterInner(request, response,fc);
		}
		finally
		{
			if(Convert.isNotNullEmpty(lan))
				Lan.setLangInThread(null);
			
			synchronized(AdminFilter.class)
			{
				runJspNum -- ;
			}
		}
	}

	public void doFilterInner(ServletRequest request, ServletResponse response,
			FilterChain fc) throws ServletException, IOException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		
		String sp = req.getServletPath() ;
		if(sp!=null&&sp.startsWith("/login"))
		{
			fc.doFilter(req, resp);
			return ;
		}
		
		if(log.isDebugEnabled())
		{
			log.debug("sp="+sp) ;
		}
		
		if(!LoginUtil.checkAdminLogin(req))
		{
			if(sp.endsWith("_ajax.jsp"))
			{
				resp.getWriter().write(g("need_login"));
				return ;
			}
			resp.sendRedirect("/admin/login/login.jsp");
			return ;
		}
		
		req.setCharacterEncoding("UTF-8");
		
//		resp.setHeader( "Pragma", "no-cache" );
//		resp.setHeader( "Cache-Control", "no-cache" );
//		resp.setHeader( "Cache-Control", "no-store" );
//		resp.setDateHeader( "Expires", 0 );

		
		fc.doFilter(req, resp);
	}

	public void destroy()
	{
		
	}
}