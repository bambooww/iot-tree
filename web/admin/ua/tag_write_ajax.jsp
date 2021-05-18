<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
if(!Convert.checkReqEmpty(request, out, "repid","id","strv"))
	return;
String repid = request.getParameter("repid") ;
String id=request.getParameter("id");
String strv = request.getParameter("strv");
UAManager uam = UAManager.getInstance();
UARep dc = uam.getRepById(repid) ;
if(dc==null)
{
	out.print("no rep found with id="+repid) ;
	return ;
}

UANode n = dc.findNodeById(id) ;
if(n==null)
{
	out.print("no node found") ;
	return ;
}
if(!(n instanceof UATag))
{
	out.print("not tag node found") ;
	return ;
}

UATag tag = (UATag)n ;
boolean b = tag.RT_writeVal(strv);

if(!b)
{
	out.print("write err") ;
	return ;
}
else
{
	out.print("succ="+id) ;
	return ;
}
%>