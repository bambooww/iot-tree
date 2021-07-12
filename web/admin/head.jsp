<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page
	import="java.util.*,
				java.io.*,
				java.net.*,
				org.iottree.core.*,
				org.iottree.core.util.*
				"%><%
String ver = Config.getVersion() ;
				boolean oc = "true".equals(request.getParameter("oc"));
%><script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js?v=<%=ver%>"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
<%
if(oc)
{
%>
<script src="/_js/oc/oc.js?v=<%=ver%>"></script>
<script src="/_js/oc/oc_util.js?v=<%=ver%>"></script>
<link type="text/css" href="/_js/oc/oc.css?v=<%=ver%>" rel="stylesheet" />
<%
}
%>