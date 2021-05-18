<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "drv","op"))
	return ;
String op = request.getParameter("op");
String drvname = request.getParameter("drv");
DevDriver dd = DevManager.getInstance().getDriver(drvname) ;
if(dd==null)
{
	out.print("no driver found") ;
	return ;
}
switch(op)
{
case "add":
	String name = request.getParameter("name") ;
	String title = request.getParameter("title") ;
	try
	{
		DevCat dc = dd.addDevCat(name, title) ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break;
case "chg":
	break;
case "del":
	break;
case "list":
	List<DevCat> cats = dd.getDevCats() ;
	out.print("[") ;
	boolean bfirst = true; 
	for(DevCat dc:cats)
	{
		if(bfirst)
			bfirst=false;
		else
			out.print(",");
		out.print("{\"n\":\""+dc.getName() +"\",\"t\":\""+dc.getTitle() +"\"}");
	}
	out.print("]") ;
	break ;
}
%>