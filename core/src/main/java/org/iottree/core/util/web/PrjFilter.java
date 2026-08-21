package org.iottree.core.util.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import org.iottree.core.msgnet.MNBase;
import org.iottree.core.msgnet.MNBase.OuterApi;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.modules.RESTful_M;
import org.iottree.core.msgnet.modules.RESTful_Resp;
import org.iottree.core.msgnet.nodes.NM_RESTfulReadApi;
import org.iottree.core.plugin.PlugAuth;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.web.LoginUtil.SessionItem;
import org.iottree.portal.NavFrame;
import org.json.JSONArray;
import org.json.JSONObject;

public class PrjFilter extends CommonFilter
{
	private static ILogger log = LoggerManager.getLogger(PrjFilter.class) ;
	
	private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";
    
	
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
	
	private boolean doPortal(String path,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	//		throws IOException, Exception
	{
		List<String> ss = Convert.splitStrWith(path, "/") ;
		if(ss.size()<1)
			return false;
		String prjn = ss.get(0) ;
		UAPrj prj = UAManager.getInstance().getPrjByName(prjn) ;
		if(prj==null)
			return false;
		
		NavFrame nf = null;
		int n = ss.size() ; 
		if(n>1)
		{
			String s1 = ss.get(1) ;
			if(s1.startsWith("_page"))
			{//do page output
				if(n<=2)
					return true; 
				String qs = request.getQueryString();
				String p = ss.get(2) ;
				for(int i = 3 ; i < n ; i ++)
					p += "."+ss.get(i) ;
				if (qs == null)
					qs = "";
				else
					qs = "&" + qs;
				request.getRequestDispatcher("/portal__page.jsp?prjid="+prj.getId()+"&path=" + p + qs).forward(request, response);
				return true;
			}
			
			if(!s1.startsWith("_portal_"))
				return false;
			s1 = s1.substring(8) ;
			nf = prj.getPortalManager().getNavFrameByName(s1) ;
		}
		else
		{
			nf=  prj.getPortalManager().getNavFrameDefault() ;
		}
		if(nf==null)
			return false;
		request.getRequestDispatcher("/portal_"+nf.getLayout()+".jsp?prjid="+prj.getId()+"&nf_id="+nf.getId()).forward(request, response);
		
		return true;
	}
	
	private void handleOuterApi(List<String> ss,HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		byte[] bs = readPostBS(request, response) ;
		String input_txt=  null ;
		
		if(bs!=null&&bs.length>0)
			input_txt = new String(bs,"UTF-8").trim() ;
		
		JSONArray req_jarr = null ;
		JSONObject req_jo = null ;
		if(Convert.isNotNullEmpty(input_txt))
		{
			if(input_txt.startsWith("{"))
				req_jo = new JSONObject(input_txt) ;
			else if(input_txt.startsWith("["))
				req_jarr = new JSONArray(input_txt) ;
		}
		
		StringBuilder failedr = new StringBuilder() ;
		Object retob = null;
		
		int nnn = ss.size() ;
		UAPrj prj = null ;
		MNNet net = null ;
		MNBase node = null ;
		MNBase.OuterApi oa = null;
		if(nnn>1)
		{
			String prjn = ss.get(1);
			prj = UAManager.getInstance().getPrjByName(prjn) ;
			if(prj==null)
				return ;
		}
		
		if(nnn>2)
		{
			String netname = ss.get(2) ;
			net = prj.getMNManager().getNetByName(netname) ;
			if(net==null)
				return ;
		}
		
		if(nnn>3)
		{
			String node_n = ss.get(3) ;
			node = net.getItemByName(node_n) ;
			if(node==null)
				return ;
		}
		if(nnn>4)
		{
			String apiname = ss.get(4) ;
			oa = node.getUsingOuterApi(apiname) ;
			if(oa==null)
				return ;
		}
		
		if(nnn<=4 && req_jarr==null)
		{
			response.sendError(500, "<pre>no request JSONArray input</pre>");
			return ;
		}
			
		switch(nnn)
		{
		case 1://all prj in server
			retob = OuterApi.RT_callInServer(req_jarr, failedr) ;
			break ;
		case 2:// in prj call
			retob = OuterApi.RT_callInPrj(prj,req_jarr, failedr) ;
			break ;
		case 3: //in msg net call
			retob = OuterApi.RT_callInNet(net,req_jarr, failedr) ;
			break;
		case 4: // in node call
			retob = OuterApi.RT_callInNode(node,req_jarr, failedr) ;
			break;
		case 5: // api call
			retob = oa.RT_call(req_jo,failedr) ;
			break;
		default:
			return ;
		}
		
		//return response
		if(retob==null)
		{
			response.sendError(500, "<pre>"+failedr.toString()+"</pre>");
			return  ;
		}
		java.io.Writer ww = response.getWriter() ;
		ww.write(retob.toString());
		ww.flush();
		return ;
	}
	
	
	private boolean doPrjMsgNetRESTfulApi(String path,HttpServletRequest request, HttpServletResponse response)
			throws IOException, Exception
	{
		List<String> ss = Convert.splitStrWith(path, "/") ;
		if(ss.size()<4)
			return false;
		String s1 = ss.get(1) ;
		if(!s1.startsWith("_mn_"))
			return false;
		s1 = s1.substring(4) ;
		
		if(RESTful_M.TP.equals(s1))
		{
			return handleRestfulModule(request,response,ss) ;
		}
		
		if(NM_RESTfulReadApi.TP.equals(s1)||s1.equals("restful_api"))
		{
			return handleRestfulReadApi(request, response, ss);
		}
		
		return false;
	}
	
	
	private boolean handleRestfulReadApi(HttpServletRequest request, HttpServletResponse response, List<String> ss)
			throws ServletException, IOException, UnsupportedEncodingException
	{
		String prjn = ss.get(0);
		UAPrj prj = UAManager.getInstance().getPrjByName(prjn) ;
		if(prj==null)
			return false;
		String netname = ss.get(2) ;
		String apiname = ss.get(3) ;
		MNManager mnm = MNManager.getInstance(prj) ;
		if(mnm==null)
			return false;
		List<NM_RESTfulReadApi> rapis = mnm.findNodesByTP(NM_RESTfulReadApi.class, true) ;
		if(rapis==null)
			return false;
		NM_RESTfulReadApi api = null ;
		for(NM_RESTfulReadApi rapi:rapis)
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
		if(api.isContentTypeJson())
			response.setContentType("text/json;charset=UTF-8");
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
	
	private boolean handleRestfulModule(HttpServletRequest request, HttpServletResponse response, List<String> ss)
			throws ServletException, IOException, UnsupportedEncodingException
	{
		if(ss.size()!=5)
			return false;
		
		String prjn = ss.get(0);
		UAPrj prj = UAManager.getInstance().getPrjByName(prjn) ;
		if(prj==null)
			return false;
		String netname = ss.get(2) ;
		String module = ss.get(3) ;
		String apiname = ss.get(4) ;
		MNManager mnm = MNManager.getInstance(prj) ;
		if(mnm==null)
			return false;
		MNNet net = mnm.getNetByName(netname) ;
		if(net==null)
			return false;
		List<RESTful_M> mms = net.findItemByTpMark(RESTful_M.class, null) ;
		if(mms==null)
			return false;
		RESTful_M m = null ;
		for(RESTful_M mm:mms)
		{
			if(module.equals(mm.getModuleName()))
			{
				m = mm;
				break ;
			}
		}
		if(m==null)
			return false;
		//
		String method = request.getMethod() ;
		byte[] bs = readPostBS(request, response) ;
		boolean bres = m.RT_onReqResp(apiname, request,response, bs) ;
		
		if(!bres)
		{
			//String errm ="error" ;
			//response.getOutputStream().write(errm.getBytes("UTF-8"));
			response.sendError(500, "no reponse data");
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
		
		LoginUtil.getUserLoginSession(req) ;//read from cookie
		//this.getServletContext()..getRequestDispatcher(getServletInfo())
		response.setContentType("text/html;charset=UTF-8");
		String uri = req.getRequestURI();
		String qs = req.getQueryString();
		//System.out.println("uri="+uri +"  qs="+qs);
		if(uri.startsWith("/_ws"))
		{
			if(session.isNew())
				session.setAttribute("ClientIP", req.getRemoteAddr());
			chain.doFilter(request, response);
			return ;
		}
		
		if(uri.startsWith(OuterApi.MN_OUTER_API_PRE))
		{
			List<String> ss = Convert.splitStrWith(uri, "/") ;
			if("_doc".equals(ss.get(ss.size()-1)))
			{
				ss.remove(ss.size()-1) ;
				ss.remove(0) ;
				req.getRequestDispatcher("/mn_outer_api_doc.jsp?path="+Convert.combineStrWith(ss,  '.')).forward(req, resp);
				return ;
			}
			
			try
			{
				handleOuterApi(ss,req, resp) ;
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				PrintWriter w = resp.getWriter();
				
				w.write("<pre>check read right exception:");
				e.printStackTrace(w);
				w.write("</pre>");
				//w.write(e.getMessage());
				return ;
			}
			return ;
		}
		
		if(uri.startsWith("/iottree"))
			uri = uri.substring(8) ;
		
		if(uri.endsWith(".jsp") || uri.endsWith(".html") || uri.endsWith(".txt"))
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
		
		try
		{
			if(doPrjMsgNetRESTfulApi(uri, req, resp))
			{//restful api in project 's msg net
				return ;
			}
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			PrintWriter w = resp.getWriter();
			
			w.write("<pre>check read right exception:");
			e.printStackTrace(w);
			w.write("</pre>");
			//w.write(e.getMessage());
			return ;
		}
		
		if(doPortal(uri, req, resp))
		{//http server conn in
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
