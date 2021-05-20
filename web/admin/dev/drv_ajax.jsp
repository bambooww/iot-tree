<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%!
%><%if(!Convert.checkReqEmpty(request, out, "repid","op"))
	return;
String repid = request.getParameter("repid") ;
UAPrj rep = UAManager.getInstance().getPrjById(repid);
if(rep==null)
{
	out.print("no rep found") ;
	return;
}
String op = request.getParameter("op") ;
switch(op)
{
case "ch_drv_set":
	String chid = request.getParameter("chid") ;
	String name = request.getParameter("name") ;
	UACh ch = rep.getChById(chid) ;
	if(ch==null)
	{
		out.print("no ch found") ;
		return ;
	}
	if(ch.setDriverName(name))
	{
		out.print("succ") ;
	}
	else
	{
		out.print("failed") ;
	}
	break ;
case "del":

case "join_add":

	break ;
}%>