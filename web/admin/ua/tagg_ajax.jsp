<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
if(!Convert.checkReqEmpty(request, out, "path","op"))
	return ;
String path = request.getParameter("path") ;
String op = request.getParameter("op") ;
UANode node = UAUtil.findNodeByPath(path) ;
UATagG tagg = null ;
switch(op)
{
case "add":
	if(!Convert.checkReqEmpty(request, out, "name"))
		return ;
	if(node==null || !(node instanceof UANodeOCTagsGCxt))
	{
		out.print("no node found");
		return ;
	}

	UANodeOCTagsGCxt nodecxt = (UANodeOCTagsGCxt)node ;
	try
	{
		String name=request.getParameter("name");
		String title = request.getParameter("title");
		String desc = request.getParameter("desc");
		tagg = nodecxt.addTagG(name, title, desc) ;
		out.print("succ="+tagg.getId()) ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage());
	}
	break ;
case "edit":
	if(!Convert.checkReqEmpty(request, out, "name"))
		return ;
	if(node==null || !(node instanceof UATagG))
	{
		out.print("no node found");
		return ;
	}
	tagg = (UATagG)node ; 
	try
	{
		
		String name=request.getParameter("name");
		String title = request.getParameter("title");
		String desc = request.getParameter("desc");
		
		tagg.getBelongTo().updateTagG(tagg, name, title, desc);
		
		out.print("succ="+tagg.getId()) ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage());
	}
	break;
case "del":
	if(!(node instanceof UATagG))
	{
		out.print("not hmi node") ;
		return ;
	}
	tagg = (UATagG)node ;
	tagg.delFromParent();
	out.print("succ");
	break;
}

%>