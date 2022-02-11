<%@page import="org.iottree.core.util.web.WebRes"%><%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	org.iottree.core.sim.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
if(!Convert.checkReqEmpty(request, out, "insid"))
	return;

String insid = request.getParameter("insid") ;
SimManager simmgr = SimManager.getInstance() ;
SimInstance ins = simmgr.getInstance(insid) ;
if(ins==null)
{
	out.print("no instance found") ;
	return ;
}

simmgr.exportIns(response, insid) ;
%>