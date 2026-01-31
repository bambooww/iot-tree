<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.service.*,
				org.iottree.ext.grpc.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
ServiceGRPC ser = (ServiceGRPC)ServiceManager.getInstance().getService(ServiceGRPC.NAME);
	HashMap<String,String> pms = ser.getConfPMS() ;
	boolean enable = ser.isEnable();//ser.isMqttEn();
	
	String chked_en = "" ;
	if(enable)
		chked_en = "checked=checked";
	
	String port = ser.getPortStr() ;

%>
<html>
<head>
<title>editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(600,400);
</script>
<style>
.client_item {border:1px solid #ccc;border-radius: 3px;padding: 2px;,maring:3px;}
</style>
</head>
<body>
<form class="layui-form" action="">

<div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>enable</wbt:g>:</label>
	  <div class="layui-input-inline" style="width: 80px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
	  <div class="layui-form-mid"><wbt:g>local</wbt:g> IP:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	  	<select id="loc_ip">
	  	<option value="" > --- </option>

</select>
	  </div>
	  <div class="layui-form-mid"><wbt:g>port</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="number" id="port" name="port" value="<%=port%>"  class="layui-input">
	  </div>
  </div>
<%--  
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>port</wbt:g>:</label>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="number" id="port" name="port" value="<%=port%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid"></div>
	  <div class="layui-input-inline" style="width: 70px;">
	    
	  </div>
  </div>
 --%>
   <div class="layui-form-item">
    <label class="layui-form-label"></label>
	  <div class="layui-input-inline" style="width: 350px;">
<%
List<ConnStateMonitor.ConnItem> cis = ConnStateMonitor.listConnItems() ;
for(ConnStateMonitor.ConnItem ci:cis)
{
%><div class="client_item"><%=ci %></div><%
}
%>
	  </div>
  </div>
</form>
</body>
<script type="text/javascript">

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
	let enable = $("#enable").prop("checked") ;
	var port = $('#port').val();
	if(port==null||port=='')
	{
		cb(false,'<wbt:g>pls,input</wbt:g><wbt:g>port</wbt:g>') ;
		return ;
	}
	var int_port = parseInt(port);
	if(int_port==NaN||int_port<=0)
	{
		cb(false,'Please input valid port') ;
	}
	
	cb(true,{enable:enable,port:int_port});
}

</script>
</html>