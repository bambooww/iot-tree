<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.json.*,
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
String jstr = request.getParameter("jstr") ;
JSONObject inputjo = null ;
if(Convert.isNotNullEmpty(jstr))
	inputjo = new JSONObject(jstr) ;
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
	if(!Convert.checkReqEmpty(request, out, "jstr"))//"val_style","gather_intv","keep_days","min_rec_intv"))
		return;
		
		RecTagParam rtp = RecTagParam.fromJO(recmgr, inputjo) ;
		/*
	int valsty = Convert.parseToInt32(request.getParameter("val_style"), 0) ;
	RecValStyle vsty = RecValStyle.valOfInt(valsty) ;
	if(vsty==null)
	{
		out.print("invalid value style="+valsty) ;
		return ;
	}
	long gather_intv = Convert.parseToInt64(request.getParameter("gather_intv"), -1) ;
	int keep_days =  Convert.parseToInt32(request.getParameter("keep_days"), -1) ;
	long min_rec_intv = Convert.parseToInt32(request.getParameter("min_rec_intv"), -1) ;
	RecTagParam rtp = new RecTagParam(recmgr,tag,gather_intv,vsty,keep_days,min_rec_intv) ;
	*/
	try
	{
		
		//(RecManager belongto,UATag tag,long gather_intv,RecValStyle vstyle,int keep_days)
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
case "list_pros":
	if(!Convert.checkReqEmpty(request,  out, "val_style"))
		return ;
	int valsty = Convert.parseToInt32(request.getParameter("val_style"), -1) ;
	RecValStyle vsty = RecValStyle.valOfInt(valsty) ;
	if(vsty==null)
	{
		out.print("invalid value style="+valsty) ;
		return ;
	}
	List<RecProL1> pros = recmgr.listFitRecProsByValStyle(vsty) ;
	JSONArray jarr = new JSONArray() ;
	if(pros!=null)
	{
		for(RecProL1 rp:pros)
		{
			jarr.put(rp.toListJO()) ;
		}
	}
	jarr.write(out) ;
	return ;
default:
	break ;
}
%>