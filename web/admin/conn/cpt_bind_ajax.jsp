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
	if(!Convert.checkReqEmpty(request, out, "prjid","cptp","op"))
	return;
String repid = request.getParameter("prjid") ;
String op = request.getParameter("op");

String cptp = request.getParameter("cptp") ; //reConnProOPCUA.TP;//
ConnProvider cp = ConnManager.getInstance().getOrCreateConnProviderSingle(repid, cptp);
if(cp==null)
{
	String cpid = request.getParameter("cpid") ;
	if(Convert.isNotNullEmpty(cpid))
	{
		cp =  ConnManager.getInstance().getConnProviderById(repid, cpid);
	}
}
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnPtBinder cpt = null ;


if(Convert.isNullOrEmpty(connid))
{
	out.print("no connid input") ;
	return;
}

cpt = (ConnPtBinder)cp.getConnById(connid) ;
if(cpt==null)
{
	out.print("no connection found") ;
	return ;
}

switch(op)
{
case "sub":
	//cpt.opcBrowseNodeOut(out);
	break ;
case "tree":
	boolean blist = "true".equalsIgnoreCase(request.getParameter("list")) ;
	try
	{
		cpt.writeBindBeSelectedTreeJson(out,blist);
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break;
case "set_binds":
	try
	{
		String bindidstr = request.getParameter("bindids") ;
		String mapstr =  request.getParameter("mapstr") ;
		List<String> bindids = Convert.splitStrWith(bindidstr, "|") ;
		Map<String,String> bm = Convert.transPropStrToMap(mapstr,"|","=") ;
		cpt.setBindList(bindids);
		cpt.setBindMapTag2Conn(bm) ;
		cpt.getConnProvider().save();
		out.print("succ");
	}
	catch(Exception e)
	{
		out.print(e.getMessage());
	}
	break ;
case "syn_bind_tags":
	// create group and tags in channel by bind list
	try
	{
		String bindidstr = request.getParameter("bindids") ;
		List<String> bindids = Convert.splitStrWith(bindidstr, "|") ;
		UACh ch = cpt.getJoinedCh() ;
		if(ch==null)
		{
			out.print("no joined channel") ;
			return ;
		}
		bindids = cpt.transBindIdsToConnLeafPath(bindids) ;
		ch.Path_refreshByPathList(bindids);
		out.print("succ") ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage());
	}
	break ;
case "sub_nodes":
	if(!Convert.checkReqEmpty(request, out, "nodeid"))
		return;
	String nodeid = request.getParameter("nodeid");
	//cpt.writeSubUaVarNodeJson(out,nodeid);
	break;
}
%>
