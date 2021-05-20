<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.bind.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
	if(!Convert.checkReqEmpty(request, out, "repid","hmiid"))
	return;
String repid = request.getParameter("repid") ;
String hmiid = request.getParameter("hmiid") ;
UAPrj rep = UAManager.getInstance().getPrjById(repid) ;
if(rep==null)
{
	out.print("{res:false,err:\"no rep found\"}") ;
	return ;
}
UAHmi hmi = rep.findHmiById(hmiid) ;
if(hmi==null)
{
	out.print("{res:false,err:\"no hmi found\"}") ;
	return ;
}
List<PropBindItem> pbis = hmi.getBinds() ;
if(pbis==null||pbis.size()<=0)
{
	out.print("{}") ;
	return ;
}
%>