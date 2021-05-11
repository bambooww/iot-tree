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
String id=request.getParameter("id");
String name=request.getParameter("name");
String title = request.getParameter("title");
String desc = request.getParameter("desc");

StringBuilder errsb = new StringBuilder() ;

UARep dc = UAManager.getInstance().addRep(name, title, desc) ;
if(dc==null)
{
	out.print(errsb.toString()) ;
	return ;
}
else
{
	out.print("succ="+dc.getId()) ;
	return ;
}
%>