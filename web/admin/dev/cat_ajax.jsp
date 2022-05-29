<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "libid","catid","op"))
	return ;
String op = request.getParameter("op");
String libid = request.getParameter("libid");
String catid = request.getParameter("catid");

DevCat cat = DevManager.getInstance().getDevCatById(libid, catid);
if(cat==null)
{
	out.print("no cat found") ;
	return ;
}

DevDef dev=  null ;
String devid = request.getParameter("devid") ;
if(Convert.isNotNullEmpty(devid))
{
	dev = cat.getDevDefById(devid) ;
	if(dev==null)
	{
		out.print("no dev found") ;
		return ;
	}
	
}

switch(op)
{
case "dev_add":
case "dev_edit":
	String name = request.getParameter("name") ;
	String title = request.getParameter("title") ;
	String drv = request.getParameter("drv") ;
	try
	{
		if(dev!=null)
			cat.updateDevDef(devid, name, title, "", drv);
		else
			dev = cat.addDevDef(name, title, "",drv) ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break;
case "chg":
	break;
case "dev_del":
	if(!Convert.checkReqEmpty(request, out, "devid"))
		return ;
	
	cat.delDevDef(devid);
	out.print("succ") ;
	break;
case "list":
	/*
	List<DevCat> cats = dd.getDevCats() ;
	out.print("[") ;
	boolean bfirst = true; 
	for(DevCat dc:cats)
	{
		if(bfirst)
			bfirst=false;
		else
			out.print(",");
		out.print("{\"id\":\""+dc.getId()+"\",\"n\":\""+dc.getName() +"\",\"t\":\""+dc.getTitle() +"\"}");
	}
	out.print("]") ;
	*/
	break ;
}
%>