package org.iottree.core;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.web.LoginUtil;

/**
 * 
 * @author jason.zhu
 *
 */
public class ConfigFilter implements Filter
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

	
	static ILogger log = LoggerManager.getLogger(ConfigFilter.class);
	
	public static final String KEY_AUTH_SESSION_NAME = "access_auth_timelimit" ;
	
	
	
	
	public ConfigFilter()
	{

	}

	public void init(FilterConfig config) throws ServletException
	{
			Config.appConfigInitSucc = true;
	}


	
	static int runJspNum = 0 ;
	
	
	public static int getRunJspNum()
	{
		return runJspNum ;
	}
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain fc) throws ServletException, IOException
	{
		try
		{
			synchronized(ConfigFilter.class)
			{
				runJspNum ++ ;
			}
			
			doFilterInner(request, response,fc);
		}
		finally
		{
			synchronized(ConfigFilter.class)
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
				return ;
			resp.sendRedirect("/admin/login/login.jsp");
			return ;
		}
		
		req.setCharacterEncoding("UTF-8");
		
		resp.setHeader( "Pragma", "no-cache" );
		resp.setHeader( "Cache-Control", "no-cache" );
		resp.setHeader( "Cache-Control", "no-store" );
		resp.setDateHeader( "Expires", 0 );

		
		fc.doFilter(req, resp);
	}

	public void destroy()
	{
		
	}
}