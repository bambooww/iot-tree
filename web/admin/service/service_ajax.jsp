<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.service.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%

if(!Convert.checkReqEmpty(request, out, "op"))
	return;
String op = request.getParameter("op") ;

switch(op)
{
case "start":
case "stop":
	if(!Convert.checkReqEmpty(request, out,"n"))
		return;
	String n = request.getParameter("n") ;
	AbstractService as = ServiceManager.getInstance().getService(n) ;
	if(as==null)
	{
		out.print("no service found") ;
		return ;
	}
	if("start".equals(op))
		as.startService();
	else
		as.stopService() ;
	out.print("ok") ;
	break;
case "list":
	break ;
}
%>