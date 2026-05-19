<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*,
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.store.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.station.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%>
<%
	JSONObject conf_jo = StationLocal.getInstance().toConfigJO() ;
%>
<html>
<head>
<title>local station editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(700,500);
</script>
<style>
</style>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Station Id:</label>
    <div class="layui-input-inline" style="width: 250px;">
      <input type="text" id="id" name="id" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid"><w:g>enable</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable"   lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
 </div>
  <div class="layui-form-item">
<div class="layui-form-label"><w:g>remote,host</w:g>:</div>
	  <div class="layui-input-inline" style="width: 250px;">
	    <input type="text" id="platform_host" name="platform_host" value=""  autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid"><w:g>port</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="number" id="platform_port" name="platform_port"  value="9090" class="layui-input">
	  </div>
</div>
  <div class="layui-form-item">
<div class="layui-form-label">Key:</div>
	  <div class="layui-input-inline" style="width: 250px;">
	    <input type="text" id="key" name="key" value=""  autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid"><w:g>remote_can_write</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="can_write" name="can_write"   lay-skin="switch"  lay-filter="can_write" class="layui-input">
	  </div>
</div>
 </form>
</body>
<script type="text/javascript">
var input = <%=conf_jo%>;
if(input)
{
	$("#id").val(input.id||"") ;
	$("#enable").prop("checked",input.enable) ;
	$("#platform_host").val(input.platform_host||"") ;
	$("#platform_port").val(input.platform_port||"") ;
	$("#can_write").prop("checked",input.can_write||false) ;
	$("#key").val(input.key||"") ;
}

layui.use('form', function(){
	  var form = layui.form;
	 
	  form.render();
});

function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	var id = $('#id').val();
	if(!id)
	{
		cb(false,'<w:g>pls,input</w:g>ID') ;
		return ;
	}
	var h = $('#platform_host').val();
	if(!h)
	{
		cb(false,'<w:g>pls,input,remote,host</w:g>ID') ;
		return ;
	}
	let p = parseInt($("#platform_port").val()||"9090");

	var ben = $("#enable").prop("checked") ;
	var bw = $("#can_write").prop("checked") ;
	
	var key = $('#key').val();
	if(!key)
	{
		cb(false,'<w:g>pls,input</w:g>Key') ;
		return ;
	}
	
	cb(true,{id:id,platform_host:h,platform_port:p,enable:ben,key:key,can_write:bw});
}

</script>
</html>