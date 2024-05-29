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
dlg.resize_to(600,600);
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
    <div class="layui-form-mid" id="gap_now"></div>
 </div>
 <div class="layui-form-item">
    <div class="layui-form-label"><w:g>content</w:g>:</div>
	  <div class="layui-input-inline" style="width:450px;font-size:10px;">
	    <textarea type="text" id="d" name="d" style="height:200px;" autocomplete="off" class="layui-input"></textarea>
	  </div>
 </div>
 <div class="layui-form-item">
    <div class="layui-form-label"><w:g>exception</w:g>:</div>
	  <div class="layui-input-inline" style="width:450px;font-size:10px;">
	    <textarea type="text" id="e" name="e" style="height:200px;" autocomplete="off" class="layui-input"></textarea>
	  </div>
 </div>
</form>
</body>
<script type="text/javascript">

var pm = dlg.get_opener_opt("pm") ;
if(pm)
{
	if(pm.dt)
		$("#dt").val(new Date(pm.dt).format("yyyy-MM-dd hh:mm:ss")) ;
	if(pm.gap_now)
		$("#gap_now").html(pm.gap_now) ;
	if(pm.content)
	{
		$("#d").val(pm.content);
	}
	if(pm.exception)
		$("#e").val(pm.exception) ;
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