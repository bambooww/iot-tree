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
case "cat_add":
	String title = request.getParameter("title") ;
	if(Convert.isNullOrEmpty(title))
		title = "noname" ;
	CompCat cc = compmgr.addCat(title) ;
	out.print("{id:'"+cc.getId()+"',title:'"+Convert.plainToJsStr(cc.getTitle())+"'}") ;
	return ;
case "comp_add":
	if(!Convert.checkReqEmpty(request, out, "catid"))
		return;
	String catid = request.getParameter("catid") ;
	title = request.getParameter("title") ;
	if(Convert.isNullOrEmpty(title))
		title = "noname" ;
	cc = compmgr.getCatById(catid) ;
	if(cc==null)
	{
		out.print("no cat found!") ;
		return ;
	}
	CompItem ci = compmgr.addComp(cc, title) ;
	out.print("{id:'"+ci.getId()+"',title:'"+Convert.plainToJsStr(ci.getTitle())+"'}") ;
	return ;
case "comp_list":
	long st = System.currentTimeMillis() ;
	if(!Convert.checkReqEmpty(request, out, "catid"))
		return;
	catid = request.getParameter("catid") ;
	cc = compmgr.getCatById(catid) ;
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
	ci = compmgr.getItemById(compid) ;
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
case "comp_txt_save":
	if(!Convert.checkReqEmpty(request, out, "catid","id"))
		return;
	catid = request.getParameter("catid") ;
	id = request.getParameter("id") ;
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
	txt = request.getParameter("txt") ;
	ci.saveCompData(txt);
	out.print("save ok") ;
	break;
}

%>