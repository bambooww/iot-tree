<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.util.web.*,
	org.iottree.core.util.xmldata.*
"%><%!

%><%
	//response.sendRedirect("/admin") ;
UAManager uamgr = UAManager.getInstance();
List<UAPrj> prjs = uamgr.listPrjs();
if(prjs==null||prjs.size()<=0)
{
	response.sendRedirect("/admin");
	return ;
}

UAPrj uprj = uamgr.getPrjDefault() ;
if(uprj!=null)
{
	UAHmi hmi = uprj.getHmiMain() ;
	if(hmi!=null)
	{
		response.sendRedirect(hmi.getNodePath());
		return ;
	}
}

%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <title></title>
  <link rel="stylesheet" href="/_js/layui/css/layui.css">
</head>
<body class="layui-layout-body">
 	Welcome to IOT-Tree Server
 	
</body>
</html>