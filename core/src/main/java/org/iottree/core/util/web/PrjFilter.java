package org.iottree.core.util.web;

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

import org.iottree.core.ConnManager;
import org.iottree.core.ConnPt;
import org.iottree.core.UAHmi;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAUtil;
import org.iottree.core.conn.ConnPtHTTPSer;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.msgnet.nodes.NM_RESTfulApi;
import org.iottree.core.plugin.PlugAuth;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

public class PrjFilter implements Filter
{
	private static ILogger log = LoggerManager.getLogger(PrjFilter.class) ;
	
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

	private boolean checkRestfulApiRight(HttpServletRequest req,UANode node,UAPrj prj)
	{
		
		PrjRestful restful = prj.getEnabledRestfulToken() ;
		if(restful==null)
			return true ;
		
		try
		{
			return restful.checkRequest(req) ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return false;
		}
	}
	
	private boolean isWriteTagCutoff(UAPrj prj)
	{
		return prj.getOrDefaultPropValueBool("prj_restful", "wtag_cutoff", false) ;
	}
	
	public static final String CONN_HTTPSER = "_conn_httpser" ;
	
	private boolean doConnHttpSer(String path,HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		List<String> ss = Convert.splitStrWith(path, "/") ;
		if(ss.size()!=3)
			return false;
		if(!CONN_HTTPSER.contentEquals(ss.get(1)))
			return false;
		String prjn = ss.get(0);
		UAPrj prj = UAManager.getInstance().getPrjByName(prjn) ;
		if(prj==null)
			return false;
		String connptn = ss.get(2) ;
		ConnPtHTTPSer cpt_hs = prj.getConnPtHTTPSerByName(connptn);
		if(cpt_hs==null)
			return false;
		
		String limit_ip = cpt_hs.getLimitIP() ;
		if(Convert.isNotNullEmpty(limit_ip))
		{
			if(!limit_ip.equals(request.getRemoteHost()))
				return false;
		}
		
		String auth_h = cpt_hs.getAuthHead();
		String auth_v = cpt_hs.getAuthVal() ;
		if(Convert.isNotNullEmpty(auth_h) && Convert.isNotNullEmpty(auth_v))
		{
			String vv = request.getHeader(auth_h) ;
			if(!auth_v.equals(vv))
				return false;
		}
		
		byte[] bs = readPostBS(request, response) ;
		String resptxt = cpt_hs.onRecvedFromConn(null, bs);
		
		if(Convert.isNotNullEmpty(resptxt))
			response.getOutputStream().write(resptxt.getBytes(cpt_hs.getEncod()));
		return true;
	}
	
