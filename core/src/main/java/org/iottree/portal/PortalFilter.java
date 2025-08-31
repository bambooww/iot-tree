package org.iottree.portal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;

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

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

public class PortalFilter implements Filter
{
	private static ILogger log = LoggerManager.getLogger(PortalFilter.class);

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
		// this.getServletContext()..getRequestDispatcher(getServletInfo())
		response.setContentType("text/html;charset=UTF-8");
		String uri = req.getRequestURI();
		String qs = req.getQueryString();
		// System.out.println("uri="+uri +" qs="+qs);

		if (uri.endsWith(".jsp"))
		{
			chain.doFilter(request, response);
			return;
		}

//		if (uri.contentEquals("/"))
//		{
//			chain.doFilter(request, response);
//			return;
//		}

		if (uri.startsWith("/_portal"))
		{
			uri = uri.substring(8);
			// UAHmi hmi = (UAHmi)node ;
			if (qs == null)
				qs = "";
			else
				qs = "&" + qs;
			req.getRequestDispatcher("/portal_page.jsp?path=" + uri + qs).forward(req, resp);
			return;
		}

		chain.doFilter(request, response);
		return;
	}

	protected byte[] readPostBS(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{

		// 使用ByteArrayOutputStream动态接收数据
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		// 设置最大允许大小（例如10MB）
		final int MAX_SIZE = 5 * 1024 * 1024;

		try (InputStream inputStream = request.getInputStream())
		{
			byte[] tempBuffer = new byte[4096]; // 4KB缓冲区
			int bytesRead;
			int totalBytes = 0;

			while ((bytesRead = inputStream.read(tempBuffer)) != -1)
			{
				totalBytes += bytesRead;

				// 检查大小限制
				if (totalBytes > MAX_SIZE)
				{
					response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
							"Request payload exceeds 10MB limit");
					return null;
				}

				buffer.write(tempBuffer, 0, bytesRead);
			}
		}
		return buffer.toByteArray();
	}

	@Override
	public void destroy()
	{

	}

}
