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
if(!Convert.checkReqEmpty(request, out, "op"))
	return;

String op = request.getParameter("op") ;

String id=request.getParameter("id");
String name=request.getParameter("name");
String title = request.getParameter("title");
String desc = request.getParameter("desc");
switch(op)
{
case "add":
	StringBuilder errsb = new StringBuilder() ;

	UARep dc = UAManager.getInstance().addRep(name, title, desc) ;
	if(dc==null)
	{
		out.print(errsb.toString()) ;
		return ;
	}
	else
	{
		out.print("succ="+dc.getId()) ;
		return ;
	}
case "edit":
	break ;
case "del":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return;
	dc = UAManager.getInstance().getRepById(id) ;
	if(dc==null)
	{
		out.print("ok") ;
		return ;
	}
	UAManager.getInstance().delRep(id) ;
	out.print("ok") ;
	break ;
}

%>