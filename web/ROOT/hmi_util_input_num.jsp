<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
String tp = request.getParameter("tp") ;
String strv = request.getParameter("v") ;
boolean bfloat = "true".equals(request.getParameter("float")) ;
double min = Convert.parseToDouble(request.getParameter("min"), 0) ;
double max = Convert.parseToDouble(request.getParameter("max"), 100) ;
Object val = null ;

if(bfloat)
	val = Convert.parseToDouble(strv, 0) ;
else
	val = Convert.parseToInt64(strv, 0) ;

%>	
<html>
<head>
<title>hmi input</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/jquery-ui.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/jquery-ui.css" />
<script src="/_js/jq_mobile/jquery.mobile-1.4.5.min.js"></script>
<link rel="stylesheet" href="/_js/jq_mobile/jquery.mobile-1.4.5.min.css" />
<script>
dlg.resize_to(700,350);

var bfloat = <%=bfloat%>
var min = <%=min%>;
var max = <%=max%> ;
var val = <%=val%>;

</script>
</head>
<body scroll="no">



<%
if("slide".equals(tp))
{
%>
<form style="height:150px">
    <label for="val" style="font-size:3em">Input Value:<span id="val_lb"><%=val %></span></label>
    <input type="range" name="val" id="val" data-highlight="true"
			min="<%=min %>" max="<%=max %>" value="<%=val%>" onchange="val_chged()">
</form>

 <%
}
 %>
 
</body>
<script type="text/javascript">

function val_chged()
{
	$("#val_lb").html($("#val").val());
}

function win_close()
{
	dlg.close(0);
}
function do_submit(cb)
{
	var v =  $("#val").val() ;
	if(bfloat)
		v = parseFloat(v);
	else
		v = parseInt(v) ;
	cb(true,v);
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>