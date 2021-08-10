<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.eclipse.milo.opcua.stack.core.types.structured.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*,
				org.eclipse.milo.opcua.sdk.client.nodes.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%!
	
	%><%
if(!Convert.checkReqEmpty(request, out, "prjid","op"))
	return;
String repid = request.getParameter("prjid") ;
String op = request.getParameter("op");
String cptp = ConnProOPCUA.TP;//request.getParameter("cptp") ;
ConnProOPCUA cp = (ConnProOPCUA)ConnManager.getInstance().getOrCreateConnProviderSingle(repid, cptp);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnPtOPCUA cpt = null ;


if(Convert.isNullOrEmpty(connid))
{
	out.print("no connid input") ;
	return;
}

cpt = (ConnPtOPCUA)cp.getConnById(connid) ;
if(cpt==null)
{
	out.print("no connection found") ;
	return ;
}

switch(op)
{
case "sub":
	cpt.opcBrowseNodeOut(out);
	break ;
case "tree":
	cpt.writeUaNodeTreeJson(out, null);
	break;
case "sub_nodes":
	if(!Convert.checkReqEmpty(request, out, "nodeid"))
		return;
	String nodeid = request.getParameter("nodeid");
	cpt.writeSubUaVarNodeJson(out,nodeid);
	break;
}
%>
