<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
	String path = request.getParameter("path") ;
	UANode node = UAUtil.findNodeByPath(path) ;
	if(node==null)
	{
		out.print("no node found") ;
		return ;
	}
	
	DevDriver dd = null ;
	if(node instanceof UACh)
	{
		UACh ch  = (UACh)node ;
		dd = ch.getDriver() ;
	}
	else if(node instanceof UADev)
	{
		UADev dev = (UADev)node ;
		dd = dev.getRelatedDrv() ;
	}
	
	if(dd!=null && dd.supportDriverUrl())
	{
		response.sendRedirect("./drv_"+dd.getName()+"/main.jsp?path="+path) ;
	}
	
%>no driver management url found