<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.*,
	org.iottree.core.*,
	org.iottree.core.store.record.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*
	"%>
<%//通信节点下挂载的设备
if(!Convert.checkReqEmpty(request, out, "op","prjid","tag"))
	return;
String op = request.getParameter("op");
String prjid = request.getParameter("prjid") ;
String path=request.getParameter("tag");

UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
UATag tag = (UATag)prj.getDescendantNodeByPath(path) ;
if(tag==null)
{
	out.print("no tag node found") ;
	return ;
}
RecManager recmgr = RecManager.getInstance(prj) ;

switch(op)
{
case "set":
	if(!Convert.checkReqEmpty(request, out, "val_style","gather_intv"))
		return;
	int valsty = Convert.parseToInt32(request.getParameter("val_style"), 0) ;
	RecValStyle vsty = RecValStyle.valOfInt(valsty) ;
	if(vsty==null)
	{
		out.print("invalid value style="+valsty) ;
		return ;
	}
	long gather_intv = Convert.parseToInt64(request.getParameter("gather_intv"), -1) ;
	
	try
	{
		RecTagParam rtp = new RecTagParam(tag,gather_intv,vsty) ;
		recmgr.setRecTagParam(path, rtp) ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	return ;
case "unset":
	try
	{
		recmgr.setRecTagParam(path, null) ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	return  ;
default:
	break ;
}
%>