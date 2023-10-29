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
if(!Convert.checkReqEmpty(request, out, "path","op"))
	return ;
String op = request.getParameter("op");
String path = request.getParameter("path") ;
//String sub_nid = request.getParameter("sub_nid");
String sub_nid = request.getParameter("id");
//System.out.println("path="+path+" id="+sub_nid) ;
if("#".equals(sub_nid))
	sub_nid = null ;
UANode n = UAUtil.findNodeByPath(path) ;
JsSubOb sub_ob = null ;
String path_title = "" ;
if(n==null)
{
	out.print("no node found") ;
	return ;
}

if(!(n instanceof UANodeOCTagsCxt))
{
	out.print("not node oc cxt") ;
	return ;
}

UANodeOCTagsCxt cxt_n = (UANodeOCTagsCxt)n;

if(Convert.isNotNullEmpty(sub_nid))
{
	sub_ob = cxt_n.JS_CXT_get_sub_by_id(sub_nid) ;
	if(sub_ob==null)
	{
		out.print("no js sub found");
		return ;
	}
}

switch(op)
{
case "sub_json":
	if(sub_ob==null)
	{
		//out.write(",\"state\": {\"opened\": true}");
		//out.write(",\"children\":[");
		out.write("[");
		List<JsSub> jps = cxt_n.JS_CXT_get_root_subs();
		boolean bfirst = true;
		for(JsSub jp:jps)
		{
			if (bfirst)
				bfirst = false;
			else
				out.write(',');
			jp.writeTree(null, out) ;
		}
		out.write("]");
	}
	else
	{
		out.write("[");
		JSObMap subv = (JSObMap)sub_ob.getSubVal() ;
		List<JsSub> jps = subv.JS_get_subs();//.JS_props();//.JS_get_props_cxt();
		boolean bfirst = true;
		for(JsSub jp:jps)
		{
			if (bfirst)
				bfirst = false;
			else
				out.write(',');
			jp.writeTree(sub_nid, out) ;
		}
		out.write("]");
	}
	return ;
case "sub_detail":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	JsSub jssub = sub_ob.getJsSub() ;
	Object subv = sub_ob.getSubVal() ;
%>
	<div><%=jssub.getSubTitle()%> - <%=jssub.getDesc() %><br> <%=jssub.getTitle() %></div>
<%
	return ;
default:
	break ;
}

//out.write("}");

%>