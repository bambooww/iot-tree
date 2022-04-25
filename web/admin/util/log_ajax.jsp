<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*,
	org.iottree.core.util.logger.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "op"))
	return ;
String logid = request.getParameter("logid") ;
String op = request.getParameter("op") ;
String v =  request.getParameter("v") ;
switch(op)
{
case "ctrl":
	if(!Convert.checkReqEmpty(request, out, "logid","v"))
		return ;
	ILogger log  = LoggerManager.getLoggerExisted(logid) ;
	int cv = Integer.parseInt(v) ;
	if(log==null)
	{
		out.print("no log found") ;
		return ;
	}
	log.setCtrl(cv) ;
	out.print("{ctrl:"+cv+"}") ;
	return ;
case "lvl":
	if(!Convert.checkReqEmpty(request, out, "logid","v"))
		return ;
	cv = Integer.parseInt(v) ;
	log  = LoggerManager.getLoggerExisted(logid) ;
	if(log==null)
	{
		out.print("no log found") ;
		return ;
	}
	log.setCurrentLogLevel(cv) ;
	out.print("{trace:"+log.isTraceEnabled()+",debug:"+log.isDebugEnabled()+
		",info:"+log.isInfoEnabled()+",warn:"+log.isWarnEnabled()+",error:"+log.isErrorEnabled()+"}") ;
	return;
case "def_lvl":
	if(!Convert.checkReqEmpty(request, out, "v"))
		return ;
	cv = Integer.parseInt(v) ;
	LoggerManager.setDefaultLogLevel(cv) ;
	out.print("{v:"+cv+"}") ;
}
%>