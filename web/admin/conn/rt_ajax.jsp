<%@ page language="java" contentType="text/json; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%!
	
%><%if(!Convert.checkReqEmpty(request, out, "repid"))
	return;
String repid = request.getParameter("repid") ;
String op = request.getParameter("op") ;
if(op==null)
	op="" ;
UAPrj rep = UAManager.getInstance().getPrjById(repid) ;
if(rep==null)
{
	out.print("{\"err\":\"no rep\"}");
	return ;
}

switch(op)
{
case "cp_start":
case "cp_stop":
	if(!Convert.checkReqEmpty(request, out, "cp_id"))
		return;
	String cpid = request.getParameter("cp_id") ;
	ConnProvider cp = ConnManager.getInstance().getConnProviderById(repid, cpid) ;
	try
	{
		if("cp_start".equals(op))
			cp.start() ;
		else
			cp.stop() ;
		out.print("{\"res\":true}");
	}
	catch(Exception e)
	{
		out.print("{\"err\":\""+e.getMessage()+"\"}");
		return ;
	}
	break ;
case "conn_start":
case "conn_stop":
	break ;
default:
	ConnManager.getInstance().renderRTJson(repid, out) ;
	break ;
}%>