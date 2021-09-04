<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.service.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	List<AbstractService> ass = ServiceManager.getInstance().listServices() ;
%>
<html>
<head>
<title>channel editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(600,400);
</script>
<style>
</style>
</head>
<body>
<%
for(AbstractService as:ass)
{
%>
<%=as.getTitle() %>  <a href="javascript:start_stop(true,'<%=as.getName()%>')">start</a> <a href="javascript:start_stop(false,'<%=as.getName()%>')">stop</a> <br>
<%
}
%>
</body>
<script type="text/javascript">

function start_stop(b,n)
{
	var op = "start" ;
	if(!b)
		op = "stop";
	$.ajax({
        type: 'post',
        url:'service_ajax.jsp',
        data: {op:op,n:n},
        async: true,  
        success: function (result) {  
        	if("ok"==result)
        	{
        		document.location.href=document.location.href ;
        	}
        	else
        	{
        		dlg.msg(result) ;
        	}
        },
        error:function(req,err,e)
        {
        	dlg.msg(e);
        }
    });
}

</script>
</html>