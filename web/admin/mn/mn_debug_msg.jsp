<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*,
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.node.*,
				org.iottree.core.router.*,org.iottree.ext.roa.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%><%
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(600,500);
</script>
<style>
.conf
{
	position: relative;
	width:90%;
	height:30px;
	border:1px solid ;
}
</style>
</head>
<body>
<form class="layui-form" action="">
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>date</w:g>:</label>
    <div class="layui-input-inline" style="width: 150px;">
      <input type="text" id="dt" name="dt" value=""  autocomplete="off"  class="layui-input" >
    </div>
 </div>
 <div class="layui-form-item">
    <div class="layui-form-label"><w:g>data</w:g>:</div>
	  <div class="layui-input-inline" style="width:450px;font-size:10px;">
	    <textarea type="text" id="d" name="d" style="height:300px;" autocomplete="off" class="layui-input"></textarea>
	  </div>
 </div>
</form>
</body>
<script type="text/javascript">

var pm = dlg.get_opener_opt("pm") ;
console.log(pm);
if(pm)
{
	if(pm.time_ms)
		$("#dt").val(new Date(pm.time_ms).format("yyyy-MM-dd hh:mm:ss")) ;
	
	$("#d").val(JSON.stringify(pm, null, '\t'));
}

layui.use('form', function(){
	  var form = layui.form;
	  
	  
	  form.render();
	});

function win_close()
{
	dlg.close(0);
}


</script>
</html>