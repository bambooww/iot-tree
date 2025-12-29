<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.ai.*,
	org.iottree.core.msgnet.nodes.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "op"))
		return ;
	
	String op = request.getParameter("op") ;
String host = request.getParameter("host") ;
int port = Convert.parseToInt32(request.getParameter("port"), -1);
switch(op)
{
case "list_models":
	if(!Convert.checkReqEmpty(request, out, "host","port"))
		return ;
	if(port<=0)
	{
		out.print("invalid port="+port) ;return ;
	}
	JSONObject retjo = VLLMCtrl_M.RT_readModelList(host,port) ;
	if(retjo==null)
	{
		out.print("list models failed") ;return;
	}
	retjo.write(out) ;
	return ;
default:
	return ;
}
%>