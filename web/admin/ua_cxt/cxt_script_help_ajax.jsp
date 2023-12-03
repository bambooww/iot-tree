<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.json.*,
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

boolean no_parent = "true".equals(request.getParameter("no_parent")) ;
boolean no_this = "true".equals(request.getParameter("no_this")) ;
String pm_objs = request.getParameter("pm_objs") ;
JSONObject pm_jo = null ;
if(Convert.isNotNullEmpty(pm_objs))
{
	pm_objs = URLDecoder.decode(pm_objs, "UTF-8") ;
	pm_jo = new JSONObject(pm_objs) ;
}

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

JsEnv jsenv = null;
if(pm_jo!=null)
{
	jsenv = new JsEnv() ;
	jsenv.asPmJO(pm_jo) ;
}

try
{
	if(jsenv!=null)
		JsEnv.setInThLoc(jsenv) ;

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
			List<JsSub> jps = null;
			if(no_parent)
				jps = cxt_n.JS_CXT_get_root_subs(no_parent,no_this) ;
			else
				jps = cxt_n.JS_CXT_get_root_subs();
			
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
}
finally
{
	if(jsenv!=null)
		JsEnv.delInThLoc() ;
}
//out.write("}");

%>