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
		org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
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
if(!Convert.checkReqEmpty(request, out, "op"))
	return ;
String op = request.getParameter("op");

String prjid = request.getParameter("prjid");
String netid = request.getParameter("netid") ;
String itemid = request.getParameter("itemid") ;

String sub_nid = request.getParameter("id");

MNManager mnm= null;
UAPrj prj = null;
if(Convert.isNotNullEmpty(prjid))
{
	prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	mnm= MNManager.getInstance(prj) ;
}

MNNet net = null;
if(Convert.isNotNullEmpty(netid))
{
	net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
}

MNBase item = null;

if(Convert.isNotNullEmpty(itemid))
{
	item = net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
}

String pm_objs = request.getParameter("pm_objs") ;
JSONObject pm_jo = null ;
if(Convert.isNotNullEmpty(pm_objs))
{
	pm_objs = URLDecoder.decode(pm_objs, "UTF-8") ;
	pm_jo = new JSONObject(pm_objs) ;
}

if("#".equals(sub_nid))
	sub_nid = null ;
JsSubOb sub_ob = null ;

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
		sub_ob = item.JS_CXT_get_sub_by_id(sub_nid) ;
		if(sub_ob==null)
		{
			out.print("no js sub found");
			return ;
		}
	}
	
	switch(op)
	{
	case "sub_json":
		if(!Convert.checkReqEmpty(request, out, "prjid","netid","itemid"))
			return ;
		if(sub_ob==null)
		{
			//out.write(",\"state\": {\"opened\": true}");
			//out.write(",\"children\":[");
			out.write("[");
			List<JsSub> jps = item.JS_CXT_get_root_subs();
			
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
	case "tp_help":
		if(!Convert.checkReqEmpty(request, out, "tptp","tp"))
			return ;
		String tptp = request.getParameter("tptp") ;
		String tp = request.getParameter("tp") ;
		if("node".equals(tptp))
		{
			MNBase nnn = MNManager.getNodeByFullTP(tp) ;
			String ss = nnn.getTPDesc() ;
			//ss= Convert.plainToHtmlTitle(ss) ;
			out.print(ss) ;
		}
		else if("module".equals(tptp))
		{
			MNBase nnn = MNManager.getModuleByFullTP(tp) ;
			String ss = nnn.getTPDesc() ;
			//ss= Convert.plainToHtmlTitle(ss) ;
			out.print(Convert.plainToHtmlTitle(ss)) ;
		}
		break ;
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