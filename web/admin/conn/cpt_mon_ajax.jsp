<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%><%!
final int LN_LEN = 120 ;
%><%
	if(!Convert.checkReqEmpty(request, out,"prjid", "cpid","cid","op"))
	return;
String op = request.getParameter("op") ;
String repid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
String connid = request.getParameter("cid") ;

ConnProvider cp = ConnManager.getInstance().getConnProviderById(repid, cpid) ;
if(cp==null)
{
	out.print("no ConnProvider found") ;
	return ;
}

ConnPt cpt = cp.getConnById(connid) ;
if(cpt==null)
{
	out.print("no ConnPt found") ;
	return ;
}

long last_dt = Convert.parseToInt64(request.getParameter("last_dt"), -1) ;
switch(op)
{
case "list":
	List<ConnPt.MonItem> monitems = cpt.getMonitorList(last_dt) ;
	ConnPt.MonItem first_mi = cpt.getMonItemFirst();
	ConnPt.MonItem last_mi = cpt.getMonItemLast();
	String minid = "",maxid="" ;
	if(first_mi!=null)
		minid = first_mi.getMonId() ;
	if(last_mi!=null)
		maxid = last_mi.getMonId() ;
	out.print("{\"min_id\":\""+minid+"\",\"max_id\":\""+maxid+"\",\"items\":[");
	boolean bfirst = true ;
	for(ConnPt.MonItem mi:monitems)
	{
		if(bfirst) bfirst=false;
		else out.print(",") ;
		mi.writeJsonOut(out,false) ;
	}
	out.print("]}") ;
	break ;
case "mitem":
	if(!Convert.checkReqEmpty(request, out,"id"))
		return;
	String id = request.getParameter("id") ;
	ConnPt.MonItem mi =  cpt.getMonItemById(id) ;
	if(mi==null)
	{
		out.print("no Mon Item found") ;
		return ;
	}
	mi.writeJsonOut(out,true) ;
	break ;
}
%>
