<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.json.*,
	org.iottree.core.station.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out,"op"))
	return ;

String op = request.getParameter("op");
String jstr = request.getParameter("jstr");
JSONObject jo = null ;
if(Convert.isNotNullEmpty(jstr))
	jo = new JSONObject(jstr) ;

StringBuilder failedr = new StringBuilder() ;
switch(op)
{
case "set_conf":
	if(!Convert.checkReqEmpty(request, out,"jstr"))
			return ;
	
	if(!StationLocal.getInstance().setConfig(jo, failedr))
		out.print(failedr.toString()) ;
	else
		out.print("succ") ;
	return;
case "unset":
	if(!StationLocal.getInstance().setConfig(null, failedr))
		out.print(failedr.toString()) ;
	else
		out.print("succ") ;
	break;
case "start":
	StationLocal.getInstance().RT_start();
	out.print("succ") ;
	return ;
case "stop":
	StationLocal.getInstance().RT_stop();
	out.print("succ") ;
	return ;
case "st":
	StationLocal.getInstance().RT_toJO().write(out) ;
	return ;
default:
	out.print("unknown op="+op) ;
	return ;
}

%>