<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page
	import="java.util.*,
				java.io.*,
				java.net.*,
				org.iottree.core.*,
				org.iottree.core.util.*
				"%><%
String ver = Config.getVersion() ;
				boolean bsimple = "true".equals(request.getParameter("simple"));
				boolean oc = "true".equals(request.getParameter("oc"));
				boolean oc_min= "true".equals(request.getParameter("oc_min"));
				boolean tree  ="true".equals(request.getParameter("tree"));
				boolean tab = "true".equals(request.getParameter("tab"));
				boolean mn = "true".equals(request.getParameter("mn"));
				boolean cm = "true".equals(request.getParameter("cm"));
				boolean sel_menu = "true".equals(request.getParameter("sel_menu")) ;
				boolean echarts = "true".equals(request.getParameter("echarts"));
	if(bsimple)
	{
%>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script defer src="/_js/font6/js/all.js"></script>
<link href="/_js/font6/css/all.css" rel="stylesheet">
<script src="/admin/js/util.js"></script>
<%
	}
	else
	{
%><script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js?v=<%=ver%>"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script defer src="/_js/font6/js/all.js"></script>
<link href="/_js/font6/css/all.css" rel="stylesheet">
<script src="/admin/js/util.js"></script>
<%--
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
 --%>
<%
}
if(oc)
{
%>
<script src="/_js/oc/hmi_util.js?v=<%=ver%>"></script>
<script src="/_js/oc/oc.js?v=<%=ver%>"></script>
<script src="/_js/oc/oc_util.js?v=<%=ver%>"></script>
<link type="text/css" href="/_js/oc/oc.css?v=<%=ver%>" rel="stylesheet" />
<%
}
if(oc_min)
{
%>
<script src="/_js/oc/oc.min.js?v=<%=ver%>"></script>
<script src="/_js/oc/oc_util.js?v=<%=ver%>"></script>
<link type="text/css" href="/_js/oc/oc.css?v=<%=ver%>" rel="stylesheet" />
<%
}
if(cm)
{
%>
<link href="/_js/cm/lib/codemirror.css" rel="stylesheet">
<script src="/_js/cm/lib/codemirror.js"></script>
  <link href="/_js/cm/addon/hint/show-hint.css" rel="stylesheet" >
  <script src="/_js/cm/addon/hint/show-hint.js"></script>
  <script src="/_js/cm/addon/hint/javascript-hint.js"></script>
  <script src="/_js/cm/mode/javascript/javascript.js"></script>
  <link href="/_js/cm/theme/eclipse.css" rel="stylesheet" >
<%
}
if(tree)
{
%>
<script src="/_js/jstree/jstree.min.js"></script>
<link rel="stylesheet" href="/_js/jstree/themes/default/style.min.css" />
<link type="text/css" href="/admin/inc/tree.css" rel="stylesheet" />
<%
}
if(sel_menu)
{
%>
<link rel="stylesheet" href="/_js/selectmenu/selectmenu.css" />
<script src="/_js/selectmenu/selectmenu.min.js"></script>
<%
}
if(echarts)
{
%>
<script type="text/javascript" src="/_js/echarts/echarts.min.js"></script>
<%
}
if(mn)
{
%><script src="/_js/mn/mn.js"></script>
<script type="text/javascript" src="/_js/bignumber.min.js"></script>
<script type="text/javascript" src="/_js/jquery.json.js"></script>
<%
}
%>