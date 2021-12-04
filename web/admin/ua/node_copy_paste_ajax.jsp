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
	if(!Convert.checkReqEmpty(request, out, "ppath","path"))
	return;

	
String ppath = request.getParameter("ppath") ;
String path = request.getParameter("path") ;
UANode pnode = UAManager.getInstance().findNodeByPath(ppath);
UANode node = UAManager.getInstance().findNodeByPath(path);
if(pnode==null||node==null)
{
	out.print("node not found") ;
	return ;
}

System.out.println(ppath+" <- "+path);
if(node instanceof UADev)
{
	if(!(pnode instanceof UACh))
	{
		out.print("copy paste is not matched");
		return;
	}
	try
	{
		((UACh)pnode).deepPasteDev((UADev)node);
		out.print("succ");
	}
	catch(Exception e)
	{
		out.print(e.getMessage());
	}
	return;
}

if(node instanceof UACh)
{
	if(!(pnode instanceof UAPrj))
	{
		out.print("copy paste is not matched");
		return;
	}
	try
	{
		((UAPrj)pnode).deepPasteCh((UACh)node);
		out.print("succ");
	}
	catch(Exception e)
	{
		out.print(e.getMessage());
	}
	return;
}
//UADev dev = (UADev)node;
//dev.
%>succ