<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.*,
	org.iottree.core.*,
	org.iottree.core.cxt.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*
	"%><%
if(!Convert.checkReqEmpty(request, out, "path"))
	return ;
String path = request.getParameter("path") ;
String sub_nid = request.getParameter("sub_nid");
UANode n = UAUtil.findNodeByPath(path) ;
UANode subn = null ;
String path_title = "" ;
if(n==null)
{
	out.print("no node found") ;
	return ;
}

if(!(n instanceof UANodeOCTags))
{
	out.print("not node oc tags") ;
	return ;
}

UANodeOCTags cxt_n = (UANodeOCTags)n;

if(Convert.isNotNullEmpty(sub_nid))
{
	subn = n.findNodeById(sub_nid);
	if(subn==null)
	{
		out.print("no sub node found");
		return ;
	}
}


out.write("{\"id\":\"lib_and_cats\"");
out.write(",\"nc\":0");
out.write(",\"icon\": \"fa-solid fa-puzzle-piece fa-lg\"");
out.write(",\"text\":\"HMI Lib and Category\"");


//path_title = n.getNodePathTitle()+" "+n.getNodePath() ;
path_title = n.getNodePath() ;
if(subn==null)
{
	out.write(",\"state\": {\"opened\": true}");
	out.write(",\"children\":[");
	List<JsProp> jps = cxt_n.JS_get_props_cxt();
	boolean bfirst = true;
	for(JsProp jp:jps)
	{
		if (bfirst)
			bfirst = false;
		else
			out.write(',');
		
	}
	out.write("]");
}
else
{
	
}

out.write("}");

%>