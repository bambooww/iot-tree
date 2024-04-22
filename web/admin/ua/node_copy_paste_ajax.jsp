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
if(!Convert.checkReqEmpty(request, out, "op","path"))
	return;

String op = request.getParameter("op") ;
String ppath = request.getParameter("ppath") ;
String path = request.getParameter("path") ;

switch(op)
{
case "copy":
	session.setAttribute("_node_copied_path", path) ;
	out.print("succ");
	break ;
case "paste":
	UANode pnode = null;
	UANode node = null;
	if(Convert.isNullOrEmpty(ppath))
	{
		ppath = path ;
		path = (String)session.getAttribute("_node_copied_path") ;
		if(Convert.isNullOrEmpty(path))
		{
			out.print("no copied node") ;
			return ;
		}
	}
	
	pnode = UAManager.getInstance().findNodeByPath(ppath);
	node = UAManager.getInstance().findNodeByPath(path);
	if(pnode==null||node==null)
	{
		out.print("node not found") ;
		return ;
	}

	//System.out.println(ppath+" <- "+path);
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
	
	if(node instanceof UATagG)
	{
		if(!(pnode instanceof UANodeOCTagsGCxt))
		{
			out.print("copy paste is not matched");
			return;
		}
		try
		{
			((UANodeOCTagsGCxt)pnode).deepPasteTagG((UATagG)node);
			out.print("succ");
		}
		catch(Exception e)
		{
			out.print(e.getMessage());
		}
		return;
	}
	
	if(node instanceof UAHmi)
	{
		if(!(pnode instanceof UANodeOCTagsCxt))
		{
			out.print("copy paste is not matched");
			return;
		}
		try
		{
			((UANodeOCTagsCxt)pnode).pasteHmi((UAHmi)node);
			out.print("succ");
		}
		catch(Exception e)
		{
			out.print(e.getMessage());
		}
		return ;
	}
	out.print("paste failed,may be not matched") ;
	return;
}


//UADev dev = (UADev)node;
//dev.
%>