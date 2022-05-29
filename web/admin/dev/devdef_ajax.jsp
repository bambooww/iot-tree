<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "libid","catid","op"))
	return ;
boolean bmgr=  "true".equals(request.getParameter("mgr")) ;
String op = request.getParameter("op");
String libid = request.getParameter("libid") ;
//String drvname = request.getParameter("drv");
String catname = request.getParameter("cat") ;
String catid = request.getParameter("catid") ;

DevCat cat = DevManager.getInstance().getDevCatById(libid,catid) ;
if(cat==null)
{
	out.print("no cat found") ;
	return ;
}

String name = request.getParameter("name") ;
String title = request.getParameter("title") ;
String desc = request.getParameter("desc") ;
switch(op)
{
case "add":
case "edit":
	/*
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
				out.print("no device definition found") ;
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
	*/
	break;
case "chg":
	break;
case "del":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	String id = request.getParameter("id") ;
	DevDef ddef = cat.getDevDefById(id);
	if(ddef==null)
	{
		out.print("no device definition found") ;
		break;
	}
	cat.delDevDef(ddef);
	out.print("succ") ;
	break;
case "chk_name":
	if(!Convert.checkReqEmpty(request, out, "name"))
		return ;
	
	DevDef tmpdd = cat.getDevDefByName(name) ;
	out.print((tmpdd==null)?"no":"ok");
	break ;
case "add_by_prj":
	if(!Convert.checkReqEmpty(request, out, "devpath","name"))
		return ;
	String devpath = request.getParameter("devpath") ;
	UADev dev = (UADev)UAUtil.findNodeByPath(devpath);
	if(dev==null)
	{
		out.print("no device found");
		return ;
	}
	
	DevDef newdd = cat.setDevDefFromPrj(dev, name, title);
	out.print("succ="+newdd.getId()) ;
	break ;
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