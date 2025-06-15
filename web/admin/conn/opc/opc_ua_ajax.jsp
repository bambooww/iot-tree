<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,org.json.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.eclipse.milo.opcua.stack.core.types.structured.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*,
				org.eclipse.milo.opcua.sdk.client.nodes.*,
				org.eclipse.milo.opcua.stack.core.security.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%!
	
	%><%
if(!Convert.checkReqEmpty(request, out,"op"))
	return;
String prjid = request.getParameter("prjid") ;
String op = request.getParameter("op");
String uri = request.getParameter("uri");
String cptp = ConnProOPCUA.TP;//request.getParameter("cptp") ;
ConnProOPCUA cp = null;
if(Convert.isNotNullEmpty(prjid))
{
	cp = (ConnProOPCUA)ConnManager.getInstance().getOrCreateConnProviderSingle(prjid, cptp);
	if(cp==null)
	{
		out.print("no single provider found with "+cptp);
		return ;
	}
}

//String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnPtOPCUA cpt = null ;


if(Convert.isNotNullEmpty(connid))
{
	cpt = (ConnPtOPCUA)cp.getConnById(connid) ;
	if(cpt==null)
	{
		out.print("no connection found") ;
		return ;
	}
}

switch(op)
{
case "endpts_find":
	if(!Convert.checkReqEmpty(request, out,"uri"))
		return;
	try
	{
		LinkedHashMap<String,ConnPtOPCUA.ServerPk> spks = ConnPtOPCUA.findServerPks(uri) ;
		JSONArray jarr = new JSONArray() ;
		for(ConnPtOPCUA.ServerPk spk:spks.values())
		{
			jarr.put(spk.toJO()) ;
		}
		//List<ApplicationDescription> servers = DiscoveryClient.findServers(uri).get();
		/*
		List<EndpointDescription> epts = ConnPtOPCUA.getEndpointsByUri(uri) ;
		JSONArray jarr = new JSONArray() ;
		for(EndpointDescription ep:epts)
		{
			ApplicationDescription appd = ep.getServer() ;
			String appname = appd.getApplicationName().text() ;
			String epu = ep.getEndpointUrl();
			String sm = ep.getSecurityMode().getName() ;
			//ep.get
			String stt = ep.getSecurityPolicyUri() ;
			SecurityPolicy sp = SecurityPolicy.fromUri(stt) ;
			System.out.println(appname+" "+epu+" "+sm+" "+sp.name()) ;
			jarr.put(epu) ;
		}
		
		*/
		jarr.write(out) ;
	}
	catch(Exception ee)
	{
		//ee.printStackTrace();
		out.print(ee.getMessage()) ;
	}
	return ;
case "sub":
	if(cpt!=null)
		cpt.opcBrowseNodeOut(out);
	break ;
case "tree":
	if(cpt!=null)
		cpt.writeUaNodeTreeJson(out,null);
	break;
case "sub_nodes":
	if(!Convert.checkReqEmpty(request, out, "nodeid"))
		return;
	String nodeid = request.getParameter("nodeid");
	cpt.writeSubUaVarNodeJson(out,nodeid);
	break;
}
%>
