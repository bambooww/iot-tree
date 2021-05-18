<%@ page language="java" contentType="text/json; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%!
%><%
if(!Convert.checkReqEmpty(request, out, "repid","op"))
	return;
String repid = request.getParameter("repid") ;
String op = request.getParameter("op") ;
switch(op)
{
case "list":
	List<ConnProvider> cps = ConnManager.getInstance().getConnProviders(repid) ;
	List<ConnJoin> cjs = ConnManager.getInstance().getConnJoins(repid) ;
%>{"conn_pts":[
<%
	boolean bf1=true;
	for(ConnProvider cp:cps)
	{
		if(bf1)bf1=false;
		else out.print(",");
			
		List<ConnPt> cpts = cp.listConns() ;
%>
{"id":"<%=cp.getId()%>","title":"<%=cp.getTitle()%>","tp":"<%=cp.getProviderType()%>","static_txt":"<%=cp.getStaticTxt()%>","connections":[
<%
		boolean bf = true;
		for(ConnPt cpt:cpts)
		{
			if(bf) bf=false;
			else out.print(",") ;
%>
		{"id":"<%=cpt.getId()%>","join":<%=cpt.hasJoinedCh()%>,"name":"<%=cpt.getName()%>","title":"<%=cpt.getTitle()%>","static_txt":"<%=cpt.getStaticTxt()%>","dyn_txt":"<%=cpt.getDynTxt()%>","dt":<%=cpt.getCreatedDT()%>}
<%
		}
%>
	]}
<%
	}
%>
],"joins":[
<%
	boolean bf2 = true;
	for(ConnJoin cj:cjs)
	{
		if(bf2)bf2=false;
		else out.print(",") ;
		String connid =cj.getConnId() ;
		String chid = cj.getChId() ;		
%>
		{"connid":"<%=connid%>","chid":"<%=chid%>"}
<%
	}
%>
]}
<%
	break ;
case "cp_set":
	String json = request.getParameter("json");
	//System.out.println("json="+json) ;
	try
	{
		ConnManager.getInstance().setConnProviderByJson(repid, json);
		out.print("{\"res\":true}");
	}
	catch(Exception e)
	{
		out.print("{\"res\":false,\"err\":\""+e.getMessage()+"\"}");
		return ;
	}
	break ;
case "cp_del":
	if(!Convert.checkReqEmpty(request, out, "cpid"))
		return;
	String cpid = request.getParameter("cpid") ;
	try
	{
		if(ConnManager.getInstance().delConnProvider(repid, cpid))
			out.print("{\"res\":true}");
		else
			out.print("{\"res\":false,\"err\":\"delete error\"}");
	}
	catch(Exception e)
	{
		out.print("{\"res\":false,\"err\":\""+e.getMessage()+"\"}");
		return ;
	}
	break ;
case "conn_set":
	if(!Convert.checkReqEmpty(request, out, "cpid","json"))
		return;
	cpid = request.getParameter("cpid") ;
	json = request.getParameter("json");
	//System.out.println("json="+json) ;
	try
	{
		ConnProvider cp = ConnManager.getInstance().getConnProviderById(repid, cpid) ;
		cp.setConnPtByJson(json);
		out.print("{\"res\":true}");
	}
	catch(Exception e)
	{
		out.print("{\"res\":false,\"err\":\""+e.getMessage()+"\"}");
		return ;
	}
	break ;
case "conn_del":
	if(!Convert.checkReqEmpty(request, out, "cpid","connid"))
		return;
	cpid = request.getParameter("cpid") ;
	String connid = request.getParameter("connid") ;
	try
	{
		ConnProvider cp = ConnManager.getInstance().getConnProviderById(repid, cpid) ;
		if(cp.delConnPt(connid))
			out.print("{\"res\":true}");
		else
			out.print("{\"res\":false,\"err\":\"delete error\"}");
	}
	catch(Exception e)
	{
		out.print("{\"res\":false,\"err\":\""+e.getMessage()+"\"}");
		return ;
	}
	break ;
case "join_add":
case "join_del":
	if(!Convert.checkReqEmpty(request, "{\"err\":\"no id input\"}", out,"connid","chid"))
		return;
	connid = request.getParameter("connid") ;
	String chid = request.getParameter("chid") ;
	if(!connid.startsWith("conn_") || !chid.startsWith("ch_"))
	{
		out.print("not conn_ or ch_ id") ;
		return ;
	}
	connid = connid.substring(5) ;
	chid = chid.substring(3) ;
	try
	{
		if("join_add".equals(op))
			ConnManager.getInstance().setConnJoin(repid, connid, chid) ;
		else if("join_del".equals(op))
			ConnManager.getInstance().delConnJoin(repid, connid) ;
		out.print("{\"res\":true}") ;
	}
	catch(Exception e)
	{
		out.print("{\"res\":false,\"err\":\""+e.getMessage()+"\"}") ;
		return ;
	}
	
	break ;
}

%>