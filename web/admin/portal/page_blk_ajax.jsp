<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %><%!

		
%><%
	if(!Convert.checkReqEmpty(request, out, "op"))
		return ;
	//UserProfile up = UserProfile.getUserProfile(request);

	String op=request.getParameter("op") ;
	String cat = request.getParameter("cat") ;
	String page_uid = request.getParameter("page_uid") ;
	String blkn = request.getParameter("blkn") ;
	String pblk_tp = request.getParameter("pblk_tp") ;
	Page page_item = null ;
	
	if(Convert.isNotNullEmpty(page_uid))
	{
		page_item = PortalManager.getInstance().getPageByUID(page_uid) ;
		if(page_item==null)
		{
			out.print("no page found with uid="+page_uid) ;
			return;
		}
		
	}
	
	String jstr = request.getParameter("jstr") ;
	JSONObject inputjo = null;
	if(Convert.isNotNullEmpty(jstr))
		inputjo = new JSONObject(jstr) ;
	
	String jarr = request.getParameter("jarr") ;
	JSONArray inputjarr = null;
	if(Convert.isNotNullEmpty(jarr))
		inputjarr = new JSONArray(jarr) ;
	
	StringBuilder failedr = new StringBuilder() ;
	JSONObject tmpjo = null ;
	
	switch(op)
	{

	case "set_pblk_detail":
		if(!Convert.checkReqEmpty(request, out, "page_uid","blkn","pblk_tp","jstr"))
			return ;
		if(page_item.setPageBlk(blkn,pblk_tp,inputjo,failedr))
			out.print("succ") ;
		else
			out.print(failedr.toString()) ;
		return ;
	default:
		out.print("unknown op="+op) ;
		return ;
	}
%>