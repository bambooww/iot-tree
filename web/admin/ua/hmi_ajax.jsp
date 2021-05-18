<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
/*
if(!Convert.checkReqEmpty(request, out, "repid","pid","name"))
	return;
String repid = request.getParameter("repid") ;
String pid = request.getParameter("pid") ;
String name=request.getParameter("name");
String title = request.getParameter("title");
String desc = request.getParameter("desc");
float x = Convert.parseToFloat(request.getParameter("x"), 0.0f);
float y = Convert.parseToFloat(request.getParameter("y"), 0.0f);

StringBuilder errsb = new StringBuilder() ;
UAManager uam = UAManager.getInstance();
UARep dc = uam.getRepById(repid) ;
if(dc==null)
{
	out.print("no rep found with id="+repid) ;
	return ;
}
UANode pnode = dc.findNodeById(pid) ;
if(pnode==null||!(pnode instanceof UANodeOCTagsCxt))
{
	out.print("no parent node found") ;
	return ;
}
*/
if(!Convert.checkReqEmpty(request, out, "path","op"))
	return ;
//boolean bdev = "true".equals(request.getParameter("bdev")) ;
boolean bmgr ="true".equals(request.getParameter("mgr")) ;
String path = request.getParameter("path") ;
String op = request.getParameter("op") ;
UANode node = UAUtil.findNodeByPath(path) ;
UAHmi hmi = null;
switch(op)
{
case "add":
	if(!Convert.checkReqEmpty(request, out, "name"))
		return ;
	if(node==null || !(node instanceof UANodeOCTagsCxt))
	{
		out.print("no node found");
		return ;
	}

	UANodeOCTagsCxt nodecxt = (UANodeOCTagsCxt)node ;
	try
	{
		
		String name=request.getParameter("name");
		String title = request.getParameter("title");
		String desc = request.getParameter("desc");
		
		hmi = nodecxt.addHmi("",name, title, desc,null) ;
		out.print("succ="+hmi.getId()) ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage());
	}
	break;
case "edit":
	if(!Convert.checkReqEmpty(request, out, "name"))
		return ;
	if(node==null || !(node instanceof UAHmi))
	{
		out.print("no node found");
		return ;
	}
	hmi = (UAHmi)node ; 
	try
	{
		
		String name=request.getParameter("name");
		String title = request.getParameter("title");
		String desc = request.getParameter("desc");
		
		hmi.getBelongTo().updateHmi(hmi, name, title, desc);
		
		out.print("succ="+hmi.getId()) ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage());
	}
	break;
case "del":
	if(!(node instanceof UAHmi))
	{
		out.print("not hmi node") ;
		return ;
	}
	hmi = (UAHmi)node ;
	hmi.delFromParent();
	out.print("succ");
	break;
}
%>