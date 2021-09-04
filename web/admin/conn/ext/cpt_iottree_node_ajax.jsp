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
ConnPtIOTTreeNode cpt = null ;


if(Convert.isNullOrEmpty(connid))
{
	out.print("no connid input") ;
	return;
}

cpt = (ConnPtIOTTreeNode)cp.getConnById(connid) ;
if(cpt==null)
{
	out.print("no connection found") ;
	return ;
}

switch(op)
{
case "syn_tree":
	cpt.getCaller().callShareTree();
	out.print("send syn tree");
	return ;
}
%>unknown op