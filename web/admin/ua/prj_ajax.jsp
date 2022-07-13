<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.node.*,
				org.iottree.core.util.xmldata.*,
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
UAPrj dc = null;
switch(op)
{
case "add":
	StringBuilder errsb = new StringBuilder() ;

	try
	{
		dc = UAManager.getInstance().addPrj(name, title, desc) ;
		out.print("succ="+dc.getId()) ;
		return ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
		return ;
	}
case "edit":
	dc = UAManager.getInstance().updatePrj(id,name, title, desc);
	out.print("succ="+dc.getId()) ;
	break ;
case "del":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return;
	dc = UAManager.getInstance().getPrjById(id) ;
	if(dc==null)
	{
		out.print("ok") ;
		return ;
	}
	UAManager.getInstance().delPrj(id) ;
	out.print("ok") ;
	break ;
case "main":
	dc = UAManager.getInstance().getPrjById(id) ;
	if(dc==null)
	{
		out.print("no prj found") ;
		return ;
	}
	UAManager.getInstance().setPrjDefault(dc);
	out.print("ok");
	return ;
case "auto_start":
	dc = UAManager.getInstance().getPrjById(id) ;
	if(dc==null)
	{
		out.print("no prj found") ;
		return ;
	}
	boolean b = "true".equals(request.getParameter("auto_start")) ;
	dc.setAutoStart(b);
	out.print("ok");
	return;
case "start":
case "stop":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return;
	dc = UAManager.getInstance().getPrjById(id) ;
	if(dc==null)
	{
		out.print("no project found") ;
		return ;
	}
	if("start".equals(op))
		dc.RT_start();
	else
		dc.RT_stop();
	out.print(op+" ok") ;
	break ;
case "share":
	XmlData tmpxd = XmlData.parseFromHttpRequest(request, "dx_") ;
	boolean benable = "true".equalsIgnoreCase(request.getParameter("enable")) ;
	boolean bw =  "true".equalsIgnoreCase(request.getParameter("w")) ;
	long pushint = Convert.parseToInt64(request.getParameter("push_int"), 10000) ;
	PrjShareManager.getInstance().setSharer(id,benable,bw,pushint,tmpxd) ;
	out.print("ok");
	break ;
case "list_json":
	out.print("[");
	List<UAPrj> prjs = UAManager.getInstance().listPrjs();
	boolean bfirst = true;
	for(UAPrj p:prjs)
	{
		if(bfirst) bfirst=false;
		else out.print(",") ;
		
		out.print("{\"id\":\""+p.getId()+"\",\"title\":\""+p.getTitle()+"\"}");
	}
	out.print("]");
	break ;
}
%>