<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%!
	static boolean updownNd(UANode nd,int dir)
	{
		if(nd instanceof UADev)
		{
			UADev dev = (UADev)nd ;
			UACh pch = dev.getBelongTo() ;
			return pch.chgDevPosUpDown(dir,dev) ;
		}
		if(nd instanceof UACh)
		{
			UACh ch = (UACh)nd ;
			UAPrj prj = ch.getBelongTo() ;
			return prj.chgChPosUpDown(dir,ch) ;
		}
		return false;
	}
%><%
if(!Convert.checkReqEmpty(request, out, "op","path"))
	return;

String op = request.getParameter("op") ;
String path = request.getParameter("path") ;

UANode nd =  UAManager.getInstance().findNodeByPath(path);
if(nd==null)
{
	out.print("no node found") ;
	return ;
}

switch(op)
{
case "up":
	if(updownNd(nd,-1))
		out.print("succ") ;
	else
		out.print("failed");
	return ;
case "down":
	if(updownNd(nd,1))
		out.print("succ") ;
	else
		out.print("failed");
	return ;
}

%>