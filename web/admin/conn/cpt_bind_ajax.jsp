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
case "list":
	if(!Convert.checkReqEmpty(request, out, "idx","size"))
		return;
	int idx = Convert.parseToInt32(request.getParameter("idx"), 0) ;
	int size = Convert.parseToInt32(request.getParameter("size"), -1) ;
	String sk = request.getParameter("sk") ;
	try
	{
		cpt.writeBindBeSelectedListRows(out,sk, idx, size);
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break;
case "clear_cache":
	cpt.clearBindBeSelectedCache();
	out.print("succ");
	break ;
case "import":
	if(!Convert.checkReqEmpty(request, out, "txt"))
		return;
	String txt = request.getParameter("txt") ;
	boolean bcreate_tag = true;//"true".equals(request.getParameter(auto_create_tag))
	boolean bignore_null = true;//"true".equals(request.getParameter(ignore_null))
	//ignore_null:ignore_null,auto_create_tag
	StringBuilder ressb = new StringBuilder() ;
	int cc = cpt.importBindMap(txt,bcreate_tag,ressb);
	String res = ressb.toString() ;
	if(Convert.isNullOrEmpty(res))
	{
		out.println("succ="+cc) ;
	}
	else
	{
		out.println("import bind number="+cc+"\r\n"+res) ;
	}
	break ;
case "tmp_paths_vals":
	if(!Convert.checkReqEmpty(request, out, "paths"))
		return;
	String paths = request.getParameter("paths") ;
	List<String> ps = Convert.splitStrWith(paths, ",|") ;
	HashMap<String,String> p2vstr = cpt.Tmp_readValStrsByPaths(ps) ;
	if(p2vstr==null)
	{
		out.print("{}") ;
		break ;
	}
	out.print("{");
	boolean bfirst = true ;
	for(Map.Entry<String,String> p2v:p2vstr.entrySet())
	{
		if(bfirst) bfirst=false;
		else out.print(",") ;
		out.print("\""+p2v.getKey()+"\":\""+p2v.getValue()+"\"") ;
	}
	out.print("}");
	break ;
case "tree":
	boolean blist = "true".equalsIgnoreCase(request.getParameter("list")) ;
	boolean brefresh = "true".equalsIgnoreCase(request.getParameter("refresh")) ;
	try
	{
		cpt.writeBindBeSelectedTreeJson(out,blist,brefresh);
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break;
case "set_binds":
	try
	{
		//String bindidstr = request.getParameter("bindids") ;
		String mapstr =  request.getParameter("mapstr") ;
		//List<String> bindids = Convert.splitStrWith(bindidstr, "|") ;
		Map<String,String> bm = Convert.transPropStrToMap(mapstr,"|","=") ;
		//cpt.setBindList(bindids);
		cpt.setBindMapTag2Conn(bm,true,true) ;
		cpt.getConnProvider().save();
		out.print("succ");
	}
	catch(Exception e)
	{
		e.printStackTrace();
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
