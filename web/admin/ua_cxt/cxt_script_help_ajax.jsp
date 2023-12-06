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
	org.iottree.core.plugin.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*
	"%><%!
	public boolean checkJsObj(Class<?> c)
	{
		if(c.isPrimitive()) return false;
		if(c == String.class) return false;
		if(PlugJsApi.class.isAssignableFrom(c))
			return false;
		
		return true ;
	}
	%><%
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
		//Object subv = sub_ob.getSubVal() ;
		
	%>
		<div><%=jssub.getSubTitle()%> - <%=jssub.getDesc() %><br> <%=jssub.getTitle() %></div>
		
		<br><br>Help For:
	<%
		if(jssub instanceof JsProp)
		{
			JsProp jsp = (JsProp)jssub ;
			Class<?> vt_c = jsp.getValTp() ;
			if(checkJsObj(vt_c))
			{
				String cn = vt_c.getSimpleName() ;
				String fcn = vt_c.getCanonicalName() ;
%><button onclick="javascript:open_help_ob('<%=fcn%>')"><%=cn %></button><%
			}
		}
		else if(jssub instanceof JsMethod)
		{
			JsMethod jsm = (JsMethod)jssub ;
			Class<?> ret_vt_c = jsm.getReturnValTp() ;
			if(checkJsObj(ret_vt_c))
			{
				String cn = ret_vt_c.getSimpleName();
				String fcn = ret_vt_c.getCanonicalName() ;
%><button onclick="open_help_ob('<%=fcn%>')"><%=cn %></button><%
			}
			Class<?>[] pmcs = jsm.getParamsValTp() ;
			if(pmcs!=null)
			{
				for(Class<?> c : pmcs)
				{
					if(!checkJsObj(c)) continue ;
					String cn = c.getSimpleName();
					String fcn = c.getCanonicalName() ;
%><button onclick="open_help_ob('<%=fcn%>')"><%=cn %></button><%
				}
			}
		}
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