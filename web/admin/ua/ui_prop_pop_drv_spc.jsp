<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out, "nodepath"))
		return ;
	String nodep = request.getParameter("nodepath") ;
	UANode node = UAManager.getInstance().findNodeByPath(nodep) ;
	if(node==null || !(node instanceof UACh))
	{
		out.print("no UACh found with path="+nodep) ;
		return ;
	}
	
	UACh ch = (UACh)node ;
	DevDriver drv = ch.getDriver() ;
	if(drv==null)
	{
		out.println("no driver found") ;
		return ;
	}
	if(!drv.hasDriverConfigPage())
	{
		out.println("driver has not config page") ;
		return ;
	}
	response.sendRedirect("./drv/drv."+drv.getName()+".config.jsp?nodepath="+nodep) ;
	return ;
%>