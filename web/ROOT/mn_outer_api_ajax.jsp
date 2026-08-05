<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,org.json.*,
	org.iottree.core.util.*,
	org.iottree.core.res.*,
	org.iottree.web.oper.*,org.iottree.core.msgnet.*,
	org.iottree.core.comp.*,org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
	if(!Convert.checkReqEmpty(request, out, "op"))
		return ;
	String op = request.getParameter("op") ;
	if(op==null)
		op= "" ;
	
	List<String> sub_uids = Convert.splitStrWith(request.getParameter("sub_uids"),",") ;
	
	String prj_n = request.getParameter("prj_n") ;
	UAPrj prj = null ;
	if(Convert.isNotNullEmpty(prj_n))
	{
		prj = UAManager.getInstance().getPrjByName(prj_n) ;
		if(prj==null)
		{
			out.print("no prj found") ;
			return ;
		}
	}
	String net_n = request.getParameter("net_n") ;
	MNNet net = null ;
	if(Convert.isNotNullEmpty(net_n))
	{
		net = prj.getMNManager().getNetByName(net_n) ;
		if(net==null)
		{
			out.print("no net found") ;
			return ;
		}
	}
	String node_n = request.getParameter("node_n") ;
	MNBase node = null ;
	LinkedHashMap<String,MNBase.OuterApi> use_apis = null ;
	if(Convert.isNotNullEmpty(node_n))
	{
		node = net.getItemByName(node_n) ;
		if(node==null)
		{
			out.print("no node found") ;
			return ;
		}
		
		use_apis = node.getUsingOuterApis() ;
	}
	String api_n = request.getParameter("api_n") ;
	MNBase.OuterApi api = null ;
	if(Convert.isNotNullEmpty(api_n))
	{
		api = node.getOuterApi(api_n);
		if(api==null)
		{
			out.print("no api found") ;
			return ;
		}
	}
	
	switch(op)
	{
	case "api_detail":
		if(!Convert.checkReqEmpty(request, out, "prj_n","net_n","node_n","api_n"))
			return ;

	String color = "red" ;
	JSONObject[] inout = node.getOuterApiIOSample(api_n) ;
	boolean bopen = use_apis.containsKey(api_n) ;
	if(bopen)
		color="green" ;
	String url = MNBase.OuterApi.MN_OUTER_API_PRE+ "/"+prj_n+"/"+net_n+"/"+node_n+"/"+api_n+"</span>" ;

%>
<blockquote class="layui-elem-quote">
  <span style="color:<%=color%>;"><span class="pre"></span><%=url %></span>
  <%=api.getName() %> <%=api.getTitle() %>
  <%=api.getDesc() %>
</blockquote>
<div>
 <code><pre>
<%
if(inout!=null)
{
	if(inout.length>=1 && inout[0]!=null)
	{
%><br>In:<%=inout[0]%><%
	}
	if(inout.length>=2 && inout[1]!=null)
	{
%><br>Out:<%=inout[1]%><%
	}
}

%>
 </pre></code></div>
<%
		return ;
	case "sub_apis":
		JSONArray sub_apis = MNBase.OuterApi.listSubApiJArr(prj_n, net_n, node_n) ;
		sub_apis.write(out) ;
		return ;
	case "sub_api_sample":
		JSONArray[] sub_api_s = MNBase.OuterApi.listSubApiInOutSample(prj_n,net_n,node_n,sub_uids) ;
		StringBuilder buff = new StringBuilder() ;
		buff.append("\r\n// client post to server JSON format sample\r\n");
		buff.append("<textarea>"+sub_api_s[0].toString(4)+"</textarea>") ;
		buff.append("\r\n\r\n// server response to client JSON format sample\r\n");
		buff.append("<textarea>"+sub_api_s[1].toString(4)+"</textarea>") ;
		out.print(buff.toString()) ;
		break;
	case "api_sample":
		if(!Convert.checkReqEmpty(request, out, "prj_n","net_n","node_n","api_n"))
			return ;
		JSONObject[] api_sps = api.getApiSample();
		buff = new StringBuilder() ;
		buff.append("\r\n// client post to server JSON format sample");
		if(api_sps!=null&&api_sps.length>0 && api_sps[0]!=null)
		{
			buff.append("<textarea>"+api_sps[0].toString(4)+"</textarea>") ;
		}
		else
		{
			buff.append("<textarea>{}</textarea>") ;
		}
		
		buff.append("\r\n\r\n// server response to client JSON format sample");
		if(api_sps!=null&&api_sps.length>1 && api_sps[1]!=null)
		{
			buff.append("<textarea>"+api_sps[1].toString(4)+"</textarea>") ;
		}
		else
		{
			buff.append("<textarea>{}</textarea>") ;
		}
		out.print(buff.toString()) ;
		return ;
	default:
		out.print("unknown op");
		break;
	}%>
