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
	"%><%
if(!Convert.checkReqEmpty(request, out, "prjid","cptp","op"))
	return;
String op = request.getParameter("op") ;
String prjid = request.getParameter("prjid") ;
String cptp = request.getParameter("cptp") ;//ConnProOPCUA.TP;//
ConnProvider cp = ConnManager.getInstance().getOrCreateConnProviderSingle(prjid, cptp);
if(cp==null)
{
	String cpid = request.getParameter("cpid") ;
	if(Convert.isNotNullEmpty(cpid))
	{
		cp =  ConnManager.getInstance().getConnProviderById(prjid, cpid);
	}
}
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnPtMSG cpt = null ;


if(Convert.isNullOrEmpty(connid))
{
	out.print("no connid input") ;
	return;
}

cpt = (ConnPtMSG)cp.getConnById(connid) ;
if(cpt==null)
{
	out.print("no connection found") ;
	return ;
}

String topic = request.getParameter("topic") ;
String pl = request.getParameter("payload") ;
switch(op)
{
case "send":
	byte[] bs = null;
	if(pl!=null)
		bs = pl.getBytes("UTF-8");
	cpt.sendMsg(topic, bs) ;
	out.print("send ok");
	return ;
}
%>unknown op