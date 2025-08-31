<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %><%
	if(!Convert.checkReqEmpty(request, out, "uid"))
		return ;
	String uid = request.getParameter("uid") ;
	Templet temp = PortalManager.getInstance().getTempletByUID(uid) ;
	if(temp==null)
	{
		out.print("no temp found") ;
		return ;
	}
	
	temp.renderOutSor(out) ;
%>