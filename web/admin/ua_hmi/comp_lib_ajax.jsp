<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.comp.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out,"op"))
	return ;
String op = request.getParameter("op");
String libid = request.getParameter("libid");
CompLib lib = null ;
if(Convert.isNotNullEmpty(libid))
{
	lib = CompManager.getInstance().getCompLibById(libid) ;
	if(lib==null)
	{
		out.print("no lib found with id="+libid) ;
		return ;
	}
}

CompCat cat = null;
String catid = request.getParameter("catid") ;
if(Convert.isNotNullEmpty(catid))
{
	cat = lib.getCatById(catid) ;
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
			CompLib nlib = CompManager.getInstance().addCompLib(title);
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
	CompManager.getInstance().delCompLib(libid) ;
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
			CompCat dc = lib.addCat(title) ;
			catid = dc.getId() ;
		}
		else
		{
			lib.updateCat(catid, title) ;
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
	lib.delCat(catid) ;
	out.print("succ") ;
	break;
case "list":
	if(!Convert.checkReqEmpty(request, out,"libid"))
		return ;
	List<CompCat> cats = lib.getAllCats() ;
%>
{"code":0,"msg":"","count":<%=cats.size() %>,

"data":
	[
<%
boolean bfirst = true; 
for(CompCat dc:cats)
{
	if(bfirst)
		bfirst=false;
	else
		out.print(",");
	out.print("{\"id\":\""+dc.getId()+"\",\"t\":\""+dc.getTitle() +"\"}");
}
%>		
	]
}
<%
	break ;
}
%>