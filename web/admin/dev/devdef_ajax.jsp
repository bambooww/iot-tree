<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "drv","op","cat"))
	return ;
boolean bmgr=  "true".equals(request.getParameter("mgr")) ;
String op = request.getParameter("op");
String drvname = request.getParameter("drv");
String catname = request.getParameter("cat") ;
DevDriver dd = DevManager.getInstance().getDriver(drvname) ;
if(dd==null)
{
	out.print("no driver found") ;
	return ;
}
DevCat cat = dd.getDevCatByName(catname) ;
if(cat==null)
{
	out.print("no cat found") ;
	return ;
}

switch(op)
{
case "add":
case "edit":
	String name = request.getParameter("name") ;
	String title = request.getParameter("title") ;
	String desc = request.getParameter("desc") ;
	try
	{
		if("add".equals(op))
		{
			DevDef dc = cat.addDevDef(name, title, desc) ;
			out.print("succ") ;
		}
		else
		{
			if(!Convert.checkReqEmpty(request, out, "id"))
				return ;
			String id = request.getParameter("id") ;
			DevDef ddef = cat.getDevDefById(id);
			if(ddef==null)
			{
				out.print("no Device Definition found") ;
				break;
			}
			ddef.setDefNameTitle(name,title,desc) ;
			out.print("succ") ;
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "chg":
	break;
case "del":
	break;
case "list":
	List<DevDef> dds = cat.getDevDefs() ;
	out.print("[") ;
	boolean bfirst = true; 
	for(DevDef d:dds)
	{
		if(bfirst)
			bfirst=false;
		else
			out.print(",");
		out.print("{\"id\":\""+d.getId()+"\",\"n\":\""+d.getName() +"\",\"t\":\""+d.getTitle() +"\"}");
	}
	out.print("]") ;
	break ;
case "list_tb":
	dds = cat.getDevDefs() ;
	%>{"code":0,"msg":"","count":<%=dds.size() %>,

"data":
	[
<%
bfirst=true;
for(DevDef d:dds)
{
	if(bfirst)bfirst=false;
	else out.print(",") ;
%>
	{"id":"<%=d.getId() %>","n":"<%=d.getName() %>","t":"<%=d.getTitle() %>"}
<%
}
if(true)
{
if(!bfirst)
	out.print(",") ;
%>{"id":"","n":"","t":""}
<%
}
%>
	]
}
<%
	break;
}
%>