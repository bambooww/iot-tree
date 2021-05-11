<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
if(!Convert.checkReqEmpty(request, out, "op"))
	return;
String op = request.getParameter("op") ;
CompManager compmgr = CompManager.getInstance() ;
switch(op)
{

case "comp_list":
	long st = System.currentTimeMillis() ;
	if(!Convert.checkReqEmpty(request, out, "catid"))
		return;
	String catid = request.getParameter("catid") ;
	CompCat cc = compmgr.getCatById(catid) ;
	if(cc==null)
	{
		out.print("no cat found!") ;
		return ;
	}
	
	List<CompItem> items = cc.getItems() ;
	out.print("[") ;
	boolean bfirst = true;
	for(CompItem tmpci : items)
	{
		if(bfirst)bfirst = false;
		else out.print(",") ;
		out.print("{id:'"+tmpci.getId()+"',title:'"+Convert.plainToJsStr(tmpci.getTitle())+"'}") ;
	}
	out.print("]");
	//System.out.println("cose==="+ (System.currentTimeMillis()-st)) ;
	break ;
case "comp_load":
	if(!Convert.checkReqEmpty(request, out, "compid"))
		return;
	
	String compid = request.getParameter("compid") ;
	CompItem ci = compmgr.getItemById(compid) ;
	if(ci==null)
	{
		out.print("no comp found") ;
		return ;
	}
	String txt = ci.getOrLoadCompData() ;
	out.print(txt) ;
	break;
case "comp_txt":
	if(!Convert.checkReqEmpty(request, out, "catid","id"))
		return;
	catid = request.getParameter("catid") ;
	String id = request.getParameter("id") ;
	cc = compmgr.getCatById(catid) ;
	if(cc==null)
	{
		out.print("no cat found!") ;
		return ;
	}
	ci = cc.getItemById(id);
	if(ci==null)
	{
		out.print("no comp found") ;
		return ;
	}
	txt = ci.getOrLoadCompData() ;
	out.print(txt) ;
	break;

}

%>