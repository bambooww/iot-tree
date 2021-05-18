<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%!
	private static UATagG addTagG(UANode n,HttpServletRequest request) throws Exception
	{
		if(!(n instanceof UANodeOCTagsGCxt))
			return null ;
		String name=request.getParameter("name");
		String title = request.getParameter("title");
		String desc = request.getParameter("desc");
		String addr = request.getParameter("addr");
		
		UANodeOCTagsGCxt nt = (UANodeOCTagsGCxt)n ;
		UATagG ret = nt.addTagG(name, title, desc);
		return ret ;
	}
%><%
if(!Convert.checkReqEmpty(request, out, "op","path"))
	return;
String op = request.getParameter("op") ;
String path = request.getParameter("path") ;
UANode n = UAUtil.findNodeByPath(path);
if(n==null)
{
	out.print("no node with path="+path) ;
	return ;
}

UATagG tag = null;
switch(op)
{
case "add_tagg":
	try
	{
		tag = addTagG(n,request) ;
		out.print("succ="+tag.getId()) ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage());
		return ;
	}
	break ;
case "del_tagg":
	String tagid = request.getParameter("id") ;
	if(Convert.isNullOrEmpty(tagid))
	{
		out.print("no tag id input") ;
		break ;
	}
	if(!(n instanceof UANodeOCTagsGCxt))
	{
		out.print("not tags node") ;
		break ;
	}
	
	UATagG t = ((UANodeOCTagsGCxt)n).getSubTagGById(tagid) ;
	if(t==null)
	{
		out.print("no tag found") ;
		break ;
	}
	boolean b =t.delFromParent();
	if(!b)
	{
		out.print("del err") ;
	}
	else
	{
		out.print("succ="+tagid) ;
	}
	
	break ;
}
%>