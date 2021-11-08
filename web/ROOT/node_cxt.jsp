<%@ page contentType="text/json;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
				java.net.*"%>
<%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
	//String op = request.getParameter("op");
	String node_path = request.getParameter("path");
	UANode n = UAUtil.findNodeByPath(node_path) ;
	if(n==null)
	{
		out.print("no node found") ;
	}
	
	if(!(n instanceof UANodeOCTags))
	{
		out.print("not node oc tags") ;
		return ;
	}
	
	
	UANodeOCTags ntags = (UANodeOCTags)n ;
	if(!(n instanceof UANodeOCTagsCxt))
	{
		out.print("not node oc tags cxt") ;
		return ;
	}
	
	UANodeOCTagsCxt ntcxt = (UANodeOCTagsCxt)n ;
	ntcxt.CXT_renderJson(out) ;
%>