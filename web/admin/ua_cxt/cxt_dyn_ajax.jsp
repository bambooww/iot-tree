<%@ page language="java" contentType="text/json; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*
	"%>
<%//通信节点下挂载的设备
if(!Convert.checkReqEmpty(request, out, "path"))
	return;

String path=request.getParameter("path");
String op = request.getParameter("op");
UANode n = UAUtil.findNodeByPath(path) ;
if(n==null)
{
	out.print("no node found") ;
	return ;
}
if(!(n instanceof UANodeOCTagsCxt))
{
	out.print("not node oc tags") ;
	return ;
}
UANodeOCTagsCxt ntags = (UANodeOCTagsCxt)n ;
switch(op)
{
case "w":
	ntags.getTagById(id)
	break ;
default:
	
	ntags.CXT_renderJson(out);
break ;
}
%>