<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
if(!Convert.checkReqEmpty(request, out, "repid","op"))
	return;
String repid = request.getParameter("repid") ;
String op=request.getParameter("op");
UAManager uam = UAManager.getInstance();
UARep dc = uam.getRepById(repid) ;
if(dc==null)
{
	out.print("no rep found with id="+repid) ;
	return ;
}

StringBuilder failedr = new StringBuilder() ;
if("start".equals(op))
{
	if(!dc.RT_start())
	{
		out.print("start err");
	}
	else
	{
		out.print("start ok");
	}
}
else if("stop".equals(op))
{
	dc.RT_stop();
	out.print("stop ok");
}
else if("start_stop".equals(op))
{
	if(dc.RT_isRunning())
	{
		dc.RT_stop() ;
		out.print("stop ok");
	}
	else
	{
		dc.RT_start() ;
		out.print("start ok");
	}
}

%>