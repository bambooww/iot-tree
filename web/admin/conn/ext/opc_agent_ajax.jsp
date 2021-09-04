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
if(!Convert.checkReqEmpty(request, out, "prjid","op","cpid","connid"))
	return;
String repid = request.getParameter("prjid") ;
String op = request.getParameter("op");
String cpid = request.getParameter("cpid") ;

ConnProOpcAgent cp = (ConnProOpcAgent)ConnManager.getInstance().getConnProviderById(repid, cpid);
if(cp==null)
{
	out.print("no ConnProOpcAgent found with id="+cpid);
	return ;
}

String connid = request.getParameter("connid") ;
ConnPtOpcAgent cpt = (ConnPtOpcAgent)cp.getConnById(connid) ;
if(cpt==null)
{
	out.print("no connection found") ;
	return ;
}

switch(op)
{
case "progs":
	//cpt.opcBrowseNodeOut(out);
	break ;
case "tree":
	//cpt.writeUaNodeTreeJson(out);
	break;
case "sub_nodes":
	
	break;
}
%>
