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
if(!Convert.checkReqEmpty(request, out, "repid","name","conntp"))
	return;
String repid = request.getParameter("repid") ;
StringBuilder errsb = new StringBuilder() ;
UAManager uam = UAManager.getInstance();
UARep rep = uam.getRepById(repid) ;
if(rep==null)
{
	out.print("no rep found with id="+repid) ;
	return ;
}
String name = request.getParameter("name") ;
String title = request.getParameter("title") ;
String desc=  request.getParameter("desc") ;
String conntp = request.getParameter("conntp") ;
UAConn conn = null ;
try
{
	conn = rep.addConn(conntp, name, title, desc, null) ;
}
catch(Exception e)
{
	out.print(e.getMessage());
	return ;
}
if(conn==null)
{
	out.print(errsb.toString()) ;
	return ;
}
else
{
	out.print("succ="+conn.getId()) ;
	return ;
}
%>