	private boolean doMsgNetRESTfulApi(String path,HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		List<String> ss = Convert.splitStrWith(path, "/") ;
		if(ss.size()!=4)
			return false;
		String s1 = ss.get(1) ;
		if(!s1.equals("_mn_restful_api"))
			return false;
		
		String prjn = ss.get(0);
		UAPrj prj = UAManager.getInstance().getPrjByName(prjn) ;
		if(prj==null)
			return false;
		String netname = ss.get(2) ;
		String apiname = ss.get(3) ;
		MNManager mnm = MNManager.getInstance(prj) ;
		if(mnm==null)
			return false;
		List<NM_RESTfulApi> rapis = mnm.findNodesByTP(NM_RESTfulApi.class, true) ;
		if(rapis==null)
			return false;
		NM_RESTfulApi api = null ;
		for(NM_RESTfulApi rapi:rapis)
		{
			if(apiname.equals(rapi.getApiName()))
			{
				api = rapi;
				break ;
			}
		}
		if(api==null)
			return false;
		//
		String method = request.getMethod() ;
		byte[] bs = readPostBS(request, response) ;
		//String resptxt = cpt_hs.onRecvedFromConn(null, bs);
		String req_txt = new String(bs,"UTF-8") ;
		if("GET".equals(method) || Convert.isNullOrEmpty(req_txt))
		{//may send_ajax post req
			Object objv = api.getOutputObj() ;
			if(objv==null)
			{
				response.sendError(404);
				return true ;
			}
			response.getOutputStream().write(objv.toString().getBytes("UTF-8"));
			return true ;
		}
		if("POST".equals(method))
		{
			if(Convert.isNullOrEmpty(req_txt))
				return true ;
			try
			{
				api.RT_onApiPosted(req_txt);
				String ok_resp = api.getOkRespTxt() ;
				if(Convert.isNotNullEmpty(ok_resp))
					response.getOutputStream().write(ok_resp.getBytes("UTF-8"));
			}
			catch(Exception e)
			{
				String errm = e.getMessage() ;
				response.getOutputStream().write(errm.getBytes("UTF-8"));
			}
		}
		return true;
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
			session.setAttribute("ClientIP", req.getRemoteAddr());
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
		
		if(uri.startsWith("/_open"))
		{//res_node_id="+resnodeid+"&name="+name
			java.util.List<String> ss = Convert.splitStrWith(uri.substring(6), "/") ;
			if(ss.size()<=1)
				return ;
			String tmpu = "/open.jsp?resnodeid="+ss.get(0)+"&name="+ss.get(1);
			if(qs!=null)
				tmpu += "?"+qs ;
			req.getRequestDispatcher(tmpu).forward(req, resp);
			return ;
		}
		
		if(doConnHttpSer(uri, req, resp))
		{//http server conn in
			return ;
		}
		
		if(doMsgNetRESTfulApi(uri, req, resp))
		{//restful api in msg net
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
			if(qs==null)
				qs = "" ;
			else
				qs = "&"+qs ;
			req.getRequestDispatcher("/hmi.jsp?path="+uri+qs).forward(req, resp);
			return ;
		}
	
		//restful api
		UANode topn = node.getTopNode() ;
		if(topn==null || !(topn instanceof UAPrj))
			return ;
		
		UAPrj prj = (UAPrj)topn ;
		
		if(!checkRestfulApiRight(req,node,prj))
		{
			JSONObject jo = new JSONObject() ;
			jo.put("result",false) ;
			jo.put("err", "no right to access restful api");
			jo.write(resp.getWriter());
			return ;
		}
		
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
			{//tag write
				if(pa!=null)
				{
					try
					{
						if(!pa.checkWriteRight(node.getNodePath(), req))
						{//no right
							JSONObject jo = new JSONObject() ;
							jo.put("result",false) ;
							jo.put("err", pa.getNoWriteRightPrompt());
							jo.write(resp.getWriter()); //.write(pa.getNoWriteRightPrompt());
							
							if(log.isTraceEnabled())
								log.trace("PlugAuth ["+pa.getClass().getCanonicalName()+"] checkWriteRight Tag=["+node.getNodePath()+"] failed");
							
							return ;
						}
					}
					catch(Exception e)
					{
						//w.write("check write right exception:");
						JSONObject jo = new JSONObject() ;
						jo.put("result",false) ;
						jo.put("err", "check write right exception:"+e.getMessage());
						jo.write(resp.getWriter());
						
						if(log.isDebugEnabled())
							log.debug(e);
						return ;
					}
				}
				
				StringBuilder failedr = new StringBuilder() ;
				boolean bres = doPut(req, resp,(UATag)node,prj,failedr) ;
				if(bres)
					resp.getWriter().write("{\"result\":true}");
				else
					resp.getWriter().write("{\"result\":false,\"err\":\""+failedr.toString()+"\"}");
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
		case "ui":
			if(node instanceof UAPrj)
			{
				prj = (UAPrj)node ;
				UAHmi tmphmi = prj.getHmiMain() ;
				if(tmphmi==null)
				{
					List<UAHmi> hmis = prj.getHmis() ;
					if(hmis==null||hmis.size()<=0)
						return ;
					tmphmi = hmis.get(0) ;
				}
				req.getRequestDispatcher("/hmi.jsp?path="+tmphmi.getNodePath()).forward(req, resp);
				return ;
			}
			break;
		case "nav":
			if(node instanceof UAPrj)
			{
				req.getRequestDispatcher("/hmi_nav.jsp?path="+node.getNodePath()+"&tp="+tp).forward(req, resp);
			}
			else
			{
				resp.getWriter().write("not prj node");
			}
			break ;
		default:
			req.getRequestDispatcher("/node_cxt.jsp?path="+uri+"&tp="+tp).forward(req, resp);
			break ;
		}
		
		return ;
	
	}
	
	
	protected byte[] readPostBS(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
	    // 使用ByteArrayOutputStream动态接收数据
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    
	    // 设置最大允许大小（例如10MB）
	    final int MAX_SIZE = 5 * 1024 * 1024;
	    
	    try (InputStream inputStream = request.getInputStream()) {
	        byte[] tempBuffer = new byte[4096]; // 4KB缓冲区
	        int bytesRead;
	        int totalBytes = 0;
	        
	        while ((bytesRead = inputStream.read(tempBuffer)) != -1) {
	            totalBytes += bytesRead;
	            
	            // 检查大小限制
	            if (totalBytes > MAX_SIZE) {
	                response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, 
	                                 "Request payload exceeds 10MB limit");
	                return null;
	            }
	            
	            buffer.write(tempBuffer, 0, bytesRead);
	        }
	    }
	    return buffer.toByteArray();
	}
	
	protected boolean doPut(HttpServletRequest req, HttpServletResponse resp,UATag tag,UAPrj prj,StringBuilder failedr) throws ServletException, IOException
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
			return false;
		
		if(!(node instanceof UATag))
			return false;
		
		boolean cutoff = isWriteTagCutoff(prj);
		
		try
		{
			//String pv0 = req.getParameter("_pv") ;
			for(Enumeration<String> ens = req.getParameterNames() ;ens.hasMoreElements();)
			{
				String pn = ens.nextElement() ;
				String pv = req.getParameter(pn) ;
				if(cutoff)
				{
					log.warn("cut off write tag ["+tag.getNodePath()+"] with "+pn+"="+pv);
				}
				else
				{
					if(log.isDebugEnabled())
						log.debug("write tag ["+tag.getNodePath()+"] with "+pn+"="+pv);
					tag.JS_set(pn, pv);
				}
			}
			
			return true;
		}
		catch(Exception ee)
		{
			if(log.isDebugEnabled())
				log.debug(ee);
			
			failedr.append(ee.getMessage()) ;
			return false;
		}
		
	}

	@Override
	public void destroy()
	{
		
	}

}
