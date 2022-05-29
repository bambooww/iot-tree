<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out,"op"))
	return ;
String op = request.getParameter("op");
String libid = request.getParameter("libid");
DevLib lib = null ;
if(Convert.isNotNullEmpty(libid))
{
	lib = DevManager.getInstance().getDevLibById(libid) ;
	if(lib==null)
	{
		out.print("no lib found with id="+libid) ;
		return ;
	}
}

DevCat cat = null;
String catid = request.getParameter("catid") ;
if(Convert.isNotNullEmpty(catid))
{
	cat = lib.getDevCatById(catid) ;
	if(cat==null)
	{
		out.print("no cat found with id="+catid) ;
		return ;
	}
}
switch(op)
{
case "add":
case "edit":
	if(!Convert.checkReqEmpty(request, out,"title"))
		return ;
	String title = request.getParameter("title") ;
	try
	{
		if(Convert.isNullOrEmpty(libid))
		{
			DevLib nlib = DevManager.getInstance().addDevLib(title);
			libid=  nlib.getId();
		}
		else
		{
			lib.asTitle(title) ;
			lib.save();
		}
		out.print("succ="+libid) ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break;
case "del":
	if(!Convert.checkReqEmpty(request, out,"libid"))
		return ;
	DevManager.getInstance().delDevLib(libid) ;
	out.print("succ") ;
	break ;
case "edit_cat":
	if(!Convert.checkReqEmpty(request, out,"libid","name","title"))
		return ;
	
	String name = request.getParameter("name") ;
	title = request.getParameter("title") ;
	try
	{
		if(cat==null)
		{
			DevCat dc = lib.addDevCat(name, title) ;
			catid = dc.getId() ;
		}
		else
		{
			lib.updateDevCat(catid, name, title) ;
		}
		out.print("succ="+catid) ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break ;
case "chg":
	break;
case "del_cat":
	
	if(!Convert.checkReqEmpty(request, out, "catid"))
		return ;
	lib.delDevCat(catid) ;
	out.print("succ") ;
	break;
case "list":
	if(!Convert.checkReqEmpty(request, out,"libid"))
		return ;
	List<DevCat> cats = lib.getDevCats() ;
%>
{"code":0,"msg":"","count":<%=cats.size() %>,

"data":
	[
<%
boolean bfirst = true; 
for(DevCat dc:cats)
{
	if(bfirst)
		bfirst=false;
	else
		out.print(",");
	out.print("{\"id\":\""+dc.getId()+"\",\"n\":\""+dc.getName() +"\",\"t\":\""+dc.getTitle() +"\"}");
}
%>		
	]
}
<%
	break ;
}
%>