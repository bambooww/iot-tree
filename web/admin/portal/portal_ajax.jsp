<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %><%!

		
%><%
	if(!Convert.checkReqEmpty(request, out, "op","prjid"))
		return ;
	String prjid = request.getParameter("prjid") ;
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	PortalManager pmgr = prj.getPortalManager() ;
	String op=request.getParameter("op") ;
	String nf_id = request.getParameter("nf_id") ;
	NavFrame navf = null ;
	if(Convert.isNotNullEmpty(nf_id))
	{
		navf = pmgr.getNavFrameById(nf_id) ;
		if(navf==null)
		{
			out.print("no NavFrame found with id="+nf_id) ;
			return ;
		}
	}
	String name = request.getParameter("name") ;
	String title = request.getParameter("title") ;
	String templet_uid= request.getParameter("templet_uid") ;
	int pageidx = Convert.parseToInt32(request.getParameter("pageidx"), 0) ;
	int pagesize = Convert.parseToInt32(request.getParameter("pagesize"), -1) ;
	boolean b_def = "true".equals(request.getParameter("def")) ;
	
	String search_txt = request.getParameter("search_txt") ;
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
	case "set_nf":
		if(!Convert.checkReqEmpty(request, out, "title"))
			return ;
		if(pmgr.setNavFrameBasic(nf_id, title, name,b_def, failedr)!=null)
			out.print("succ") ;
		else
			out.print("failed:"+failedr.toString()) ;
		return ;
	case "set_nf_detail":
		if(!Convert.checkReqEmpty(request, out, "nf_id","jstr"))
			return ;
		if(pmgr.setNavFrameDetail(nf_id, inputjo, failedr)!=null)
			out.print("succ");
		else
			out.print(failedr.toString());
		return ;
	case "edit_page":
		
		return ;

	default:
		out.print("unknown op="+op) ;
		return ;
	}
%>